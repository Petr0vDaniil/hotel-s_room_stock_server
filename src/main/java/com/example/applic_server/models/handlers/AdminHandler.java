package com.example.applic_server.models.handlers;

import com.example.applic_server.controllers.SerMainController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import com.example.applic_server.models.*;

public class AdminHandler extends UserHandler {
    JsonHandler js_handler = new JsonHandler();
    private HashMap<String, String> users = new HashMap<>();


    public static ArrayList<AdminHandler> adminHandlers = new ArrayList<>();
    public AdminHandler(){

    }

    public AdminHandler(Socket socket, ObjectInputStream ois, ObjectOutputStream oos){
        super(socket, ois, oos);
    }

        @Override
        public void run(){
            adminHandlers.add(this);
            send_applications();
            while(socket.isConnected()){
                String command;
                try {
                    if (name==null) {
                        name = (String) ois.readObject();
                        send_rooms();
                        server_logger.info("New cleaner's name was received");
                    }
                    while (true) {
                        command = (String) ois.readObject();
                        switch (command) {
                            case "New":
                                String type = (String) ois.readObject();
                                String cleaner = (String) ois.readObject();
                                Integer nums_of_beds = (Integer) ois.readObject();
                                Integer floor = (Integer) ois.readObject();
                                Double cost = (Double) ois.readObject();
                                HotelRoom newRoom = new HotelRoom(SerMainController.get_total_id(), type, false,
                                        false, cost, nums_of_beds, floor, "", cleaner);
                                SerMainController.increment_total_id();
                                SerMainController.serialize_total();
                                SerMainController.add_room(newRoom);
                                SerMainController.serialize_rooms();
                                break;
                            case "Room":
                                Integer id_inp = (Integer) ois.readObject();
                                SerMainController.removeRoomById(id_inp);
                                SerMainController.serialize_rooms();
                                break;
                            case "Booking":
                                Integer id_inp2 = (Integer) ois.readObject();
                                SerMainController.removeBookingById(id_inp2);
                                SerMainController.serialize_bookings();
                                break;
                            case "List":
                                Integer id_inp1 = (Integer) ois.readObject();
                                List<Booking> bookings = new ArrayList<>();
                                SerMainController.get_all_bookings().forEach(booking -> {
                                    if (booking.getRoom() == id_inp1) {
                                        bookings.add(booking);
                                    }
                                });
                                List<String> bookings_to_string = converter1.booking_to_list(bookings);
                                oos.writeObject(bookings_to_string);
                                oos.flush();
                                break;
                        }
                    }
                }

                catch (IOException e) {
//                e.printStackTrace();
                    closeEverything(socket,ois,oos);
                    break;
                } catch (ClassNotFoundException e) {
                    System.out.println("Fatal");
                }

            }
        }

    @Override
    public void send_applications() {}

    public void send_rooms(){
        users = js_handler.load_json_users();
        List<String> cleaners = new ArrayList<>();
        for (String key : users.keySet()) {
            String value = users.get(key);
            if (value.equals("Cleaner")) {
                cleaners.add(key);
            }
        }
//        System.out.println("--------Cleaner Handlers: " + cleanerHandlers);
        for (AdminHandler adminHandler : adminHandlers){
            List<HotelRoom> every_room = SerMainController.get_all_rooms();
            List<String> every_room_string = converter2.room_to_list(every_room);
//            System.out.println("--------Applications for cleaner list" + every_application_string);
            try {
                adminHandler.oos.writeObject(every_room_string);
                adminHandler.oos.writeObject(cleaners);
                System.out.println(cleaners);
                adminHandler.oos.flush();
//                System.out.println("--------Applications were sent to client");
                server_logger.info("Applications were sent to client");
            }
            catch (IOException ignored){
                System.out.println("Fatal");
            }
        }
    }
}