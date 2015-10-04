package ShoppingList;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ShoppingList {

	JFrame mainFrame;
	JPanel pnlShoppingList;
	DefaultListModel modelList;
	ArrayList<Product> arrayListProduct;
	DataBaseShoppingList dataBaseShopping = new DataBaseShoppingList();
	JList productList = new JList();
	JScrollPane scrollPane;
	int currentIndexProduct;
	JButton btnAddToList;
	JButton btnRemoveOne;
	JButton btnRemoveAll;
	JButton btnSendToEmail;
	String productListString;
	JTextField txtEmailFrom;
	JTextField txtEmailTo;
	JPanel mainLayoutShopping;
	JPasswordField txtPasswordToMail;
	SendingEmailThread emailThread = new SendingEmailThread(this);
	JPanel pnlLoadingCircle = new JPanel();
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
			"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public ShoppingList() {

		mainFrame = new JFrame();
		Container container = mainFrame.getContentPane();

		mainFrame.setTitle("Shopping list");
		mainFrame.setMinimumSize(new Dimension(500, 450));

		btnAddToList = new JButton("Add");
		mainFrame.getRootPane().setDefaultButton(btnAddToList);
		btnAddToList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zd) {

				AddProductFrame frameAddPosition = new AddProductFrame(ShoppingList.this);
				frameAddPosition.show();
			}
		});

		btnRemoveOne = new JButton("Remove");
		btnRemoveOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zd) {
				dataBaseShopping.removeOne(currentIndexProduct);
				refreshList();
			}
		});

		btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zd) {
				dataBaseShopping.removeAll();
				refreshList();
			}
		});

		btnSendToEmail = new JButton("Send to e-mail");
		btnSendToEmail.setEnabled(false);
		btnSendToEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zd) {
				Thread thread = new Thread(emailThread);
				pnlLoadingCircle.setVisible(true);
				thread.start();
			}
		});

		refreshList();
		scrollPane = new JScrollPane(productList);
		scrollPane.setPreferredSize(new Dimension(200, 200));

		txtEmailTo = new JTextField(15);
		GreyTextInTextField greyTextEmailTo = new GreyTextInTextField(txtEmailTo, "e-mail address");
		txtEmailFrom = new JTextField(15);
		GreyTextInTextField greyTextEmailFrom = new GreyTextInTextField(txtEmailFrom, "mailAddress@gmail.com");
		txtPasswordToMail = new JPasswordField(15);
		GreyTextInTextField greyTextPassword = new GreyTextInTextField(txtPasswordToMail, "Password");

		mainLayoutShopping = new JPanel();
		mainLayoutShopping.setLayout(new GridBagLayout());
		Utils.addItem(mainLayoutShopping, new JLabel("Shopping list: "), 0, 0, 2, 1, GridBagConstraints.CENTER);
		Utils.addItem(mainLayoutShopping, scrollPane, 0, 1, 2, 5, GridBagConstraints.CENTER);
		Utils.addItem(mainLayoutShopping, new JLabel("Send to e-mail: (Support only for gmail)"), 0, 6, 2, 1,
				GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, new JLabel("From: "), 0, 7, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, new JLabel("To: "), 0, 8, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, txtEmailFrom, 1, 7, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, txtEmailTo, 1, 8, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, btnAddToList, 2, 2, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, btnRemoveOne, 2, 3, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, btnRemoveAll, 2, 4, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, txtPasswordToMail, 2, 7, 1, 1, GridBagConstraints.WEST);
		Utils.addItem(mainLayoutShopping, btnSendToEmail, 2, 8, 1, 1, GridBagConstraints.WEST);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 1) {
					int productId = theList.locationToIndex(mouseEvent.getPoint());

					if (productId >= 0 && productId < arrayListProduct.size()) {
						Product chooseProduct = arrayListProduct.get(productId);
						currentIndexProduct = chooseProduct.id;
					}
				}
			}
		};
		productList.addMouseListener(mouseListener);

		txtEmailTo.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				validate();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				validate();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				validate();
			}

		});

		txtEmailFrom.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				validate();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				validate();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				validate();
			}

		});

		JLabel lblLoading = new JLabel(new ImageIcon("src/ajax-loader.gif"));
		lblLoading.setOpaque(true);
		lblLoading.setBackground(new Color(0, 0, 0, 0));

		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createVerticalStrut(120));
		verticalBox.add(lblLoading);
		verticalBox.add(Box.createHorizontalGlue());

		pnlLoadingCircle = new JPanel();
		pnlLoadingCircle.setBackground(new Color(142, 152, 156, 20));
		pnlLoadingCircle.setVisible(false);
		pnlLoadingCircle.setSize(new Dimension(500, 450));
		pnlLoadingCircle.add(verticalBox);
		container.add(pnlLoadingCircle);

		container.add(mainLayoutShopping);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.show();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void refreshList() {

		modelList = new DefaultListModel();

		arrayListProduct = dataBaseShopping.getProducts();
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < arrayListProduct.size(); i++) {
			modelList.addElement(arrayListProduct.get(i));

			builder.append(arrayListProduct.get(i)).append("\n");
			btnRemoveOne.setEnabled(true);
			btnRemoveAll.setEnabled(true);
		}

		if (modelList.isEmpty()) {
			modelList.addElement("No products.");
			btnRemoveOne.setEnabled(false);
			btnRemoveAll.setEnabled(false);
		}

		productList.setModel(modelList);
		productListString = builder.toString();

	}

	// Validate all e-mail fields.
	private void validate() {

		Matcher matcherFrom = VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmailFrom.getText());
		Matcher matcherTo = VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmailTo.getText());

		if (matcherFrom.find() && matcherTo.find()) {
			btnSendToEmail.setEnabled(true);
			mainFrame.getRootPane().setDefaultButton(btnSendToEmail);

		} else if (btnSendToEmail.isEnabled()) {
			btnSendToEmail.setEnabled(false);
			mainFrame.getRootPane().setDefaultButton(btnAddToList);

		}
	}

	public static void main(String[] args) {

		new ShoppingList();
	}

}
