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

		// Leaving some commented out query strings as examples
		// System.out.println( "Opened database successfully" );
		//
		// String query = "CREATE TABLE IF NOT EXISTS Item(\r\n" +
		// " upc INTEGER PRIMARY KEY,\r\n" +
		// " department INTEGER NOT NULL,\r\n" +
		// " restockAmount INTEGER,\r\n" +
		// " price NUMERICAL NOT NULL,\r\n" +
		// " interimPrice NUMERICAL NOT NULL,\r\n" +
		// " wholesalePrice NUMERICAL NOT NULL,\r\n" +
		// " currentStock INTEGER NOT NULL,\r\n" +
		// " supplier INT NOT NULL\r\n" +
		// ");";

		// String query = "CREATE TABLE IF NOT EXISTS ExpirationDates(\r\n"
		// + " expireDate TEXT NOT NULL,\r\n"
		// + " item INTEGER NOT NULL,\r\n"
		// + " department TEXT NOT NULL,\r\n"
		// + " PRIMARY KEY (expireDate, item),\r\n"
		// + " FOREIGN KEY (item) REFERENCES Item(upc)\r\n"
		// + " FOREIGN KEY (department) REFERENCES Item(department)\r\n"
		// + ");";
		// String query = "CREATE TABLE IF NOT EXISTS Orders("
		// + " id INTEGER NOT NULL,\r\n"
		// + " itemOrdered INTEGER NOT NULL,\r\n"
		// + " qty INTEGER NOT NULL,\r\n"
		// + " orderDate TEXT NOT NULL,\r\n"
		// + " onDelivery NUMERIC NOT NULL,\r\n"
		// + " delivery INTEGER,\r\n"
		// + " PRIMARY KEY (id),\r\n"
		// + " FOREIGN KEY (itemOrdered) REFERENCES Item(upc),\r\n"
		// + " FOREIGN KEY (delivery) REFERENCES Delivery(id)\r\n"
		// + ");";
		String query = "CREATE TABLE IF NOT EXISTS ItemsBought(\r\n"
				+ "  item INTEGER NOT NULL,\r\n"
				+ "  transactionID INTEGER NOT NULL,\r\n"
				+ "  quantity INTEGER NOT NULL,\r\n"
				+ "  price NUMERIC NOT NULL,\r\n"
				+ "  PRIMARY KEY(item, transactionID),\r\n"
				+ "  FOREIGN KEY(item) REFERENCES Item(upc),\r\n"
				+ "  FOREIGN KEY(transactionID) REFERENCES Transactions(id)\r\n"
				+ ");";

		// String insertQuery = "INSERT INTO Orders VALUES(42, 4798, 32, '2022-01-14',
		// TRUE, 2341);";
		String insertQuery = "INSERT INTO ItemsBought VALUES(3875, 89, 4, 1599.96);";
		// String insertQuery = "INSERT INTO Delivery VALUES(2341, '2022-01-17', 2,
		// 14);";
		// stmt.dropQuery = "DROP TABLE IF EXISTS Orders;";
		// String insertQuery2 = "INSERT INTO ExpirationDates VALUES('2022-04-26', 3972,
		// 1);";
		// String insertQuery3 = "INSERT INTO ExpirationDates VALUES('2022-04-28', 4912,
		// 3);";
		String selectQuery = "SELECT * FROM itemsBought;";
		
		
		
		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement();) {
			// stmt.executeUpdate(query);
			// stmt.executeUpdate(insertQuery);
			// ResultSet res = stmt.executeQuery(selectQuery);
			// while(res.next()) {
			// System.out.println(res.getInt("transactionID"));
			// System.out.println(res.getInt("item"));
			// }

			int choice = homeScreen();
			if (choice == 1) {

				Item newItem = createItem();
				insertNewItem(newItem, stmt);

			} else if (choice == 2) {
				Scanner sc = new Scanner(System.in);
				int input;
				while (true) { // This while loop controls invalid input by attempting to cast input as an
								// Integer
					try {
						System.out.println("Input a department: ");
						input = Integer.parseInt(sc.nextLine()); // Reads input from console
						break;
					} catch (NumberFormatException e) {
						System.out.println("That was not a valid number, please try again.");
					}
				}
				ResultSet rs = getDates(stmt, input); // Gets all expiration dates from the expireDate table

				ArrayList<Integer> expiringItems = fillItemsArray(rs); // Creates an arraylist of items w/ dates within
																		// 2 days of current date

				System.out.println("Items about to expire:");

				for (int i = 0; i < expiringItems.size(); i++) {
					System.out.println(expiringItems.get(i));
				}
			} else if (choice == 3) {
				int input;
				while (true) {
					try {
						Scanner sc = new Scanner(System.in);
						System.out.println("Input department number: ");
						input = Integer.parseInt(sc.nextLine());
						break;

					} catch (NumberFormatException e) {
						System.out.println("That was not a valid number, please try again.");
					}
				}

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
					for (int i = 0; i < itemsToOrder.size(); i++) {
						System.out.println(itemsToOrder.get(i)); // Print all items that need restocking
					}
					if (previousOrderIDs.size() > 0) {
						System.out.println("Previous Order IDs for these items: ");
						for (int i = 0; i < previousOrderIDs.size(); i++) {
							System.out.println(previousOrderIDs.get(i)); // Print all order IDs returned by previous SQL
																			// queries.
						}
					} else {
						System.out.println("No previous orders for these item numbers");
					}
				}
			} else if (choice == 4) {
				
				ItemBought newItemBought = new ItemBought();
				System.out.println("Enter item ID: ");

				int input = readInput();
				if (itemExists(stmt, input)) {
					newItemBought.setItemID(input);
					System.out.println("Input customer ID");
					input = readInput();
					if (customerExists(stmt, input)) {
						newItemBought.setCustomerID(input);
						System.out.println("Input a transaction ID, or enter a new ID to create a new transaction: ");
						input = readInput();
						newItemBought.setTransactionID(input);
						if (transactionExists(stmt, input)) {
							System.out.println("How many to purchase?: ");
							newItemBought.setQty(readInput());
							newItemBought.setPrice(queryPrice(stmt, newItemBought.getItemID()));
							newItemBought.calculatePrice();
							addToItemsBought(stmt, newItemBought.getItemID(), newItemBought.getTransactionID(), newItemBought.getQty(), newItemBought.getTransactionPrice());
						} else {
							Transaction newTransaction = createTransaction(newItemBought.getCustomerID(), newItemBought.getTransactionID());
							insertTransaction(stmt, newTransaction);
							System.out.println("How many to purchase?: ");
							newItemBought.setQty(readInput());
							newItemBought.setPrice(queryPrice(stmt, newItemBought.getItemID()));
							newItemBought.calculatePrice();
							addToItemsBought(stmt, newItemBought.getItemID(), newItemBought.getTransactionID(), newItemBought.getQty(), newItemBought.getTransactionPrice());
						}
					}
				} else {
					System.out.println("Item not found.");
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
		while (choice <= 0 || choice > 4) { // Input must be between 1 and 4
			while (true) {
				try {
					System.out.println("1. Add item to database");
					System.out.println("2. Get items about to expire");
					System.out.println("3. Get needed restock");
					System.out.println("4. Customer transaction");
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

	public static int readInput() {
		Scanner sc = new Scanner(System.in);
		int input;
		while (true) { // This while loop controls invalid input by attempting to cast input as an
						// Integer
			try {
				input = Integer.parseInt(sc.nextLine()); // Reads input from console
				return input;
			} catch (NumberFormatException e) {
				System.out.println("That was not a valid number, please try again.");
			}
		}
		// return input;
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
	
	public static void addToItemsBought(Statement stmt, int item, int transactionID, int qty, float price) {
		String query = "INSERT INTO ItemsBought VALUES(" + item + ", " + transactionID + ", " + qty + ", " + price + ");";
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
		returnTransaction.setCustomerID(customerID);
		return returnTransaction;
	}
	
	public static void insertTransaction(Statement stmt, Transaction newTransaction) {
		String query = "INSERT INTO Transactions VALUES(" + newTransaction.getId() + ", " + newTransaction.getDateOfPurchase() + ", " + newTransaction.getCustomerID() + ");";
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
		// TODO: STUB
	}

	// Total Transaction Function
	// Calculates the total for the transaction given
	public static int totalTransaction(Statement stmt, int transactionID, int customerID) {
		int total = 0;
		// Locate the transaction and customerID

		// Calculate total

		// Return total // Returns 0 if the transaction does not exist
		return total;

	}

	// Receive Delivery
	// Should remove The delivery and any orders associated with it from the
	// database
	// Should add the number of each item received to the stock of that item
	public static void receiveDelivery(Statement stmt, int deliveryID) {
		// TODO: STUB
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

}
