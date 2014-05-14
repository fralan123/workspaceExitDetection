package tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class DatabaseFilter {
	Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    static StringBuilder sb = new StringBuilder();
    
	public static void main(String[] args) {
		
		DatabaseFilter obj = new DatabaseFilter();
		sb = obj.readDatabase();
		obj.writeToFile(sb);

	}
	public StringBuilder readDatabase()
	{
		StringBuilder _sb = new StringBuilder();
		 try {
	    		String bd = "xdtec"; 
	            String dbURL = "jdbc:mysql://localhost/"+bd;
	            String username = "root";
	            String password = "karmic";
				Class.forName("com.mysql.jdbc.Driver");

	            conn =
	                DriverManager.getConnection(dbURL, username, password);

	            stmt = conn.createStatement();

	            if (stmt.execute("select * from samples order by now")) {
	                rs = stmt.getResultSet();
	            } else {
	                System.err.println("select failed");
	            }
	            Time nodeA = null;
	            Time nodeB = null;
	            while (rs.next()) {
	            	if(nodeA == null)            	
	            		nodeA = rs.getTime(4);            		            	
	            	else
	            	{
	            		if(nodeB==null)
	            			nodeB = rs.getTime(4);
	            		else
	            		{
	                		nodeA = nodeB;
	                		nodeB = rs.getTime(4);            			
	            		}
	            	}
	                
	                if(nodeA!=null&&nodeB!=null)
	                {
	                	double diff = (nodeB.getTime()-nodeA.getTime())/1000;
	                	if(diff>5)
	                	{
	                		System.out.println("00000\t000\t0000-00-00\t00:00:00");
	                		_sb.append("00000\t000\t0000-00-00\t00:00:00");
	                	}
	                }
	                
	                String entry = rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4);
	                System.out.println(entry);
	                _sb.append(entry);
	            }

	        } catch (ClassNotFoundException ex) {
	            System.err.println("Failed to load mysql driver");
	            System.err.println(ex);
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage()); 
	            System.out.println("SQLState: " + ex.getSQLState()); 
	            System.out.println("VendorError: " + ex.getErrorCode()); 
	        } finally {
	            if (rs != null) {
	                try {
	                    rs.close();
	                } catch (SQLException ex) { /* ignore */ }
	                rs = null;
	            }
	            if (stmt != null) {
	                try {
	                    stmt.close();
	                } catch (SQLException ex) { /* ignore */ }
	                stmt = null;
	            }
	            if (conn != null) {
	                try {
	                    conn.close();
	                } catch (SQLException ex) { /* ignore */ }
	                conn = null;
	            }
	        }
		 return _sb;
	}

    public void writeToFile(StringBuilder sb)
    {
    	  try{
    		  // Create file 
    		  FileWriter fstream = new FileWriter("~/out.txt");
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(sb.toString());
    		  //Close the output stream
    		  out.close();
    		  }catch (Exception e){//Catch exception if any
    			  System.out.println("ERROR WRITING THE FILE");
    		  System.err.println("Error: " + e.getMessage());
    		  }
    		  
    }

}
