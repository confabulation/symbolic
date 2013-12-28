package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MessageFileChooser extends JPanel implements ActionListener {

	protected JFileChooser fc;

	public MessageFileChooser(String message) {
		super();
		setLayout(new BorderLayout());

		JPanel msg_container = new JPanel(new BorderLayout());
		JPanel msg_list = new JPanel();
		msg_list.setLayout(new BoxLayout(msg_list, BoxLayout.Y_AXIS));

		for (String s : message.split("\n")) {
			JLabel label = new JLabel(s);
			msg_list.add(label);
		}

		msg_container.add(msg_list);
		add(msg_container, BorderLayout.NORTH);

		fc = new JFileChooser();
		fc.addActionListener(this);
		add(fc, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
			// pipe.offer + need a mechanism to dispose the window? TODO
			System.out.println("OK" + e);
		}
		if (e.getActionCommand() == JFileChooser.CANCEL_SELECTION) {
			System.out.println("CANCEL" + e);
		}
	}

	/**
	 * get the file selected by the user
	 * 
	 * @return the file, or null if no file was selected or the file chooser is
	 *         closed. Blocking call
	 */
	public File getSelectedFile() {
		return null; // TODO pipe.take() to wait for an answer
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @return
	 */
	private static void createAndShowGui() {
		// Create and set up the window.
		JFrame frame = new JFrame("MessageFileChooser");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Add content to the window.
		MessageFileChooser mfc = new MessageFileChooser("Choose your file\nlol");
		frame.add(mfc);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);

				createAndShowGui();
			}
		});
	}

}
