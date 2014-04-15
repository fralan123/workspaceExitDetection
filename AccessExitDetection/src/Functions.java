
public class Functions 
{

	final int pyrometer= 702; 
	final int magnetometro = 703;
	
	public int validateEventType(int first, int last)
	{
		if(first==pyrometer&&last==magnetometro)
			return 1; //Exit event
		else if(first==magnetometro&&last==pyrometer)
			return 2; //Access event
		else if(first==pyrometer&&last==0)
			return 3;
		else if(first==magnetometro&&last==0)
			return 4;
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
