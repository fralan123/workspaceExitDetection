
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

public class Tester implements MessageListener
{
	// TinyOS required.
	private PhoenixSource phoenix;
	private MoteIF mif;
	// Magnetometer
	int MagTA = 0;
	String PCTime;
	Calendar now2 = Calendar.getInstance();
	DateTime arrival = new DateTime();
	double T = 2000;			//Time in milliseconds	
	
	public Tester(final String source)
	{
		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		mif = new MoteIF(phoenix);
		mif.registerListener(new DataMsg(), this);	
	}
	public void messageReceived(int i, Message msg)
	{
		if (msg instanceof DataMsg)
		{
			DataMsg results = (DataMsg) msg;
			// if(results.get_dataX()!=65295)
			//if (results.get_dataX() < 65200)
			if(true)
			{
				Calendar now = Calendar.getInstance();
				String time = now.get(Calendar.HOUR_OF_DAY) + ":"
						+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
				
				
				System.out.println("Node_ID:" + results.get_NodeID() + " Node_time:" + results.get_localTime()
						+ " time:" + time + " dataX=" + results.get_dataX() + " dataM=" + results.get_dataM());
				
			}

		}
	}

	public static void main(String[] args)
	{
		new Tester("serial@/dev/ttyUSB1:iris");
	}
}
