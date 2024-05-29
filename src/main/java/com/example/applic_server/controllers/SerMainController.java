package com.example.applic_server.controllers;

import com.example.applic_server.models.handlers.JsonHandler;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//TODO ApplicationData class
//TODO Applications controller render
//TODO Applications controller reject/accept(two another arrays)
import com.example.applic_server.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerMainController {
    private static final Logger file_logger = LoggerFactory.getLogger("file_data");

    private static final Logger console_logger = LoggerFactory.getLogger("console");
    private Stage stage;
    Server server;
    private static List<HotelRoom> all_rooms = new ArrayList<>();
    private static List<Booking> all_bookings = new ArrayList<>();
    private static List<String> cleaners = new ArrayList<>();
    //TODO Read total_id from ser/des
    private static Integer total_id;
    private static Integer total_id_bookings;
    JsonHandler js_handler = new JsonHandler();


    public void start_menu() throws IOException {
//        all_rooms = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            String type = switch (i) {
//                case 0 -> "Standard";
//                case 1 -> "Standard Business";
//                case 2 -> "Standard Premium";
//                case 3 -> "Deluxe Comfort";
//                case 4 -> "Lux Studio";
//                case 5 -> "Lux Premium";
//                case 6 -> "Lux Romantic";
//                case 7 -> "Lux Marine";
//                case 8 -> "Business Lux";
//                case 9 -> "Apartments";
//                default -> "";
//            };
//            for (int j = 1; j < 5; j++) {
//                HotelRoom room = new HotelRoom((i * 4 )+ (j-1), type, false, true, j*500.0, j, i+1, "", "Vitya");
//                all_rooms.add(room);
//                //increment_total_id();
//            }
//        }
//        serialize_rooms();
//        all_bookings = new ArrayList<>();
//        total_id_bookings = 0;
//        LocalDate[] startDates = {
//                LocalDate.of(2024, 1, 1),
//                LocalDate.of(2024, 3, 1),
//                LocalDate.of(2024, 5, 1),
//                LocalDate.of(2024, 7, 1),
//                LocalDate.of(2024, 9, 1),
//        };
//
//        LocalDate[] endDates = {
//                LocalDate.of(2024, 1, 10),
//                LocalDate.of(2024, 3, 10),
//                LocalDate.of(2024, 5, 10),
//                LocalDate.of(2024, 7, 10),
//                LocalDate.of(2024, 9, 10),
//        };
//        for (int i = 7; i < 20; i++) {
//            for (int j = 0; j < 5; j++) {
//                LocalDate startDate = startDates[j];
//                LocalDate endDate = endDates[j];
//                Booking booking = new Booking(((i-7) * 5) + (j), i, startDate, endDate, "Test");
//                all_bookings.add(booking);
//                increment_total_id_bookings();
//            }
//        }
//        serialize_bookings();
//        serialize_total_bookings();
        deserialize_rooms();
        deserialize_bookings();
        deserialize_total();
        deserialize_total_bookings();
        ConsoleMenu menu = new ConsoleMenu();
        Thread menu_thread = new Thread(menu);
        menu_thread.start();
    }

    public void get_cleaners(){
      for(Map.Entry<String,String> entry: js_handler.load_json_users().entrySet()){
          if (entry.getValue().equals("Cleaner")){
              cleaners.add(entry.getKey());
          }
      }
    }
   //TODO Pass three lists of applications here
    // Then make this prepare for other controllers
    public void connect(){
        try{
            deserialize_total();
            deserialize_rooms();
            deserialize_bookings();
            deserialize_total_bookings();
            server = new Server(new ServerSocket(1234));
            Thread serverThread = new Thread(server);
            serverThread.start();
//            System.out.println("Server is running.");

//            server.receiveApplicationsFromClient(waiting_applics);
        } catch (IOException e) {
            System.out.println("Fatal");
        }
    }

    public static void removeRoomById(Integer id) {
        all_rooms.removeIf(room -> id.equals(room.getId()));
    }
    public static void removeBookingById(Integer id) {
        all_bookings.removeIf(booking -> id.equals(booking.getId()));
    }

    public static Integer get_total_id(){
        return total_id;
    }
    public static void increment_total_id(){
        total_id += 1;
    }
    public static Integer get_total_id_bookings(){
        return total_id_bookings;
    }
    public static void increment_total_id_bookings(){
        total_id_bookings += 1;
    }

   public static void serialize_total() {
       file_logger.info("Serialized total");
        try {
           FileOutputStream fos = new FileOutputStream("total.ser");
           ObjectOutputStream oos = new ObjectOutputStream(fos);

           oos.writeObject(total_id);
           oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
       }
   }

    public static void serialize_total_bookings() {
        file_logger.info("Serialized total bookings");
        try {
            FileOutputStream fos = new FileOutputStream("total_bookings.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(total_id_bookings);
            oos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void serialize_rooms() {
        file_logger.info("Serialized hotel rooms");
        try {
            FileOutputStream fos = new FileOutputStream("rooms.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(all_rooms);
            oos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void serialize_bookings() {
        file_logger.info("Serialized bookings");
        try {
            FileOutputStream fos = new FileOutputStream("bookings.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(all_bookings);
            oos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void deserialize_rooms() throws IOException{
        file_logger.info("DeSerialized hotel rooms");
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("rooms.ser"))){
            all_rooms = (List<HotelRoom>) ois.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            System.exit(228);
            System.out.println("Fatal");
        } catch (IOException e) {
            System.out.println("Fatal");
        }
    }

    public static void deserialize_bookings() throws IOException{
        file_logger.info("DeSerialized bookings");
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bookings.ser"))){
            all_bookings = (List<Booking>) ois.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            System.exit(228);
            System.out.println("Fatal");
        } catch (IOException e) {
            System.out.println("Fatal");
        }
    }

   public static void deserialize_total(){
        file_logger.info("DeSerialized total");
       try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("total.ser"))){
           total_id = (Integer) ois.readObject();
       } catch (ClassNotFoundException | ClassCastException e) {
           e.printStackTrace();
           System.exit(228);
           System.out.println("Fatal");
       } catch (IOException e) {
           System.out.println("Fatal");
       }
   }

    public static void deserialize_total_bookings(){
        file_logger.info("DeSerialized total bookings");
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("total_bookings.ser"))){
            total_id_bookings = (Integer) ois.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            System.exit(228);
            System.out.println("Fatal");
        } catch (IOException e) {
            System.out.println("Fatal");
        }
    }

    public static void add_room(HotelRoom room){
        console_logger.info("Room added");
        all_rooms.add(room);
        serialize_rooms();
        //db_handler.update_db(all_rooms);
    }

    public static void add_booking(Booking booking){
        console_logger.info("New Booking added");
        all_bookings.add(booking);
        serialize_bookings();
        //db_handler.update_db(all_bookings);
    }

    public static List<HotelRoom> get_all_rooms(){
        return all_rooms;
    }
    public static List<Booking> get_all_bookings(){
        return all_bookings;
    }

    public static void updateRoom(HotelRoom room){
        console_logger.info("Room updated");
        //Update a room when admin sends it to us
        all_rooms.forEach((app) -> {
            if (app.getId().equals(room.getId())) {
                app.setType(room.getType());
                app.setBusy(room.isBusy());
                app.setDirty(room.isDirty());
                app.setCost(room.getCost());
                app.setNumber_of_beds(room.getNumber_of_beds());
                app.setFloor(room.getFloor());
                app.setGuest_name(room.getGuest_name());
                app.setCleaner(room.getCleaner());
            }
        });
        serialize_rooms();
    }

    public static void updateBooking(Booking booking){
        console_logger.info("Booking updated");
        //Update a booking when admin sends it to us
        all_bookings.forEach((app) -> {
            if (app.getId().equals(booking.getId())) {
                app.setStartDate(booking.getStartDate());
                app.setEndDate(booking.getEndDate());
                app.setGuestName(booking.getGuestName());
            }
        });
        serialize_bookings();
    }

    public static List<String> getCleaners(){
        return cleaners;
    }


//    public void save() throws IOException {
//        db_handler.update_db(get_all_applications());
//        List<ApplicationData> all_applics = get_all_applications();
//        FileOutputStream fos = new FileOutputStream("devs.ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(all_applics);
//        oos.close();
//        file_logger.info("Devices were serialized");
//
//
//    }

//    public static void sort_users_applic(List<ApplicationData> all_applics) {
//        //Sort a list of applications between lists of applications for each status
//        for (ApplicationData app : all_applics) {
//            switch (app.get_status()) {
//                case "On wait" -> waiting_applics.add(app);
//                case "In Progress" -> progress_applics.add(app);
//                case "Finished", "Rejected", "Cancelled" -> closed_applics.add(app);
//                default -> throw new IllegalStateException("Unexpected value: " + app.get_status());
//            }
//        }
//    }
//    public void load(){
//        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("devs.ser"))){
//            List<ApplicationData> applics = (List<ApplicationData>) ois.readObject();
//            file_logger.info("Devices were Deserialized");
//        sort_users_applic(applics);
//        } catch (ClassNotFoundException | ClassCastException e) {
//            System.exit(228);
//            System.out.println("Fatal");
//        } catch (IOException e) {
//            System.out.println("Fatal");
//        }
//    }
}
