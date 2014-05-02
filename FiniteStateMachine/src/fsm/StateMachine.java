package fsm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Maintains and validates the application state.
 */
public class StateMachine {
	static List<ChangeListener>					stateChangeListenerList	= new ArrayList<ChangeListener>(3);
	static Logger								log;
	private static volatile State				currentState;
	private static Map<State, EnumSet<State>>	stateMap;
	public static final ActionListener			actionListener;

	/**
	 * Application events.
	 */
	public enum Event {		
		INPUT,
		OUTPUT,
		PYROMETER,
		INACTIVITY,
	}
	
	/**
	 * Application states.
	 */
	public enum State {
		E0,
		E1,
		E2,
		E3,
		E4,
	}
	
	
	/**
	 * A map of reachable states from any given state
	 */
	static {
		stateMap = new HashMap<State, EnumSet<State>>();
		
		stateMap.put(State.E0, EnumSet.of(State.E1));
		stateMap.put(State.E1, EnumSet.of(State.E2,State.E3));
		stateMap.put(State.E2, EnumSet.of(State.E0));
		stateMap.put(State.E3, EnumSet.of(State.E2,State.E4));
		stateMap.put(State.E4, EnumSet.of(State.E4));

		// set initial state
		currentState = State.E0;
	}
	
	static {
		log = Logger.getLogger(StateMachine.class.getName());
		log.setLevel(Level.FINER);
		
		actionListener = new ActionListener() {
			//private ConfigurationFrame	configFrame;

			/**
			 * (non-Javadoc).
			 * 
			 * @param e the e
			 * 
			 * @see    java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				log.fine("actionPerformed: " + command);
				
				Event event = Event.valueOf(Event.class, command);
				
				switch (event) {
						
					case INPUT:
						if(getCurrent()==State.E0)
							setCurrent(State.E1);
						else if(getCurrent()==State.E1)
							setCurrent(State.E2);
						else if(getCurrent()==State.E2)
							System.out.println("ALERT: 3 people in the room");
						else if(getCurrent()==State.E3)
							setCurrent(State.E2);
						else
							log.severe("Unreachable state from "+getCurrent()+" in Event = "+event);
						break;
						
					case OUTPUT:
						if(getCurrent()==State.E1)
							setCurrent(State.E3);
						else if(getCurrent()==State.E2)
							setCurrent(State.E0);
						else if(getCurrent()==State.E3)
							System.out.println("ALERT: Escape!!");
						else if(getCurrent()==State.E3)
							setCurrent(State.E2);
						else
							log.severe("Unreachable state from "+getCurrent()+" in Event = "+event);
						break;
						
					default:
						log.severe("Unhandled event: " + command);
						break;
				}
			} // end method actionPerformed
		};
	}

	/**
	 * Add a listener for state change occurrences
	 * 
	 * @param listener
	 */
	public static void addChangeListener(ChangeListener listener) {
		stateChangeListenerList.add(listener);
	}

	/**
	 * Return the current application state.
	 * 
	 * @return EnumSet
	 */
	public static State getCurrent() {
		return currentState;
	}


	/**
	 * Determine if the given state is allowed to be next.
	 * 
	 * @param desiredState
	 *            the desired state
	 * 
	 * @return boolean True if desiredState is allowed from current state
	 */
	private static boolean isReachable(State desiredState) {
		return stateMap.get(currentState).contains(desiredState);
	}


	/**
	 * Sets the current application state.
	 * 
	 * @param desiredState
	 *            the desired state
	 * 
	 * @return the requested state, if it was reachable
	 * 
	 * @throws IllegalArgumentException
	 *             If the desired state is not reachable from current state
	 */
	private static State setCurrent(State desiredState) {
		log.info("at state: " + currentState + "; requesting state: " + desiredState);		

		if (!isReachable(desiredState)) {
			throw new IllegalArgumentException();
		}

		return setAsFinal(desiredState);
	}
	
	
	/**
	 * Finalizes the new requested state; send notification to listeners.
	 * 
	 * @param desiredState
	 *            The state to be finalized
	 */
	private static State setAsFinal(State desiredState) {
		currentState = desiredState;
		final ChangeEvent e = new ChangeEvent(currentState);

		for (final ChangeListener l : stateChangeListenerList) {
			l.stateChanged(e);
		}

		return currentState;
	}


	/**
	 * Helper method for calls to the actionListener.
	 * 
	 * @param source Source of event
	 * @param event The action event
	 */
	public static void actionPerformed(Object source,
			fsm.StateMachine.Event event) {
		
		actionListener.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, event.toString()));
		
	}


} // end class StateMachine
