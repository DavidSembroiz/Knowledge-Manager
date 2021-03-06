package data;

import domain.Mqtt;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBListener extends Thread {

	private Mqtt callback;
	private Connection conn;
	private PGConnection pgconn;
	private Statement st;
	
	public DBListener(Mqtt cb, Connection conn) {
		this.callback = cb;
		this.conn = conn;
		this.pgconn = (PGConnection) conn;
		try {
			st = this.conn.createStatement();
			st.execute("LISTEN SO_CHANNEL");
			st.execute("LISTEN ACTUATION_CHANNEL");
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.start();
	}
	
	public void run() {
		while(true) {
			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT 1");
				rs.close();
				st.close();
				PGNotification nots[] = pgconn.getNotifications();
				if (nots != null) {
					System.out.println("-------------------------------- NOTIFICATION -----------------------------------");
					callback.subscribe(getNewSOIds(nots));
					printActuations(nots);
				}
				Thread.sleep(2000);
			} catch(SQLException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	private ArrayList<String> getNewSOIds(PGNotification[] nots) {
		ArrayList<String> ids = new ArrayList<String>();
		for (PGNotification n : nots) {
			if (n.getName().toUpperCase().equals("SO_CHANNEL")) {
				String id = n.getParameter();
				callback.addId(id);
				ids.add(id);
			}
		}
		return ids;
	}
	
	private void printActuations(PGNotification[] nots) {
		for (PGNotification n : nots) {
			if (n.getName().toUpperCase().equals("ACTUATION_CHANNEL")) {
				System.out.println(n.getParameter());
			}
		}
	}
}
