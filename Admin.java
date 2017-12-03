import java.sql.*;

public class Admin extends User {
    Connection conn;

    public String getType() {
        return "ADMIN";
    }

    public static int checkTuples(Connection conn, String table) {
        int count = -1;
        try {
            String countQuery = "SELECT COUNT(*) FROM " + table + ";";
            Statement getCount = conn.createStatement();
            ResultSet res = getCount.executeQuery(countQuery);
            res.next();
            count = res.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void currentStatus(Connection conn) {
        try {
            System.out.println("Current Status:");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, "rooms", null);
            if (res.next()) {
                System.out.print("\trooms exists, ");
            } else {
                System.out.print("\trooms does not exist, ");
            }
            res = meta.getTables(null, null, "reservations", null);
            if (res.next()) {
                System.out.println("reservations exists, ");
            } else {
                System.out.println("reservations does not exist, ");
            }

            System.out.println("\trooms: " + checkTuples(conn, "rooms"));
            System.out.println("\treservations: " 
                    + checkTuples(conn, "reservations"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Admin(Connection conn) {
        this.conn = conn;
    }
}
