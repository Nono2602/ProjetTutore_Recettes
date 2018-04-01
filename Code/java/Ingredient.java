
public class Ingredient {

	private String name;
	private char typeQ;
	private double quantity;
	
	public Ingredient(String name, char typeQ, double quantity) {
		this.name = name;
		this.typeQ = typeQ;
		this.quantity = quantity;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char getTypeQ() {
		return typeQ;
	}
	public void setTypeQ(char typeQ) {
		this.typeQ = typeQ;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
