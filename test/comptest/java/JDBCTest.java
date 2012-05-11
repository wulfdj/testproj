import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.acme.testmanager.ITestManager;
import de.acme.testmanager.entities.TestEntity;


public class JDBCTest {

	private static Connection dbConnection;
	private static long counter = 0;
	public static class CreateThread extends Thread {
		Statement stmt = null;
		boolean running = true;
		long number = 1;
		int threadNumber = 0;
		
		public CreateThread(Statement stmt , int threadNumber) {
			this.threadNumber =threadNumber; 
			this.stmt = stmt;
		}
		
		public void stopp() {
			running = false;
		}

		public void run() {
			while (running) {
				 String query = "insert into testentity (bean, text, time) values('" + number + "', 'thread: " + threadNumber + "', '" + System.currentTimeMillis() + "')"; 
			        try {
						stmt.executeUpdate(query);
						counter++;
						number++;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/testmanager", "root", "password");
		} catch (SQLException e) {
					e.printStackTrace();
					System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	
	    Statement stmt = null;
	    long startTime = System.currentTimeMillis();
//	    for(int i=0;i<1000;i++) {
//		    String query = "insert into testentity (bean, text, time) values('" + i + "', 'text: " + i + "', '" + System.currentTimeMillis() + "')"; 
//			        stmt.executeUpdate(query);
//	    }
	    ArrayList<CreateThread> threads = new ArrayList<CreateThread>();
	    for(int i=0; i<4; i++) {
	    	stmt = dbConnection.createStatement();
	    CreateThread thread = new CreateThread(stmt, i + 1);
	    threads.add(thread);
	    }
	    for(CreateThread thread: threads) {
	    thread.start();
	    }
	    Thread.sleep(10000);
	    for(CreateThread thread: threads) {
		    thread.stopp();
		    }
	    System.out.println("Took: " + (System.currentTimeMillis() - startTime));
	    System.out.println("Counter: " + counter);
	}

}
