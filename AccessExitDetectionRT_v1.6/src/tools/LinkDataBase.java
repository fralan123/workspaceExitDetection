package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LinkDataBase {
	//Elementos para establecer la conexión de JAVA con MYSQL.
		static String bd = "ExitDetection";
		//static String bd = "xdtec";
		static String login = "root";
		static String password = "karmic";
		static String url = "jdbc:mysql://localhost/"+bd;
		public Connection con = null;
		
		public Connection getConnection(){
			return con;
		}
		
		public void conectar(){
			//Se construye la conexión de datos.
			try{
				Class.forName("com.mysql.jdbc.Driver");
				//Obtenemos la conexión.
				con = DriverManager.getConnection(url,login,password); 
				//System.out.println("OK !!!");
			}
			catch (SQLException e) {
				System.out.println(e);
			}
			catch(ClassNotFoundException e){
				System.out.println(e);
			}
		}
		
		public void desconectar(){
			con = null;
		}
		
}
