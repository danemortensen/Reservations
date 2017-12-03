import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Owner {
    private Connection conn;

    public Owner(Connection conn) {
        this.conn = conn;
    }

    public void occupancy(String date) {
        try {
   	  		Statement s = conn.createStatement();       
   	  		ResultSet result = s.executeQuery("select rm.roomName, rm.roomId, (case " + 
   	  				"when sum(case when checkin <= '" + date + 
   	  				"' and " + 
   	  				"checkout > '" + date+ "' then 1 else 0 end) = 1 " + 
   	  				"then 'Occupied' else 'Empty' end) as Status " + 
   	  				"from INN_RESERVATIONS rv, INN_Rooms rm " + 
   	  				"where rm.roomId = rv.Room " + 
   	  				"group by rv.Room " + 
   	  				"order by rm.roomId;");
   	  		print(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void occupancy(String start, String end) {
        try {
            Statement s = conn.createStatement();       
            ResultSet result = s.executeQuery("select Room, (case when count(*) = 0 then 'Empty' "
   	  				+ "when datediff('"+end+"','"+start+"') <= sum(case when(checkout > '"+end+"' and checkin <= '"+start+"') then datediff('"+end+"', checkin) "
   	  				+ "when (checkout <= '"+end+"' and checkin >= '"+start+"') then datediff(checkout, checkin) "
   	  				+ "when (checkout > '"+end+"' and checkin < '"+start+"') then datediff('"+end+"', '"+start+"') end) then 'Full' else 'Partial' end) as occupancy "
   	  				+ "from INN_RESERVATIONS where checkout > '"+start+"' and checkin <= '"+end+"' group by Room;");
   	  		print(result);
        } catch (Exception ex) {
            System.out.println("Two date occupancy error: " + ex);
        }
    }

    public void revenue(){
        try {
            Statement s = conn.createStatement();       
   	  		ResultSet rev = s.executeQuery("select room, sum(case when monthname(checkout) = 'January' then Rate*(datediff(checkout,checkin)) else 0 end) as Jan, " +
   	  				"sum(case when monthname(checkout) = 'February' then Rate*(datediff(checkout,checkin)) else 0 end) as Feb, " +
   	  				"sum(case when monthname(checkout) = 'March' then Rate*(datediff(checkout,checkin)) else 0 end) as Mar, " +
   	  				"sum(case when monthname(checkout) = 'April' then Rate*(datediff(checkout,checkin)) else 0 end) as Apr, " +
   	  				"sum(case when monthname(checkout) = 'May' then Rate*(datediff(checkout,checkin)) else 0 end) as May, " +
   	  				"sum(case when monthname(checkout) = 'June' then Rate*(datediff(checkout,checkin)) else 0 end) as June, " +
   	  				"sum(case when monthname(checkout) = 'July' then Rate*(datediff(checkout,checkin)) else 0 end) as July, " +
   	  				"sum(case when monthname(checkout) = 'August' then Rate*(datediff(checkout,checkin)) else 0 end) as Aug, " +
   	  				"sum(case when monthname(checkout) = 'September' then Rate*(datediff(checkout,checkin)) else 0 end) as Sept, " +
   	  				"sum(case when monthname(checkout) = 'October' then Rate*(datediff(checkout,checkin)) else 0 end) as Oct, " +
   	  				"sum(case when monthname(checkout) = 'November' then Rate*(datediff(checkout,checkin)) else 0 end) as Nov, " +
   	  				"sum(case when monthname(checkout) = 'December' then Rate*(datediff(checkout,checkin)) else 0 end) as Dece, " +
   	  				"sum(case when year(checkout) = 2010 then Rate*(datediff(checkout,checkin)) else 0 end) as Total from INN_RESERVATIONS group by Room "
   	  				+ "union "
   	  				+ "select 'Total' as Room, sum(Jan) as Jan, sum(Feb) as Feb, sum(Mar) as Mar, sum(Apr) as Apr, sum(May) as May, sum(June) as June, sum(July) as July, sum(Aug) as Aug, sum(Sept) as Sept, sum(Oct) as Oct, sum(Nov) as Nov, sum(Dece) as Dece, sum(Total) as Total "
   	  				+ "from (select room, sum(case when monthname(checkout) = 'January' then Rate*(datediff(checkout,checkin)) else 0 end) as Jan, " + 
   	  				"sum(case when monthname(checkout) = 'February' then Rate*(datediff(checkout,checkin)) else 0 end) as Feb, " + 
   	  				"sum(case when monthname(checkout) = 'March' then Rate*(datediff(checkout,checkin)) else 0 end) as Mar, " + 
   	  				"sum(case when monthname(checkout) = 'April' then Rate*(datediff(checkout,checkin)) else 0 end) as Apr, " + 
   	  				"sum(case when monthname(checkout) = 'May' then Rate*(datediff(checkout,checkin)) else 0 end) as May, " + 
   	  				"sum(case when monthname(checkout) = 'June' then Rate*(datediff(checkout,checkin)) else 0 end) as June, " + 
   	  				"sum(case when monthname(checkout) = 'July' then Rate*(datediff(checkout,checkin)) else 0 end) as July, " + 
   	  				"sum(case when monthname(checkout) = 'August' then Rate*(datediff(checkout,checkin)) else 0 end) as Aug, " + 
   	  				"sum(case when monthname(checkout) = 'September' then Rate*(datediff(checkout,checkin)) else 0 end) as Sept, " + 
   	  				"sum(case when monthname(checkout) = 'October' then Rate*(datediff(checkout,checkin)) else 0 end) as Oct, " + 
   	  				"sum(case when monthname(checkout) = 'November' then Rate*(datediff(checkout,checkin)) else 0 end) as Nov, " + 
   	  				"sum(case when monthname(checkout) = 'December' then Rate*(datediff(checkout,checkin)) else 0 end) as Dece, " + 
   	  				"sum(case when year(checkout) = 2010 then Rate*(datediff(checkout,checkin)) else 0 end) as Total from INN_RESERVATIONS group by Room) as revTbl;");
   	  		//NEED TO PRINT
   	  		print(rev);
        } catch(Exception ex) {
            System.out.println("revenue error: " + ex);
        }
    }

    public void reservations(String start, String end) {
        try {
            Statement s = conn.createStatement();       
   	  		ResultSet reservations = s.executeQuery("select Code from INN_RESERVATIONS where checkin >= '" + start + "' and checkin <= '" + end +"';");	
   	  		print(reservations);
        } catch(Exception ex) {
            System.out.println("revenue error: " + ex);
        }
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
