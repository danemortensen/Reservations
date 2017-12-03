<<<<<<< HEAD
import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.text.SimpleDateFormat;
import java.text.ParseException;
=======
import java.util.*;
>>>>>>> b04b87fe61faf940addc801b28ac33dd95dfc451

public class Guest extends User {

<<<<<<< HEAD
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final char ESC = 27;

    private static final String listRoomsQuery =
        "SELECT RoomCode, RoomName, BasePrice FROM rooms;";

    private static final String showRoomDetailsQuery =
        "SELECT RoomName, Beds, bedType, maxOcc, basePrice, decor " +
        "FROM rooms WHERE RoomCode = ";
    
    private Connection conn;
    private Scanner scan;
    
    public Guest(Connection conn) {

        this.conn = conn;
        this.scan = new Scanner(System.in);

    }

    public String getType() {

        return "GUEST";
    }

    public void listRooms() {

        try {

            Statement s1 = conn.createStatement();
            ResultSet result = s1.executeQuery(listRoomsQuery);
            System.out.println("Room List:");
            boolean b = result.next();

            while (b) {

                String roomCode = result.getString(1);
                String roomName = result.getString(2);
                System.out.println(roomCode + "\t" + roomName);
                b = result.next();

            }

            System.out.println("Enter Room Code or [R]eturn");

        } catch (Exception e) {
        
            e.printStackTrace();

        }
    }

    public boolean showRoomDetails(String roomCode) {
    
        String query = showRoomDetailsQuery + roomCode + ";";

        try {
            
            Statement s1 = conn.createStatement();
            ResultSet result = s1.executeQuery(query);
            boolean b = result.next();

            if (b) {

               String roomName = result.getString(1);
               String beds = result.getString(2);
               String bedType = result.getString(3);
               String maxOcc = result.getString(4);
               String basePrice = result.getString(5);
               String decor = result.getString(6);
               System.out.println(roomCode + "\t" + roomName);
               System.out.println("Number of bed(s): " + beds);
               System.out.println("Type of bed(s): " + bedType);
               System.out.println("Max occupancy: " + maxOcc);
               System.out.println("Base price: " + basePrice);
               System.out.println("Decor: " + decor);
               System.out.println("\n[C]heck Availability or [R]eturn");
               return true;

            } else {

                System.out.println("Room not found");
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
            
    }

    public boolean checkRoomAvailability(String roomCode) {

        System.out.print("Enter Check-In Date (YYYY-MM-DD): ");
        String checkInString = scan.nextLine();
        Date checkIn = stringToDate(checkInString);
        
        if (checkIn == null) {

            System.out.println("Invalid date");
            return false;
        }

        System.out.print("Enter Check-Out Date (YYYY-MM-DD): ");
        String checkOutString = scan.nextLine();
        Date checkOut = stringToDate(checkOutString);

        if (checkOut == null) {

            System.out.println("Invalid date");
            return false;
        }

        return true;

    }

    private LocalDate getLocalDate(Date d) {

        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    }

    private Date stringToDate(String str) {

        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
=======
    public static void roomRates() {
        
>>>>>>> b04b87fe61faf940addc801b28ac33dd95dfc451
    }
}
