import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Date;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
	    // Connection to Database.
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
	    
        try {
    		String bd = "ExitDetection"; 
            String dbURL = "jdbc:mysql://localhost/"+bd;
            String username = "root";
            String password = "karmic";
			Class.forName("com.mysql.jdbc.Driver");

            conn =
                DriverManager.getConnection(dbURL, username, password);

            stmt = conn.createStatement();
            
            String now1 = "13:03:30";
            String now2 = "13:04:18";
            int idNode = 701;

            if (stmt.execute("select * from samples where today=\"2012-04-15\" and now > \""+now1+"\" and now < \""+now2+"\" and id_node=\""+idNode+"\" order by now asc")) {
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
                		System.out.println("-------------------------------");
                }
                
                String entry = rs.getString(2)+"\t"+rs.getString(4)+"\t1";
                System.out.println(entry);
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
	}

}
