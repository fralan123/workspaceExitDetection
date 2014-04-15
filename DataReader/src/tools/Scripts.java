package tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import tools.LinkDataBase;

public class Scripts {

	//Declaracion de variables.
		LinkDataBase conexion = new LinkDataBase();
		Statement stm = null;
		PreparedStatement ps = null, psa = null;
		ResultSet rs = null, rsa = null;
		int a=0, i=0, r=0, node_id[], num_nodes=0,  initial_limit = 0, size_w = 0;
		float media = 0;
		String path="/home/atempa/Desktop/";
		
		public void processData() throws IOException{    
	          	String arch = path+"Pirometro.dat";
	    		FileWriter fw = new FileWriter (arch);
	            BufferedWriter bw = new BufferedWriter (fw);
	            PrintWriter salArch = new PrintWriter (bw);
	        	System.out.println("Comenzando a generar el archivo resultset");
	        	System.out.println("Limite inicial en la posicion "+a);
	        	conexion.conectar();
	        	try {
	    			stm = conexion.con.createStatement();
	    			rs = stm.executeQuery("SELECT * FROM samples WHERE id_node = 802 ORDER BY day asc, month asc, hour asc, min asc, sec asc");
	                   while(rs.next()){
	                       salArch.println(rs.getString("day")+"/"+rs.getString("month")+"/12"+"\t"+rs.getString("hour")+rs.getString("min")+rs.getString("sec")+"\t"+rs.getInt("id_node")+"\t"+rs.getInt("data_nodes"));                              
	                   }
	                   salArch.close();
	    		} catch (SQLException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		} 
	    		
	            System.out.println("Se ha completado el archivo ");
	            conexion.desconectar();
		}
		
		public void createNodes(){
			conexion.conectar(); //Abrimos una conexion con la base de datos.
			try {
				stm = conexion.con.createStatement();
				rs = stm.executeQuery("SELECT * FROM nodes"); //Se busca el indicador o la marca inicial.
				while(rs.next()){
					num_nodes++;		
				}
				System.out.println("Se creo el arreglo de longitud "+num_nodes);
				node_id = new int[num_nodes]; // Se define una longitud en el arreglo
				i=0;
				stm = conexion.con.createStatement();
				rs = stm.executeQuery("SELECT * FROM nodes "); //Se busca el indicador o la marca inicial.
				while(rs.next()){
		        	node_id[i] = rs.getInt("id_node");
		        	System.out.println("Node ID "+node_id[i]+" en la posicion "+i);
		        	i++;
		        }
				rs.close();
				stm.close();
			}  catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
	        conexion.desconectar();
		}
		
		public void updateColumns(){
			conexion.conectar(); //Abrimos una conexion con la base de datos.
			try{
				System.out.println("Actualizando la columna 'day'");
				for(int i=0; i < 10; i++){
					ps = conexion.con.prepareStatement("UPDATE samples SET day='0"+i+"' WHERE day='"+i+"';");
					ps.executeUpdate();
					ps.close();
				}
				
				System.out.println("Actualizando la columna 'month'");
				for(int i=0; i < 10; i++){
					ps = conexion.con.prepareStatement("UPDATE samples SET month='0"+i+"' WHERE month='"+i+"';");
					ps.executeUpdate();
					ps.close();
				}
				
				System.out.println("Actualizando la columna 'hour'");
				for(int i=0; i < 10; i++){
					ps = conexion.con.prepareStatement("UPDATE samples SET hour='0"+i+"' WHERE hour='"+i+"';");
					ps.executeUpdate();
					ps.close();
				}
				
				System.out.println("Actualizando la columna 'min'");
				for(int i=0; i < 10; i++){
					ps = conexion.con.prepareStatement("UPDATE samples SET min='0"+i+"' WHERE min='"+i+"';");
					ps.executeUpdate();
					ps.close();
				}
				
				System.out.println("Actualizando la columna 'sec'");
				for(int i=0; i < 10; i++){
					ps = conexion.con.prepareStatement("UPDATE samples SET sec='0"+i+"' WHERE sec='"+i+"';");
					ps.executeUpdate();
					ps.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conexion.desconectar();
		}
		
		public void iteraciones(int id_node){
			conexion.conectar(); //Abrimos una conexion con la base de datos.
			try {
				psa = conexion.con.prepareStatement("SELECT  count(data_nodes) as total FROM samples WHERE id_node = "+id_node);
				//Procesamiento de la sentencia SQL.
				rsa = psa.executeQuery();
				//Comienza a determinar la cantidad de registros.
				if(rsa.next()){
					r = rsa.getInt("total");
				}
				rsa.close();
				psa.close();
				System.out.println("No. de datos del nodo ID "+id_node+" es igual a "+r);
				psa = conexion.con.prepareStatement("SELECT  sum(data_nodes) / count(data_nodes) as media FROM samples WHERE id_node = "+id_node);
				//Procesamiento de la sentencia SQL.
				rsa = psa.executeQuery();
				//Comienza a determinar la media de los registros.
				if(rsa.next()){
					media = rsa.getFloat("media");
				}
				rsa.close();
				psa.close();
				System.out.println("La media del nodo ID "+id_node+" es igual a "+media);
			} catch (SQLException e) {
				//Se devuelve algun tipo de error.
				e.printStackTrace();
			}
			conexion.desconectar();
		}
		
		public void normalizationData() throws IOException{    
			createNodes();
	        for(int i=0; i < node_id.length; i++){
	        	a = 0;   	
	          	String arch = path+"resultset_"+node_id[i]+".dat";
	    		FileWriter fw = new FileWriter (arch);
	            BufferedWriter bw = new BufferedWriter (fw);
	            PrintWriter salArch = new PrintWriter (bw);
	        	System.out.println("Comenzando a generar el archivo resultset para el NODO "+node_id[i]);
	        	System.out.println("Limite inicial en la posicion "+a);
	        	iteraciones(node_id[i]);//Se obtiene la cantidad a repetir el procedimiento.
	        	conexion.conectar();
	        	try {
	    			stm = conexion.con.createStatement();
	    			rs = stm.executeQuery("SELECT *, ((data_nodes / "+media+" )) as media FROM samples WHERE id_node ="+node_id[i]+" ORDER BY day asc, month asc, hour asc, min asc, sec asc");
	                   while(rs.next()){
	                       salArch.println(rs.getString("day")+"/"+rs.getString("month")+"/12"+"\t"+rs.getString("hour")+rs.getString("min")+rs.getString("sec")+"\t"+rs.getInt("id_node")+"\t"+rs.getInt("data_nodes")+"\t"+rs.getFloat("media"));                              
	                   }
	                   salArch.close();
	    		} catch (SQLException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		} 
	    		
	            System.out.println("Se ha completado el archivo para el NODO "+node_id[i]);
	            conexion.desconectar();
	       }
		}
		
		public void createScript() throws IOException{
	        conexion.conectar();
	        try {
				stm = conexion.con.createStatement();
				rs = stm.executeQuery("SELECT * FROM nodes");
				while(rs.next()){
		          	String script = path+"Script_Node_"+rs.getInt("id_node");
		    		FileWriter fw = new FileWriter(script);
		            BufferedWriter bw = new BufferedWriter (fw);
		            PrintWriter output = new PrintWriter (bw);

		            output.println("set terminal png");
		            output.println("set output 'Muestras "+rs.getString("description")+".png'");
		            output.println("set title 'Muestras "+rs.getString("description")+"'");
		            output.println("set xdata time");
		            output.println("set timefmt \"%d/%m/%y\\t%H%M%S\"");
		            output.println("set format x \"%H:%M\"");
		            output.println("set xrange [\"16/07/12\t1800\":\"19/07/12\t2200\"]");
		            output.println("set xtics");
		            output.println("#set ytics");
		            output.println("#set yrange[-0.5:4]");
		            output.println("plot \"resultset_"+rs.getInt("id_node")+".dat\" using 1:4 t 'Nodo "+rs.getInt("id_node")+" - "+rs.getString("description")+"' with lines");
		    		output.close();
		            System.out.println("Se ha completado el archivo para el NODO "+rs.getInt("id_node"));
				}
				rs.close();
				stm.close();
	        } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        conexion.desconectar();
		}
		
		public void createScriptNorm() throws IOException{
	        try {
		          	String script = path+"Script";
		    		FileWriter fw = new FileWriter(script);
		            BufferedWriter bw = new BufferedWriter (fw);
		            PrintWriter output = new PrintWriter (bw);

		            output.println("set terminal png");
		            output.println("set output 'Muestras Normalizadas.png'");
		            output.println("set title 'Muestras Normalizadas'");
		            output.println("set xdata time");
		            output.println("set timefmt \"%d/%m/%y\\t%H%M%S\"");
		            output.println("set format x \"%H:%M\"");
		            output.println("set xrange [\"16/07/12\t1800\":\"19/07/12\t2200\"]");
		            output.println("set xtics");
		            output.println("set ytics");
		            output.println("set yrange[-0.5:4]");
		            output.println("plot \"resultset_801.dat\" using 1:5 t 'Nodo 801 - Microfono' with lines, \"resultset_802.dat\" using 1:5 t 'Nodo 802 - Magnetometro', \"resultset_803.dat\" using 1:5 t 'Nodo 803 - Pirometro', \"resultset_804.dat\" using 1:5 t 'Nodo 804 - Intensidad Luminosa' with lines");
		    		output.close();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void createScript(String dya, String mtha, String hra, String mina, String dyb, String mthb, String hrb, String minb) throws IOException{
	        conexion.conectar();
	        try {
				stm = conexion.con.createStatement();
				rs = stm.executeQuery("SELECT * FROM nodes");
				while(rs.next()){
		          	String script = path+"Script_Node_"+rs.getInt("id_node");
		    		FileWriter fw = new FileWriter(script);
		            BufferedWriter bw = new BufferedWriter (fw);
		            PrintWriter output = new PrintWriter (bw);

		            output.println("set terminal png");
		            output.println("set output 'Muestras "+rs.getString("description")+".png'");
		            output.println("set title 'Muestras "+rs.getString("description")+"'");
		            output.println("set xdata time");
		            output.println("set timefmt \"%d/%m/%y\\t%H%M%S\"");
		            output.println("set format x \"%H:%M\"");
		            output.println("set xrange [\""+dya+"/"+mtha+"/12\\t"+hra+mina+"\":\""+dyb+"/"+mthb+"/12\\t"+hrb+minb+"\"]");
		            output.println("set xtics");
		            output.println("plot \"resultset_"+rs.getInt("id_node")+".dat\" using 1:4 t 'Nodo "+rs.getInt("id_node")+" - "+rs.getString("description")+"' with lines");
		    		output.close();
		            System.out.println("Se ha completado el archivo para el NODO "+rs.getInt("id_node"));
				}
	        } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        conexion.desconectar();
		}

}
