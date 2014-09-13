
public class Functions 
{
	
	final int pyrometerOutside= 702;
	final int pyrometerInside= 701; 
	final int magnetometer = 703;
	
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
	
	final double l_thresholdInput = pyrometerInsideInputMean+pyrometerInsideInputStandarDeviation;
	final double b_thresholdInput = intersectionInputMean+intersectionInputStandarDeviation;
	
	final double l_thresholdOutput = intersectionOutputMean+intersectionOutputStandarDeviation;
	final double b_thresholdOutput = pyrometerInsideOutputMean+pyrometerInsideOutputStandarDeviation;
	
	final double l_threshold = pyrometerInsideInputMean+pyrometerInsideInputStandarDeviation;
	final double b_threshold = intersectionOutputMean+intersectionOutputStandarDeviation;
	
	final int O = 1;		//Output 	= 1
	final int I = 2;		//Input 	= 2
	final int PI = 3;	//Pyrometer Inside	= 3
	final int PO = 4;	//Pyrometer Outside	= 4
	final int M = 5;		//Magnetometer	= 5
	
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
		System.out.println("Formula value = "+value);
		return value;
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

}
