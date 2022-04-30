
public class ItemBought {
	private int itemID;
	private int customerID;
	private int transactionID;
	private float price;
	private int qty;
	private float transactionPrice;
	
	public int getItemID() {
		return itemID;
	}
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
	public int getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public float getTransactionPrice() {
		return transactionPrice;
	}
	public void setTransactionPrice(float transactionPrice) {
		this.transactionPrice = transactionPrice;
	}
	public void calculatePrice() {
		transactionPrice = this.price * this.qty;
	}

}
