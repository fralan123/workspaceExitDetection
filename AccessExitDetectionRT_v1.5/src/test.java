
import java.util.Date;
	 
	public class test 
	{
	    public static void main( String[] args )
	    {

		Node nodo = new Node(700,new Date());
		System.out.println("nodo "+nodo.getTime());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		Node nodo2 = new Node(701,new Date());
		System.out.println("nodo2 "+nodo2.getTime());

		System.out.println("Diff =" +(nodo2.getTime().getTime()-nodo.getTime().getTime()));
		
	    }
	}

