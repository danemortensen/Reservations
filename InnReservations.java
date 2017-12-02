/* Lab 8 - Inn Reservations
 * Dane Mortensen and Kartik Mendiratta */

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InnReservations {
    private static final String settings = "ServerSettings.txt";

    public static Connection connect() {
        Connection conn = null;
        String url, username, password;
        url = username = password = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(settings));
            url = br.readLine().trim();
            username = br.readLine().trim();
            password = br.readLine().trim();

            System.out.println("url = " + url);
            System.out.println("username = " + username);
            System.out.println("password = " + password);

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url + "?"
                    + "user=" + username + "&password=" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void main(String args[]) {
        Connection conn = null;

        System.out.println("connecting... ");
        conn = connect();
        System.out.println("connected");

        System.out.println("closing connection... ");
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connection closed");
    }
}
