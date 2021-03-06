package test;

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

public class Tester implements MessageListener {

    // DateTime;
    DateTime arrival = new DateTime();
    // Connection to Database.
    LinkDataBase conexion = new LinkDataBase();
    Statement stm = null;
    PreparedStatement ps = null, psa = null;
    ResultSet rs = null, rsa = null;
    // TinyOS required.
    private PhoenixSource phoenix;
    private MoteIF mif;
    // Object of class Main.
    private static Tester hy;
    int MagTA = 0;
    String PCTime = "";
       Calendar now2 = Calendar.getInstance();

    public Tester(final String source) {
        phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
        mif = new MoteIF(phoenix);
        mif.registerListener(new DataMsg(), this);
    }
    public void dbInsertion(int nodeID, int dataM, int localTime)
    {
    	Calendar now = Calendar.getInstance();
    	String time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
        conexion.conectar(); // Open the connection with database.
        try {
        	ps = conexion.con.prepareStatement(
                    "INSERT INTO samples "
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, 0);
            switch (nodeID) {
                case 701:
                    for (int j = 0; j < 5; j++) {                                               
                        ps.setInt(2, nodeID);
                        ps.setString(3, arrival.Today());
                        ps.setString(4, time);
                        ps.setInt(5, dataM);
                        ps.setInt(6, localTime + (1024*j)); //ps.setInt(6, localTime);
                        ps.executeUpdate();
                        ps.close();
                        now.add(Calendar.SECOND, 1);
                        System.out.println(time);
                        //System.out.println(results.get_dataM()+"="+j);
                    }
                    
                case 703:                
                        ps.setInt(2, nodeID);
                        ps.setString(3, arrival.Today());
                        //inicia algoritmo normalizador magnetometro
                        int MagTB = (int) localTime;
                        int resta=MagTB - MagTA;
                        System.out.println("resta="+resta);
                        if ((MagTB - MagTA) > 1224) {
                            now2 = Calendar.getInstance();
                            PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":" + now2.get(Calendar.MINUTE) + ":" + now2.get(Calendar.SECOND);
                            MagTA = MagTB;
                            ps.setString(4,PCTime);
                        }else{
                            now2.add(Calendar.SECOND, 1);
                            PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":" + now2.get(Calendar.MINUTE) + ":" + now2.get(Calendar.SECOND);
                            ps.setString(4,PCTime);
                            System.out.println(PCTime);
                            MagTA = (int) localTime;

                        }
                        //termina algoritmo normalizador magnetometro

                        ps.setInt(5, dataM);
                        ps.setInt(6, (int) localTime);
                        ps.executeUpdate();
                        ps.close();

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Error ");
            e.printStackTrace();
        }
        conexion.desconectar();
    }

    public void messageReceived(int i, Message msg) {
        if (msg instanceof DataMsg) {
            DataMsg results = (DataMsg) msg;
            System.out.println("Node_ID: " + results.get_NodeID() + " Node's time:" + results.get_localTime() + " dataX=" + results.get_dataX() + " dataM=" + results.get_dataM() + "\n");
            Calendar now = Calendar.getInstance();
            String time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

            //System.out.println(time);
            //dbInsertion(results.get_NodeID(), results.get_dataM(), (int)results.get_localTime());

            switch (results.get_NodeID()) {
            case 701: {
                conexion.conectar(); // Open the connection with database.
                try {

                    switch (results.get_dataM()) {
                        case 1:
                            for (int j = 0; j < 5; j++) {
                                time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
                                ps = conexion.con.prepareStatement(
                                        "INSERT INTO samples "
                                        + "VALUES (?, ?, ?, ?, ?, ?)");
                                ps.setInt(1, 0);
                                ps.setInt(2, results.get_NodeID());
                                ps.setString(3, arrival.Today());
                                ps.setString(4, time);
                                ps.setInt(5, results.get_dataM());
                                ps.setInt(6, (int) results.get_localTime() + (1024 * j));
                                ps.executeUpdate();
                                ps.close();
                                now.add(Calendar.SECOND, 1);
                                System.out.println(time);
                                //System.out.println(results.get_dataM()+"="+j);
                            }
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    System.out.println("Error ");
                    e.printStackTrace();
                }
                conexion.desconectar();
            }
            break;
                case 702: {
                    conexion.conectar(); // Open the connection with database.
                    try {

                        switch (results.get_dataM()) {
                            case 1:
                                for (int j = 0; j < 5; j++) {
                                    time = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
                                    ps = conexion.con.prepareStatement(
                                            "INSERT INTO samples "
                                            + "VALUES (?, ?, ?, ?, ?, ?)");
                                    ps.setInt(1, 0);
                                    ps.setInt(2, results.get_NodeID());
                                    ps.setString(3, arrival.Today());
                                    ps.setString(4, time);
                                    ps.setInt(5, results.get_dataM());
                                    ps.setInt(6, (int) results.get_localTime() + (1024 * j));
                                    ps.executeUpdate();
                                    ps.close();
                                    now.add(Calendar.SECOND, 1);
                                    System.out.println(time);
                                    //System.out.println(results.get_dataM()+"="+j);
                                }
                        }
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Error ");
                        e.printStackTrace();
                    }
                    conexion.desconectar();
                }
                break;

                case 703:
                    conexion.conectar(); // Open the connection with database.
                    try {
                        ps = conexion.con.prepareStatement(
                                "INSERT INTO samples "
                                + "VALUES (?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, 0);
                        ps.setInt(2, results.get_NodeID());
                        ps.setString(3, arrival.Today());
                        //inicia algoritmo normalizador magnetometro
                        int MagTB = (int) results.get_localTime();
                        int resta=MagTB - MagTA;
                        System.out.println("resta="+resta);
                        if ((MagTB - MagTA) > 1224) {
                            now2 = Calendar.getInstance();
                            PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":" + now2.get(Calendar.MINUTE) + ":" + now2.get(Calendar.SECOND);
                            MagTA = MagTB;
                            ps.setString(4,PCTime);
                        }else{
                            now2.add(Calendar.SECOND, 1);
                            PCTime = now2.get(Calendar.HOUR_OF_DAY) + ":" + now2.get(Calendar.MINUTE) + ":" + now2.get(Calendar.SECOND);
                            ps.setString(4,PCTime);
                            System.out.println(PCTime);
                            MagTA = (int) results.get_localTime();

                        }
                        //termina algoritmo normalizador magnetometro

                        ps.setInt(5, results.get_dataM());
                        ps.setInt(6, (int) results.get_localTime());
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Error 703");
                        e.printStackTrace();
                    }
                    conexion.desconectar();
                    break;                    
            }
        }
    }

    public static void main(String[] args) {
        hy = new Tester("serial@/dev/ttyUSB1:iris");
    }
}

