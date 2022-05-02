import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

import org.sqlite.SQLiteDataSource;

import java.util.ArrayList;

public class Proj2Database {

	public static void main(String[] args) {
		SQLiteDataSource ds = null;

		try {
			ds = new SQLiteDataSource();
			ds.setUrl("jdbc:sqlite:test.db");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement();) {

			LocalDate date = LocalDate.now();
			System.out.println(date.toString());

			int choice = homeScreen();
			if (choice == 1) {

				Item newItem = createItem();
				insertNewItem(newItem, stmt);

			} else if (choice == 2) {
				int input = readInteger("Input a department");
				ResultSet rs = getDates(stmt, input); // Gets all expiration dates from the expireDate table

				ArrayList<Integer> expiringItems = fillItemsArray(rs); // Creates an arraylist of items w/ dates within
																		// 2 days of current date
				System.out.println("Items about to expire:");
				printAll(expiringItems);

			} else if (choice == 3) {
				int input = readInteger("Input a department number: ");

				ResultSet rsItems = getItemsToOrder(stmt, input); // Queries the database for all items that have less
																	// currentStock than the RestockAmount
				ArrayList<Integer> itemsToOrder = new ArrayList<Integer>(1); // Arraylist of item IDs that need to be
																				// restocked
				ArrayList<Integer> previousOrderIDs = new ArrayList<Integer>(1); // Arraylist of order IDs that have
																					// been previously made for items
																					// that are currently low on stock
				if (rsItems.isClosed()) { // If the SQL query returns a closed set, the database inputed doesn't exist.
					System.out.println("Department doesn't exist or invalid input.");
				} else {
					while (rsItems.next()) {
						itemsToOrder.add(rsItems.getInt("upc"));
						ResultSet item = getPreviousOrderIDs(stmt, rsItems.getInt("upc")); // Gets the order ID from the
																							// item that's returned by
																							// previous SQL query
						previousOrderIDs = parseIDs(item, previousOrderIDs); // Adds all of the order IDs from the
																				// previous SQL query to the
																				// previousOrderIDs arrayList
					}
					System.out.println("Items needing restock: ");
					printAll(itemsToOrder);

					if (previousOrderIDs.size() > 0) {
						System.out.println("Previous Order IDs for these items: ");
						printAll(previousOrderIDs);
					} else {
						System.out.println("No previous orders for these item numbers");
					}
				}
			} else if (choice == 4) {
				ItemBought newItemBought = orderItem(stmt);
				insertItemBought(stmt, newItemBought);

			} else if (choice == 7) {
				int deliveryID = readInteger("Please enter ID of delivery you have received: ");
				receiveDelivery(stmt, deliveryID);

			} else if (choice == 8) {
				// Apply Coupon to transaction option
				clearScreen();

				System.out.print("\n\t===== TRANSACTION MODIFICATION - COUPONS =====\n\n");
				int custId = readInteger("Please enter customer ID: ");
				int tId = readInteger("Please enter transaction ID");
				applyCoupon(stmt, tId, custId);

			} else if (choice == 9) {
				// Total Transaction Option
				clearScreen();

				System.out.print("\n\t===== TRANSACTION TOTAL INQUIRY =====\n\n");
				int tId = readInteger("Please enter transaction ID: ");
				int custId = readInteger("Please enter customer ID: ");
				double total = totalTransaction(stmt, tId, custId);

				if (total == 0.0) {
					System.out.println("\nERROR: Transaction not found!\n");
				} else {
					System.out.println("\nTotal for Transaction (#" + tId + "): $ " + total +
							"\n");
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static int homeScreen() {
		Scanner sc = new Scanner(System.in);
		int choice = 0;
		while (choice <= 0 || choice > 9) { // Input must be between 1 and 4
			while (true) {
				try {
					clearScreen();
					System.out.println("\n\t===== PROJECT 2 DATABASE PROGRAM =====\n");
					System.out.println("1. Add item to database");
					System.out.println("2. Get items about to expire");
					System.out.println("3. Get needed restock");
					System.out.println("4. Customer transaction");
					System.out.println("7. Receive Delivery");
					System.out.println("8. Apply coupon to transaction");
					System.out.println("9. Get Transaction Total");
					choice = Integer.parseInt(sc.nextLine());
					break;
				} catch (NumberFormatException e) {
					System.out.println("That was not a valid number, please try again.");
				}
			}
		}
		return choice;
	}

	public static Item createItem() {
		Scanner sc = new Scanner(System.in);
		Item newItem = new Item();
		while (true) {
			System.out.println("Input UPC: ");
			try {
				newItem.setUpc(Integer.parseInt(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input department number: ");
			try {
				newItem.setDept(Integer.parseInt(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input restock amount: ");
			try {
				newItem.setRestockAmount(Integer.parseInt(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input standard price: ");
			try {
				newItem.setPrice(Float.parseFloat(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input sale price: ");
			try {
				newItem.setSalePrice(Float.parseFloat(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input Wholesale price: ");
			try {
				newItem.setWholesalePrice(Float.parseFloat(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input currentStock: ");
			try {
				newItem.setCurrentStock(Integer.parseInt(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		while (true) {
			System.out.println("Input supplier ID: ");
			try {
				newItem.setSupplierID(Integer.parseInt(sc.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number");
			}
		}

		// System.out.println(newItem.getUpc());
		// System.out.println(newItem.getDept());
		// System.out.println(newItem.getRestockAmount());
		// System.out.println(newItem.getPrice());
		// System.out.println(newItem.getSalePrice());
		// System.out.println(newItem.getWholesalePrice());
		// System.out.println(newItem.getCurrentStock());
		// System.out.println(newItem.getSupplierID());
		return newItem;
	}

	public static int insertNewItem(Item newItem, Statement stmt) {
		String query = "INSERT INTO Item VALUES(" +
				newItem.getUpc() + ", '" +
				newItem.getDept() + " ', " +
				newItem.getRestockAmount() + ", " +
				newItem.getPrice() + ", " +
				newItem.getSalePrice() + ", " +
				newItem.getWholesalePrice() + ", " +
				newItem.getCurrentStock() + ", " +
				newItem.getSupplierID() + ");";
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean checkAfter(LocalDate date) {
		LocalDate targetDate = LocalDate.now().plusDays(2); // Add 2 days to the current day
		return targetDate.isAfter(date);
	}

	public static ResultSet getDates(Statement stmt, int department) {
		String query = "SELECT item, expireDate FROM ExpirationDates WHERE Department = " + department + ";";
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Integer> fillItemsArray(ResultSet rs) throws SQLException {
		ArrayList<Integer> expiringItems = new ArrayList<Integer>(1); // Create an arrayList of size 1 to be able to add
																		// to later
		while (rs.next()) {
			if (checkAfter(LocalDate.parse(rs.getString("expireDate")))) { // returns true if expire date is sooner than
																			// 2 days from the current date
				if (!expiringItems.contains(rs.getInt("item"))) // Only adds to arraylist if the item isn't already
																// inside
					expiringItems.add(rs.getInt("item"));
			}
		}
		return expiringItems;
	}

	public static ResultSet getItemsToOrder(Statement stmt, int department) {
		String query = "SELECT upc FROM Item WHERE department = " + department + " AND restockAmount > currentStock;"; // To
																														// execute
																														// a
																														// SQL
																														// command,
																														// save
																														// in
																														// a
																														// string
																														// and
																														// then
																														// pass
																														// the
																														// queryString
																														// as
																														// an
																														// arg
																														// in
																														// an
																														// execute
																														// function
		try {
			ResultSet returnRS = stmt.executeQuery(query);
			return returnRS;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Null returning");
		return null;

	}

	public static ResultSet getPreviousOrderIDs(Statement stmt, int item) {
		String query = "SELECT id FROM Orders WHERE itemOrdered = " + item + ";";
		try {
			ResultSet returnRS = stmt.executeQuery(query);
			return returnRS;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Null returning");
		return null;
	}

	public static ArrayList<Integer> parseIDs(ResultSet rs, ArrayList<Integer> previousOrderIDs) throws SQLException {
		while (rs.next()) {
			previousOrderIDs.add(rs.getInt("id"));
		}
		return previousOrderIDs;
	}

	public static boolean itemExists(Statement stmt, int item) throws SQLException {
		String query = "SELECT upc FROM Item WHERE upc = " + item + ";";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return !rs.isClosed();
	}

	public static int readInteger(String message) {
		Scanner sc = new Scanner(System.in);
		int input;
		while (true) { // This while loop controls invalid input by attempting to cast input as an
						// Integer
			System.out.print(message);
			try {
				input = Integer.parseInt(sc.nextLine()); // Reads input from console
				return input;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number, please try again.");
			}
		}
	}

	public static boolean customerExists(Statement stmt, int customerID) throws SQLException {
		String query = "SELECT id FROM Customer WHERE id = " + customerID + ";";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return !rs.isClosed();
	}

	public static boolean transactionExists(Statement stmt, int transactionID) throws SQLException {
		String query = "SELECT id FROM Transactions WHERE id = " + transactionID + ";";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			return !rs.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static float queryPrice(Statement stmt, int item) {
		String query = "SELECT price FROM item WHERE upc = " + item + ";";
		try {
			ResultSet returnRS = stmt.executeQuery(query);
			return returnRS.getFloat("price");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Null returning");
		return 0;
	}

	public static float calculatePrice(float price, int qty) {
		return price * qty;
	}

	public static void insertItemBought(Statement stmt, ItemBought newItemBought) {// int item, int transactionID, int
																					// qty, float price) {
		String query = "INSERT INTO ItemsBought VALUES(" + newItemBought.getItemID() + ", "
				+ newItemBought.getTransactionID() + ", " + newItemBought.getQty() + ", "
				+ newItemBought.getTransactionPrice()
				+ ");";
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Transaction createTransaction(int customerID, int id) {
		Transaction returnTransaction = new Transaction();
		returnTransaction.setId(id);
		returnTransaction.createDateOfPurchase();
		System.out.println("Main dop: " + returnTransaction.getDateOfPurchase());
		returnTransaction.setCustomerID(customerID);
		return returnTransaction;
	}

	public static void insertTransaction(Statement stmt, Transaction newTransaction) {
		System.out.println("insert dop: " + newTransaction.getDateOfPurchase());
		String query = "INSERT INTO Transactions VALUES(" + newTransaction.getId() + ", "
				+ newTransaction.getDateOfPurchase() + ", " + newTransaction.getCustomerID() + ");";
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Apply Coupons to Transactions
	// If the customer has coupons downloaded the
	// coupons will be applied so only the modified
	// item price shows up on the transaction assuming the
	// condition of the individual coupons were met.
	public static void applyCoupon(Statement stmt, int transactionID, int customerID) { // TODO: Is this void?

		// Get list of coupons that customer can apply
		// ResultSet custCoupons = getCustomerCoupons(stmt, custId);

		// Get coupons info using

		// System.out.println("\tCOUPON LIST: \n#\tItem #\t$ off\tCount Req.");
		// while (custCoupons.next()) {
		// String query1 = "SELECT * FROM Coupons WHERE id = " +
		// custCoupons.getInt("id");
		// ResultSet currentCoupon = getCouponInfo(stmt, custCoupons.getInt("id"));

		// // Print entire list of coupons available to customer
		// System.out.println(custCoupons.getInt("id") + "\t" +
		// currentCoupon.getInt("item") + "\t"
		// + currentCoupon.getInt("amountOff") + "\t" +
		// currentCoupon.getInt("itemCountReq"));
		// }

		// Have user select coupon to use, then gather available transactions that the
		// user can select from

		String query = "SELECT * from ListOfCoupons WHERE customerID = " + customerID;
		int couponCount = 0;
		try {
			ResultSet rs = stmt.executeQuery(query); // Gather information into rs

			couponCount = rs.getFetchSize();
			// TODO: Fix how to find coupon Count!
			if (couponCount == 0) {
				// Error, no coupons found
				System.out.println("\nERROR: No coupons found for customer #" + customerID + "!\n");
				return; // Exit this function
			}

			// Get coupons info using

			System.out.println("\tCOUPON LIST: \n#\tItem #\t$ off\tCount Req.");
			while (rs.next()) {
				String query1 = "SELECT * FROM Coupons WHERE id = " + rs.getInt("id");
				try {
					ResultSet currentCoupon = stmt.executeQuery(query1);

					// Print entire list of coupons available to customer
					System.out.println(rs.getInt("id") + "\t" + currentCoupon.getInt("item") + "\t"
							+ currentCoupon.getInt("amountOff") + "\t" + currentCoupon.getInt("itemCountReq"));

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			// By this point, couponList will contain all the coupons a customer has of size
			// couponCount
			// Now lets have the user decide which coupon they want to use

			// USER DECIDES WHICH COUPON TO USE

			// Now that we have the specific coupon in mind, we need to gather a list of
			// transactions in the customers ID for them to choose which to apply to

			// DISPLAY LIST OF TRANSACTIONS FROM CUSTOMER

			// GET USER INPUT AND SELECT TRANSACTION

			// CHECK IF COUPON IS ABLE TO APPLY TO PRODUCTS WITHIN TRANSACTION SELECTED

			// ERROR: UNABLE TO APPLY COUPON DUE TO COUPON IS FOR ITEM ID #XXXX
			// RESULT: APPLIED COUPON SUCCESSFULLY TO TRANSACTION. COUPON HAS BEEN CONSUMED.

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Total Transaction Function
	// Calculates the total for the transaction given
	public static double totalTransaction(Statement stmt, int transactionID, int customerID) {
		double total = 0;
		ResultSet rs = null;
		String query = "SELECT * from ItemsBought WHERE transactionID = " + transactionID;

		// Locate the transaction and customerID
		try {
			rs = stmt.executeQuery(query); // Gather information into rs
			// At this point, rs should have all of the queries such that transactionID is
			// correct and what we desire
			// int x = 0;
			while (rs.next()) {
				// System.out.println("[" + x + "] Item: " + rs.getString("item")); // Debug
				// line
				total += rs.getDouble("price");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Calculate total

		// Return total // Returns 0 if the transaction does not exist
		return total;

	}

	// Receive Delivery
	// Should remove The delivery and any orders associated with it from the
	// database
	// Should add the number of each item received to the stock of that item
	public static void receiveDelivery(Statement stmt, int deliveryID) {
		System.out.print("Here is the deliverID to be deleted: " + deliveryID);

	}

	// Place order by employee
	// If the employee’s permission level is 0 return a message saying they do not
	// have
	// permission and reject the order
	// If the employee does have permission then add the order to the database with
	// the order not having been added to a delivery yet
	public static void placeOrder(Statement stmt, int employeeID, int itemID, int amount) {
		// TODO: STUB
	}

	// Clear screen function
	public static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public static void printAll(ArrayList<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}

	public static ItemBought orderItem(Statement stmt) throws SQLException {
		// ItemBought newItemBought = new ItemBought();
		// newItemBought = createItemBought()
		int item;
		int customer;
		int transaction;
		int qty;
		double price;
		int input = readInteger("Enter item ID: ");
		if (itemExists(stmt, input)) {
			item = input;
			// newItemBought.setItemID(input);
			input = readInteger("Enter Customer ID: ");
			if (customerExists(stmt, input)) {
				customer = input;
				input = readInteger("Enter a transaction ID, or enter a new ID to create a new tranaction");
				transaction = input;
				if (transactionExists(stmt, input)) {
					return createItemBought(stmt, item, customer, transaction);
				} else {
					Transaction newTransaction = createTransaction(customer, transaction);
					insertTransaction(stmt, newTransaction);
					return createItemBought(stmt, item, customer, transaction);
				}
			} else {
				System.out.println("Customer not found.");
			}
		} else {
			System.out.println("Item not found.");
		}
		return null;
	}

	public static ItemBought createItemBought(Statement stmt, int item, int customer, int transaction) {
		ItemBought newItemBought = new ItemBought();
		newItemBought.setItemID(item);
		newItemBought.setCustomerID(customer);
		newItemBought.setTransactionID(transaction);
		newItemBought.setQty(readInteger("How many to purchase?: "));
		newItemBought.setPrice(queryPrice(stmt, newItemBought.getItemID()));
		newItemBought.calculatePrice();
		return newItemBought;
	}

}
