import jdk.swing.interop.DropTargetContextWrapper;

import javax.swing.plaf.nimbus.State;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {

    public static boolean run = true;
    static Scanner sc = new Scanner(System.in);
    public static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    public static final String username = "root";
    public static final String pass = "kuchbhi01";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers loaded succesfully");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection(url, username, pass);
            System.out.println("connected to database");
            Statement st = con.createStatement();

            while (run) {
                System.out.println();
                System.out.println("\t\tHOTEL MANAGEMENT SYSTEM\n");
                System.out.println("1. Reserve a Room");
                System.out.println("2.View Reservations");
                System.out.println("3.Get Room Number");
                System.out.println("4.Update Reservations");
                System.out.println("5.Delete Reservations");
                System.out.println("0. Exit");

                System.out.println("Enter A Choice:");

                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(con, sc, st);
                        break;
                   case 2:
                        viewReservations(con,st);
                        break;
                   case 3:
                       getRoomNo(con,st,sc);
                       break;
                   case 4:
                       updateReservation(con,st,sc);
                       break;
                   case 5:
                       deleteReservations(con,sc,st);
                       break;
                   case 0:
                       exit();
                       run = false;
                       break;
                    default:
                        System.out.println("Enter valid choice");
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException();
        }
    }

    public static void reserveRoom(Connection connection, Scanner sc, Statement statement) throws SQLException {
        try {
            System.out.println("Enter Guest Name:");
            String guestname = sc.next();
            sc.nextLine();
            System.out.println("Enter Room No:");
            int room_no = sc.nextInt();
            System.out.println("Enter Contact No:");
            String contact = sc.next();

            String sql = "INSERT INTO reservations (guest_name,Room_NO,contact_no)" +
                    " VALUES('" + guestname + "','" + room_no + "','" + contact + "')";

            try (Statement st = connection.createStatement()) {
                int affectedrows = statement.executeUpdate(sql);

                if (affectedrows > 0) {
                    System.out.println("Reservation Succesfull ! 😊");
                } else {
                    System.out.println("Reservation failed ! ");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public  static void viewReservations(Connection connection,Statement statement){
        String sql = "SELECT res_id,guest_name,Room_no,contact_no,res_time FROM reservations;";

        try(Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(sql);

            System.out.println("Current Reservations:\n");
            System.out.println("+--------+------------+---------+------------+----------------------+");
            System.out.println("| res_id | guest_name | Room_NO | contact_no | res_time             |");
            System.out.println("+--------+------------+---------+------------+----------------------+");

            while (rs.next()) {
                int reservationid = rs.getInt("res_id");
                String guestname = rs.getString("guest_name");
                int roomno = rs.getInt("Room_NO");
                String contactno = rs.getString("contact_no");
                String resdatetime = rs.getTimestamp("res_time").toString();

                System.out.printf("| %6d | %-10s | %6d  | %-10s | %-19s|\n",
                        reservationid,guestname,roomno,contactno,resdatetime);
            }
            System.out.println("+--------+------------+---------+------------+----------------------+");
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void getRoomNo(Connection connection, Statement statement , Scanner scanner){
        System.out.println("Enter the Reservation Id : ");
        int res = sc.nextInt();
        System.out.println("Enter Guest Name : ");
        String guest =sc.next();

        String sql = "SELECT Room_NO FROM reservations"+
                " WHERE res_id = '"+ res +"' AND guest_name = '"+ guest +"';";

        try (Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()){
                int roomno = rs.getInt("Room_NO");
                System.out.println("Room NO for reservation ID "
                        + res + " AND guest_name = "+ guest +" is : " + roomno);
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void updateReservation(Connection connection , Statement statement , Scanner scanner){
        try{
            System.out.println("Enter Reservation Id to update:");
            int resid = sc.nextInt();
            sc.nextLine();

            if(!reservationExists(connection,resid)){
                System.out.println("Reservation is not found for hte given ID. ");
                return;
            }
            System.out.println("Enter new Guest name :");
            String newguestname = sc.next();
            System.out.println("Enter new Room no : ");
            int newroomno = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter new contact number : ");
            String newcontact = sc.next();

            String sql = "UPDATE reservations SET guest_name = '"+newguestname +"' , "+
                    "Room_NO = '"+ newroomno+"' , contact_no = '"+ newcontact+"'"+
                    "WHERE res-id = "+ resid;

            try(Statement st = connection.createStatement()){
                int affectedrows = st.executeUpdate(sql);

                if (affectedrows>0){
                    System.out.println("Updated succesfully 😊");
                }
                else {
                    System.out.println("Update Failed !");
                }
            }catch (SQLException e ){
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void deleteReservations(Connection connection , Scanner scanner , Statement statement){
        try {
            System.out.println("Enter the Reservation ID to delete:");
            int resid = sc.nextInt();

            if (!reservationExists(connection, resid)) {
                System.out.println("Reservation not found for the given ID");
                return;
            }
            String sql = "DELETE FROM reservations WHERE res_id = " + resid;

            try (Statement st = connection.createStatement()) {
                int affectedrows = st.executeUpdate(sql);
                if (affectedrows>0){
                    System.out.println("Reservation Deleted succesfully 😊");
                }
                else {
                    System.out.println("Deletion  Failed !");
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static boolean reservationExists(Connection connection  , int res_id) {
        try {
            String sql = "SELECT res_id from reservations WHERE res_id = "+ res_id;

            try(Statement st = connection.createStatement()){
                ResultSet rs = st.executeQuery(sql);

                return rs.next();

            }catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void exit() throws InterruptedException{
        System.out.print("\n\n\t\tExiting System");
        int i= 5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou for using hotel reservation system !!");

    }
}