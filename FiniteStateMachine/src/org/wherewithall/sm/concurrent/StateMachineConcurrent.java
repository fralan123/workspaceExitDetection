package org.wherewithall.sm.concurrent;

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
 * Maintains information about the application state.
 * <p>
 * The states CONFIGURING and RESET are concurrent states.
 * 
 */
public class StateMachineConcurrent {
	static List<ChangeListener>							stateChangeListenerList	= new ArrayList<ChangeListener>(3);

	/** The log. */
	static Logger										log;

	private static volatile EnumSet<State>				currentState;

	private static Map<EnumSet<State>, EnumSet<?>[]>	stateMap;

	public static final ActionListener	actionListener;
	
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
		RESET,
		ENDED,

		// concurrent states...
		CONFIGURING,
	}

	/**
	 * Reachable state map
	 */
	static {
		stateMap = new HashMap<EnumSet<State>, EnumSet<?>[]>();

		stateMap.put(EnumSet.of(State.RUNNING), new EnumSet[] { EnumSet.of(State.PAUSED), EnumSet.of(State.ENDED),
			EnumSet.of(State.RUNNING, State.CONFIGURING) });

		stateMap.put(EnumSet.of(State.PAUSED), new EnumSet[] { EnumSet.of(State.RUNNING), 
			EnumSet.of(State.RESET), EnumSet.of(State.PAUSED, State.CONFIGURING) });

		stateMap.put(EnumSet.of(State.RESET), new EnumSet[] { EnumSet.of(State.RESET),
			EnumSet.of(State.PAUSED), EnumSet.of(State.RESET, State.CONFIGURING) });

		stateMap.put(EnumSet.of(State.ENDED), new EnumSet[] { EnumSet.of(State.ENDED, State.CONFIGURING),
			EnumSet.of(State.RESET) });

		// concurrent states
		stateMap.put(EnumSet.of(State.RUNNING, State.CONFIGURING), new EnumSet[] { EnumSet.of(State.RUNNING),
			EnumSet.of(State.ENDED, State.CONFIGURING), EnumSet.of(State.PAUSED, State.CONFIGURING)});

		stateMap.put(EnumSet.of(State.PAUSED, State.CONFIGURING), new EnumSet[] { EnumSet.of(State.PAUSED), EnumSet.of(State.RESET, State.CONFIGURING), EnumSet.of(State.RUNNING, State.CONFIGURING)});

		stateMap.put(EnumSet.of(State.RESET, State.CONFIGURING), new EnumSet[] { EnumSet.of(State.RESET), EnumSet.of(State.PAUSED, State.CONFIGURING)});

		stateMap.put(EnumSet.of(State.ENDED, State.CONFIGURING), new EnumSet[] { EnumSet.of(State.ENDED), EnumSet.of(State.RESET, State.CONFIGURING) });

		// set initial state
		currentState = EnumSet.of(State.PAUSED); 
	}

	static {
		log = Logger.getLogger(StateMachineConcurrent.class.getName());
		log.setLevel(Level.FINER);

		actionListener = new ActionListener() {
			private ConfigurationFrame	configFrame;

			/*
			 * (non-Javadoc).
			 * @see    java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				log.fine("actionPerformed: " + command);
				
				Event event = Event.valueOf(Event.class, command);
				
				switch (event) {
					case RESET:
						int answer =
							JOptionPane.showConfirmDialog(StateMachineConcurrentDemo.getApp(),
									"Are you sure you wish to reset?",
									"Please confirm",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);

						if (answer == JOptionPane.YES_OPTION) {
							setPrimary(State.RESET);
						}
						break;
					case PAUSE:
						setPrimary(State.PAUSED);
						break;
					case START:
						setPrimary(State.RUNNING);
						break;
					case CONFIGURE:
						if (configFrame == null) {
							configFrame = new ConfigurationFrame(StateMachineConcurrentDemo.getApp());
						}

						configFrame.setVisible(true);
						setConcurrent(State.CONFIGURING);
						break;
					case CONFIG_DONE:
						removeConcurrent(State.CONFIGURING);
						break;
					case END:
						setPrimary(State.ENDED);
						break;
						
					default:
						log.severe("Unhandled event: " + command);
						break;
				}
			} // end method actionPerformed
		};
	}

	

	/**
	 * Add a listener for state change events.
	 * @param listener The listener
	 */
	public static void addChangeListener(ChangeListener listener) {
		stateChangeListenerList.add(listener);
	}


	/**
	 * Return the current application state.
	 * 
	 * @return The application state(s)
	 */
	public static EnumSet<State> getCurrent() {
		return EnumSet.copyOf(currentState);
	}

	/**
	 * @return the primary app state
	 */
	public static State getPrimary() {
		State[] states = new State[2];
		states = currentState.toArray(states);
		
		return states[0];
	}

	/**
	 * @return The concurrent app state
	 */
	public static State getConcurrent() {
		State[] states = new State[2];
		states = currentState.toArray(states);
		
		return states[1];
	}
	

	/**
	 * Determine if the given state is allowed to be next.
	 * 
	 * @param desiredState
	 *            the desired state
	 * 
	 * @return boolean True if the state is reachable from the current state
	 */
	private static boolean isReachable(EnumSet<State> desiredState) {
		
		for (EnumSet<?> s : stateMap.get(currentState)) {

			if (s.equals(desiredState)) {
				return true;
			}
		}

		return false;	
	}
	

	/**
	 * Finalize the state change and notify listeners
	 * @param desiredState The new state
	 */
	private static EnumSet<State> setAsFinal(EnumSet<State> desiredState) {
		currentState = desiredState;
		final ChangeEvent e = new ChangeEvent(currentState);

		for (final ChangeListener l : stateChangeListenerList) {
			l.stateChanged(e);
		}

		return currentState;
	}

	/**
	 * Sets the current application state.
	 * 
	 * @param desiredState
	 *            the desired state
	 * 
	 * @return EnumSet
	 * 
	 * @throws IllegalArgumentException
	 *             If the desired state is not reachable from current state
	 */
	private static EnumSet<State> setCurrent(EnumSet<State> desiredState) {

		log.info("at state: " + currentState + "; requesting state: " + desiredState);

		if (!isReachable(desiredState)) {
			throw new IllegalArgumentException();
		}

		return setAsFinal(desiredState);
	}

	/**
	 * Sets the current application's primary state; negates all concurrent states.
	 * 
	 * @param primaryState
	 *            The desired state
	 * @return EnumSet
	 */
	private static EnumSet<State> setPrimary(State primaryState) {
		State[] states = new State[2];
		states = currentState.toArray(states);
		states[0] = primaryState;
		
		EnumSet<State> newState = EnumSet.of(states[0]);
		if (states[1] != null) newState.add(states[1]);
		
		return setCurrent(newState);
	}

	
	/**
	 * Add a concurrent state to the set of states.
	 * 
	 * @param state
	 *            the state
	 * 
	 * @return EnumSet
	 * 
	 * @throws IllegalArgumentException
	 */
	private static EnumSet<State> setConcurrent(State concurrentState) {
		State[] states = new State[2];
		states = currentState.toArray(states);
		states[1] = concurrentState;
		
		return setCurrent(EnumSet.of(states[0], states[1]));
	}

	/**
	 * Remove the concurrent state (no concurrent state)
	 * @param concurrentState The concurrent state to remove
	 */
	private static EnumSet<State> removeConcurrent(State concurrentState) {
		State[] states = new State[2];
		states = currentState.toArray(states);
		assert(states[1] == concurrentState);
		
		EnumSet<State> newState = EnumSet.of(states[0]);
		
		return setCurrent(newState);		
	}
	
	/**
	 * Helper method for calls to the actionListener.
	 * 
	 * @param source Source of event
	 * @param event The action event
	 */
	public static void actionPerformed(Object source, Event event) {
		
		actionListener.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, event.toString()));
		
	}
} // end class StateMachine
