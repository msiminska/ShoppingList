package ShoppingList;

public class Product {

	int id;
	String nameProduct;

	public Product(int id, String nameProduct) {
		this.id = id;
		this.nameProduct = nameProduct;
	}

	public String toString() {
		return nameProduct;
	}

}
