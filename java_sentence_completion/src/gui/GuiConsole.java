package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Mimic a terminal in gui, with equivalents to System.out and System.in
 * 
 * @author bernard paulus
 */
public class GuiConsole extends JPanel implements ActionListener,
		AncestorListener {

	private static final long serialVersionUID = -1467490432851738156L;

	/**
	 * Blocking read from the GuiConsole, like System.in
	 */
	public final GuiIn in;
	/**
	 * Print to the console, like System.out
	 */
	public final PrintStream out;

	protected JTextArea ta; // gui output
	protected JTextField tfin; // gui prompt

	public static final int default_term_cols = 80; // from my terminal
	public static final int default_term_lines = 24;

	/**
	 * Builds the console, but does not display it
	 */
	public GuiConsole() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.addAncestorListener(this);

		ta = new JTextArea(default_term_lines, default_term_cols);
		ta.setEditable(false);
		ta.setLineWrap(true);

		JScrollPane ta_scroll = new JScrollPane(ta);
		ta_scroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(ta_scroll);

		tfin = new JTextField();
		// tfin.setPreferredSize(new Dimension(100, 20));
		tfin.addActionListener(this);
		add(tfin);

		in = new GuiIn();
		out = new PrintStream(new GuiOut());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = tfin.getText() + "\n";
		ta.append("> " + s);
		in.add(s);
		tfin.setText("");
	}

	@Override
	public void ancestorAdded(AncestorEvent arg0) {
	}

	@Override
	public void ancestorMoved(AncestorEvent arg0) {
	}

	@Override
	public void ancestorRemoved(AncestorEvent e) {
		in.close();
	}

	protected class GuiIn extends InputStream {

		protected LinkedBlockingQueue<Byte> buf;
		protected boolean closed;

		protected GuiIn() {
			closed = false;
			buf = new LinkedBlockingQueue<Byte>();
		}

		@Override
		public void close() {
			closed = true;
		}

		/**
		 * add strings to read in the InputStream. Arguments are ignored if the
		 * stream is closed.
		 * 
		 * @param s
		 *            a string. Ignored if null
		 */
		protected void add(String s) {
			if (closed || s == null) {
				return;
			}
			byte[] bs = s.getBytes();
			LinkedList<Byte> lbs = new LinkedList<Byte>();
			for (byte b : bs) {
				lbs.add(b);
			}
			buf.addAll(lbs);
		}

		@Override
		public int available() {
			return buf.size();
		}

		@Override
		public synchronized int read() throws InterruptedIOException {
			if (closed && buf.isEmpty()) {
				return -1;
			}
			Byte b = 0;

			while (true) {
				try {
					
					if ((b = buf.poll(100, TimeUnit.MILLISECONDS)) == null) {
						if (closed && buf.isEmpty())
							return -1;
					} else
						break;

				} catch (InterruptedException e) { 
					// TODO: close and return 
					throw new InterruptedIOException("interrupted: "
							+ e.getMessage());
				}
			}
			return b;
		}

	}

	protected class GuiOut extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			ta.append(new String(new byte[] { (byte) b }));
		}

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @return
	 */
	private static GuiConsole createAndShowConsole() {
		// Create and set up the window.
		JFrame frame = new JFrame("GuiConsole");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Add content to the window.
		GuiConsole console = new GuiConsole();
		frame.add(console);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		return console;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {

		final LinkedBlockingQueue<GuiConsole> pipe = new LinkedBlockingQueue<GuiConsole>();
		// Schedule a job for the event dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);

				// make the ref available to the main method
				pipe.offer(createAndShowConsole());
			}
		});

		// wait for the gui
		GuiConsole guiConsole = pipe.take();

		guiConsole.out.println("Welcome to GuiConsole.");

		System.out.println("Reading from GuiConsole");
		int chr = -1;
		while ((chr = guiConsole.in.read()) != -1) {
			System.out.write(chr);
		}
		// String line;
		// BufferedReader buf = new BufferedReader(new InputStreamReader(
		// guiConsole.in), 1);
		// while ((line = buf.readLine()) != null) {
		// System.out.println(line);
		// }
		System.out.println("End of read.");
	}
}
