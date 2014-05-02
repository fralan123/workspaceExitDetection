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
import java.util.Vector;
import fsm.*;
import fsm.StateMachine.Event;
public class Main {

	Main(double lambda2)
	{
		this.lambda = lambda2;
		System.out.println("Lambda = "+lambda2);
	}
	Main()
	{	
		
	}
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 	// For time
	BufferedReader bufferedReader;
	DataInputStream in;
	Vector<Node> W = new Vector<Node>();
	int b,m,l;
	double T = 1000;			//Time in milliseconds
	
	final static int O = 1;		//Output 	= 1
	final static int I = 2;		//Input 	= 2
	final static int PI = 3;	//Pyrometer Inside	= 3
	final static int PO = 4;	//Pyrometer Outside	= 4
	final static int M = 5;		//Magnetometer	= 5
	double lambda = 1;	
	final double outputMean = 28.55;
	//final double outputMean = 29.8;	
	final double outputStandarDeviation = 1.7312909695;
	//final double outputStandarDeviation = 1.7;
	final double inputMean = 27.3;
	final double inputStandarDeviation = 2.2501461941;
	//final double inputStandarDeviation = 2.25;
	
	final double pyrometerOutsideInputMean = 3.75;
	final double pyrometerOutsideInputStandarDeviation = 1.0699237553;
	//final double pyrometerOutsideInputStandarDeviation = 1.0699237553;
	final double pyrometerOutsideOutputMean = 3.1;
	//final double pyrometerOutsideOutputMean = 4.35;
	final double pyrometerOutsideOutputStandarDeviation = 1.9973666875;
	//final double pyrometerOutsideOutputStandarDeviation = 1.3484884325;
	
	final double magnetometerInputMean = 3.8;
	final double magnetometerInputStandarDeviation = 0.4216370214;
	final double magnetometerOutputMean = 0.9;
	final double magnetometerOutputStandarDeviation = 1.1972189997;
	
	final double pyrometerInsideInputMean = 6.55;
	final double pyrometerInsideInputStandarDeviation = 1.8488972531;
	final double pyrometerInsideOutputMean = 7.5;
	final double pyrometerInsideOutputStandarDeviation = 0.8885233166;
	
	double l_thresholdInput = pyrometerInsideInputMean+pyrometerInsideInputStandarDeviation;
	double b_thresholdInput = pyrometerOutsideInputMean+pyrometerOutsideInputStandarDeviation;
	
	double l_thresholdOutput = pyrometerOutsideOutputMean+pyrometerOutsideOutputStandarDeviation;
	double b_thresholdOutput = pyrometerInsideOutputMean+pyrometerInsideOutputStandarDeviation;
	
	int counterInput=0;
	int counterOutput=0;
	//Variable para saber si existe magnetómetro
	boolean Magnetometer = false;

	ChangeState cs;
	Functions function;
	public void initializeVariables()
	{
		b = 0; m = 0; l = 0;
		W = new Vector<Node>();
		cs = new ChangeState();
		function = new Functions();
	}
	public void algorithm()
	{
		while(true)
		{			
			Node nodeA = new Node(); 
			Node nodeB = new Node();
			initializeVariables();
			//First node the first time the algorithm runs 
			//Keep the first node in nodeA
			nodeA = nextNode();
			W.add(nodeA);
			if(W.lastElement()==null)
				break;
			if(W.lastElement().getNodeID()==0)
				evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
			
			
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
					b++;
					//System.out.println(i+"\t");
					W.add(nextNode());
					if(W.lastElement()==null)
						break;
					if(W.lastElement().getNodeID()==0)
						evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				}
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
				//AQUI VA LA NUEVA CONDICION SI LA DIFERENCIA ES <= Q				
				W.add(nextNode());
				if(W.lastElement()==null)
					break;
				if(W.lastElement().getNodeID()==0)
					evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				//Verificar como adecuar el valor de umbral
				while((getDifference(W.lastElement(),W.get(W.size()-2))<=T)&&(l<=l_thresholdInput))
				{
					l++;
					W.add(nextNode());
					if(W.lastElement()==null)
						break;
					if(W.lastElement().getNodeID()==0)
						evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				}

				//Keep the last node in nodeB
				nodeB = W.lastElement();
				
				
				if(function.validateEventType(nodeA, nodeB)==O)
				{
					if(b > b_thresholdOutput)
						b = (int) l_thresholdOutput;
				}
				else if(function.validateEventType(nodeA, nodeB)==I)
				{
					if(b > b_thresholdInput)
						b = (int) l_thresholdInput;
				}
				
				String e = event(b,m,l,nodeA,nodeB); 
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
			}
			
		}
	}
	private void evaluateInterrupted(Node nodeA, Node nodeB)
	{
		if(function.validateEventType(nodeA, nodeB)==O)
		{
			if(b > b_thresholdOutput)
				b = (int) l_thresholdOutput;
		}
		else if(function.validateEventType(nodeA, nodeB)==I)
		{
			if(b > b_thresholdInput)
				b = (int) l_thresholdInput;
		}
		
		String e = event(b,m,l,nodeA,nodeB); 
		//send_event(e);
		System.out.println(e);
		System.out.println("------------------------------------------------------------");
		if(e=="Output")
			counterOutput++;
		if(e=="Input")
			counterInput++;
		algorithm();
	}
	
	private String event(int b2, int m2, int l2, Node nodeA, Node nodeB) {
		Functions function = new Functions();
		String e = null;
		int eventType = function.validateEventType(nodeA, nodeB);
		
		if(eventType==O)
		{
			if(function.evaluateAccessOrExit(b2+m2+l2, outputMean, lambda, outputStandarDeviation)>=0)
			{
				cs.changeState(Event.OUTPUT);
				e = "Output";			
			}			
		}
		if(eventType==I)
		{
			if(function.evaluateAccessOrExit(b2+m2+l2, inputMean, lambda, inputStandarDeviation)>=0)
			{
				cs.changeState(Event.INPUT);
				e = "Input";
			}
		}
		if(eventType==PI)
			e = "Pyrometer Inside";
		if(eventType==PO)
			e = "Pyrometer Outside";
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
	 * Method for reading the data from the file	 
	 */
	public void reading()
	{	
		try {
			// Open the file that is the first command line parameter
			FileInputStream fstream = new FileInputStream(
					"src/Files/Test.txt");
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			bufferedReader = new BufferedReader(new InputStreamReader(in));
			//Run the algorithm
			algorithm();
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	/**
	 * Method that returns the next reading from the bufferedReader
	 * @return Node
	 */
	public Node nextNode()
	{
		Node node = new Node();
		String strLine;
		// Read File Line By Line
		try {			
			if ((strLine = bufferedReader.readLine()) != null) {
				// Print the content on the console
				String lineArray[] = strLine.split("\t");					
				node.setNodeID(Integer.parseInt(lineArray[1]));
				node.setTime(formatter.parse(lineArray[3]));
				node.setDbID(Integer.parseInt(lineArray[0]));
				
			}
			else
			{
				// Close the input stream
				in.close();
				System.out.println("Total Input = "+counterInput);
				System.out.println("Total Output = "+counterOutput);				
				//System.exit(0);
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return node;		
		
	}
	public static void main(String[] args) {
//		for (int i = 10; i <= 20; i++) {
//			Main main = new Main((double)i/10);
//			main.reading();	
//		}
		Main main = new Main(1);
		main.reading();	
	}

}
