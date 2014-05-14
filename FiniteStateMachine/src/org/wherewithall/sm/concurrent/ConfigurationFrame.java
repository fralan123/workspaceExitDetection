package org.wherewithall.sm.concurrent;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * The dialog for configuring the application
 *
 */
@SuppressWarnings("serial")
public class ConfigurationFrame extends JFrame {
	
	private JComboBox	numBombsCombo;
	private JComboBox	numBouncesCombo;
	private JCheckBox	isSoundOnCB;
	private JButton	button;

	/**
	 * data holder
	 *
	 */
	public static class Config {
		public final int numBombs;
		public final int numBounces;
		public final boolean isSoundOn;
		
		public Config(int _numBombs, int _numBounces, boolean _isSound) {
			numBombs = _numBombs;
			numBounces = _numBounces;
			isSoundOn = _isSound;
		}
	}

	private static Config currConfig;
	
	static {
		currConfig = new Config(3, 10, true);
	}
	
	/**
	 * Creates a new Frame-based config dialog within the app context.
	 * @param app The application
	 */
	public ConfigurationFrame(Component app) {
        setLocation(app.getX() + app.getWidth(), app.getY());
        
        initLayout();
        
		addWindowListener(new WindowAdapter() {

			/*
			 * Tell the application when we've closed the config window
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				StateMachineConcurrentDemo.getApp().configWindowClosed();
			}
		});
	}
	
	/**
	 * Layout the controls.
	 */
	private void initLayout() {
		numBombsCombo = new JComboBox(new Integer[] {1, 2, 3, 4, 5});
		numBouncesCombo = new JComboBox(new Integer[] {5, 10, 15});
		isSoundOnCB = new JCheckBox("Sound?");
	
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setPreferredSize(new Dimension(160, 150));
		
		JPanel p1 = new JPanel();
		p1.add(new JLabel("# of bombs"));
		p1.add(numBombsCombo);
		
		JPanel p2 = new JPanel();
		p2.add(new JLabel("<html>Bounces <br>before boom!</html>"));
		p2.add(numBouncesCombo);
		
		panel.add(p1);
		panel.add(p2);
		panel.add(isSoundOnCB);
		
		button = new JButton("OK");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int bombs = (Integer)numBombsCombo.getSelectedItem();
				int bounces = (Integer)numBouncesCombo.getSelectedItem();
				boolean isSoundOn = isSoundOnCB.isSelected();
				
				currConfig = new Config(bombs, bounces, isSoundOn);
				ConfigurationFrame.this.setVisible(false);
				StateMachineConcurrentDemo.getApp().configWindowClosed();
				
			}
			
		});
		
		setInitValues();
		
		panel.add(new JSeparator());
		panel.add(button);
		add(panel);
		pack();
	}

	/**
	 * Initialize the field values
	 */
	private void setInitValues() {
		numBombsCombo.setSelectedItem(3);
		numBouncesCombo.setSelectedItem(10);
		isSoundOnCB.setSelected(true);
	}

	/**
	 * Return the current configuration settings
	 */
	public static Config getCurrConfig() {
		return currConfig;
	}

}
