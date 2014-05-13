package tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Calendar;

public class DatabaseNode {
	Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    static StringBuilder sb = new StringBuilder();
    String bd = "ExitDetection"; 
    String dbURL = "jdbc:mysql://localhost/"+bd;
    String username = "root";
    String password = "karmic";
    
	public StringBuilder getLastNode()
	{
		StringBuilder _sb = new StringBuilder();
		 try {
				Class.forName("com.mysql.jdbc.Driver");
	            conn = DriverManager.getConnection(dbURL, username, password);
	            stmt = conn.createStatement();
	            
	            Calendar now = Calendar.getInstance();
	            String today = now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH);

	            //if (stmt.execute("select * from samples where today=\""+today+"\" and now > \""+now1+"\" and now < \""+now2+"\" order by now")) {
	            if (stmt.execute("select * from samples where today=\""+today+"\" order by now")) {	            
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
	                		_sb.append("00000\t000\t0000-00-00\t00:00:00\n");
	                	}
	                }
	                
	                String entry = rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\n";
	                System.out.print(entry);
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

    public static void main(String args[])
    {
    	new DatabaseNode().getLastNode();
    }

}
