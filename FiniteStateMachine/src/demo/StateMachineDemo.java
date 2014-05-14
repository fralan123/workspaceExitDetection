package demo;

import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import demo.StateMachine.Event;

/**
 * The main application
 */
@SuppressWarnings("serial")
public class StateMachineDemo extends JFrame implements ChangeListener {
	/**
	 * @return
	 */
	public static StateMachineDemo getApp() {
		return app;
	}

	Logger							log	= Logger.getLogger(StateMachineDemo.class.getName());

	private static StateMachineDemo	app;

	/**
	 * Application entry point
	 * @param args command line args, if any
	 */
	public static void main(String[] args) {
		app = new StateMachineDemo();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.startDemo();
	}

	Thread				animationThread;

	private BombPanel	bombPanel;
	private ButtonPanel	buttonPanel;

	/**
	 * ctor
	 */
	public StateMachineDemo() {
		StateMachine.addChangeListener(this);
	}

	/**
	 * Called whenever the configuration dialog is closed
	 */
	public void configWindowClosed() {
		StateMachine.actionPerformed(this, Event.CONFIG_DONE);
	}

	/**
	 * Layout the components
	 */
	private void initLayout() {
		this.setLayout(new FlowLayout());
		buttonPanel = new ButtonPanel();

		bombPanel = new BombPanel();
		this.add(bombPanel);
		this.add(buttonPanel);
		this.pack();
		setVisible(true);
	}

	/**
	 *  Start the animation
	 */
	private void startDemo() {
		initLayout();

		setVisible(true);

		animationThread = new AnimationThread();
		animationThread.start();

	} 

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		log.info("state changed to " + StateMachine.getCurrent());
		animationThread.interrupt();
	}

	/**
	 * The animation thread
	 *
	 */
	private class AnimationThread extends Thread {
		@Override
		public void run() {

			for (;;) {
				try {
					if (StateMachine.getCurrent() == StateMachine.State.RUNNING) {
						bombPanel.moveBombs();
					}

					if (StateMachine.getCurrent() == StateMachine.State.RESET) {
						bombPanel.reset();
					}

					Thread.sleep(50);
				} catch (final InterruptedException ex) {
				}
			} // end for
		}
	}

}
