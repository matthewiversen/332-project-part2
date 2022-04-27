import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Scanner;

import org.sqlite.SQLiteDataSource;

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
        System.out.println( "Opened database successfully" );

//        String query = "CREATE TABLE IF NOT EXISTS Item(\r\n" + 
//        		"  upc INTEGER PRIMARY KEY,\r\n" + 
//        		"  department TEXT NOT NULL,\r\n" + 
//        		"  restockAmount INTEGER,\r\n" + 
//        		"  price NUMERICAL NOT NULL,\r\n" + 
//        		"  interimPrice NUMERICAL NOT NULL,\r\n" + 
//        		"  wholesalePrice NUMERICAL NOT NULL,\r\n" + 
//        		"  currentStock INTEGER NOT NULL,\r\n" + 
//        		"  supplier INT NOT NULL\r\n" + 
//        		");";

        try ( Connection conn = ds.getConnection();
              Statement stmt = conn.createStatement(); ) {
        	
        	
        	int choice = homeScreen();
            if (choice == 1) {
            	
            	Item newItem = createItem();
            	insertNewItem(newItem, stmt);
            	
            }
        	
        	
        	
        } catch ( SQLException e ) {
            e.printStackTrace();
            System.exit( 0 );
        }
        

    }
    
    public static int homeScreen() {
    	Scanner sc = new Scanner(System.in);
    	int choice = 0;
    	while(choice <= 0 || choice > 4) {
    		while (true) {
    			try {
    				System.out.println("1. Add item to database");
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
    	
    	System.out.println("Input department:");
    	newItem.setDept(sc.nextLine());
    	
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
    	
    	System.out.println(newItem.getUpc());
    	System.out.println(newItem.getDept());
    	System.out.println(newItem.getRestockAmount());
    	System.out.println(newItem.getPrice());
    	System.out.println(newItem.getSalePrice());
    	System.out.println(newItem.getWholesalePrice());
    	System.out.println(newItem.getCurrentStock());
    	System.out.println(newItem.getSupplierID());
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
    
}