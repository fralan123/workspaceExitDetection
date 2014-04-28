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
	
	final static int O = 1;	//Output 	= 1
	final static int I = 2;	//Input 	= 2
	final static int P = 3;	//Pyrometer	= 3
	final static int M = 4;	//Magnetometer	= 4
	double lambda = 1;	
	final double outputMean = 9.9;	
	final double outputStandarDeviation = 1.28;
	final double inputMean = 14;
	final double inputStandarDeviation = 2.10;
	
	final double magnetometerInputMean = 3.8;
	final double magnetometerInputStandarDeviation = 0.4216370214;
	final double magnetometerOutputMean = 0.9;
	final double magnetometerOutputStandarDeviation = 1.1972189997;
	
	final double pyrometerInputMean = 7.8;
	final double pyrometerInputStandarDeviation = 2.5298221281;
	final double pyrometerOutputMean = 3.3;
	final double pyrometerOutputStandarDeviation = 0.6749485577;
	
	double l_threshold = pyrometerInputMean+pyrometerInputStandarDeviation;
	double b_threshold = magnetometerInputMean+magnetometerInputStandarDeviation;
	
	int counterInput=0;
	int counterOutput=0;
	//Variable para saber si existe magnetómetro
	boolean Magnetometer = false;

	ChangeState cs;
	public void initializeVariables()
	{
		b = 0; m = 0; l = 0;
		W = new Vector<Node>();
		cs = new ChangeState();
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
				l=1;
				//AQUI VA LA NUEVA CONDICION SI LA DIFERENCIA ES <= Q				
				W.add(nextNode());
				if(W.lastElement()==null)
					break;
				if(W.lastElement().getNodeID()==0)
					evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				while((getDifference(W.lastElement(),W.get(W.size()-2))<=T)&&(l<=l_threshold))
				{
					l++;
					W.add(nextNode());
					if(W.lastElement()==null)
						break;
					if(W.lastElement().getNodeID()==0)
						evaluateInterrupted(nodeA, W.elementAt(W.size()-2));
				}
				if(b > b_threshold)
					b = (int) l_threshold;
				
				//Keep the last node in nodeB
				nodeB = W.lastElement();
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
		if(b > b_threshold)
			b = (int) l_threshold;
		
		String e = event(b,m,l,nodeA,nodeB); 
		//send_event(e);
		System.out.println(e);
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
			if(function.evaluateAccessOrExit(b2+m2+l2, outputMean, lambda, outputMean)>=0)
			{
				cs.changeState(Event.OUTPUT);
				e = "Output";			
			}			
		}
		if(eventType==I)
		{
			if(function.evaluateAccessOrExit(b2+m2+l2, inputMean, lambda, inputMean)>=0)
			{
				cs.changeState(Event.INPUT);
				e = "Input";
			}
		}
		if(eventType==P)
			e = "Pyrometer";
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
					"src/060313/02_entraSoloSaleSolo_nopyro.txt");
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
		for (int i = 10; i <= 20; i++) {
			Main main = new Main((double)i/10);
			main.reading();	
		}
	}

}
