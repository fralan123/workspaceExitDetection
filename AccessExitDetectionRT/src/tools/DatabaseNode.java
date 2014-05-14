package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

public class DatabaseNode {
	Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    static StringBuilder sb = new StringBuilder();
    String bd = "ExitDetection"; 
    String dbURL = "jdbc:mysql://localhost/"+bd;
    String username = "root";
    String password = "karmic";
    
	public Node getLastNode(String time)
	{
		Node node = new Node();
		 try {
				Class.forName("com.mysql.jdbc.Driver");
	            conn = DriverManager.getConnection(dbURL, username, password);
	            stmt = conn.createStatement();
	            
	            Calendar now = Calendar.getInstance();
	            String today = now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH);
	            today = "2012-05-14";
	            
	            //if (stmt.execute("select * from samples where today=\""+today+"\" and now > \""+now1+"\" and now < \""+now2+"\" order by now")) {
	            if (stmt.execute("select * from samples where today=\""+today+"\" and now >\""+time+"\" order by now asc")) {	            
	                rs = stmt.getResultSet();
	            } else {
	                System.err.println("select failed");
	            }
	            
	            if(rs.first())
	            {
	            	node.setDbID(rs.getInt(1));
	            	node.setNodeID(rs.getInt(1));
	            	node.setTime(rs.getTime(4));
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
		 System.out.println("Node "+node.getNodeID()+" time="+node.getTime());
		 return node;
	}
	public Node getLastNode(String time, int ID)
	{
		Node node = new Node();
		 try {
				Class.forName("com.mysql.jdbc.Driver");
	            conn = DriverManager.getConnection(dbURL, username, password);
	            stmt = conn.createStatement();
	            
	            Calendar now = Calendar.getInstance();
	            String today = now.get(Calendar.YEAR)+"-"+now.MONTH+"-"+now.get(Calendar.DAY_OF_MONTH);
	            today = "2012-05-14";
	            

	            //if (stmt.execute("select * from samples where today=\""+today+"\" and now > \""+now1+"\" and now < \""+now2+"\" order by now")) {
	            if (stmt.execute("select * from samples where today=\""+today+"\" and now >\""+time+"\" and samplesID!="+ID+" order by now asc")) {	            
	                rs = stmt.getResultSet();
	            } else {
	                System.err.println("select failed");
	            }
	            
	            if(rs.first())
	            {
	            	node.setDbID(rs.getInt(1));
	            	node.setNodeID(rs.getInt(1));
	            	node.setTime(rs.getTime(4));
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
		 System.out.println("Node "+node.getNodeID()+" time="+node.getTime());
		 return node;
	}	
    public static void main(String args[])
    {
    	//new DatabaseNode().getLastNode("11:32:58");
    	new DatabaseNode().getLastNode("11:32:58",10182);
    }

}
