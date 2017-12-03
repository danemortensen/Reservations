import java.io.*;
import java.sql.*;
import java.util.*;

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

    public static void displayReservations(Connection conn) {
        try {
            String query = "SELECT * FROM rooms;";
            ResultSet res = conn.createStatement().executeQuery(query);

            System.out.println("CODE\tRoom\tCheckIn\tCheckOut\tRate\t"
                    + "LastName\tFirstName\tAdults\tKids");
            while (res.next()) {
                String code = res.getString("CODE");
                String room = res.getString("Room");
                String checkIn = res.getString("CheckIn");
                String checkOut = res.getString("CheckOut");
                String rate = res.getString("Rate");
                String lastName = res.getString("LastName");
                String firstName = res.getString("FirstName");
                String adults = res.getString("Adults");
                String kids = res.getString("Kids");
                System.out.println(code + "\t" + room + "\t" + checkIn + "\t"
                        + checkOut + "\t" + rate + "\t" + lastName + "\t"
                        + firstName + "\t" + adults + "\t" + kids);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayRooms(Connection conn) {
        try {
            String query = "SELECT * FROM reservations;";
            ResultSet res = conn.createStatement().executeQuery(query);

            System.out.println("roomCode\troomName\tbeds\tbedType\tmaxOcc\t"
                    + "basePrice\tdecor");
            while (res.next()) {
                String roomCode = res.getString(1);
                String roomName = res.getString(2);
                String beds = res.getString(3);
                String bedType = res.getString(4);
                String maxOcc = res.getString(5);
                String basePrice = res.getString(6);
                String decor = res.getString(7);
                System.out.println(roomCode + "\t" + roomName + "\t" + beds
                        + "\t" + bedType + "\t" + maxOcc + "\t" + basePrice
                        + "\t" + decor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearTables(Connection conn) {
        try {
            conn.createStatement().executeQuery("DELETE FROM rooms");
            conn.createStatement().executeQuery("DELETE FROM reservations");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int populated(Connection conn, String table) {
        int count = 0;
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

    public static void populateTable(Connection conn, String table) {
        try (BufferedReader br =
                new BufferedReader(new FileReader("INN-build-" + table
                        + ".txt"))) {
            Statement s = conn.createStatement();
            String line;
            while((line = br.readLine()) != null)
                s.executeUpdate(line); 
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadTable(Connection conn, String table) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rooms = meta.getTables(null, null, table, null);
            System.out.print("checking rooms... ");
            if (!rooms.next()) {
                System.out.print("rooms not found... creating table... ");
                String roomQuery = "CREATE TABLE rooms "
                    + "(RoomCode CHAR(5), RoomName VARCHAR(30), Beds INT, "
                    + "bedType VARCHAR(8), maxOcc INT, basePrice FLOAT, "
                    + "decor VARCHAR(20), PRIMARY KEY (RoomCode));";
                Statement createRooms = conn.createStatement();
                createRooms.executeQuery(roomQuery);
            }
            if (populated(conn, "rooms") > 0) {
                populateTable(conn, "rooms");
            }
            if (populated(conn, "reservations") > 0) {
                populateTable(conn, "reservations");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prompt(Connection conn) {
        Scanner s = new Scanner(System.in);
        System.out.print("[admin] Enter a command (display | clear | load): ");
        String cmd = s.nextLine();
        while (cmd != "return") {
            switch (cmd) {
                case "display":
                    System.out.print("[display] Enter table name: ");
                    String table = s.nextLine();
                    switch (table) {
                        case "rooms":
                            displayRooms(conn);
                            break;
                        case "reservations":
                            displayReservations(conn);
                            break;
                        default:
                            System.out.println("Invalid table name");
                            break;
                    }
                    break;
                case "clear":
                    clearTables(conn);
                    break;
                case "load"
                default:
                    break;
            System.out.print("[admin] Enter a command: ");
            String cmd = s.nextLine();
            }
        }
    }

    public Admin(Connection conn) {
        this.conn = conn;
    }
}
