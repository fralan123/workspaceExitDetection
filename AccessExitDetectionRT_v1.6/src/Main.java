
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
import java.util.Date;
import java.util.Vector;

public class Main implements MessageListener
{
	// TinyOS required.
	private PhoenixSource phoenix;
	private MoteIF mif;
	// Magnetometer
	int MagTA = 0;
	String PCTime;
	Calendar now2 = Calendar.getInstance();
	DateTime arrival = new DateTime();
	double T = 1100;			//Time in milliseconds	
	
	Vector<Node> vector;
	Functions function =  new Functions();
	Date timeA, timeB;
	private boolean inactivityP1;
	private boolean inactivityP2;

	public Main(final String source)
	{
		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		mif = new MoteIF(phoenix);
		mif.registerListener(new DataMsg(), this);
		init();
	}
	public void init()
	{
		vector = new Vector<Node>();
		inactivityP1 = false;
		inactivityP2 = false;
	}
	public void createVector(int nodeID)
	{		
		Node node = new Node(nodeID, new Date());
		if (vector.size() > 1)
		{
			if(function.getDifference(node,vector.lastElement())>T)
			{
				//new Algorithm(vector);
				new Algorithm(vector,10);
				init();
			}
		}
		vector.add(node);
	}
	public void messageReceived(int i, Message msg)
	{
		if (msg instanceof DataMsg)
		{
			DataMsg results = (DataMsg) msg;
			// if(results.get_dataX()!=65295)
			if (results.get_dataX() < 65200)
			{
				Calendar now = Calendar.getInstance();
				String time = now.get(Calendar.HOUR_OF_DAY) +":"+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);				
				System.out.println("Node_ID:" + results.get_NodeID() + " Node_time:" + results.get_localTime()
						+ " time:" + time + " dataX=" + results.get_dataX() + " dataM=" + results.get_dataM());
				//Inserta el ID del nodo en el vector
				createVector(results.get_NodeID());
			}
//			else
//			{
//				if(results.get_NodeID()==701)
//					inactivityP1=true;
//				if(results.get_NodeID()==702)
//					inactivityP2=true;
//				if(inactivityP1&&inactivityP2&&(vector.size()>2))
//				{
//					new Algorithm(vector);
//					init();
//				}
//					
//			}

		}
	}

	public static void main(String[] args)
	{
		new Main("serial@/dev/ttyUSB1:iris");
	}
}
