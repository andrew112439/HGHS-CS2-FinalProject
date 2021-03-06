package hacker_typer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import swing_utils.loc;

public class Monitor extends JPanel implements Runnable, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8791554566652975440L;
	private Image PowerMonitor, PowerComputer, computer, cdFlasher,
			floppyFlasher, backgroundImage;
	private boolean isOn;
	private int buttonPosX, buttonPosY, scale;
	private loc cdBlinkOne, cdBlinkTwo, floppyBlink, hardBlink, powerButton;
	private Random randGen;
	private int lastTime;
	private Font monitorFont;
	public Cookie cookie;
	Thread thread;

	public Monitor(String computer, String letters, String powerMonitor,
			String powerComputer, String cdFlasher, String floppyFlasher,
			int scale, Color bGColor) throws IOException, FontFormatException {
		{ // image stuff
			// Read in backgroundImage
			backgroundImage = ImageIO.read(new File(computer));

			// Read in the button image
			this.PowerMonitor = ImageIO.read(new File(powerMonitor));
			this.PowerComputer = ImageIO.read(new File(powerComputer));

			// Read in the computer image
			this.computer = ImageIO.read(new File(computer));

			// Read in the cd flasher image
			this.cdFlasher = ImageIO.read(new File(cdFlasher));
			
			// Read in the floppy flasher image
			this.floppyFlasher = ImageIO.read(new File(floppyFlasher));

			// Set button's position in the panel
			buttonPosX = 0;
			buttonPosY = 0;

			// Set location for the blinkers located in the background
			cdBlinkOne = new loc(239, 65);
			cdBlinkTwo = new loc(239, 131);
			floppyBlink = new loc(82, 244);
			hardBlink = new loc(168, 310);
			powerButton = new loc(932, 448);
		}

		// "Computer" initial state is off
		isOn = false;

		// set bGColor
		this.setBackground(bGColor);

		// Initialize scale
		this.scale = scale;

		// Initialize random number generator
		randGen = new Random();

		// Initialize last seconds holder
		lastTime = 0;

		// set preferred size
		this.setPreferredSize(getDimension());

		// read in font
		monitorFont = Font.createFont(Font.TRUETYPE_FONT, new File(letters))
				.deriveFont(Font.BOLD, 12f);

		cookie = new Cookie();

		initialize();

		

		thread = new Thread(this);
		thread.start();
	}

	
	/*
	 * This method initializes the child of this panel. since I want to position
	 * the child of this panel based on x and y coords, the use of the layout
	 * manager becomes more of a nuisance than a blessing, so it is set to null
	 * and the child is positioned by calling it's setBounds() method
	 */
	private void initialize() {
		// set the layout manager to null
		this.setLayout(null);

		// initialize the text area
		JTextArea foo = new JTextArea();
		// this allows the "cookie clicker" part of this to actually work
		foo.addKeyListener(this);
		// set size and position of the child
		foo.setBounds(464, 71, 478, 341);
		// set Background Color
		foo.setBackground(new Color(46, 46, 46));
		// set font style and color
		foo.setForeground(new Color(104, 246, 0));
		foo.setFont(monitorFont.deriveFont(monitorFont.getSize() * 72));
		// set wrapping to true
		foo.setLineWrap(true);
		foo.setAutoscrolls(true);
		foo.getCaret().setVisible(true);
		DefaultCaret caret = (DefaultCaret) foo.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// finally we add it to the parent
		this.add(foo, 0);

		// just to refresh the panel (though its being refreshed every 50mil for
		// the blinkers)
		this.repaint();
	}

	/*
	 * just to simplify life when i actually need this. returns the current time
	 * down to the second and not past
	 */
	private int getSeconds() {
		return (int) ((int) System.currentTimeMillis() * Math.pow(10, -3));
	}

	// not sure why this guy exists, probably for a decent reason
	public Dimension getDimension() {
		return new Dimension(backgroundImage.getWidth(this),
				backgroundImage.getHeight(this));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw the background image.
		g.drawImage(getImage(), 0, 0, this);

		// TODO make it so the lights toggle on and off rather than flicker

		// Cd drive 1
		if (randGen.nextInt(10) < 2) {
			g.drawImage(cdFlasher, cdBlinkOne.getX(), cdBlinkOne.getY(), this);
		}

		// Cd drive 2
		if (randGen.nextInt(10) < 2) {
			g.drawImage(cdFlasher, cdBlinkTwo.getX(), cdBlinkTwo.getY(), this);
		}

		// Floppy drive
		if (randGen.nextInt(10) < 2) {
			g.drawImage(floppyFlasher, floppyBlink.getX(), floppyBlink.getY(),
					this);
		}

		// Hard drive
		if (randGen.nextInt(10) < 4) {
			g.drawImage(floppyFlasher, hardBlink.getX(), hardBlink.getY(), this);
			g.drawImage(floppyFlasher, hardBlink.getX(), hardBlink.getY() + 1,
					this);
		}
		lastTime = getSeconds();

		// turn on power
		if (isOn) {
			g.drawImage(PowerMonitor, powerButton.getX(), powerButton.getY(),
					this);
			this.getComponent(0).setForeground(new Color(104, 246, 0));
			((JTextArea)this.getComponent(0)).setEditable(true);

		} else {
			this.getComponent(0).setForeground(
					this.getComponent(0).getBackground());
			((JTextArea)this.getComponent(0)).setEditable(false);

		}

		// turn on computer power
		g.drawImage(PowerComputer, 240, 239, this);
	}

	private Image getImage() {

		Image foo = this.createImage(WIDTH, HEIGHT);
		// TODO Auto-generated method stub
		return backgroundImage;
	}

	@Override
	public void run() {
		while (true) {
			repaint();
			// Delay
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {

			}

		}
	}

	public void eventInput(MouseEvent e) {
		// TODO Auto-generated method stub
		

	}

	public void mouseClick(MouseEvent e) {
		if (e.getX() > 929 && e.getX() < 976 && e.getY() > 472
				&& e.getY() < 519) {
			isOn = !isOn;
		}
	}

	public double getLinesPerSec() {
return cookie.getCookiesPerSec();	// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		if (isOn) {
			cookie.keyPressed();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public int getAmountOfLines() {
		return cookie.getNumberOfCookies();
	}
}
