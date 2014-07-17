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
public class Algorithm {

	Algorithm(double lambda2)
	{
		this.lambda = lambda2;
		System.out.println("Lambda = "+lambda2);
	}
	Algorithm(Vector<Node> W){}
	
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 	// For time
	BufferedReader bufferedReader;
	DataInputStream in;
	Vector<Node> W = new Vector<Node>();
	final static String PATH = "src/Files/";
	final static String FILE = "04_EntraAcompanadoSaleAcompanado.txt";
	int b,m,l;
	double T = 2000;			//Time in milliseconds	
	double lambda = 1;				
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
		Node nodeA = new Node(); 
		Node nodeB = new Node();
		boolean B=true;
		boolean M=false;
		boolean L=false;
		boolean Th=true;
		initializeVariables();
		double l_threshold=0;
		b=2;
		m=1;
		for (int i=2;i<W.size();i++)
		{
			if(W.get(i).getNodeID()==W.get(i-1).getNodeID()&&B)
			{
				b++;
			}else
			{
				if(!M&&!L)
				{
					B=false;				
					M=true;	
				}
			}
			//M
			if((M&&!B)&&((W.get(i).getNodeID()!=W.get(i-1).getNodeID())
					||(W.get(i-1).getNodeID()!=W.get(i-2).getNodeID())
					||(W.get(i).getNodeID()!=W.get(i-2).getNodeID())))										
			{
				m++;
			}else
			{
				if(!B&&!L)
				{
					M=false;
					L=true;
				}
			}
			
			//L
			if((!B&&!M)&&(l<=l_threshold))
			{
				if(Th)
				{
					Th=false;
					//End of intersection
					if(W.get(i).getNodeID()==W.get(i-1).getNodeID()&&
							W.get(i).getNodeID()==W.get(i-2).getNodeID())
					{
						m-=2;
						l=2;
					}				
				}				
				l++;
			}
		}
		while(true)
		{			
			nodeA = nextNode();
			W.add(nodeA);
			if(W.lastElement()==null)
				break;
			//CORRECCION DE BUG: Cuando ya termino el evento y despues el NodeID es 0, entonces se reinicia el algoritmo
			if(W.lastElement().getNodeID()==0&&W.size()==1)
				algorithm();
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
				//double l_threshold=0;
				if(function.validateEventType(nodeA, nodeB)==function.O)				
					l_threshold=function.l_thresholdOutput;				
				else if(function.validateEventType(nodeA, nodeB)==function.I)				
					l_threshold=function.l_thresholdInput;				
				
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
				
				if(function.validateEventType(nodeA, nodeB)==function.O)
				{
					if(b > function.b_thresholdOutput)
						b = (int) function.l_thresholdOutput;//b = (int) l_threshold;
				}
				else if(function.validateEventType(nodeA, nodeB)==function.I)
				{
					if(m > function.b_thresholdInput)
						m = (int) function.l_thresholdInput;//m = (int) l_threshold;
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
	public void algorithm2()
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
			//CORRECCION DE BUG: Cuando ya termino el evento y despues el NodeID es 0, entonces se reinicia el algoritmo
			if(W.lastElement().getNodeID()==0&&W.size()==1)
				algorithm();
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
				if(function.validateEventType(nodeA, nodeB)==function.O)				
					l_threshold=function.l_thresholdOutput;				
				else if(function.validateEventType(nodeA, nodeB)==function.I)				
					l_threshold=function.l_thresholdInput;				
				
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
				
				if(function.validateEventType(nodeA, nodeB)==function.O)
				{
					if(b > function.b_thresholdOutput)
						b = (int) function.l_thresholdOutput;//b = (int) l_threshold;
				}
				else if(function.validateEventType(nodeA, nodeB)==function.I)
				{
					if(m > function.b_thresholdInput)
						m = (int) function.l_thresholdInput;//m = (int) l_threshold;
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
//		if(b > b_threshold)
//			b = (int) l_threshold;
		
		if(function.validateEventType(nodeA, nodeB)==function.O)
		{
			if(b > function.b_thresholdOutput)
				b = (int) function.l_thresholdOutput;//b = (int) l_threshold;
		}
		else if(function.validateEventType(nodeA, nodeB)==function.I)
		{
			if(m > function.b_thresholdInput)
				m = (int) function.l_thresholdInput;//m = (int) l_threshold;
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
		
		if(eventType==function.O)
		{
			if(function.evaluateAccessOrExit(b2+m2, function.outputMean, lambda, function.outputStandarDeviation)>=0)
			{
				if(inactivity)
					cs.changeState(Event.INACTIVITY);
				
				cs.changeState(Event.OUTPUT);
				
				e = "Output";			
			}
			else
				e = "Invalid Output";
		}
		if(eventType==function.I)
		{
			if(function.evaluateAccessOrExit(m2+l2, function.inputMean, lambda, function.inputStandarDeviation)>=0)
			{
				cs.changeState(Event.INPUT);
				e = "Input";
			}
			else
				e = "Invalid Input";
		}
		if(eventType==function.PI)
		{
			//cs.changeState(Event.PI);
			e = "Pyrometer Inside";
		}
		if(eventType==function.PO)
		{
			//cs.changeState(Event.PO);
			e = "Pyrometer Outside";
		}
		if(eventType==function.M)
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
					PATH+FILE);
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
		Algorithm main = new Algorithm(10);
		main.reading();	
	}

}
