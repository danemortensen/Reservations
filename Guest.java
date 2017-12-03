import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;

public class Guest extends User {

    private static int resCode = 0;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final char ESC = 27;

    private static final String listRoomsQuery =
        "SELECT RoomCode, RoomName, BasePrice FROM rooms;";

    private static final String showRoomDetailsQuery =
        "SELECT RoomName, Beds, bedType, maxOcc, basePrice, decor " +
        "FROM rooms WHERE RoomCode = ";

    private static final String getBasePriceQuery =
        "SELECT basePrice FROM rooms WHERE RoomCode = ";

    private static final String getRoomsQuery =
        "SELECT RoomCode FROM rooms;";

    private static final String getRoomNameAndPriceQuery =
        "SELECT RoomName, BasePrice FROM rooms WHERE RoomCode = ";

    private Connection conn;
    private Scanner scan;

    private ArrayList<String> myRoomsAndPrices = new ArrayList<String>();

    private LocalDate chosenStart = null;
    private LocalDate chosenEnd = null;
    private int chosenPrice = 0;

    private static String isRoomAvailableQuery(String roomCode, LocalDate date) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM reservations WHERE RoomCode = Room ");
        sb.append("AND Room = ");
        sb.append(roomCode);
        sb.append(" AND CheckIn <= \"");
        sb.append(date);
        sb.append("\" AND Checkout > \"");
        sb.append(date);
        sb.append("\";");
        return sb.toString();

    }
 
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

            System.out.println("Enter Room Code");

        } catch (Exception e) {
        
            e.printStackTrace();

        }
    }

    public int showRoomDetails(String roomCode) {
    
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
                System.out.println("\n[C]heck Availability");
                return 0;

            } else {

                System.out.println("Room not found");
                return 1;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int showRoomDetails2(String roomCode) {

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
                System.out.println("\n[M]ake a Reservation: ");
                scan.nextLine();
                return 0;

            } else {

                System.out.println("Room not found");
                return 1;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;

    }

    public int checkRoomAvailability(String roomCode) {

        LocalDate[] dates = getDatesFromUser();
        LocalDate start = dates[0];
        LocalDate end = dates[1];

        System.out.println("Availability for Room " + roomCode);

        boolean occupied = false;
        double maxMult = 0;
        double basePrice = getBasePrice(roomCode);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {

            System.out.print(date + "\t");
            
            if (isRoomAvailable(roomCode, date)) {
                double multiplier = getPriceMultiplier(date);
                maxMult = multiplier > maxMult ? multiplier : maxMult;
                int roomPrice = (int) (basePrice * multiplier);
                System.out.println(roomPrice);
            } else {
                System.out.println("Occupied");
                occupied = true;
            }
        }

        if (!occupied) {
            System.out.println("[P]lace a Reservation: ");
            chosenStart = start;
            chosenEnd = end;
            return (int) (maxMult * basePrice);
        } else {
            return 0;
        }
    }

    public String listAvailableRooms() {

        LocalDate[] dates = getDatesFromUser();
        LocalDate start = dates[0];
        LocalDate end = dates[1];

        chosenStart = start;
        chosenEnd = end;

        myRoomsAndPrices = new ArrayList<String>();

        try {

            Statement s1 = conn.createStatement();
            ResultSet result = s1.executeQuery(getRoomsQuery);
            boolean b = result.next();

            while (b) {

                String roomCode = result.getString(1);
                boolean avail = true;
                double maxMult = 0;

                for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {

                    if (!isRoomAvailable(roomCode, date)) {
                        
                        avail = false;
                        break;

                    }

                    double mult = getPriceMultiplier(date);
                    maxMult = mult > maxMult ? mult : maxMult;
                }

                if (avail) {

                    String query = getRoomNameAndPriceQuery + roomCode + ";";
                    ResultSet r2 = s1.executeQuery(query);

                    if (r2.next()) {

                        String roomName = r2.getString(1);
                        double basePrice = Double.parseDouble(result.getString(2));
                        int nightlyPrice = (int) (basePrice * maxMult);
                        myRoomsAndPrices.add(roomCode);
                        myRoomsAndPrices.add(nightlyPrice + "");
                        System.out.println(roomCode + "\t" + roomName + "\t" + nightlyPrice);
                    }
                }                

                b = result.next();
            }

            return scan.nextLine();

        } catch (Exception e) {

            e.printStackTrace();
            return "";

        }
    }

    private void completeReservation(String roomCode, LocalDate start, LocalDate end, int rate) {

        System.out.print("Enter your last name: ");
        String lastName = scan.nextLine();

        System.out.print("Enter your first name: ");
        String firstName = scan.nextLine();

        System.out.print("Enter the number of adults: ");
        int numAdults = scan.nextInt();

        System.out.print("Enter the number of children: ");
        int numChildren = scan.nextInt();

        System.out.println("Apply discount:");
        System.out.println("\tAAA  10% [1]");
        System.out.println("\tAARP 15% [2]");
        System.out.println("\tNone     [3]");

        int discount = scan.nextInt();
        if (discount == 1) rate = (int) (rate * 0.9);
        else if (discount == 2) rate = (int) (rate * 0.85);

        System.out.println("[P]lace Reservation: ");
        scan.nextLine();

        int myResCode = resCode++;
        
        try {

            String query = "INSERT INTO reservations VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, myResCode);
            ps.setString(2, roomCode);
            ps.setString(3, start.toString());
            ps.setString(4, end.toString());
            ps.setInt(5, rate);
            ps.setString(6, lastName);
            ps.setString(7, firstName);
            ps.setInt(8, numAdults);
            ps.setInt(9, numChildren);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Your reservation is complete.");
    }

    private LocalDate[] getDatesFromUser() {
        
        String checkInString = "", checkOutString = "";
        Date checkIn = null, checkOut = null;
        
        do {

            System.out.print("Enter Check-In Date (YYYY-MM-DD): ");
            checkInString = scan.nextLine();
            checkIn = stringToDate(checkInString);
        
        } while (checkIn == null);

        do {
        
            System.out.print("Enter Check-Out Date (YYYY-MM-DD): "); 
            checkOutString = scan.nextLine();
            checkOut = stringToDate(checkOutString);

        } while (checkOut == null);

        return new LocalDate[] {getLocalDate(checkIn), getLocalDate(checkOut)};
    }

    private double getBasePrice(String roomCode) {

        String query = getBasePriceQuery + roomCode + ";";

        try {

            Statement s1 = conn.createStatement();
            ResultSet result = s1.executeQuery(query);
            boolean b = result.next();

            if (b) {

                return Double.parseDouble(result.getString(1));

            } else {

                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private double getPriceMultiplier(LocalDate date) {

        if (isHoliday(date)) {
            return 1.25;
        } else if (isWeekend(date)) {
            return 1.1;
        } else {
            return 1;
        }
    }

    private boolean isRoomAvailable(String roomCode, LocalDate date) {

        String query = isRoomAvailableQuery(roomCode, date);

        try {
                
            Statement s1 = conn.createStatement();
            ResultSet result = s1.executeQuery(query);
            boolean b = result.next();

            if (b) {

                return false;                

            } else {

                return true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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

    private static boolean isHoliday(LocalDate date) {
        return
            (date.getMonthValue() == 1 && date.getDayOfMonth() == 1) ||
            (date.getMonthValue() == 7 && date.getDayOfMonth() == 4) ||
            (date.getMonthValue() == 9 && date.getDayOfMonth() == 6) ||
            (date.getMonthValue() == 10 && date.getDayOfMonth() == 30);
    }

    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
            date.getDayOfWeek() == DayOfWeek.SUNDAY;

    }

    public void prompt() {

        System.out.println("Rooms and Rates [1]");
        System.out.println("Reservations    [2]");
        System.out.println("Return to Home  [3]");

        int input = scan.nextInt();

        if (input == 1) {

            listRooms();
            String roomCode = scan.nextLine();
            showRoomDetails(roomCode);
            scan.nextLine();
            int nightlyPrice = checkRoomAvailability(roomCode);

            if (nightlyPrice == 0) return;

            scan.nextLine();
            completeReservation(roomCode, chosenStart, chosenEnd, nightlyPrice);
            return;


        } else if (input == 2) {

            String roomCode = listAvailableRooms();
            showRoomDetails2(roomCode);
            int myPrice = 0;

            for (int i = 0; i < myRoomsAndPrices.size(); i += 2) {

                if (myRoomsAndPrices.get(i).equals(roomCode)) {

                    myPrice = Integer.parseInt(myRoomsAndPrices.get(i + 1));
                    break;
                }

            }

            completeReservation(roomCode, chosenStart, chosenEnd, myPrice);
            return;

        } else {

            return;

        }

    }
}
