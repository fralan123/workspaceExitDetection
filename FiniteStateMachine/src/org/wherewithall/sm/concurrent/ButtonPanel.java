package org.wherewithall.sm.concurrent;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wherewithall.sm.concurrent.StateMachineConcurrent.Event;
import org.wherewithall.sm.concurrent.StateMachineConcurrent.State;

/**
 * The panel that contains the application buttons.
 *
 */
@SuppressWarnings("serial")
public class ButtonPanel extends JPanel implements ChangeListener {
	private JButton	startButton;
	private JButton	pauseButton;
	private JButton	configButton;
	private JButton	resetButton;
	private ActionListener	buttonListener;

	private static final Map<State, EnumSet<Event>> stateActionMap;
	
	static {
		stateActionMap = new HashMap<State, EnumSet<Event>>();
		
		stateActionMap.put(State.PAUSED, EnumSet.of(Event.START, Event.RESET));
		stateActionMap.put(State.RUNNING, EnumSet.of(Event.PAUSE));
		stateActionMap.put(State.RESET, EnumSet.of(Event.START, Event.RESET));
		stateActionMap.put(State.ENDED, EnumSet.of(Event.RESET));
	}
	
	/**
	 * Determine if the proposed actionEvent is allowed.
	 * 
	 * @param actionEvent the proposed event
	 * @return true if the event is allowed
	 */
	private static boolean isAllowedAction(Event actionEvent) {
		if (actionEvent == Event.CONFIGURE) {
			return StateMachineConcurrent.getConcurrent() != State.CONFIGURING;
		}
		
		EnumSet<Event> allowed = stateActionMap.get(StateMachineConcurrent.getPrimary()); 
		return allowed != null && allowed.contains(actionEvent);
	}
	
	/**
	 * ctor
	 */
	ButtonPanel() {
		setPreferredSize(new Dimension(200,400));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		buttonListener = StateMachineConcurrent.actionListener;
		initLayout();
		StateMachineConcurrent.addChangeListener(this);
	}

	
	/**
	 * Enable buttons according to last user action.
	 */
	protected void changeButtonEnablement() {
		pauseButton.setEnabled(isAllowedAction(Event.PAUSE));
		configButton.setEnabled(isAllowedAction(Event.CONFIGURE));
		startButton.setEnabled(isAllowedAction(Event.START));
		resetButton.setEnabled(isAllowedAction(Event.RESET));
	}

	/**
	 * Layout the elements in this panel
	 */
	private void initLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut( 50 ));
		startButton = new JButton(Event.START.toString());
		addButton(this, startButton);
		add(Box.createVerticalStrut( 5 ));
		pauseButton = new JButton(Event.PAUSE.toString());
		addButton(this, pauseButton);
		add(Box.createVerticalStrut( 20 ));
		configButton = new JButton(Event.CONFIGURE.toString());
		addButton(this, configButton);
		add(Box.createVerticalStrut( 5 ));
		resetButton = new JButton(Event.RESET.toString());
		addButton(this, resetButton);
		changeButtonEnablement();
		return;		
	}

	/**
	 * Helper function to add buttons
	 * @param buttonPanel The button panel
	 * @param startButton2 The button to add
	 */
	private void addButton(JPanel buttonPanel, JButton button) {
		button.addActionListener(buttonListener);
		button.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.add(button);
	}


	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		changeButtonEnablement();
	}
}
