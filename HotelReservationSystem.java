import java.sql.*;
import java.util.Scanner;
import java.sql.ResultSet;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/myhotel ";

    private static final String name = "root";

    private static final String password = "admin123";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, name, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM ");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1: Reserve a room");
                System.out.println("2: View reservation");
                System.out.println("3: Get room number");
                System.out.println("4: Update reservation");
                System.out.println("5: Delete reservation");
                System.out.println("0: Exit");
                System.out.print("Choose Any Option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1: {
                        reserveRoom(connection, scanner, connection.createStatement());
                        break;
                    }

                    case 2: {
                        viewReservation(connection, connection.createStatement());
                        break;
                    }

                    case 3: {
                        getRoomNumber(connection, scanner, connection.createStatement());
                        break;
                    }

                    case 4: {
                        updateReservation(connection,scanner);
                        break;
                    }
                    case 5: {
                        deleteReservation(connection,scanner, connection.createStatement());
                        break;
                    }

                    case 0: {
                        exit();
                        scanner.close();
                        return;
                    }

                    default: {
                        System.out.println("Invalid Choice, Try Again");
                    }
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner, Statement statement) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            int contactNumber = scanner.nextInt();

            String query = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                    "VALUES('" + guestName + "'," + roomNumber + ", '" + contactNumber + "')";


            statement = connection.createStatement();
            int affectRows = statement.executeUpdate(query);

            if (affectRows > 0) {
                System.out.println("Reservation SuccessFul!");
            } else {
                System.out.println("Reservation Failed..");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservation(Connection connection, Statement statement) throws SQLException {
        String query = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date From reservation";

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Current Reservation:");
            System.out.println("+------------------+--------------+--------------+--------------------+--------------------------+");
            System.out.println("|  Reservation_Id  |  Guest_Name  | Room_Number  | Contact_Number     | Reservation_Date         |");
            System.out.println("+------------------+--------------+--------------+--------------------+--------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s, | %-13d | %-20s | %-19s | \n", reservationId, guestName, roomNumber, contactNumber, reservationDate);

            }
            System.out.println("+------------------+--------------+--------------+--------------------+-------------------------------+");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    private static void getRoomNumber(Connection connection, Scanner scanner, Statement statement) {
        try {
            System.out.print("Enter Reservation Id: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter Guest Name: ");
            String guestName = scanner.next();

            String query = "SELECT room_number FROM reservation WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";


            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for reservation id " + reservationId + "and Guest name " +
                        guestName + " RoomNo is: " + roomNumber);
            } else {
                System.out.println("Reservation not found for the given ID and guest name.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: "+ e.getMessage());
        }

    }

    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter reservation Id to update: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }


            System.out.print("Enter guest name: ");
            String newGuestName = scanner.next();
            System.out.print("Enter room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            int newContactNumber = scanner.nextInt();

            String query = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = " + newContactNumber +
                    " WHERE reservation_id = " + reservationId;



            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);

            if (affectedRows > 0) {
                System.out.println("Reservation Update Successfully!!");
            } else {
                System.out.println("Reservation not Updated!!");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());;
        }
    }


    private static void deleteReservation(Connection connection, Scanner scanner, Statement statement) {
        try {
            System.out.print("Enter reservation ID to Delete: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String query = "DELETE FROM reservation WHERE reservation_id = " + reservationId;
            statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);

            if (affectedRows > 0) {
                System.out.println("Reservation Delete Successfully!!");
            } else {
                System.out.println("Reservation not Delete!!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());;
        }
    }

   private static boolean reservationExists(Connection connection, int reservationId){
        try {
            String query = "SELECT reservation_id FROM reservation WHERE reservation_id =" + reservationId;

            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {

                return resultSet.next();
            }

            }catch (SQLException e){
            e.getMessage();
            return false;
        }
   }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;

        }
        System.out.println();
        System.out.println("ThankYou for Using Hotel Reservation System!!!");
    }
}
