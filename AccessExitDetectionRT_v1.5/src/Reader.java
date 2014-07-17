
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

public class Reader implements MessageListener
{

	// Database Connection
	LinkDataBase conexion = new LinkDataBase();
	Statement stm = null;
	PreparedStatement ps = null, psa = null;
	ResultSet rs = null, rsa = null;
	// TinyOS required.
	private PhoenixSource phoenix;
	private MoteIF mif;
	// Magnetometer
	int MagTA = 0;
	String PCTime;
	Calendar now2 = Calendar.getInstance();
	DateTime arrival = new DateTime();
	
	Vector<Node> vector;
	private Date timeA;
	private boolean inactivityP1;
	private boolean inactivityP2;

	public Reader(final String source)
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
	public void dbInsertion(int nodeID, int dataM, int dataX, int localTime)
	{
		Calendar now = Calendar.getInstance();
		String time = now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
		conexion.conectar(); // Open the connection with database.
		if (dataX != 65295)
			System.out.println("Node_ID:" + nodeID + " Node_time:" + localTime
					+ " time:" + time + " dataX=" + dataX + " dataM=" + dataM);

		try
		{
			ps = conexion.con.prepareStatement("INSERT INTO samples "
					+ "VALUES (?, ?, ?, ?, ?, ?)");
			ps.setInt(1, 0);
			switch (nodeID)
			{

			default:
				ps.setInt(2, nodeID);
				ps.setString(3, arrival.Today());
				ps.setString(4, time);
				ps.setInt(5, dataM);
				ps.setInt(6, localTime);
				ps.executeUpdate();
				ps.close();
				now.add(Calendar.SECOND, 1);
				break;

			case (703):
				ps.setInt(2, nodeID);
				ps.setString(3, arrival.Today());
				// inicia algoritmo normalizador magnetometro
				int MagTB = localTime;
				int resta = MagTB - MagTA;
				// System.out.println("resta="+resta);
				if ((resta) > 1224)
				{
					now2 = Calendar.getInstance();
					PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":"
							+ now2.get(Calendar.MINUTE) + ":"
							+ now2.get(Calendar.SECOND);
					MagTA = MagTB;
					ps.setString(4, time);
					// ps.setString(4,PCTime);
				} else
				{
					now2.add(Calendar.SECOND, 1);
					PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":"
							+ now2.get(Calendar.MINUTE) + ":"
							+ now2.get(Calendar.SECOND);
					ps.setString(4, time);
					// ps.setString(4,PCTime);
					// System.out.println(PCTime);
					MagTA = MagTB;

				}
				// termina algoritmo normalizador magnetometro

				ps.setInt(5, dataM);
				ps.setInt(6, (int) localTime);
				ps.executeUpdate();
				ps.close();
				break;

			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			System.out.println("Error ");
			e.printStackTrace();
		}
		conexion.desconectar();
	}

	public void createVector(int nodeID)
	{
		if (vector.size() > 0)
			timeA = vector.lastElement().getTime();
		vector.add(new Node(nodeID, new Date()));

	}

	public void messageReceived(int i, Message msg)
	{
		if (msg instanceof DataMsg)
		{
			DataMsg results = (DataMsg) msg;
			// if(results.get_dataX()!=65295)
			if (results.get_dataX() < 65200)
			{
				dbInsertion(results.get_NodeID(), results.get_dataM(),results.get_dataX(), (int) results.get_localTime());
				createVector(results.get_NodeID());
			}
			else
			{
				if(results.get_NodeID()==701)
					inactivityP1=true;
				if(results.get_NodeID()==702)
					inactivityP2=true;
				if(inactivityP1&&inactivityP2&&(vector.size()>2))
				{
					new Algorithm(vector);
					init();
				}
					
			}

		}
	}

	public static void main(String[] args)
	{
		new Reader("serial@/dev/ttyUSB1:iris");
	}
}
