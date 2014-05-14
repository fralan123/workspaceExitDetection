
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import tools.Calculate;
import tools.LinkDataBase;
import tools.DateTime;
import net.tinyos.message.*;
import net.tinyos.util.*;
import net.tinyos.packet.*;
import java.util.Calendar;

public class DataReader2 implements MessageListener {

    // DateTime;
    DateTime arrival = new DateTime();
    // TinyOS required.
    private PhoenixSource phoenix;
    private MoteIF mif;
    //Magnetometer
    int MagTA = 0;
    String PCTime = "";
       Calendar now2 = Calendar.getInstance();

    public DataReader2(final String source) {
        phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
        mif = new MoteIF(phoenix);
        mif.registerListener(new DataMsg(), this);
    }
    public void dbInsertion(int nodeID, int dataM, int dataX, int localTime)
    {
    	Calendar now = Calendar.getInstance();
    	String time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
        
        if(dataX!=65295)
        	System.out.println("Node_ID:" + nodeID + " Node_time:" + localTime + " time:" + time + " dataX=" + dataX + " dataM=" + dataM );
        
      
            switch (nodeID) {
                case 701:
                	if(dataX<65200)
                	{
                		System.out.println(nodeID);
                		System.out.println(arrival.Today());
                		System.out.println(time);
                		System.out.println(dataM);
                		System.out.println(localTime);
                	}
                	break;
                case 702:
                	if(dataX<65200)
                	{      
                		System.out.println(nodeID);
                		System.out.println(arrival.Today());
                		System.out.println(time);
                		System.out.println(dataM);
                		System.out.println(localTime);                		
                	}
                	break;
                case 703:	                	
                		System.out.println(nodeID);
	            		System.out.println(arrival.Today());
	            		
	            		
                        //inicia algoritmo normalizador magnetometro
                        int MagTB = (int) localTime;
                        int resta=MagTB - MagTA;
                        //System.out.println("resta="+resta);
                        if ((MagTB - MagTA) > 1224) {
                            now2 = Calendar.getInstance();
                            PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":" + now2.get(Calendar.MINUTE) + ":" + now2.get(Calendar.SECOND);
                            MagTA = MagTB;
                            System.out.println(time);
                        }else{
                            now2.add(Calendar.SECOND, 1);
                            PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":" + now2.get(Calendar.MINUTE) + ":" + now2.get(Calendar.SECOND);
                            System.out.println(time);
                            MagTA = (int) localTime;

                        }
                        //termina algoritmo normalizador magnetometro

	            		System.out.println(dataM);
	            		System.out.println(localTime);                      
                        break;
            }
    }

    public void messageReceived(int i, Message msg) {
        if (msg instanceof DataMsg) {
            DataMsg results = (DataMsg) msg;    
            if(results.get_dataX()!=65295)
            	dbInsertion(results.get_NodeID(), results.get_dataM(), results.get_dataX(), (int)results.get_localTime());
        }
    }    

    public static void main(String[] args) {
       new DataReader2("serial@/dev/ttyUSB1:iris");
    }
}

