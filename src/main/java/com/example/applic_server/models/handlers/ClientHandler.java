package com.example.applic_server.models.handlers;

import com.example.applic_server.controllers.SerMainController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.applic_server.models.*;
public class ClientHandler extends UserHandler {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public ClientHandler(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        super(socket, ois, oos);
    }

    @Override
    public void run() {
        clientHandlers.add(this);
        try {
            while (socket.isConnected()) {
                ois.readObject();
                String name = (String) ois.readObject();
                String type = (String) ois.readObject();
                Object tmp = ois.readObject();
                System.out.println(tmp);
                Integer num_of_beds = (Integer) tmp;
                LocalDate check_in = (LocalDate) ois.readObject();
                LocalDate check_out = (LocalDate) ois.readObject();
                List<HotelRoom> rooms = new ArrayList<>();
                List<Booking> bookings = SerMainController.get_all_bookings();
                boolean isYes = false;
                SerMainController.get_all_rooms().forEach(room -> {
                    if (room.getNumber_of_beds() >= num_of_beds && room.getType().equals(type)) {
                        rooms.add(room);
                    }
                });
                for (HotelRoom room : rooms) {
                    int roomId = room.getId();
                    if (checkAvailability(bookings, roomId, check_in, check_out)) {
                        isYes = true;
                        oos.writeObject("Yes");
                        oos.flush();
                        server_logger.info("Applications were sent to client");
                        Booking newBooking = new Booking(SerMainController.get_total_id_bookings(), room.getId(),
                                check_in, check_out, name);
                        SerMainController.increment_total_id_bookings();
                        SerMainController.serialize_total_bookings();
                        SerMainController.add_booking(newBooking);
                        SerMainController.serialize_bookings();
                        break;
                    }
                    }
                    if (!isYes) {
                        oos.writeObject("No");
                        oos.flush();
                        server_logger.info("Applications were sent to client");
                    }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Fatal");
        }
    }

    @Override
    public void send_applications() {}

    public boolean checkAvailability(List<Booking> bookings, int room, LocalDate startDate, LocalDate endDate) {
        for (Booking booking : bookings) {
            if (booking.getRoom() == room) {
                if (!(endDate.isBefore(booking.getStartDate()) || startDate.isAfter(booking.getEndDate()))) {
                    return false; // Номер занят в указанный период времени
                }
            }
        }
        return true; // Номер доступен
    }
}
