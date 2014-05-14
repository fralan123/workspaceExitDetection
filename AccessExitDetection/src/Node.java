import java.util.Date;


public class Node 
{
	private int nodeID;
	private Date time;

	public Node()
	{
		
	}
	public Node(int nodeID, Date time)
	{
		this.nodeID = nodeID;
		this.time = time;
	}
	public int getNodeID()
	{
		return nodeID;
	}

	public void setNodeID(int nodeID)
	{
		this.nodeID = nodeID;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	

}
