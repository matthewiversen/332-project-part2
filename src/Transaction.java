
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Transaction {
	
	private int id;
	private String dateOfPurchase;
	private int customerID;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDateOfPurchase() {
		return dateOfPurchase;
	}
	public void createDateOfPurchase() {
		LocalDate currentDate = LocalDate.now();
		this.dateOfPurchase = currentDate.toString();
		System.out.println(this.dateOfPurchase);
	}
	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
}
