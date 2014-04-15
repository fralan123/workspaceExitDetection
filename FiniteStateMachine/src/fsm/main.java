package fsm;

import fsm.StateMachine;
import fsm.StateMachine.Event;

public class main {


	public static void main(String[] args) 
	{
		main obj = new main();
		obj.changeState();
	}
	public void changeState()
	{
		StateMachine.actionPerformed(this, Event.INPUT);
		StateMachine.actionPerformed(this, Event.INPUT);
		StateMachine.actionPerformed(this, Event.OUTPUT);
		StateMachine.actionPerformed(this, Event.OUTPUT);
	}

}
