package fsm;

import fsm.StateMachine;
import fsm.StateMachine.Event;

public class ChangeState 
{
	public void changeState(fsm.StateMachine.Event event)
	{
		StateMachine.actionPerformed(this, event);		
	}
	public void changeState(String event)
	{
		if(event.equals("INPUT"))
			StateMachine.actionPerformed(this, Event.INPUT);
		else if(event.equals("OUTPUT"))
			StateMachine.actionPerformed(this, Event.OUTPUT);
	}
}
