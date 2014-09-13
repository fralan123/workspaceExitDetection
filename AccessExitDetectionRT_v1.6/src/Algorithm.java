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

	Algorithm(Vector<Node> W)
	{
		initializeVariables();
		this.W = W;
		algorithm();
	}
	Algorithm(Vector<Node> W, double lambda)
	{
		initializeVariables();
		this.W = W;
		this.lambda = lambda;
		algorithm();
	}
	
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 	// For time
	BufferedReader bufferedReader;
	DataInputStream in;
	Vector<Node> W = new Vector<Node>();
	int b,m,l;
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
		//initializeVariables();	//REVISAR SI SE DEBEN DE INICIALIZAR EN ESTE PUNTO
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
		nodeA = W.firstElement();
		nodeB = W.lastElement();
		
		if(function.validateEventType(nodeA, nodeB)==function.O)
		{
			if(b > function.b_thresholdOutput)
				b = (int) function.l_thresholdOutput;
		}
		else if(function.validateEventType(nodeA, nodeB)==function.I)
		{
			if(m > function.b_thresholdInput)
				m = (int) function.l_thresholdInput;
		}
		
		String e = event(b,m,l,nodeA,nodeB,false); 
		//send_event(e);
		System.out.println(e);
		if(e=="Output")
			counterOutput++;
		if(e=="Input")
			counterInput++;				
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

}
