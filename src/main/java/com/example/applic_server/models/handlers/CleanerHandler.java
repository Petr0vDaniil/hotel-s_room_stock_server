package com.example.applic_server.models.handlers;

import com.example.applic_server.models.*;
import com.example.applic_server.controllers.SerMainController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CleanerHandler extends UserHandler{
    public static ArrayList<CleanerHandler> cleanerHandlers = new ArrayList<>();

    public CleanerHandler(){

    }
    public CleanerHandler(Socket socket, ObjectInputStream ois, ObjectOutputStream oos){
        super(socket, ois, oos);
    }

    @Override
    public void run(){
        cleanerHandlers.add(this);
        while(socket.isConnected()){
            HotelRoom applicationFromCleaner;
            try {
                ois.readObject();
                if (name==null) {
                    name = (String) ois.readObject();
                    send_rooms();
                    server_logger.info("New cleaner's name was received");
                }
                while (true) {
                    Object messgFromCleaner = ois.readObject();
                    if (messgFromCleaner == null || messgFromCleaner.equals("CLOSE")) {
                        break;
                    }
                    System.out.println(messgFromCleaner);
                    applicationFromCleaner = converter2.list_to_room((List<String>) messgFromCleaner);
                    SerMainController.updateRoom(applicationFromCleaner);
                    new AdminHandler().send_applications();
                    new CleanerHandler().send_rooms();
                }
            }

            catch (IOException e) {
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
        for (CleanerHandler cleanerHandler : cleanerHandlers){
            List<HotelRoom> every_room = new ArrayList<>();
            SerMainController.get_all_rooms().forEach(app -> {
                if(app.isDirty()) if (app.getCleaner().equals(cleanerHandler.name)){every_room.add(app);}
            });
            List<String> every_room_string = converter2.room_to_list(every_room);
            try {
                cleanerHandler.oos.writeObject(every_room_string);
                cleanerHandler.oos.flush();
                server_logger.info("Applications were sent to client");
            }
            catch (IOException e){
                closeEverything(cleanerHandler.socket ,cleanerHandler.ois, cleanerHandler.oos);
            }
        }
    }
}
