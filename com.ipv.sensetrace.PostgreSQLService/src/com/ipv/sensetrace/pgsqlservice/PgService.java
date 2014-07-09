package com.ipv.sensetrace.pgsqlservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.util.PSQLException;

import com.ipv.sensetrace.pgsqlservice.PgService;

public class PgService implements IPgService {

	TimeFormat timeformat = new TimeFormat();

	Connection con = null;
	Connection con_err = null;
	Connection con_jtalis = null;

	java.sql.Statement st = null;
	java.sql.Statement st_err = null;
	java.sql.Statement st_jtalis = null;

	// PreparedStatement ps_jtalis;
	PreparedStatement ps_con;

	ResultSet rs = null;

	// ResultSet rs_err = null;
	// ResultSet rs_jtalis = null;

	public void StartCopyFromCSV() {
		try {

			st.execute("COPY \"public\".\"Rawdata\"(timestamp,sensorid,value)"
					+ "FROM '/tmpfs/TempData.csv'" + "WITH DELIMITER ','"
					+ "CSV HEADER");
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object GetData(String date, String sensorid, boolean corrected)
			throws SQLException {
		// System.out.println("SQLQuery: " + date);
		float result;
		ResultSet rs_jtalis;
		if (!corrected) {
			rs_jtalis = st_jtalis.executeQuery("SELECT value  FROM \"Rawdata\""
					+ " where sensorid='" + sensorid + "' and timestamp ='"
					+ date + "' order by timestamp");
		} else {
			rs_jtalis = st_jtalis
					.executeQuery("SELECT value_cor  FROM \"measurement\""
							+ " where sensorid='" + sensorid
							+ "' and timestamp ='" + date
							+ "' order by timestamp");
		}
		if (rs_jtalis.next() && rs_jtalis.getString(1) != null) {
			result = rs_jtalis.getFloat(1);
			rs_jtalis.close();
			return result;

		}
		rs_jtalis.close();
		// rs_jtalis.next();
		return null;

		// rs.close();
		/*
		 * tagid="$(mysql pv-data -u root -p"mysql" -BNe"SELECT tagid FROM
		 * dataloggertag where tagname= '$tagname'and partitionid=
		 * '$partitionid'")"; echo $tagid wird exportiert;
		 * file=/tmp/$locname"/"$module"/"$tagname".csv" echo $file mkdir -p
		 * /tmp/$locname"/"$module
		 * 
		 * $(mysql pv-data -u root -p"mysql" -BNe
		 * "SELECT timestamp, value INTO OUTFILE '$file' FROM measuredvalue_$partitionid where tagid = $tagid and timestamp > '$from_time' and timestamp < '$til_time' order by timestamp"
		 * );
		 */

	}

	public String GetLastImportDate() {
		try {
			rs.close();
			/*
			 * rs = st.executeQuery(
			 * "SELECT distinct value,timestamp FROM \"public\".\"Rawdata\" where sensorid='"
			 * + postgresid +
			 * "'where timestamp>2013-07-20 00:00:00' and timestamp<2013-08-01 00:00:00' order by timestamp desc LIMIT 1"
			 * );
			 */
			System.out
					.println("SELECT timestamp FROM \"public\".\"LastImportDate\"");
			rs = st.executeQuery("SELECT timestamp FROM \"public\".\"LastImportDate\"");

			// System.out.println("GetLastValue");

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.WARNING, ex.getMessage(), ex);
			return null;
		}

		GotoNextElement();
		// rs.close();
		// System.out.println("return value: " + GetElement("value"));
		System.out.println("timestamp: " + GetElement("timestamp"));
		return GetElement("timestamp");

	}

	public void SetLastImportDate(String timestamp) {
		if (timestamp != null) {
			try {

				// System.out.println("INSERT into \"public\".\"LastImportDate\" values ('"
				// + timestamp+"')");
				// date +"'," + sensorid + "," + value + ")");
				st.execute("DELETE FROM \"public\".\"LastImportDate\"");
				st.execute("INSERT into \"public\".\"LastImportDate\" values ('"
						+ timestamp + "')");
				con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Exception exception = e;
				while (((PSQLException) exception).getNextException() != null) {
					System.out.println(((PSQLException) exception)
							.getNextException()); // whatever you want to print
													// out
													// of exception
					exception = ((PSQLException) exception).getNextException();
				}
			}
		}

	}

	public void FetchData(String range, String datefrom, String dateto,
			String sensorid) throws SQLException {

		if (range.equals("1sec")) {
			rs = st.executeQuery("SELECT value, timestamp  FROM \"Rawdata\""
					+ " where sensorid='" + sensorid + "' and timestamp >='"
					+ datefrom + "' and timestamp <'" + dateto
					+ "' order by timestamp");
		} else if (range.equals("1min")) {
			System.out.println("SELECT value, timestamp  FROM \"Data_1m_avg\""
					+ " where sensorid='" + sensorid + "' and timestamp >='"
					+ datefrom + "' and timestamp <'" + dateto
					+ "' order by timestamp");
			rs = st.executeQuery("SELECT value, timestamp  FROM \"Data_1m_avg\""
					+ " where sensorid='"
					+ sensorid
					+ "' and timestamp >='"
					+ datefrom
					+ "' and timestamp <'"
					+ dateto
					+ "' order by timestamp");
		} else if (range.equals("15min")) {
			rs = st.executeQuery("SELECT value, timestamp  FROM \"Data_15m_avg\""
					+ " where sensorid='"
					+ sensorid
					+ "' and timestamp >='"
					+ datefrom
					+ "' and timestamp <'"
					+ dateto
					+ "' order by timestamp");
		} else if (range.equals("1hour")) {
			rs = st.executeQuery("SELECT value, timestamp  FROM \"Data_1h_avg\""
					+ " where sensorid='"
					+ sensorid
					+ "' and timestamp >='"
					+ datefrom
					+ "' and timestamp <'"
					+ dateto
					+ "' order by timestamp");
		} else if (range.equals("1day")) {
			System.out.println("SELECT value, timestamp  FROM \"Data_1d_avg\""
					+ " where sensorid='" + sensorid + "' and timestamp >='"
					+ datefrom + "' and timestamp <'" + dateto
					+ "' order by timestamp");
			rs = st.executeQuery("SELECT value, timestamp  FROM \"Data_1d_avg\""
					+ " where sensorid='"
					+ sensorid
					+ "' and timestamp >='"
					+ datefrom
					+ "' and timestamp <'"
					+ dateto
					+ "' order by timestamp");
		} else if (range.equals("1month")) {
			rs = st.executeQuery("SELECT value, timestamp  FROM \"Data_1month_avg\""
					+ " where sensorid='"
					+ sensorid
					+ "' and timestamp >='"
					+ datefrom
					+ "' and timestamp <'"
					+ dateto
					+ "' order by timestamp");
		}
		// rs.close();
		/*
		 * tagid="$(mysql pv-data -u root -p"mysql" -BNe"SELECT tagid FROM
		 * dataloggertag where tagname= '$tagname'and partitionid=
		 * '$partitionid'")"; echo $tagid wird exportiert;
		 * file=/tmp/$locname"/"$module"/"$tagname".csv" echo $file mkdir -p
		 * /tmp/$locname"/"$module
		 * 
		 * $(mysql pv-data -u root -p"mysql" -BNe
		 * "SELECT timestamp, value INTO OUTFILE '$file' FROM measuredvalue_$partitionid where tagid = $tagid and timestamp > '$from_time' and timestamp < '$til_time' order by timestamp"
		 * );
		 */

	}

	public String GetLastValue(String postgresid) {

		try {
			rs.close();
			/*
			 * rs = st.executeQuery(
			 * "SELECT distinct value,timestamp FROM \"public\".\"Rawdata\" where sensorid='"
			 * + postgresid +
			 * "'where timestamp>2013-07-20 00:00:00' and timestamp<2013-08-01 00:00:00' order by timestamp desc LIMIT 1"
			 * );
			 */
			System.out
					.println("SELECT distinct value,timestamp FROM \"public\".\"Registry_LastEntries\" where sensorid='"
							+ postgresid + "'");
			rs = st.executeQuery("SELECT distinct value,timestamp FROM \"public\".\"Registry_LastEntries\" where sensorid='"
					+ postgresid + "'");

			// System.out.println("GetLastValue");

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.WARNING, ex.getMessage(), ex);
			return null;
		}

		GotoNextElement();
		// rs.close();
		// System.out.println("return value: " + GetElement("value"));
		// System.out.println("Result: " + GetElement("value"));
		return GetElement("value");

	}

	public String GetLastTimestamp(String postgresid) {

		try {
			// rs.close();
			System.out
					.println("SELECT distinct timestamp FROM \"public\".\"Registry_LastEntries\" where sensorid='"
							// + postgresid +
							// "' and timestamp>'2013-07-27 00:00:00' and timestamp<'2013-08-05 00:00:00' order by timestamp desc LIMIT 1");
							+ postgresid + "'");
			System.out.println("GetLastTimestamp");
			rs = st.executeQuery("SELECT distinct timestamp FROM \"public\".\"Registry_LastEntries\" where sensorid='"
					// + postgresid +
					// "' and timestamp>'2013-07-27 00:00:00' and timestamp<'2013-08-05 00:00:00' order by timestamp desc LIMIT 1");
					+ postgresid + "'");
			System.out.println("GetLastTimestamp");
			/*
			 * rs = st.executeQuery(
			 * "SELECT distinct timestamp FROM \"public\".\"Rawdata\" where sensorid='"
			 * + postgresid + "' order by timestamp desc LIMIT 1");
			 */
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.WARNING, ex.getMessage(), ex);
			return null;
		}

		GotoNextElement();
		// rs.close();
		// System.out.println("return timestamp: " + GetElement("timestamp"));
		if (GetElement("timestamp") == null) {
			System.out
					.println("Fatal error or first import? Sensor not found in table Registry_Last_Entries!");
			// System.exit(0);
			return null;
		}
		return GetElement("timestamp");

	}

	public void GetNotUpdatedSensors() {

		try {
			rs.close();

			rs = st.executeQuery("select distinct sensorid FROM \"public\".\"Registry_LastEntries\" as r1 "
					+ "where sensorid not in (select distinct sensorid where sensorid=r1.sensorid "
					+ "and timestamp>current_date - INTERVAL '1 day') order by sensorid");
		} catch (SQLException ex) {
			// Logger lgr = Logger.getLogger(PgService.class.getName());
			// lgr.log(Level.WARNING, ex.getMessage(), ex);
		}

	}

	public void DeleteClIDsNotInList(ArrayList<String> clids) {

		String where = "";
		if (clids.size() == 1) {
			where = "clid != " + clids.get(0);
		}
		int n = 0;
		while (n < clids.size()) {
			if (n == 0) {
				where = "clid != " + clids.get(0);
			} else {
				where = where + " and clid != " + clids.get(n);
			}
			n++;
		}
		try {
			rs.close();
			System.out
					.println("select distinct clid FROM \"public\".\"Registry_Rules\"  "
							+ " where " + where);
			rs = st.executeQuery("select distinct clid FROM \"public\".\"Registry_Rules\"  "
					+ " where " + where);
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.WARNING, ex.getMessage(), ex);
		}
		System.out
				.println("Delete the following clids because they don't exists in ceprulefile, but in database: ");
		// GotoNextElement();
		ArrayList<String> clidstodelete = new ArrayList<String>();
		try {
			while (rs.next()) {
				// if (GetElement("clid") != null) {
				clidstodelete.add(GetElement("clid"));

				System.out.println("clid: " + GetElement("clid"));
				System.out.println("clidstodelete.size(): "
						+ clidstodelete.size());
				// }
			}

			rs.close();
			n = 0;
			DeleteFromCLTable(clidstodelete);
			while (n < clidstodelete.size()) {

				System.out.println("clid: " + clidstodelete.get(n));
				System.out.println("DELETE FROM \"public\".\"Classification\""
						+ "WHERE clid='" + clidstodelete.get(n) + "'");
				/*
				 * st.execute("DELETE FROM \"public\".\"Classification\"" +
				 * "WHERE clid='" + clidstodelete.get(n) + "'");
				 */

				// CLID muss natürlich auch aus der Registry gelöscht werden
				st.execute("DELETE FROM \"public\".\"Registry_Rules\""
						+ "WHERE clid='" + clidstodelete.get(n) + "'");

				n++;
			}
			con.commit();

		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}

		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// rs.close();
		// System.out.println("return timestamp: " + GetElement("timestamp"));
		return;

	}

	public String GetElement(String column) {
		try {
			return rs.getString(column);

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	public boolean GotoNextElement() {
		try {
			return rs.next();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		return false;
	}

	public void ExecuteBatch() {

		try {
			ps_con.executeBatch();
			// st.executeBatch();
		} catch (SQLException e) {
			System.out.println(e);
			Exception exception = e;
			while (((SQLException) exception).getNextException() != null) {
				System.out.println(((SQLException) exception)
						.getNextException()); // whatever you want to print out
												// of exception
				exception = ((SQLException) exception).getNextException();
			}
		}

	}

	public void Commit_Con() {
		System.out.println("Commit");

		try {
			con.commit();
		} catch (SQLException e) {
			System.out.println(e);
			Exception exception = e;
			while (((SQLException) exception).getNextException() != null) {
				System.out.println(((SQLException) exception)
						.getNextException()); // whatever you want to print out
												// of exception
				exception = ((SQLException) exception).getNextException();
			}
		}

		// System.out.println("Committed " + counts.length + " updates");
	}

	/*
	 * public void ExecuteErrorBatch2() {
	 * 
	 * try { st_err.executeBatch(); } catch (SQLException e) {
	 * System.out.println(e); Exception exception = e; while (((SQLException)
	 * exception).getNextException() != null) {
	 * System.out.println(((SQLException) exception) .getNextException()); //
	 * whatever you want to print out // of exception exception =
	 * ((SQLException) exception).getNextException(); } }
	 * 
	 * try { con_err.commit(); } catch (SQLException e) { System.out.println(e);
	 * Exception exception = e; while (((SQLException)
	 * exception).getNextException() != null) {
	 * System.out.println(((SQLException) exception) .getNextException()); //
	 * whatever you want to print out // of exception exception =
	 * ((SQLException) exception).getNextException(); } }
	 * 
	 * // System.out.println("Committed " + counts.length + " updates"); }
	 */

	/*
	 * public void ExecuteJtalisBatch() {
	 * 
	 * try { ps_jtalis.executeBatch(); } catch (SQLException e) {
	 * System.out.println(e); Exception exception = e; while (((SQLException)
	 * exception).getNextException() != null) {
	 * System.out.println(((SQLException) exception) .getNextException()); //
	 * whatever you want to print out // of exception exception =
	 * ((SQLException) exception).getNextException(); } }
	 * 
	 * 
	 * 
	 * try { con_jtalis.commit(); } catch (SQLException e) {
	 * System.out.println(e); Exception exception = e; while (((SQLException)
	 * exception).getNextException() != null) {
	 * System.out.println(((SQLException) exception) .getNextException()); //
	 * whatever you want to print out // of exception exception =
	 * ((SQLException) exception).getNextException(); } }
	 * 
	 * // System.out.println("Committed " + counts.length + " updates"); }
	 */

	public boolean CreateConnection(String cs, String user, String pwd,
			String resolution) {
		// String url = "jdbc:mysql://" + "129.69.22.115/pv-data";
		// String user = "pv-data";
		// String password = "mysql";
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception e) {
			// your error handling code goes here
		}

		/*
		 * try { Thread.currentThread().getContextClassLoader().loadClass(
		 * "org.postgresql.Driver"); } catch (ClassNotFoundException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */

		try {
			// con = DriverManager.getConnection(url, user, password);
			con = DriverManager.getConnection(cs, user, pwd);
			//
			st = con.createStatement();
			con.setAutoCommit(false);
			// Turn use of the cursor on.
			st.setFetchSize(50);
			// Streaming Mode

			/*
			 * st = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
			 * java.sql.ResultSet.CONCUR_READ_ONLY);
			 */
			// st.setFetchSize(Integer.MIN_VALUE);
			rs = st.executeQuery("SELECT VERSION()");

			if (rs.next()) {
				System.out
						.println("Connected to Server with PostgreSQL-Version: "
								+ rs.getString(1));
			}
			rs.close();
			// st.close();
			// Handler for bulk insert into Rawdata
			String sql = "";
			if (resolution.contains("1sec")) {
				sql = "INSERT INTO public.\"Rawdata\" (timestamp, sensorid, value) values (?::timestamp, ?::smallint, ?::real)";
			}
			// Etalis can work with 1 min data or one second data at maximum
			// resolution
			else if (resolution.contains("1min")) {
				sql = "INSERT INTO public.\"Data_1m_avg\" (timestamp, sensorid, value) values (?::timestamp, ?::smallint, ?::real)";
			} else {
				System.out.println("Error: Resolution must be 1min or 1sec.");
				System.exit(0);
			}
			ps_con = con.prepareStatement(sql);
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		/* Create a second connection for error queries */
		CreateStaticTestConnection(cs, user, pwd);
		CreateJtalisConnection(cs, user, pwd);
		return true;
	}

	private boolean CreateStaticTestConnection(String cs, String user,
			String pwd) {
		ResultSet rs_err;
		// String url = "jdbc:mysql://" + "129.69.22.115/pv-data";
		// String user = "pv-data";
		// String password = "mysql";
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception e) {
			// your error handling code goes here
		}

		try {
			// con = DriverManager.getConnection(url, user, password);
			con_err = DriverManager.getConnection(cs, user, pwd);
			//
			st_err = con_err.createStatement();
			con_err.setAutoCommit(false);
			// Turn use of the cursor on.
			st_err.setFetchSize(50);
			// Streaming Mode

			// st.setFetchSize(Integer.MIN_VALUE);
			rs_err = st_err.executeQuery("SELECT VERSION()");

			if (rs_err.next()) {
				System.out
						.println("Connected to Server with PostgreSQL-Version: "
								+ rs_err.getString(1));
			}
			rs_err.close();
			// st.close();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		return true;
	}

	private boolean CreateJtalisConnection(String cs, String user, String pwd) {
		// String url = "jdbc:mysql://" + "129.69.22.115/pv-data";
		// String user = "pv-data";
		// String password = "mysql";
		ResultSet rs_jtalis;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception e) {
			// your error handling code goes here
		}

		/*
		 * try { Thread.currentThread().getContextClassLoader().loadClass(
		 * "org.postgresql.Driver"); } catch (ClassNotFoundException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */

		try {
			// con = DriverManager.getConnection(url, user, password);
			con_jtalis = DriverManager.getConnection(cs, user, pwd);
			//
			st_jtalis = con_jtalis.createStatement();
			con_jtalis.setAutoCommit(false);
			// Turn use of the cursor on.
			st_jtalis.setFetchSize(50);
			// Streaming Mode
			// Handler for bulk insert into Errortable
			// String sql =
			// "INSERT INTO public.\"ErrorData\" (timestamp, sensorid, value) values (?::timestamp, ?::smallint, ?::real)";
			// ps_jtalis = con_jtalis.prepareStatement(sql);

			/*
			 * st = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
			 * java.sql.ResultSet.CONCUR_READ_ONLY);
			 */
			// st.setFetchSize(Integer.MIN_VALUE);
			rs_jtalis = st_jtalis.executeQuery("SELECT VERSION()");

			if (rs_jtalis.next()) {
				System.out
						.println("Connected to Server with PostgreSQL-Version: "
								+ rs_jtalis.getString(1));
			}
			rs_jtalis.close();
			// st.close();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(PgService.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		return true;
	}

	String LastValue = null;
	String LastTimestamp = null;
	String LastSensorID = null;

	@Override
	public void AddValueToBatch(String timestamp, String sensorid, String value) {
		try {
			ps_con.setString(1, timestamp);
			ps_con.setString(2, sensorid);
			ps_con.setString(3, value);
			ps_con.addBatch();
			/*
			 * st.addBatch("INSERT into \"public\".\"Rawdata\" values ('" +
			 * timestamp + "'," + sensorid + "," + value + ")");
			 */
		} catch (SQLException exception) {
			// TODO Auto-generated catch block
			// exception.printStackTrace();
			while (exception.getNextException() != null) {
				System.out.println(exception.getNextException());
				exception = exception.getNextException();
			}
		}
		LastTimestamp = timestamp;
		LastValue = value;
		LastSensorID = sensorid;

	}

	public void AddCorrectedSensorToBatchError(long from, long to,
			String sensorid, String value) {
		try {
			st_err.addBatch("delete from public.\"ErrorData\" where timestamp>= '"
					+ timeformat.ConvertMillisecondsToSQLTime(from)
					+ "' and timestamp<='"
					+ timeformat.ConvertMillisecondsToSQLTime(to)
					+ "' and sensorid=" + sensorid);
			// ExecuteErrorBatch();
			st_err.executeBatch();
			con_err.commit();
			String sql = "INSERT INTO public.\"ErrorData\" (timestamp, sensorid, value) values (?::timestamp, ?::smallint, ?::real)";
			PreparedStatement ps = con_err.prepareStatement(sql);

			int n = 0;
			// System.out.println("From: " + from + " to " + to);
			while (from <= to) {

				ps.setString(1, timeformat.ConvertMillisecondsToSQLTime(from));
				ps.setString(2, sensorid);
				ps.setString(3, value);
				ps.addBatch();

				if (n > 10000) {
					ps.executeBatch();
					n = 0;
				}
				n++;
				from = from + 1000;
			}

			ps.executeBatch(); // insert remaining records
			ps.close();
			con_err.commit();

			/*
			 * st_err.addBatch("delete from public.\"ErrorData\" where timestamp= '"
			 * + time + "' and sensorid=" + sensorid );
			 * st_err.addBatch("INSERT INTO public.\"ErrorData\" values( '" +
			 * time + "', " + sensorid + ", " + value + ")");
			 */

			// date +"'," + sensorid + "," + value + ")");

		} catch (SQLException exception) {
			// TODO Auto-generated catch block
			// exception.printStackTrace();
			while (exception.getNextException() != null) {
				System.out.println(exception.getNextException());
				exception = exception.getNextException();
			}

			/*
			 * Exception exception = e; while (((PSQLException)
			 * exception).getNextException() != null) {
			 * System.out.println(((PSQLException) exception)
			 * .getNextException()); // whatever you want to print out // of
			 * exception exception = ((PSQLException)
			 * exception).getNextException(); }
			 */
		}
	}

	public void AddCorrectedSensorToBatchErrorByUser(String from, String to,
			String sensorid, String value) {
		long from_l = timeformat.ConvertSQLTimeToTimestamp(from);
		long to_l = timeformat.ConvertSQLTimeToTimestamp(to);
		try {
			st_err.addBatch("delete from public.\"ErrorData_User\" where timestamp>= '"
					+ from
					+ "' and timestamp<='"
					+ to
					+ "' and sensorid="
					+ sensorid);
			// ExecuteErrorBatch();
			st_err.executeBatch();
			con_err.commit();
			String sql = "INSERT INTO public.\"ErrorData_User\" (timestamp, sensorid, value) values (?::timestamp, ?::smallint, ?::real)";
			PreparedStatement ps = con_err.prepareStatement(sql);

			int n = 0;
			// System.out.println("From: " + from + " to " + to);
			while (from_l <= to_l) {

				ps.setString(1, timeformat.ConvertMillisecondsToSQLTime(from_l));
				ps.setString(2, sensorid);
				ps.setString(3, value);
				ps.addBatch();

				if (n > 10000) {
					ps.executeBatch();
					n = 0;
				}
				n++;
				from_l = from_l + 1000;
			}

			ps.executeBatch(); // insert remaining records
			ps.close();
			con_err.commit();

		} catch (SQLException exception) {
			// TODO Auto-generated catch block
			// exception.printStackTrace();
			while (exception.getNextException() != null) {
				System.out.println(exception.getNextException());
				exception = exception.getNextException();
			}

		}
	}

	public void AddCorrectedSensorToJtalisBatch(long from, long to,
			String sensorid, String value) {
		try {
			st_jtalis
					.addBatch("delete from public.\"ErrorData\" where timestamp>= '"
							+ timeformat.ConvertMillisecondsToSQLTime(from)
							+ "' and timestamp<='"
							+ timeformat.ConvertMillisecondsToSQLTime(to)
							+ "' and sensorid=" + sensorid);
			// ExecuteErrorBatch();
			st_jtalis.executeBatch();
			con_jtalis.commit();
			String sql = "INSERT INTO public.\"ErrorData\" (timestamp, sensorid, value) values (?::timestamp, ?::smallint, ?::real)";
			PreparedStatement ps = con_jtalis.prepareStatement(sql);

			int n = 0;
			// System.out.println("From: " + from + " to " + to);
			while (from <= to) {

				ps.setString(1, timeformat.ConvertMillisecondsToSQLTime(from));
				ps.setString(2, sensorid);
				ps.setString(3, value);
				ps.addBatch();

				if (n > 10000) {
					ps.executeBatch();
					n = 0;
				}
				n++;
				from = from + 1000;
			}

			ps.executeBatch(); // insert remaining records
			ps.close();
			con_jtalis.commit();

		} catch (SQLException exception) {
			// TODO Auto-generated catch block
			// exception.printStackTrace();
			while (exception.getNextException() != null) {
				System.out.println(exception.getNextException());
				exception = exception.getNextException();
			}

		}
	}

	public void AddClRuleToBatch(long from, long to, String clid) {
		try {
			st_jtalis
					.addBatch("delete from public.\"Classification\" where timestamp>= '"
							+ timeformat.ConvertMillisecondsToSQLTime(from)
							+ "' and timestamp<='"
							+ timeformat.ConvertMillisecondsToSQLTime(to)
							+ "' and clid=" + clid);
			// ExecuteErrorBatch();
			st_jtalis.executeBatch();
			con_jtalis.commit();
			String sql = "INSERT INTO public.\"Classification\" (timestamp, clid) values (?::timestamp, ?::smallint)";
			PreparedStatement ps = con_jtalis.prepareStatement(sql);

			int n = 0;
			// System.out.println("From: " + from + " to " + to);
			while (from <= to) {

				ps.setString(1, timeformat.ConvertMillisecondsToSQLTime(from));
				ps.setString(2, clid);
				ps.addBatch();

				if (n > 10000) {
					ps.executeBatch();
					n = 0;
				}
				n++;
				from = from + 1000;
			}

			ps.executeBatch(); // insert remaining records
			ps.close();
			con_jtalis.commit();
			/*
			 * st_err.addBatch("delete from public.\"ErrorData\" where timestamp= '"
			 * + time + "' and sensorid=" + sensorid );
			 * st_err.addBatch("INSERT INTO public.\"ErrorData\" values( '" +
			 * time + "', " + sensorid + ", " + value + ")");
			 */

			// date +"'," + sensorid + "," + value + ")");

		} catch (SQLException exception) {
			// TODO Auto-generated catch block
			// exception.printStackTrace();
			while (exception.getNextException() != null) {
				System.out.println(exception.getNextException());
				exception = exception.getNextException();
			}

			/*
			 * Exception exception = e; while (((PSQLException)
			 * exception).getNextException() != null) {
			 * System.out.println(((PSQLException) exception)
			 * .getNextException()); // whatever you want to print out // of
			 * exception exception = ((PSQLException)
			 * exception).getNextException(); }
			 */
		}
	}

	/*
	 * public void AddClRuleToBatchold(String time, String clid) { try {
	 * 
	 * st_jtalis
	 * .addBatch("INSERT INTO public.\"Classification\" SELECT DISTINCT '" +
	 * time + "'::timestamp, '" + clid + "'::smallint " +
	 * " WHERE NOT EXISTS (SELECT * FROM public.\"Classification\"" +
	 * " WHERE clid= '" + clid + "' and timestamp='"
	 * 
	 * 
	 * // date +"'," + sensorid + "," + value + ")"); } catch (SQLException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); Exception
	 * exception = e; while (((PSQLException) exception).getNextException() !=
	 * null) { System.out.println(((PSQLException) exception)
	 * .getNextException()); // whatever you want to print out // of exception
	 * exception = ((PSQLException) exception).getNextException(); } } }
	 */

	public void DeleteFromErrorTableByUser(ArrayList<String> errorids,
			String timefrom, String timeto) {
		int n = 0;
		while (n < errorids.size()) {
			System.out.println("Delete the following id from ErrorTable:"
					+ errorids.get(n));
			// System.out.println(clids[n]);

			try {

				st.execute("DELETE FROM \"public\".\"ErrorData\""
						+ "WHERE sensorid='" + errorids.get(n)
						+ "' and timestamp>='" + timefrom
						+ "' and timestamp<='" + timeto + "'");
				st.execute("DELETE FROM \"public\".\"ErrorData_User\""
						+ "WHERE sensorid='" + errorids.get(n)
						+ "' and timestamp>='" + timefrom
						+ "' and timestamp<='" + timeto + "'");
				con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n++;
		}

	}

	public void DeleteFromCLTable(ArrayList<String> clids) {
		int n = 0;
		while (n < clids.size()) {
			System.out
					.println("Delete the following id from ClassificationTable:"
							+ clids.get(n));
			// System.out.println(clids[n]);

			try {

				st.execute("DELETE FROM \"public\".\"Classification\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1m_avg\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1m_avg_cover\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_15m_avg\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_15m_avg_cover\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1h_avg\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1h_avg_cover\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1d_avg\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1d_avg_cover\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1month_avg\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1month_avg_cover\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1y_avg\""
						+ " WHERE clid='" + clids.get(n) + "'");
				st.execute("DELETE FROM \"public\".\"Classification_1y_avg_cover\""
						+ " WHERE clid='" + clids.get(n) + "'");

				con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n++;
		}

	}

	public boolean CheckIfAVGExists(ArrayList<String> sensorids,
			String timefrom, String timeto, ArrayList<String> only) {

		// Remove all ids from sensorids that are in except list
		if (only.size() > 0) {
			sensorids = only;
		}
		System.out.println("Check AVG for sensorids: " + sensorids);
		String where_str;
		int n = 0;
		// Build String with timeintervall

		while (n < sensorids.size()) {
			where_str = " where sensorid='" + sensorids.get(n)
					+ "' and timestamp<'" + timeto + "' and timestamp>='"
					+ timefrom + "'";

			try {

				/*
				 * System.out.println("Select * from \"Data_1d_avg\"   " +
				 * where_str + " Limit 1");
				 */

				rs = st.executeQuery("Select * from \"Data_1d_avg\"   "
						+ where_str + " Limit 1");
				if (!rs.next()) {
					rs.close();
					System.out.println("No avg exists for sensor: "
							+ sensorids.get(n));
					return false;

				} else {
					rs.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n++;
		}

		return true;

	}

	public void RegisterRule(String clid, String rule) {

		try {

			// System.out.println("INSERT into \"public\".\"LastImportDate\" values ('"
			// + timestamp+"')");
			// date +"'," + sensorid + "," + value + ")");
			st.execute("DELETE FROM \"public\".\"Registry_Rules\" where clid="
					+ clid);
			/*
			 * System.out.println(
			 * "INSERT into \"public\".\"Registry_Rules\" values ('" + rule +
			 * "','" + clid + "')");
			 */
			st.execute("INSERT into \"public\".\"Registry_Rules\" values ('"
					+ rule + "','" + clid + "')");
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Exception exception = e;
			while (((PSQLException) exception).getNextException() != null) {
				System.out.println(((PSQLException) exception)
						.getNextException()); // whatever you want to print
												// out
												// of exception
				exception = ((PSQLException) exception).getNextException();
			}
		}

	}

	public String GetRegisteredRule(String clid, String rule) {

		int n = 0;
		// Build String with timeintervall
		try {

			rs = st.executeQuery("Select * from \"Registry_Rules\"   "
					+ "where clid=" + clid);
			if (!rs.next()) {
				rs.close();
				System.out.println("Rule not registered yet.");
				return null;

			} else {
				return rs.getString("rule");
				// rs.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rule;

	}

	public ArrayList<String> SensorsWithAvg(String t0, String t1, long days) {
		System.out.println("days:" + days);
		ArrayList<String> SensorsWithAvg = new ArrayList<String>();
		int n = 0;
		// Build String with timeintervall
		try {

			rs = st.executeQuery("select * from (select sensorid,count(*) as c from \"Data_1d_avg\"   "
					+ "where timestamp >= '"
					+ t0
					+ "' and timestamp<'"
					+ t1
					+ "' group by sensorid) as a where c >=" + days);
			while (rs.next()) {

				// Add to list
				// System.out.println("add to list:" +
				// rs.getString("sensorid"));
				SensorsWithAvg.add(rs.getString("sensorid"));
				// rs.close();
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SensorsWithAvg;
	}

	public void CalculateAverages(String calc_min_data,
			ArrayList<String> sensoridstouse,
			ArrayList<String> sensoridsnottouse, String timefrom,
			String timeto, ArrayList<String> sensors) {

		boolean execsql = false;

		if ((sensoridstouse != null) && (sensoridsnottouse != null)) {
			System.out
					.println("Conflict: One of the list sensoridstouse and sensoridsnottouseis has to be null!");
			System.exit(0);

		}

		// " sensorid >= " + sensorrange[0]
		// + " and sensorid <= " + sensorrange[1] + " and ";
		String where_str;
		// Build String with timeintervall
		/*
		 * where_str_global = "";/* " data.timestamp<'" + timeto +
		 * "-02-01 00:00:00' and data.timestamp>='" + timefrom +
		 * "-01-01 00:00:00'";
		 */

		int m = 0;
		int actual_sensor = 0;

		while (m < sensors.size()) {
			actual_sensor = Integer.parseInt(sensors.get(m));
			String where_str_global = "";
			// Build String for sensorquery
			if (sensoridstouse != null) {
				execsql = true;
				// In order to run the loop just once
				m = sensors.size();
				// m = Integer.parseInt(sensorrange[1]);
				// If one sensor

				// System.out
				// .println("Sensorids to use: " + sensoridstouse.get(0));
				if (sensoridstouse.size() == 1) {
					where_str_global = where_str_global + " sensorid="
							+ sensoridstouse.get(0) + " and ";
				}

				// If multiple sensors
				if (sensoridstouse.size() > 1) {
					where_str_global = where_str_global + " (sensorid="
							+ sensoridstouse.get(0);

					int n = 1;
					while (n < sensoridstouse.size()) {
						where_str_global = where_str_global + " or sensorid="
								+ sensoridstouse.get(n);
						n++;
					}
					where_str_global = where_str_global + ") and ";
				}
			}

			// Build String for sensoridsnottouse
			if (sensoridsnottouse != null) {

				// System.out.println("First Sensorids not to use: "
				// + sensoridsnottouse.get(0));

				if (sensoridsnottouse.contains(String.valueOf(actual_sensor))) {
					execsql = false;
				} else {
					where_str_global = where_str_global + " sensorid="
							+ actual_sensor + " and ";
					execsql = true;
				}

			}

			// If no options given...
			if ((sensoridstouse == null) && (sensoridsnottouse == null)) {
				System.out.println("Calculate averages for all sensors!");
				// System.exit(0);
				execsql = true;
				where_str_global = "";
				// In order to exit loop...
				m = sensors.size();

			}

			if (execsql) {
				try {

					if (!calc_min_data.contains("1min")) {
						where_str = where_str_global + " data.timestamp<"
								+ "ts_round('" + timeto
								+ "',60) + interval '1min'"
								+ " and data.timestamp>=" + "ts_round('"
								+ timefrom + "',60)";
						System.out

						.println("DELETE FROM \"Data_1m_avg\" as data where"
								+ where_str);
						st.execute("DELETE FROM \"Data_1m_avg\" as data where"
								+ where_str);
						con.commit();
						System.out.println("Deleted old 1m-avg-data");
						System.out
								.println("Insert into \"Data_1m_avg\""
										+ " select ts_round(data.timestamp,60) as timeg, data.sensorid,  avg(data.value_cor) "
										+ "from \"errorfilter\" as data where"
										+ where_str
										+ " group by timeg,sensorid order by timeg");
						st.execute("Insert into \"Data_1m_avg\""
								+ " select ts_round(data.timestamp,60) as timeg, data.sensorid,  avg(data.value_cor) "
								+ "from \"errorfilter\" as data where"
								+ where_str
								+ " group by timeg,sensorid order by timeg");
						con.commit();
						System.out.println("Calculated 1m-avg-data");
					}
					/*
					 * try { Thread.sleep(3); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */

					where_str = where_str_global + " data.timestamp<"
							+ "ts_round('" + timeto
							+ "',900) + interval '15min'"
							+ " and data.timestamp>=" + "ts_round('" + timefrom
							+ "',900)";
					st.execute("DELETE FROM \"Data_15m_avg\" as data where"
							+ where_str);
					con.commit();
					System.out.println("Deleted old 15m-avg-data");
					st.execute("Insert into \"Data_15m_avg\""
							+ " select ts_round(data.timestamp,900) as timeg, data.sensorid,  avg(data.value) "
							+ "from \"Data_1m_avg\" as data where" + where_str
							+ " group by timeg,sensorid order by timeg");
					con.commit();
					System.out.println("Calculated 15m-avg-data");
					/*
					 * try { Thread.sleep(3); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */

					where_str = where_str_global + " data.timestamp<"
							+ "date_trunc('hour', timestamp'" + timeto
							+ "') +interval '1hour'" + " and data.timestamp>="
							+ "date_trunc('hour', timestamp'" + timefrom + "')";
					System.out
							.println("DELETE FROM \"Data_1h_avg\" as data where"
									+ where_str);
					st.execute("DELETE FROM \"Data_1h_avg\" as data where"
							+ where_str);
					con.commit();
					System.out.println("Deleted old 1h-avg-data");
					st.execute("Insert into \"Data_1h_avg\""
							+ " select date_trunc('hour', data.timestamp) as timeg, data.sensorid,  avg(data.value) "
							+ "from \"Data_15m_avg\" as data where" + where_str
							+ " group by timeg,sensorid order by timeg");
					con.commit();
					System.out.println("Calculated 1h-avg-data");
					/*
					 * try { Thread.sleep(3); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */

					where_str = where_str_global + " data.timestamp<"
							+ "date_trunc('day', timestamp'" + timeto
							+ "') + interval '1day'" + " and data.timestamp>="
							+ "date_trunc('day', timestamp'" + timefrom + "')";
					st.execute("DELETE FROM \"Data_1d_avg\" as data where"
							+ where_str);
					con.commit();
					System.out.println("Deleted old 1h-avg-data");
					st.execute("Insert into \"Data_1d_avg\""
							+ " select date_trunc('day', data.timestamp) as timeg, data.sensorid,  avg(data.value) "
							+ "from \"Data_1h_avg\" as data where" + where_str
							+ " group by timeg,sensorid order by timeg");
					con.commit();
					System.out.println("Calculated 1day-avg-data");
					/*
					 * try { Thread.sleep(3); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */

					where_str = where_str_global + " data.timestamp<"
							+ "date_trunc('month', timestamp'" + timeto
							+ "')  + interval '1month'"
							+ " and data.timestamp>="
							+ "date_trunc('month', timestamp'" + timefrom
							+ "')";
					System.out
							.println("DELETE FROM \"Data_1month_avg\" as data where"
									+ where_str);
					st.execute("DELETE FROM \"Data_1month_avg\" as data where"
							+ where_str);
					con.commit();
					System.out.println("Deleted old 1month-avg-data");
					System.out
							.println("Insert into \"Data_1month_avg\""
									+ " select date_trunc('month', data.timestamp) as timeg, data.sensorid,  avg(data.value) "
									+ "from \"Data_1d_avg\" as data where"
									+ where_str
									+ " group by timeg,sensorid order by timeg");
					st.execute("Insert into \"Data_1month_avg\""
							+ " select date_trunc('month', data.timestamp) as timeg, data.sensorid,  avg(data.value) "
							+ "from \"Data_1d_avg\" as data where" + where_str
							+ " group by timeg,sensorid order by timeg");
					con.commit();
					System.out.println("Calculated 1month-avg-data");
					/*
					 * try { Thread.sleep(3); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */

					where_str = where_str_global + " data.timestamp<"
							+ "date_trunc('year', timestamp'" + timeto
							+ "') + interval '1year'" + " and data.timestamp>="
							+ "date_trunc('year', timestamp'" + timefrom + "')";
					st.execute("DELETE FROM \"Data_1y_avg\" as data where"
							+ where_str);
					con.commit();
					System.out.println("Deleted old 1y-avg-data");
					st.execute("Insert into \"Data_1y_avg\""
							+ " select date_trunc('year', data.timestamp) as timeg, data.sensorid,  avg(data.value) "
							+ "from \"Data_1month_avg\" as data where"
							+ where_str
							+ " group by timeg,sensorid order by timeg");
					con.commit();
					System.out.println("Calculated 1y-avg-data");
					/*
					 * try { Thread.sleep(3); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			m++;
		}
		/*
		 * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public void DeleteFromCLTable(ArrayList<String> clids, String timefrom,
			String timeto, boolean justavgs) {
		int n = 0;
		while (n < clids.size()) {
			System.out
					.println("Delete the following id from ClassificationTable:"
							+ clids.get(n));
			// System.out.println(clids[n]);

			try {
				if (!justavgs) {
					st.execute("DELETE FROM \"public\".\"Classification\""
							+ " WHERE clid='" + clids.get(n)
							+ "' and timestamp>='" + timefrom
							+ "' and timestamp<='" + timeto + "'");
				}
				st.execute("DELETE FROM \"public\".\"Classification_1m_avg\""
						+ " WHERE clid='" + clids.get(n) + "' and timestamp>="
						+ "ts_round('" + timefrom + "',60) and timestamp<="
						+ "ts_round('" + timeto + "',60)");
				st.execute("DELETE FROM \"public\".\"Classification_1m_avg_cover\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "ts_round('"
						+ timefrom
						+ "',60) and timestamp<"
						+ "ts_round('" + timeto + "',60)");
				st.execute("DELETE FROM \"public\".\"Classification_15m_avg\""
						+ " WHERE clid='" + clids.get(n) + "' and timestamp>="
						+ "ts_round('" + timefrom + "',900) and timestamp<="
						+ "ts_round('" + timeto + "',900)");
				st.execute("DELETE FROM \"public\".\"Classification_15m_avg_cover\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "ts_round('"
						+ timefrom
						+ "',900) and timestamp<="
						+ "ts_round('" + timeto + "',900)");
				st.execute("DELETE FROM \"public\".\"Classification_1h_avg\""
						+ " WHERE clid='" + clids.get(n) + "' and timestamp>="
						+ "date_trunc('hour', timestamp '" + timefrom
						+ "') and timestamp<="
						+ "date_trunc('hour', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1h_avg_cover\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "date_trunc('hour', timestamp '"
						+ timefrom
						+ "') and timestamp<="
						+ "date_trunc('hour', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1d_avg\""
						+ " WHERE clid='" + clids.get(n) + "' and timestamp>="
						+ "date_trunc('day', timestamp '" + timefrom
						+ "') and timestamp<="
						+ "date_trunc('day', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1d_avg_cover\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "date_trunc('day', timestamp '"
						+ timefrom
						+ "') and timestamp<="
						+ "date_trunc('day', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1month_avg\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "date_trunc('month', timestamp '"
						+ timefrom
						+ "') and timestamp<="
						+ "date_trunc('month', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1month_avg_cover\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "date_trunc('month', timestamp '"
						+ timefrom
						+ "') and timestamp<="
						+ "date_trunc('month', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1y_avg\""
						+ " WHERE clid='" + clids.get(n) + "' and timestamp>="
						+ "date_trunc('year', timestamp '" + timefrom
						+ "') and timestamp<="
						+ "date_trunc('year', timestamp '" + timeto + "')");
				st.execute("DELETE FROM \"public\".\"Classification_1y_avg_cover\""
						+ " WHERE clid='"
						+ clids.get(n)
						+ "' and timestamp>="
						+ "date_trunc('year', timestamp '"
						+ timefrom
						+ "') and timestamp<="
						+ "date_trunc('year', timestamp '" + timeto + "')");

				con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n++;
		}

	}

	public void CalulateClassfikationAvgs(ArrayList<String> clids,
			String timefrom, String timeto) {
		if (clids.size() == 0) {
			System.out.println("No average calculalion needed.");
			return;
		}
		int n = 0;
		String where_str = " ";
		String where_str_wo_clid = " ";
		boolean exec_one_time = false;
		if ((timefrom != null) && (timeto != null)) {
			where_str_wo_clid = " where data.timestamp<='" + timeto
					+ "' and data.timestamp>='" + timefrom + "' and ";
		} else {
			where_str_wo_clid = " where ";
		}
		while ((n < clids.size()) || exec_one_time == false) {
			exec_one_time = true;
			where_str = where_str_wo_clid;
			// System.out.println(clids[n]);
			if (clids.size() > 0) {
				where_str = where_str + "clid=" + clids.get(n) + " ";

				System.out
						.println("Calculate avgs from the following classification:"
								+ clids.get(n));

			} else {
				System.out.println("Calculate avgs from all classifications.");
			}
			try {
				// 1minute
				System.out
						.println("Insert into \"Classification_1m_avg\""
								+ "select ts_round(data.timestamp,60) as timeg, data.clid from \"Classification\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg");
				st.execute("Insert into \"Classification_1m_avg\""
						+ "select ts_round(data.timestamp,60) as timeg, data.clid from \"Classification\" as data"
						+ where_str + "group by timeg,clid order by timeg");
				con.commit();
				// 15minute
				st.execute("Insert into \"Classification_15m_avg\""
						+ "select ts_round(data.timestamp,900) as timeg, data.clid from \"Classification_1m_avg\" as data"
						+ where_str + "group by timeg,clid order by timeg");
				con.commit();
				// 1hour
				st.execute("Insert into \"Classification_1h_avg\""
						+ "select date_trunc('hour', data.timestamp) as timeg, data.clid from \"Classification_15m_avg\" as data"
						+ where_str + "group by timeg,clid order by timeg");
				con.commit();
				// 1day
				st.execute("Insert into \"Classification_1d_avg\""
						+ "select date_trunc('day', data.timestamp) as timeg, data.clid from \"Classification_1h_avg\" as data"
						+ where_str + "group by timeg,clid order by timeg");
				con.commit();
				// 1month
				st.execute("Insert into \"Classification_1month_avg\""
						+ "select date_trunc('month', data.timestamp) as timeg, data.clid from \"Classification_1d_avg\" as data"
						+ where_str + "group by timeg,clid order by timeg");
				con.commit();
				// 1year
				st.execute("Insert into \"Classification_1y_avg\""
						+ "select date_trunc('year', data.timestamp) as timeg, data.clid from \"Classification_1month_avg\" as data"
						+ where_str + "group by timeg,clid order by timeg");
				con.commit();
				// Now put to overlap-table 1minute
				System.out
						.println("Insert into \"Classification_1m_avg_cover\""
								+ " select timeg,clid from (select ts_round(data.timestamp,60) as timeg, data.clid, count(data.clid) as length from \"Classification\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg) as tmp where length=60");

				st.execute("Insert into \"Classification_1m_avg_cover\""
						+ " select timeg,clid from (select ts_round(data.timestamp,60) as timeg, data.clid, count(data.clid) as length from \"Classification\" as data"
						+ where_str
						+ "group by timeg,clid order by timeg) as tmp where length=60");
				con.commit();
				System.out
						.println("Insert into \"Classification_15m_avg_cover\""
								+ " select timeg,clid from (select ts_round(data.timestamp,900) as timeg, data.clid, count(data.clid) as length from \"Classification_1m_avg_cover\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg) as tmp where length=15");

				st.execute("Insert into \"Classification_15m_avg_cover\""
						+ " select timeg,clid from (select ts_round(data.timestamp,900) as timeg, data.clid, count(data.clid) as length from \"Classification_1m_avg_cover\" as data"
						+ where_str
						+ "group by timeg,clid order by timeg) as tmp where length=15");
				con.commit();
				System.out
						.println("Insert into \"Classification_1h_avg_cover\""
								+ " select timeg,clid from (select date_trunc('hour', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_15m_avg_cover\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg) as tmp where length=4");

				st.execute("Insert into \"Classification_1h_avg_cover\""
						+ " select timeg,clid from (select date_trunc('hour', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_15m_avg_cover\" as data"
						+ where_str
						+ "group by timeg,clid order by timeg) as tmp where length=4");
				con.commit();
				System.out
						.println("Insert into \"Classification_1d_avg_cover\""
								+ " select timeg,clid from (select date_trunc('day', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_1h_avg_cover\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg) as tmp where length=24");

				st.execute("Insert into \"Classification_1d_avg_cover\""
						+ " select timeg,clid from (select date_trunc('day', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_1h_avg_cover\" as data"
						+ where_str
						+ "group by timeg,clid order by timeg) as tmp where length=24");
				con.commit();
				System.out
						.println("Insert into \"Classification_1month_avg_cover\""
								+ " select timeg,clid from (select date_trunc('day', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_1d_avg_cover\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg) as tmp where length=getdays(timeg)");

				st.execute("Insert into \"Classification_1month_avg_cover\""
						+ " select timeg,clid from (select date_trunc('day', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_1d_avg_cover\" as data"
						+ where_str
						+ "group by timeg,clid order by timeg) as tmp where length=getdays(timeg)");
				con.commit();
				System.out
						.println("Insert into \"Classification_1y_avg_cover\""
								+ " select timeg,clid from (select date_trunc('day', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_1month_avg_cover\" as data"
								+ where_str
								+ "group by timeg,clid order by timeg) as tmp where length=12");

				st.execute("Insert into \"Classification_1y_avg_cover\""
						+ " select timeg,clid from (select date_trunc('day', data.timestamp) as timeg, data.clid, count(data.clid) as length from \"Classification_1month_avg_cover\" as data"
						+ where_str
						+ "group by timeg,clid order by timeg) as tmp where length=12");
				con.commit();
				System.out
						.println("Finished avg calculation from classifications.");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n++;
		}

	}

	/*
	 * public void AddClassfiedDataToBatch(String datebegin, String dateend,
	 * String clid) { try {
	 * 
	 * System.out
	 * .println("INSERT INTO public.\"ClassifiedData\" SELECT DISTINCT '" + clid
	 * + "'::integer, '" + datebegin + "'::timestamp, '" + dateend +
	 * "'::timestamp WHERE NOT EXISTS (SELECT * FROM public.\"ClassifiedData\""
	 * + " WHERE public.\"ClassifiedData\".\"cepid\"= '" + clid +
	 * "' and timebegin='" + datebegin + "' " + "and timeend='" + dateend +
	 * "')"); // date +"'," + sensorid + "," + value + ")");
	 * st.addBatch("INSERT INTO public.\"ClassifiedData\" SELECT DISTINCT '" +
	 * clid + "'::integer, '" + datebegin + "'::timestamp, '" + dateend +
	 * "'::timestamp WHERE NOT EXISTS (SELECT * FROM public.\"ClassifiedData\""
	 * + " WHERE public.\"ClassifiedData\".\"cepid\"= '" + clid +
	 * "' and timebegin='" + datebegin + "' " + "and timeend='" + dateend +
	 * "')"); } catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	public void SaveLastMeasurementValue() {
		if (LastTimestamp != null) {
			try {

				st.addBatch("DELETE FROM \"public\".\"Registry_LastEntries\""
						+ "WHERE sensorid='" + LastSensorID + "'");
				st.addBatch("INSERT into \"public\".\"Registry_LastEntries\" values ('"
						+ LastTimestamp
						+ "',"
						+ LastSensorID
						+ ","
						+ LastValue
						+ ")");
				st.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Exception exception = e;
				while (((PSQLException) exception).getNextException() != null) {
					System.out.println(((PSQLException) exception)
							.getNextException()); // whatever you want to print
													// out
													// of exception
					exception = ((PSQLException) exception).getNextException();
				}
			}

		}

	}

}
