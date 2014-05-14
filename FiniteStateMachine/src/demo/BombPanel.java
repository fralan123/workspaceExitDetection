package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import demo.StateMachine.Event;

/**
 * The BombPanel contains the animation of bouncing bombs.
 *
 */
@SuppressWarnings("serial")
public class BombPanel extends JPanel {
	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	protected static ImageIcon createImageIcon(String path) {
		final java.net.URL imgURL = BombPanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	final Player boom = new Player("boom.wav");
	final Player ping = new Player("ping.wav");

	final ImageIcon				bombIcon		= createImageIcon("oie_Bomb2.png");
	final ImageIcon				explIcon		= createImageIcon("oie_explosion.gif");
	private List<Bomb>			bombs;

	final Dimension				panelDim	= new Dimension(400, 400);
	private final JLayeredPane	bombPane;

	/**
	 * ctor
	 */
	BombPanel() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		bombPane = new JLayeredPane();
		bombPane.setPreferredSize(panelDim);
		bombPane.setBackground(Color.yellow);
		bombPane.setOpaque(true);

		addBombs();
		add(bombPane);
		
		//start sound threads
		boom.start();
		ping.start();
	}
	

	/**
	 * Add a number of bombs to the animation according to the configuration settings
	 */
	private void addBombs() {
		int bombCount = ConfigurationFrame.getCurrConfig().numBombs;
		bombs = new ArrayList<Bomb>(bombCount);
		
		for (int i = 0; i < bombCount; i++) {
			Bomb bomb = new Bomb(bombIcon);
			bombs.add(bomb);
			bombPane.add(bomb);
		}
	}


	/**
	 * Bounce the bombs.  Explode any bombs that have bounced x times.
	 */
	public void moveBombs() {
		if (bombs.size() == 0) {
			StateMachine.actionPerformed(this, Event.END);
		}
		else {
			List<Bomb> removeList = new ArrayList<Bomb>();
			
			for (Bomb bomb : bombs) {
				if (bomb != null) {
					if (!bomb.hasExploded()) {
						bomb.step();
						if (bomb.hasExploded()) {
							bomb.goBoom();
						}
					} else {
						bomb.setVisible(false);
						remove(bomb);
						removeList.add(bomb);
					}
				}
			}
			
			bombPane.repaint();

			if (!removeList.isEmpty()) {
				for (Bomb bomb : removeList) {
					bombs.remove(bomb);
				}
			}
		
		}
	}

	
	
	/**
	 * Reset the animation. Explode any bombs still left and add new ones.
	 */
	public void reset() {
		if (!bombs.isEmpty()) {
			for (Bomb bomb : bombs) {
				bomb.goBoom();
				remove(bomb);
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
			}
			for (Bomb bomb : bombs) {
				bomb.setVisible(false);
			}
			bombs.clear();
		}
		addBombs();
		StateMachine.actionPerformed(this, Event.PAUSE);
	}

	/**
	 * The bomb image.
	 * 
	 */
	class Bomb extends JLabel {
		private static final double	MOVEMENT_DISTANCE	= 10;
		private final Point2D		P2D_ORIGIN			= new Point2D.Double(0, 0);

		private double				direction			= 0;
		private int					bounces				= 0;
		
		private final int			MAX_BOUNCES;
		private final boolean		SOUND_ON;

		/**
		 * Create a bomb.
		 * @param icon  The image to use
		 */
		public Bomb(ImageIcon icon) {
			super(icon);
			setRandomLocation();
			setRandomDirection();
			setSize(icon.getIconWidth(), icon.getIconHeight());
			MAX_BOUNCES = ConfigurationFrame.getCurrConfig().numBounces;
			SOUND_ON = ConfigurationFrame.getCurrConfig().isSoundOn;
		}

		/**
		 * Show and play explosion
		 */
		public void goBoom() {
			setIcon(explIcon);
			if (SOUND_ON) boom.play();
		}

		/**
		 * Determine new position for bomb image.
		 * 
		 * @return The new position
		 */
		private Point calcNewPosition() {
			final Point2D delta = moveToDelta(direction, MOVEMENT_DISTANCE);
			final Point pt = (Point) getLocation().clone();

			pt.translate((int) Math.round(delta.getX()), (int) Math.round(delta.getY()));
			return pt;
		}

		/**
		 * Determine if the bomb has hit a wall and, if so, change it's direction of travel
		 */
		private void changeDirectionMaybe() {
			final Rectangle dukeRect = this.getBounds();
			final Rectangle paneRect = bombPane.getBounds();

			if (!paneRect.contains(dukeRect)) {
				direction += Math.PI / 2.0;
				if (SOUND_ON) ping.play();
				bounces++;
			}

		}

		/**
		 * Has the bomb exploded?
		 * @return true if it has
		 */
		public boolean hasExploded() {
			return (bounces >= MAX_BOUNCES);
		}

		/**
		 * Returns a delta position from origin given an angle in radians (orientation) and a distance to travel.
		 * 
		 * @param orientation
		 *            Angle to move forward from
		 * @param distance
		 *            Distance to travel
		 * 
		 * @return new location (position from origin)
		 */
		public Point2D moveToDelta(final double orientation, final double distance) {
			final AffineTransform tx = new AffineTransform();
			tx.setToRotation(orientation);
			tx.translate(0, distance);

			final Point2D newPoint = tx.transform(P2D_ORIGIN, null);
			return newPoint;
		}

		/**
		 * Set the direction of travel in radians
		 */
		private void setRandomDirection() {
			direction = Math.random() * (2 * Math.PI);
		}

		/**
		 * Return a random location in the animation pane.
		 */
		private void setRandomLocation() {
			final int x = (int) (Math.random() * (panelDim.width - getIcon().getIconWidth()));
			final int y = (int) (Math.random() * (panelDim.height - getIcon().getIconHeight()));
			setLocation(x, y);
		}

		/**
		 * Take a step in the animation.
		 */
		public void step() {
			final Point newPos = calcNewPosition();
			setLocation(newPos);
			changeDirectionMaybe();
		}

	}

}
