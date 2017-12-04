import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.Date;
import java.time.LocalDate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;

public class Owner extends User {
    Connection conn;
    Scanner scan;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final String viewDateQuery =
        "SELECT RoomName, RoomCode, (CASE WHEN SUM(CASE WHEN CheckIn <= ?" +
        " AND CheckOut > ? THEN 1 ELSE 0 END) = 0 THEN \"Empty\" ELSE \"Occupied\" END)" +
        " AS OccupationStatus FROM rooms, reservations WHERE RoomCode = Room" + 
        " GROUP BY RoomCode ORDER BY RoomCode;";

    public Owner(Connection conn) {
        this.conn = conn;
        this.scan = new Scanner(System.in);
    }

    public String getType() {

        return "OWNER";

    }

    private static LocalDate getLocalDate(Date d) {

        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    }

    private static Date stringToDate(String str) {

        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public void prompt() {

        int choice = getSelectedFunctionality();

        if (choice == 1) {

            occupancyOverview();

        } else if (choice == 2) {

            viewRevenue();

        } else if (choice == 3) {

            reviewRooms();
    
        } else if (choice == 4) {

            reviewReservations();

        } else if (choice == 5) {

            detailedReservations();

        } else {

            return;
        }
    }

    private int getSelectedFunctionality() {

        System.out.println("Occupancy Overview    [1]");
        System.out.println("View Revenue          [2]");
        System.out.println("Review Rooms          [3]");
        System.out.println("Review Reservations   [4]");
        System.out.println("Detailed Reservations [5]");
        System.out.println("Return                [6]");

        int choice = 0;

        do {

            System.out.print("\nSelect: ");

            try {

                choice = Integer.parseInt(scan.nextLine());

            } catch (Exception e) {}

        } while (choice < 1 || choice > 6);

        System.out.println();
        return choice;
    }

    private LocalDate getDateFromUser(String prompt) {
        
        String dateString = "";
        Date date = null;
        
        do {

            System.out.print(prompt + ": ");
            dateString = scan.nextLine();
            date = stringToDate(dateString);
        
        } while (date == null);

        return getLocalDate(date);
    }

    private void occupancyOverview() {

        System.out.println("View Date  [1]");
        System.out.println("View Range [2]");

        int choice = 0;

        do {

            System.out.print("\nSelect: ");

            try {

                choice = Integer.parseInt(scan.nextLine());

            } catch (Exception e) {}

        } while (choice < 1 || choice > 2);

        System.out.println();
        
        if (choice == 1) {

            LocalDate ld = getDateFromUser("Enter Date (YYYY-MM-DD)");
            viewDate(ld);

        } else {

            LocalDate start = getDateFromUser("Enter Start Date (YYYY-MM-DD)");
            LocalDate end = null;

            while (end == null || end.isBefore(start)) {

                end = getDateFromUser("Enter End Date (YYYY-MM-DD)");

            }

            viewRange(start, end);
        }
    }

    private void viewDate(LocalDate date) {

        try {
            PreparedStatement ps = conn.prepareStatement(viewDateQuery);
            ps.setString(1, date.toString());
            ps.setString(2, date.toString());
            ResultSet rs = ps.executeQuery();
            boolean b = rs.next();

            while (b) {

                String roomName = rs.getString(1);
                String roomCode = rs.getString(2);
                String occupiedState = rs.getString(3);

                System.out.println(roomName + "\t" + roomCode + "\t" + occupiedState + "\t");

                b = rs.next();
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void viewRange(LocalDate start, LocalDate end) {

    

    }

    private void viewRevenue() {

    }

    private void reviewRooms() {


    }

    private void reviewReservations() {



    }

    private void detailedReservations() {


    }

    public void reservations(String start, String end, String room) {
        try {
            Statement s = conn.createStatement();       
            ResultSet reservations = s.executeQuery("select Code from INN_RESERVATIONS where checkin >= '" + start + "' and checkin <= '" + end + "' and Room = '" + room + "';");	
            print(reservations);
        } catch(Exception ex) {
            System.out.println("revenue error: " + ex);
        }
    }

    public void reservationDetails(String code) {
        try {
            Statement s = conn.createStatement();
            ResultSet details = s.executeQuery("select rv.*,rm.RoomName from INN_RESERVATIONS rv,INN_Rooms rm where rv.Room = rm.RoomId and rm.RoomId = '" + code + "';");
            print(details);
        } catch(Exception ex) {
            System.out.println("reservation details err: " + ex);
        }
    }

    public void viewSpecificRoom(String room) {
        try {
            Statement s = conn.createStatement();     
			ResultSet roomInfo = s.executeQuery("select * from INN_Rooms where RoomId = '" + room + "';");
            roomInfo.next();
            ResultSetMetaData rsmd = roomInfo.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for(int i =1;i<=columnsNumber;i++)
                System.out.print(roomInfo.getString(i) +" ");
			
            ResultSet days = s.executeQuery("select sum(datediff(checkout,checkin)) from INN_RESERVATIONS where Room = '" + room + "';");
			
            days.next();
            float daysOccupied = (float)days.getInt(1);
            System.out.println("\nNights occupied: " + (int)daysOccupied);
            int percentOccupied = Math.round((daysOccupied/365)*100);
            System.out.println("Percent occupied: " + percentOccupied + "%");

            ResultSet roomRevenue = s.executeQuery("select sum(datediff(checkout,checkin) * rate) from INN_RESERVATIONS where Room = '" +room + "';");
            roomRevenue.next();
            double totalRoomRev = roomRevenue.getDouble(1);
            System.out.println("\nTotal Room Revenue: $" + totalRoomRev);

            ResultSet totalRevenue = s.executeQuery("select sum(datediff(checkout,checkin) * rate) from INN_RESERVATIONS;");
            totalRevenue.next();
            double percentRevenue = (totalRoomRev/totalRevenue.getDouble(1))*100;
            System.out.println("Percent of overall 2010 revenue: " + Math.round(percentRevenue) + "%");
        } catch(Exception ex) {
            System.out.println("view room reservations error: " + ex);
        }
    }

    public void viewRoomReservations(String room) {
        try {
            Statement s = conn.createStatement();       
            ResultSet reservations = s.executeQuery("select Code from INN_RESERVATIONS where Room = '" + room + "'  order by Code;");	
            print(reservations);
        } catch(Exception ex) {
            System.out.println("view room reservations error: " + ex);
        }
    }

    public void print(ResultSet r) {
        try {
            ResultSetMetaData rsmd = r.getMetaData();
   	        while (r.next()) {
   	            for(int i =1;i<=rsmd.getColumnCount();i++)
   	  	            System.out.print(r.getString(i) +"\t");
   	            System.out.println("\n");
   	  	    }
        } catch(Exception ex) {
            System.out.println("print out err: " + ex);
        }
    }	
}
