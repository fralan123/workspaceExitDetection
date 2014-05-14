
public class Functions 
{

	final int pyrometer= 702; 
	final int magnetometer = 703;
	
	public int validateEventType2(Node first, Node last)
	{
		if(first.getNodeID()==pyrometer)
			return 1; //Exit event
		else if(first.getNodeID()==magnetometer)
			return 2; //Access event
		else
			return 0; //undefinied event
	}
	public int validateEventType(Node first, Node last)
	{
		if(first.getNodeID()==pyrometer&&last.getNodeID()==magnetometer)
			return 1; //Exit event
		else if(first.getNodeID()==magnetometer&&last.getNodeID()==pyrometer)
			return 2; //Access event
		else if(first.getNodeID()==pyrometer&&last.getNodeID()==pyrometer)
			return 3; //Pyrometer (Activity inside)
		else if(first.getNodeID()==magnetometer&&last.getNodeID()==magnetometer)
			return 4; //Magnetometer (Door open)
		else
			return 0; //undefinied event
	}
	public double evaluateAccessOrExit(double sampleCount, double mean, double lambda, double standardDeviation)
	{
		double value = 0;
		value = 1 - (Math.abs(sampleCount-mean))/(lambda*standardDeviation); //formula
		return value;
	}

}
