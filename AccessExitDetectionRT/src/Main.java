/**
 * 
 * @author 		Alan Bonino
 * @date		24-June-2013
 * Changes: - Using the state machine
 *
 */
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import fsm.*;
import fsm.StateMachine.Event;
import tools.Node;
public class Main {

	Main(){}
	Main(double lambda2)
	{
		this.lambda = lambda2;
		System.out.println("Lambda = "+lambda2);
		//this.dataReader = new DataReader(("serial@/dev/ttyUSB1:iris"));
		// Aquí se pone en marcha el timer cada segundo. 
	     Timer timer = new Timer(); 
	     // Dentro de 0 milisegundos avísame cada 1000 milisegundos 
	     //timer.scheduleAtFixedRate(timerTask, 0, 1000);
	     start.getTime();
	    initializeVariables();
	    readData();
	}
	
	DataReader dataReader;
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 	// For time	
	Vector<Node> W = new Vector<Node>();
	int b,m,l;
	double T = 2000;			//Time in milliseconds	
	final static int O = 1;		//Output 	= 1
	final static int I = 2;		//Input 	= 2
	final static int PI = 3;	//Pyrometer Inside	= 3
	final static int PO = 4;	//Pyrometer Outside	= 4
	final static int M = 5;		//Magnetometer	= 5
	double lambda = 1;	
	//NUEVOS VALORES SIN CONSIDERAR AL PIROMETRO 1
	final double outputMean = 21.9;	
	final double outputStandarDeviation = 1.1192102479;
	final double inputMean = 18.45;
	final double inputStandarDeviation = 1.6693837501;
	
	final double intersectionInputMean=11.9;
	final double intersectionInputStandarDeviation=1.2096106377;
	final double intersectionOutputMean=14.4;
	final double intersectionOutputStandarDeviation=1.095445115;
	
	final double pyrometerInsideInputMean = 6.55;
	final double pyrometerInsideInputStandarDeviation = 1.8488972531;
	final double pyrometerInsideOutputMean = 7.5;
	final double pyrometerInsideOutputStandarDeviation = 0.8885233166;
	
	double l_thresholdInput = pyrometerInsideInputMean+pyrometerInsideInputStandarDeviation;
	double b_thresholdInput = intersectionInputMean+intersectionInputStandarDeviation;
	
	double l_thresholdOutput = intersectionOutputMean+intersectionOutputStandarDeviation;
	double b_thresholdOutput = pyrometerInsideOutputMean+pyrometerInsideOutputStandarDeviation;
	
	double l_threshold = pyrometerInsideInputMean+pyrometerInsideInputStandarDeviation;
	double b_threshold = intersectionOutputMean+intersectionOutputStandarDeviation;
		
	int counterInput=0;
	int counterOutput=0;
	//Variable para saber si existe magnetómetro
	boolean Magnetometer = false;

	public ChangeState cs;
	public Functions function;
	public Node lastNode;
	
    Calendar start = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	public void initializeVariables()
	{
		b = 0; m = 0; l = 0;
		W = new Vector<Node>();
		cs = new ChangeState();
		function = new Functions();   
		lastNode = new Node();
	}
	
	  // Clase en la que está el código a ejecutar 
    TimerTask timerTask = new TimerTask() 
    { 
        public void run()  
        { 
        	//readData();
        } 
    }; 
    public void readData()
    {
    	while(true)
    	{
    	Node node = new Node();
    	System.out.println(sdf.format(start.getTime()));
    	start.add(Calendar.SECOND, -1);
    	String time = sdf.format(start.getTime()); 
    	Functions function = new Functions();
        node = function.getNode(time);
        //First time there's a new event
        if(lastNode.getNodeID()==0&&node.getNodeID()!=0)
        {
        	lastNode=node;
        	W.add(node);
        }
        else 
        	//End of event
        	if(lastNode.getNodeID()!=0&&node.getNodeID()==0)
        		System.out.println("End of event");
        	else
        		//No data yet
        		if(lastNode.getNodeID()==0&&node.getNodeID()==0)
            		System.out.println("No data");
        else
        {
            //Get the next node when there are two samples at the same time
            if(lastNode.getDbID()==node.getDbID())
            {
            	node = function.getNextNode(sdf.format(node.getTime()), node.getDbID());
            	function.getNextNode(sdf.format(start.getTime()), node.getDbID());
            }
        }
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	}
    }
	public void algorithm()
	{
		Node nodeA = new Node(); 
		Node nodeB = new Node();
		
		if(W.lastElement()==null)
			System.out.println("break");
		//CORRECCION DE BUG: Cuando ya termino el evento y despues el NodeID es 0, entonces se reinicia el algoritmo
		if(W.lastElement().getNodeID()==0&&W.size()==1)
			System.out.println("break");
		if(W.lastElement().getNodeID()==0)
			evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
		
		nodeA=W.firstElement();
		nodeB=W.lastElement();
		
		b=2;
		for (int i = 2; i < W.size(); i++)
		{
			if(W.get(i).getNodeID()==W.get(i-1).getNodeID())
				if(W.get(i).getNodeID()!=701)
					b++;
			
			//Second node the first time the algorithm runs
			W.add(nextNode());
			if(W.lastElement()==null)
				break;
			if(W.lastElement().getNodeID()==0)
				evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
			while(getDifference(W.lastElement(),W.get(W.size()-2))<=T)
			{
				b=2;
				//System.out.println(i+"\t");
				W.add(nextNode());
				if(W.lastElement()==null)
					break;
				if(W.lastElement().getNodeID()==0)
					evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				while((getDifference(W.lastElement(),W.get(W.size()-2))<=T)
						&&(W.lastElement().getNodeID()==W.get(W.size()-2).getNodeID()))
				{
					if(W.lastElement().getNodeID()!=701)
						b++;
					//System.out.println(i+"\t");
					W.add(nextNode());
					if(W.lastElement()==null)
						break;
					if(W.lastElement().getNodeID()==0)
						evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				}
				//Si la diferencia es mayor a T, se evalua
				if(getDifference(W.lastElement(),W.get(W.size()-2))>T)
						evaluateInterrupted(nodeA, W.lastElement());
				//Beginning of intersection
				m=1;
				W.add(nextNode());
				if(W.lastElement()==null)
					break;
				if(W.lastElement().getNodeID()==0)
					evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				while((getDifference(W.lastElement(),W.get(W.size()-2))<=T)
						&&((W.lastElement().getNodeID()!=W.get(W.size()-2).getNodeID())
						||(W.get(W.size()-2).getNodeID()!=W.get(W.size()-3).getNodeID())
						||(W.lastElement().getNodeID()!=W.get(W.size()-3).getNodeID())))
				{
					if(W.lastElement().getNodeID()!=701)
						m++;
					W.add(nextNode());	
					//Verifica que exista magnetómetro
					if(W.lastElement().getNodeID()==0)
						Magnetometer=true;
					if(W.lastElement()==null)
						break;
					if(W.lastElement().getNodeID()==0)
						evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				}
				//End of intersection
				if(W.lastElement().getNodeID()==W.get(W.size()-2).getNodeID()&&
						W.lastElement().getNodeID()==W.get(W.size()-3).getNodeID())
				{
					m=m-2;
					l=3;
				}
				else
					l=1;
				

				//Si la diferencia es mayor a T, se evalua
				if(getDifference(W.lastElement(),W.get(W.size()-2))>T)
						evaluateInterrupted(nodeA, W.lastElement());
				
				//AQUI VA LA NUEVA CONDICION SI LA DIFERENCIA ES <= Q				
				W.add(nextNode());
				if(W.lastElement()==null)
					break;
				if(W.lastElement().getNodeID()==0)
					evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				//Se adecua el valor del umbral del final l_threshold
				double l_threshold=0;
				if(function.validateEventType(nodeA, nodeB)==O)				
					l_threshold=l_thresholdOutput;				
				else if(function.validateEventType(nodeA, nodeB)==I)				
					l_threshold=l_thresholdInput;				
				
				while((getDifference(W.lastElement(),W.get(W.size()-2))<=T)&&(l<=l_threshold))
				{
					if(W.lastElement().getNodeID()!=701)
						l++;
					W.add(nextNode());
					if(W.lastElement()==null)
						break;
					if(W.lastElement().getNodeID()==0)
						evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				}

				//Si la diferencia es mayor a T, se evalua
				if(getDifference(W.lastElement(),W.get(W.size()-2))>T)
						evaluateInterrupted(nodeA, W.lastElement());

				//Keep the last node in nodeB
				nodeB = W.lastElement();
				
//				if(b > b_threshold)
//					b = (int) l_threshold;
				
				if(function.validateEventType(nodeA, nodeB)==O)
				{
					if(b > b_thresholdOutput)
						b = (int) l_thresholdOutput;//b = (int) l_threshold;
				}
				else if(function.validateEventType(nodeA, nodeB)==I)
				{
					if(m > b_thresholdInput)
						m = (int) l_thresholdInput;//m = (int) l_threshold;
				}
				
				String e = event(b,m,l,nodeA,nodeB,false); 
				//send_event(e);
				System.out.println(e);
				if(e=="Output")
					counterOutput++;
				if(e=="Input")
					counterInput++;
				//Keep the first node in nodeA 
				nodeA = nextNode();
				W.add(nodeA);		
				if(W.lastElement()==null)
					break;
				if(W.lastElement().getNodeID()==0)
					evaluateInterrupted(nodeA, W.elementAt(W.size()-2));				
			}//Termina primer while de condicion diff<=T
			
		}
	}
	private void evaluateInterrupted(Node nodeA, Node nodeB)
	{
		if(function.validateEventType(nodeA, nodeB)==O)
		{
			if(b > b_thresholdOutput)
				b = (int) l_thresholdOutput;//b = (int) l_threshold;
		}
		else if(function.validateEventType(nodeA, nodeB)==I)
		{
			if(m > b_thresholdInput)
				m = (int) l_thresholdInput;//m = (int) l_threshold;
		}
		
		String e = event(b,m,l,nodeA,nodeB,true); 
		//send_event(e);
		System.out.println(e);
		System.out.println("------------------------------------------------------------");
		if(e=="Output")
			counterOutput++;
		if(e=="Input")
			counterInput++;
		
		cs.changeState(Event.RESTART);
		algorithm();
	}
	
	private String event(int b2, int m2, int l2, Node nodeA, Node nodeB, boolean inactivity) {
		Functions function = new Functions();
		String e = null;
		int eventType = function.validateEventType(nodeA, nodeB);
		
		if(eventType==O)
		{
			if(function.evaluateAccessOrExit(b2+m2, outputMean, lambda, outputStandarDeviation)>=0)
			{
				if(inactivity)
					cs.changeState(Event.INACTIVITY);
				
				cs.changeState(Event.OUTPUT);
				
				e = "Output";			
			}
			else
				e = "Invalid Output";
		}
		if(eventType==I)
		{
			if(function.evaluateAccessOrExit(m2+l2, inputMean, lambda, inputStandarDeviation)>=0)
			{
				cs.changeState(Event.INPUT);
				e = "Input";
			}
			else
				e = "Invalid Input";
		}
		if(eventType==PI)
		{
			//cs.changeState(Event.PI);
			e = "Pyrometer Inside";
		}
		if(eventType==PO)
		{
			//cs.changeState(Event.PO);
			e = "Pyrometer Outside";
		}
		if(eventType==M)
			e = "Magnetometer";
				
		return e;
	}
	
	/**
	 * Get the time difference between current node and last node (nodeA-nodeB)
	 * @param nodeA
	 * @param nodeB
	 * @return difference between nodeA and nodeB in milliseconds
	 */
	public double getDifference(Node nodeA, Node nodeB)
	{
		double difference = nodeA.getTime().getTime() - nodeB.getTime().getTime();
		return difference;
	}
	
	
	/**
	 * Method that returns the next reading from the bufferedReader
	 * @return Node
	 */
	public Node nextNode()
	{
		Node node = new Node();
		return node;		
		
	}
	public static void main(String[] args) {
//		for (int i = 10; i <= 20; i++) {
//			Main main = new Main((double)i/10);
//			main.reading();	
//		}
		Main main = new Main(2.6);
		
	}

}
