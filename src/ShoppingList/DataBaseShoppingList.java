package ShoppingList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBaseShoppingList {

	private static final String createStatement = "CREATE TABLE IF NOT EXISTS shoppingList (id_product INTEGER PRIMARY KEY AUTOINCREMENT, nameProduct varchar(255) )";
	String dataBase = "shoppingList";
	private Connection conn;
	private Statement stat;

	public DataBaseShoppingList() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("No JDBC driver found.");
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dataBase + ".db");
			stat = conn.createStatement();
		} catch (SQLException e) {
			System.err.println("Problem with connection.");
			e.printStackTrace();
		}

		try {
			stat.execute(createStatement);
		} catch (SQLException e) {
			System.err.println("Error in table create.");
			e.printStackTrace();
		}
	}

	public void addProductToList(String product) {

		try {
			String addStatement = "INSERT INTO " + dataBase + " (nameProduct) " + "VALUES ('" + product + "');";
			stat.executeUpdate(addStatement);
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeOne(int id) {
		try {
			String removeStatement = "DELETE FROM  shoppingList  WHERE " + "id_product = " + id;
			stat.executeUpdate(removeStatement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeAll() {
		try {
			String removeAllStatement = "DELETE FROM " + dataBase;
			stat.executeUpdate(removeAllStatement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Product> getProducts() {

		ArrayList<Product> products = new ArrayList<Product>();

		try {
			String searchStatement = "SELECT * FROM " + dataBase;
			ResultSet result = stat.executeQuery(searchStatement);

			while (result.next()) {
				int id = result.getInt("id_product");
				String nameProduct = result.getString("nameProduct");
				Product productToList = new Product(id, nameProduct);
				products.add(productToList);
			}
			result.close();
			stat.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return products;
	}
}
