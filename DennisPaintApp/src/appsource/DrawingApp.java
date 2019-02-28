package appsource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

public class DrawingApp {

	protected JFrame frame;
	protected JPanel panel;
	protected JPanel contentPane;
	protected JMenuBar menuBar;
	protected Doodler canvas;

	private boolean alreadyHasImage = false;

	public static void main(String[] args) {
		new DrawingApp();
	}

	public DrawingApp() {

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setTitle("Dennis Paint App");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(300, 300));
		frame.getContentPane().add(panel);
		frame.setBackground(Color.WHITE);

		menuBar = new JMenuBar();
		menuBar.add(makeFileMenu());
		menuBar.setOpaque(true);
		frame.setJMenuBar(menuBar);

		frame.setLocation(350, 350);
		frame.pack();
		frame.setVisible(true);
	}

	private JMenu makeFileMenu() {
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String command = event.getActionCommand();
				if (command.equals("New")) {
					doNew();
				} else if (command.equals("Open...")) {
					doOpen();
				} else if (command.equals("Exit")) {
					doExit();
				}
			}
		};

		JMenu fileMenu = new JMenu("File");

		JMenuItem newCmd = new JMenuItem("New");
		newCmd.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
		newCmd.addActionListener(listener);
		fileMenu.add(newCmd);

		JMenuItem openCmd = new JMenuItem("Open...");
		openCmd.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		openCmd.addActionListener(listener);
		fileMenu.add(openCmd);

		JMenuItem saveCmd = new JMenuItem("Save...");
		saveCmd.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		saveCmd.addActionListener(listener);
		fileMenu.add(saveCmd);

		JMenuItem exitCmd = new JMenuItem("Exit");
		exitCmd.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
		exitCmd.addActionListener(listener);
		fileMenu.add(exitCmd);

		return fileMenu;
	}

	private void doNew() {
		try {
			if (canvas != null) {
				JOptionPane.showMessageDialog(null, "This will destroy current doodle");
				panel.remove(canvas);
			}

			canvas = new Doodler(300, 300);

			panel.setBackground(Color.WHITE);

			panel.add(canvas, BorderLayout.CENTER);

			Cursor cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			canvas.setCursor(cursor);
			frame.validate();
		} catch (Exception e) {
			System.out.println("Exception in doNew() :" + e.getMessage());
			System.exit(1);
		}
	}

	private void doOpen() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.addChoosableFileFilter(new AnImageFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		int returnValue = fileChooser.showOpenDialog(frame);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				if (alreadyHasImage) {
					JOptionPane.showMessageDialog(frame, "Opening removes the current image");
					frame.getContentPane().remove(panel);
					panel = new JPanel();
					frame.getContentPane().add(panel);
				}

				BufferedImage image = ImageIO.read(file.toURI().toURL());
				JLabel imageLabel = new JLabel(new ImageIcon(image));

				canvas = new Doodler(300, 300);
				panel.add(canvas);

				panel.add(imageLabel);

				alreadyHasImage = true;
				frame.validate();
			} catch (Exception e) {
				System.out.println("Exception in doOpen(): " + e);
				System.exit(1);
			}
		}
	}

	private void doSave() {
		File outFile;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save As...");

		int action = fileChooser.showSaveDialog(frame);
		if (action != JFileChooser.APPROVE_OPTION) {
			return;
		}
		outFile = fileChooser.getSelectedFile();

		if (outFile.exists()) {
			action = JOptionPane.showConfirmDialog(frame, "Replace existing file?");
			if (action != JOptionPane.YES_OPTION)
				return;
		}
		try {
			Rectangle rect = canvas.getBounds();
			Image image = canvas.createImage(rect.width, rect.height);

			Graphics g = image.getGraphics();
			canvas.paint(g);
			ImageIO.write((RenderedImage) image, "jpg", outFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "IOException in doSave(): " + e.getMessage());
			System.out.println(e.getCause().getMessage());
			System.exit(1);
		}
	}

	private void doExit() {
		System.exit(0);
	}

	class AnImageFilter extends FileFilter {
		public void getExtension(File f) {
			String extension = null;
			String fileName = f.getName();
			int i = fileName.lastIndexOf('.');

			if (i > 0 && i < fileName.length() - 1) {
				String extention = fileName.substring(i + 1).toLowerCase();
			}
		}

		@Override
		public boolean accept(File arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// return extension;

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals("tiff") || extension.equals("tif") || extension.equals("gif")
					|| extension.equals("jpeg") || extension.equals("jpg")) {

				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private String getExtension(File f) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		return "*.tif , *.tiff, *.gif, *.jpeg, *.jpg";
	}

	class Doodler extends JComponent {

		private int lastX;
		private int lastY;
		private Vector plots = null;

		private Vector pointData = null;
		private int x1, y1;
		private Graphics graphics = null;

		public Doodler(int width, int height) {
			super();
			this.setSize(width, height);
			plots = new Vector();
			pointData = new Vector();

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					x1 = e.getX();
					y1 = e.getY();
					pointData.add(new Point(x1, y1));
				}

				public void mouseReleased(MouseEvent e) {
					plots.add((Vector) pointData.clone());
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					int x2 = e.getX();
					int y2 = e.getY();
					pointData.add(new Point(x2, y2));
					graphics = getGraphics();
					graphics.drawLine(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;
				}
			});
		}
	}

	public void paint(Graphics g) {
		 Object plots;
		ListIterator<E> it = ((Vector) plots).listIterator();

		while (it.hasNext()) {
			Vector v = (Vector) it.next();
			Point point1 = (Point) v.get(0);
			for (int i = 0, size = v.size(); i < size; i += 2) {
				Point point2 = (Point) v.get(i);
				g.drawLine(point1.x, point1.y, point2.x, point2.y);
				point1 = point2;
			}
		}
	}
}
