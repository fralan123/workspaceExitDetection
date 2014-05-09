
public class Functions 
{
	
	final int pyrometerOutside= 701;
	final int pyrometerInside= 702; 
	final int magnetometer = 703;
	
	public int validateEventType2(Node first, Node last)
	{
		if(first.getNodeID()==pyrometerInside)
			return 1; //Exit event
		else if(first.getNodeID()==pyrometerOutside)
			return 2; //Access event
		else
			return 0; //undefinied event
	}
	public int validateEventType(Node first, Node last)
	{
		if(first.getNodeID()==pyrometerInside&&last.getNodeID()==pyrometerOutside)
			return 1; //Exit event
		else if(first.getNodeID()==pyrometerOutside&&last.getNodeID()==pyrometerInside)
			return 2; //Access event
		else if(first.getNodeID()==pyrometerInside&&last.getNodeID()==pyrometerInside)
			return 3; //Pyrometer (Activity inside)
		else if(first.getNodeID()==pyrometerOutside&&last.getNodeID()==pyrometerOutside)
			return 4; //Pyrometer (Activity outside)
		else if(first.getNodeID()==magnetometer&&last.getNodeID()==magnetometer)
			return 5; //Magnetometer (Door open)
		else if(first.getNodeID()==pyrometerInside&&last.getNodeID()==0)
			return 1; //Fuzzy Exit Event
		else if(first.getNodeID()==pyrometerOutside&&last.getNodeID()==0)
			return 2; //Fuzzy Access Event
		else
			return 0; //undefinied event
	}
	public double evaluateAccessOrExit(double sampleCount, double mean, double lambda, double standardDeviation)
	{
		double value = 0;
		value = 1 - (Math.abs(sampleCount-mean))/(lambda*standardDeviation); //formula
		//System.out.println("SampleCount= "+sampleCount+" Mean= "+mean+" lambda="+lambda+" standardDeviation="+standardDeviation);
		//System.out.println("Value= "+value);
		return value;
	}
	public double evaluateAccessOrExit(double sampleCount, double mean, double lambda, double standardDeviation, double intersection, double intersectionMean)
	{
		double value = 0;
		value = 1 - (Math.abs(intersection-intersectionMean))/(lambda*standardDeviation); //formula
		//System.out.println("SampleCount= "+sampleCount+" Mean= "+mean+" lambda="+lambda+" standardDeviation="+standardDeviation);
		//System.out.println("Value= "+value);
		return value;
	}

}
