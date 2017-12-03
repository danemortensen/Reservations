/* Lab 8 - Inn Reservations
 * Dane Mortensen and Kartik Mendiratta */

import java.io.*;
import java.sql.*;
import java.util.*;

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

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url + "?"
                    + "user=" + username + "&password=" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void clearConsole() {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    public static void checkTuples(Connection conn, String table) {
        try {
            String countQuery = "SELECT COUNT(*) FROM " + table + ";";
            Statement getCount = conn.createStatement();
            ResultSet res = getCount.executeQuery(countQuery);
            res.next();
            int count = res.getInt(1);
            if (count > 0) {
                System.out.println(table + " is full");
            } else {
                System.out.println(table + " is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    public static void checkTables(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rooms = meta.getTables(null, null, "rooms", null);
            System.out.print("checking rooms... ");
            if (rooms.next()) {
                System.out.print("rooms exists... ");
            } else {
                System.out.print("rooms not found... creating table... ");
                String roomQuery = "CREATE TABLE rooms "
                    + "(RoomCode CHAR(5), RoomName VARCHAR(30), Beds INT, "
                    + "bedType VARCHAR(8), maxOcc INT, basePrice FLOAT, "
                    + "decor VARCHAR(20), PRIMARY KEY (RoomCode));";
                Statement createRooms = conn.createStatement();
                createRooms.executeQuery(roomQuery);
            }
            checkTuples(conn, "rooms");

            ResultSet res = meta.getTables(null, null, "reservations", null);
            System.out.print("checking reservations... ");
            if (res.next()) {
                System.out.print("reservations exists... ");
            } else {
                System.out.print("reservations not found... " 
                        + "creating table... ");
                String resQuery = "CREATE TABLE reservations "
                    + "(CODE INT, Room CHAR(5), CheckIn DATE, CheckOut DATE, "
                    + "Rate FLOAT, LastName VARCHAR(15), "
                    + "FirstName VARCHAR(15), Adults INT, Kids INT, "
                    + "PRIMARY KEY (CODE), "
                    + "FOREIGN KEY (Room) REFERENCES rooms(RoomCode));";
                Statement createRes = conn.createStatement();
                createRes.executeQuery(resQuery);
            }
            checkTuples(conn, "reservations");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection startup() {
        System.out.print("connecting... ");
        Connection conn = connect();
        System.out.println("connected");
        checkTables(conn);
        System.out.println("startup successful\n");

        return conn;
    }

    public static void main(String args[]) {
        Connection conn = startup();

        Scanner s = new Scanner(System.in);
        System.out.print("Enter a user mode or quit "
                + "(admin | owner | guest | quit): ");
        String mode = s.nextLine();
        while (!mode.equals("quit")) {
            System.out.print("Enter a user mode or quit "
                    + "(admin | owner | guest | quit): ");
            mode = s.nextLine();
        }

        System.out.print("closing connection... ");
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connection closed");
    }
}
