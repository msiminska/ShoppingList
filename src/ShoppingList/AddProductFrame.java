package ShoppingList;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AddProductFrame extends JFrame {

	DataBaseShoppingList dataBaseShopping;
	JTextField txtNameProduct;
	ShoppingList shoppingListObject;
	JButton btnAddProduct;

	public AddProductFrame(ShoppingList product) {

		shoppingListObject = product;
		dataBaseShopping = new DataBaseShoppingList();

		Container contentAddProduct = this.getContentPane();
		this.setTitle("Adding product");
		this.setMinimumSize(new Dimension(400, 150));

		btnAddProduct = new JButton("Add");
		btnAddProduct.setEnabled(false);
		btnAddProduct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zd) {
				String nameProduct = txtNameProduct.getText();
				dataBaseShopping.addProductToList(nameProduct);

				shoppingListObject.refreshList();
				dispose();
			}
		});
		this.getRootPane().setDefaultButton(btnAddProduct);

		txtNameProduct = new JTextField(20);
		txtNameProduct.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				changed();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				changed();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				changed();
			}

			private void changed() {
				if (txtNameProduct.getText().equals("")) {
					btnAddProduct.setEnabled(false);
				} else {
					btnAddProduct.setEnabled(true);
				}
			}
		});

		JPanel panelAddProduct = new JPanel();
		panelAddProduct.setLayout(new GridBagLayout());
		Utils.addItem(panelAddProduct, new JLabel("Enter product name: "), 0, 0, 4, 1, GridBagConstraints.WEST);
		Utils.addItem(panelAddProduct, txtNameProduct, 0, 1, 3, 1, GridBagConstraints.NORTHWEST);
		Utils.addItem(panelAddProduct, btnAddProduct, 3, 1, 1, 1, GridBagConstraints.NORTHWEST);

		contentAddProduct.add(panelAddProduct);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
