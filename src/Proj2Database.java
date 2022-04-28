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
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(0);
        }
//        System.out.println( "Opened database successfully" );
//
//        String query = "CREATE TABLE IF NOT EXISTS Item(\r\n" + 
//        		"  upc INTEGER PRIMARY KEY,\r\n" + 
//        		"  department INTEGER NOT NULL,\r\n" + 
//        		"  restockAmount INTEGER,\r\n" + 
//        		"  price NUMERICAL NOT NULL,\r\n" + 
//        		"  interimPrice NUMERICAL NOT NULL,\r\n" + 
//        		"  wholesalePrice NUMERICAL NOT NULL,\r\n" + 
//        		"  currentStock INTEGER NOT NULL,\r\n" + 
//        		"  supplier INT NOT NULL\r\n" + 
//        		");";
        
//        String query = "CREATE TABLE IF NOT EXISTS ExpirationDates(\r\n"
//        		+ "  expireDate TEXT NOT NULL,\r\n"
//        		+ "  item INTEGER NOT NULL,\r\n"
//        		+ "  department TEXT NOT NULL,\r\n"
//        		+ "  PRIMARY KEY (expireDate, item),\r\n"
//        		+ "  FOREIGN KEY (item) REFERENCES Item(upc)\r\n"
//        		+ "  FOREIGN KEY (department) REFERENCES Item(department)\r\n"
//        		+ ");";
        
//        String insertQuery = "INSERT INTO ExpirationDates VALUES('2022-04-30', 5869, 1);";
//        String insertQuery2 = "INSERT INTO ExpirationDates VALUES('2022-04-26', 3972, 1);";
//        String insertQuery3 = "INSERT INTO ExpirationDates VALUES('2022-04-28', 4912, 3);";
//        String selectQuery = "SELECT * FROM Item WHERE department = 2;";
        
        
        try ( Connection conn = ds.getConnection();
              Statement stmt = conn.createStatement(); ) {
//        		stmt.executeUpdate(query);
        	
        	int choice = homeScreen();
            if (choice == 1) {
            	
            	Item newItem = createItem();
            	insertNewItem(newItem, stmt);
            	
            } else if(choice == 2) {
            	Scanner sc = new Scanner(System.in);
            	int input;
            	while (true) { //This while loop controls invalid input by attempting to cast input as an Integer
            		try {
            			System.out.println("Input a department: ");
                    	input = Integer.parseInt(sc.nextLine()); // Reads input from console
                    	break;
            		} catch(NumberFormatException e) {
	    	    		System.out.println("That was not a valid number, please try again.");
	            	}
            	}
            	ResultSet rs = getDates(stmt, input); //Gets all expiration dates from the expireDate table
            	
            	ArrayList<Integer> expiringItems = fillItemsArray(rs); //Creates an arraylist of items w/ dates within 2 days of current date
            	
            	System.out.println("Items about to expire:");
            	
            	for(int i = 0; i < expiringItems.size(); i++) {
            		System.out.println(expiringItems.get(i));
            	}
            } else if(choice == 3) {
            	int input;
            	while (true) {
	            	try {
	            		Scanner sc = new Scanner(System.in);
	            		System.out.println("Input department number: ");
	            		input = Integer.parseInt(sc.nextLine());
	            		break;
	            		
	            	} catch(NumberFormatException e) {
	    	    		System.out.println("That was not a valid number, please try again.");
	            	}
            	}
            
            	ResultSet rs = getItemsToOrder(stmt, input);
            	ArrayList<Integer> itemsToOrder = new ArrayList<Integer>(1);
            	if (rs.isClosed()) { //If the SQL query returns a closed set, the database inputed doesn't exist.
            		System.out.println("Department doesn't exist or invalid input.");
            	} else {
	            	while (rs.next()) {
	            		itemsToOrder.add(rs.getInt("upc"));
	            	}
	            	for(int i = 0; i < itemsToOrder.size(); i++) {
	            		System.out.println(itemsToOrder.get(i));
	            	}
            	}
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
            System.exit( 0 );
        }
    }
    
    public static int homeScreen() {
    	Scanner sc = new Scanner(System.in);
    	int choice = 0;
    	while(choice <= 0 || choice > 4) { //Input must be between 1 and 4
    		while (true) {
    			try {
    				System.out.println("1. Add item to database");
    				System.out.println("2. Get items about to expire");
    				System.out.println("3. Get needed restock");
    				//System.out.println("1. Add item to database");
    				choice = Integer.parseInt(sc.nextLine());
    				break;
    			}
    			catch(NumberFormatException e) {
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
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input department number: ");
	    	try {
	    		newItem.setDept(Integer.parseInt(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input restock amount: ");
	    	try {
	    		newItem.setRestockAmount(Integer.parseInt(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input standard price: ");
	    	try {
	    		newItem.setPrice(Float.parseFloat(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input sale price: ");
	    	try {
	    		newItem.setSalePrice(Float.parseFloat(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input Wholesale price: ");
	    	try {
	    		newItem.setWholesalePrice(Float.parseFloat(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input currentStock: ");
	    	try {
	    		newItem.setCurrentStock(Integer.parseInt(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
    	while (true) {
	    	System.out.println("Input supplier ID: ");
	    	try {
	    		newItem.setSupplierID(Integer.parseInt(sc.nextLine()));
	    		break;
	    	}
	    	catch(NumberFormatException e) {
	    		System.out.println("That was not a valid number");
	    	}
    	}
    	
//    	System.out.println(newItem.getUpc());
//    	System.out.println(newItem.getDept());
//    	System.out.println(newItem.getRestockAmount());
//    	System.out.println(newItem.getPrice());
//    	System.out.println(newItem.getSalePrice());
//    	System.out.println(newItem.getWholesalePrice());
//    	System.out.println(newItem.getCurrentStock());
//    	System.out.println(newItem.getSupplierID());
    	return newItem;
    }

    public static int insertNewItem(Item newItem, Statement stmt) {
    	String query = "INSERT INTO Item VALUES(" +
    			newItem.getUpc() + ", '" +
    			newItem.getDept()+ " ', " +
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
    	LocalDate targetDate = LocalDate.now().plusDays(2); //Add 2 days to the current day
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
    	ArrayList<Integer> expiringItems = new ArrayList<Integer>(1); //Create an arrayList of size 1 to be able to add to later
    	while (rs.next()) {
    		if (checkAfter(LocalDate.parse(rs.getString( "expireDate" )))) { // returns true if expire date is sooner than 2 days from the current date
    			if(!expiringItems.contains(rs.getInt("item"))) // Only adds to arraylist if the item isn't already inside
    				expiringItems.add(rs.getInt("item"));
    		}
    	}
    	return expiringItems;
    }

    public static ResultSet getItemsToOrder(Statement stmt, int department) {
    	String query = "SELECT upc FROM Item WHERE department = " + department + " AND restockAmount > currentStock;"; // To execute a SQL command, save in a string and then pass the queryString as an arg in an execute function
    	try {
			ResultSet returnRS = stmt.executeQuery(query);
			return returnRS;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	System.out.println("Null returning");
    	return null;
    	
    }
}
