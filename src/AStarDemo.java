

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class AStarDemo {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UFrame frame = new UFrame();
				AstarPanel astarPanel = new AstarPanel(15, 60, 40);
				StatusPanel statusPanel = new StatusPanel();
				astarPanel.setStatusPanel(statusPanel);
				
				JScrollPane scroller = new JScrollPane(astarPanel);
				frame.getContentPane().add(scroller, BorderLayout.CENTER);
				
				frame.getContentPane().add(new ControlPanel(astarPanel),
						BorderLayout.NORTH);
				frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
				frame.setJMenuBar(new AStarMenuBar(astarPanel));
				frame.pack();
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				astarPanel.requestFocus();
			}
		});
	}
}
