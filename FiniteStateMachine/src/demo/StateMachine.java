package demo;

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

	public static final ActionListener actionListener;

	/**
	 * Application events.
	 */
	public enum Event {
		START,
		PAUSE,
		CONFIGURE,
		CONFIG_DONE,
		RESET,
		END,
	}
	
	/**
	 * Application states.
	 */
	public enum State {
		RUNNING,
		PAUSED,
		CONFIGURING,
		RESET,
		ENDED,
	}
	
	
	/**
	 * A map of reachable states from any given state
	 */
	static {
		stateMap = new HashMap<State, EnumSet<State>>();

		stateMap.put(State.RUNNING, EnumSet.of(State.PAUSED, State.ENDED));

		stateMap.put(State.PAUSED, EnumSet.of(State.RUNNING, State.RESET, State.CONFIGURING));

		stateMap.put(State.ENDED, EnumSet.of(State.RESET));
		
		stateMap.put(State.CONFIGURING, EnumSet.of(State.PAUSED));
		
		stateMap.put(State.RESET, EnumSet.of(State.PAUSED, State.RESET));

		// set initial state
		currentState = State.PAUSED;
	}
	
	static {
		log = Logger.getLogger(StateMachine.class.getName());
		log.setLevel(Level.FINER);
		
		actionListener = new ActionListener() {
			private ConfigurationFrame	configFrame;

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
					case RESET:
						int answer =
							JOptionPane.showConfirmDialog(StateMachineDemo.getApp(),
									"Are you sure you wish to reset?",
									"Please confirm",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);

						if (answer == JOptionPane.YES_OPTION) {
							setCurrent(State.RESET);
						}
						break;
					case PAUSE:
						setCurrent(State.PAUSED);
						break;
					case START:
						setCurrent(State.RUNNING);
						break;
					case CONFIGURE:
						if (configFrame == null) {
							configFrame = new ConfigurationFrame(StateMachineDemo.getApp());
						}

						configFrame.setVisible(true);
						setCurrent(State.CONFIGURING);
						break;
					case CONFIG_DONE:
						setCurrent(State.PAUSED);
						break;
					
					case END:
						setCurrent(State.ENDED);
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
			demo.StateMachine.Event event) {
		
		actionListener.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, event.toString()));
		
	}


} // end class StateMachine
