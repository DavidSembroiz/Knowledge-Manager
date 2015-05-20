package domain;

import java.sql.*;

import org.postgresql.*;

public class DBListener extends Thread {

	private Mqtt callback;
	private Connection conn;
	private PGConnection pgconn;
	
	public DBListener(Mqtt cb, Connection conn) {
		this.callback = cb;
		this.conn = conn;
		this.pgconn = (PGConnection) conn;
		try {
			Statement st = this.conn.createStatement();
			st.execute("LISTEN newSO");
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.start();
	}
	
	public void run() {
		while(true) {
			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT 1");
				rs.close();
				st.close();
				PGNotification nots[] = pgconn.getNotifications();
				/*if (nots != null) {
					for (int i = 0; i < nots.length; ++i)
					System.out.println("Notification received " + nots[i].getName());
					callback.subscribe();
				}*/
				if (nots != null) callback.subscribe(nots.length);
				Thread.sleep(15000);
			} catch(SQLException | InterruptedException e) {}
		}
	}
}
