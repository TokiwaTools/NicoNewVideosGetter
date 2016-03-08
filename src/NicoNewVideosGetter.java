import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.security.auth.login.FailedLoginException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;


public class NicoNewVideosGetter extends JFrame implements ActionListener {

	static NicoConfig config;
	NicoLogin nicoLogin;
	NewVideosOPML opml = new NewVideosOPML();
	CookieStore cookieStore;

	public NicoNewVideosGetter(String _title) {
		setTitle(_title);
		setSize(500, 200);

		JButton loginButton = new JButton("Account Setting");
		loginButton.addActionListener(this);
		loginButton.setActionCommand("Login Button");
		JButton getFeedsButton = new JButton("Get Feeds");
		getFeedsButton.addActionListener(this);
		getFeedsButton.setActionCommand("Get Feeds Button");

		JPanel panel = new JPanel();
		panel.add(loginButton);
		panel.add(getFeedsButton);
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		config = new NicoConfig("src\\config\\config.json");
		NicoNewVideosGetter frame = new NicoNewVideosGetter("NicoNewVideosGetter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("Login Button")) {
			loginDialog();
		} else if (cmd.equals("Get Feeds Button")) {
			getFeedsDialog();
		}
	}

	public void loginDialog() {
		String address = "";
		String password = "";

		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);

		panel.setLayout(layout);
		panel.add( new JLabel("Mail Address:") );
		JTextField addressField = new JTextField(address);
		panel.add(addressField);
		panel.add( new JLabel("Password:") );
		JPasswordField passwordField = new JPasswordField(password);
		panel.add( passwordField );

		int pane = JOptionPane.showConfirmDialog(
				null,
				panel,
				"Login Niconico",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (pane != JOptionPane.YES_OPTION) {
			return;
		}

		address = addressField.getText();
		char [] c = passwordField.getPassword();
		password = new String(c);
		nicoLogin = new NicoLogin(address, password);

		try {
			cookieStore = nicoLogin.getUserSession();
			loginResultDialog("Success Login");
		} catch (FailedLoginException e) {
			loginResultDialog("Failed to Login\nWrong Mail Address or Password");
		} catch (ClientProtocolException e) {
			loginResultDialog("Failed to Login\nClientProtocol Error");
			loginDialog();
		} catch (IOException e) {
			loginResultDialog("Failed to Login\nPost Error");
			loginDialog();
		}

		config.createJSON(address, password);
	}

	public void loginResultDialog(String _message) {
		JOptionPane.showMessageDialog(this, _message, "Login Result", JOptionPane.INFORMATION_MESSAGE);
	}

	public void getFeedsDialog() {

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OPML�t�@�C��(*.opml)", "opml");
		chooser.setFileFilter(filter);
		int selected = chooser.showSaveDialog(this);
		if (selected != 0) {
			return;
		}
		File file = chooser.getSelectedFile();
		String path = file.getAbsolutePath();
		int point = path.lastIndexOf(".");
		if (point != -1) {
			if (path.substring(point + 1).equals("opml")) {
			} else {
				path += ".opml";
			}
		} else {
			path += ".opml";
		}
		boolean b = opml.save(path, cookieStore);
		if (b) {
			JOptionPane.showMessageDialog(this, "Saved as\n" + path, "Complete", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
