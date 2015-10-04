package ShoppingList;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SendingEmailThread implements Runnable {

	private ShoppingList list;
	private String from;
	private String password;

	public SendingEmailThread(ShoppingList list) {
		this.list = list;
	}

	@Override
	public void run() {

		String[] emailTo = { list.txtEmailTo.getText() };
		from = list.txtEmailFrom.getText();
		password = list.txtPasswordToMail.getText();

		// Set parameters for gmail.
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props, new GMailAuthenticator(from, password));
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[emailTo.length];

			// To get the array of addresses
			for (int i = 0; i < emailTo.length; i++) {
				toAddress[i] = new InternetAddress(emailTo[i]);
			}

			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject("Shopping List");
			message.setText(list.productListString);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					list.pnlLoadingCircle.setVisible(false);
					JOptionPane.showMessageDialog(null, "Shopping list has been sent to given e-mail address");
				}
			});

		} catch (AddressException ae) {
			ae.printStackTrace();
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
}

class GMailAuthenticator extends Authenticator {
	private String user;
	private String pw;

	public GMailAuthenticator(String username, String password) {
		super();
		this.user = username;
		this.pw = password;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, pw);
	}
}
