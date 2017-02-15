package com.github.confabulation.symbolic.gui;

import io.LineStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
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
		String s = tfin.getText();
		ta.append("> " + s  + "\n");
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

	protected class GuiIn implements LineStream {

		protected boolean closed;
		protected LinkedBlockingQueue<String> q;

		// TODO: provide a way to transmit the close()
		public GuiIn() {
			closed = false;
			q = new LinkedBlockingQueue<String>();
		}

		@Override
		public void close() {
			closed = true;
		}

		/**
		 * queue one line to be consumed later
		 * 
		 * @param s
		 *            the string. Ignored if null
		 */
		public void add(String s) {
			if (s == null) {
				return;
			}
			q.add(s);
		}

		@Override
		public synchronized String readLine() throws InterruptedIOException {
			if (closed) {
				return null;
			}
			try {
				String ret = null;
				while (ret == null && !closed) {
					ret = q.poll(100, TimeUnit.MILLISECONDS);
				}
				return ret;
			} catch (InterruptedException e) {
				throw new InterruptedIOException(
						"Interrupted: original stack trace:\n" + e.toString());
			}
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

		String line = null;
		while ((line = guiConsole.in.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println("End of read.");
	}
}
