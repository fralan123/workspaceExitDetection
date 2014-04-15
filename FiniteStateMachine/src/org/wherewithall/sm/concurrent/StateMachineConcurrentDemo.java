package org.wherewithall.sm.concurrent;

import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wherewithall.sm.concurrent.StateMachineConcurrent.Event;

/**
 * Main application class
 */
@SuppressWarnings("serial")
public class StateMachineConcurrentDemo extends JFrame implements ChangeListener {
	/**
	 * @return
	 */
	public static StateMachineConcurrentDemo getApp() {
		return app;
	}

	Logger										log	= Logger.getLogger(StateMachineConcurrentDemo.class.getName());

	private static StateMachineConcurrentDemo	app;

	/**
	 * Application entry point
	 * 
	 * @param args
	 *            command arguments, if any
	 */
	public static void main(String[] args) {
		app = new StateMachineConcurrentDemo();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.startDemo();
	}

	Thread				animationThread;

	private BombPanel	bombPanel;
	private ButtonPanel	buttonPanel;

	/**
	 * ctor
	 */
	public StateMachineConcurrentDemo() {
		StateMachineConcurrent.addChangeListener(this);
	}

	/**
	 * called when the configuration frame is closed
	 */
	public void configWindowClosed() {
		StateMachineConcurrent.actionPerformed(this, Event.CONFIG_DONE);
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
		log.info("state changed to " + StateMachineConcurrent.getCurrent());
		animationThread.interrupt();
	}

	/**
	 * The animation thread.
	 *
	 */
	private class AnimationThread extends Thread {
		@Override
		public void run() {

			for (;;) {
				try {
					if (StateMachineConcurrent.getCurrent().contains(StateMachineConcurrent.State.RUNNING)) {
						bombPanel.moveBombs();
					}

					if (StateMachineConcurrent.getCurrent().contains(StateMachineConcurrent.State.RESET)) {
						bombPanel.reset();
					}

					Thread.sleep(50);
				} catch (final InterruptedException ex) {
				}
			} // end for
		}
	}
}
