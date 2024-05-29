package com.example.applic_server.models;

import com.example.applic_server.controllers.SerMainController;

import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleMenu implements Runnable{
    Scanner menu_scan = new Scanner(System.in);
    boolean menu = true;

    @Override
    public void run(){
        while (menu){
            System.out.println("--------Server--------------------------------------------------");
            System.out.println("1: Show all Hotel Rooms");
            System.out.println("2: Show all Bookings");
            System.out.println("3: Show total id");
            System.out.println("4: Show total id bookings");
            System.out.println("5: Shutdown");

            System.out.print(">");
            Integer input = read_integer(1,5, menu_scan);
            List<HotelRoom> all_rooms = SerMainController.get_all_rooms();
            List<Booking> all_bookings = SerMainController.get_all_bookings();
            Integer total_id = SerMainController.get_total_id();
            Integer total_id_bookings = SerMainController.get_total_id_bookings();
            switch (input) {
                case 1 -> all_rooms.forEach(HotelRoom::print);
                case 2 -> all_bookings.forEach(Booking::print);
                case 3 -> System.out.println(total_id);
                case 4 -> System.out.println(total_id_bookings);
                case 5 -> {
                    SerMainController.serialize_bookings();
                    SerMainController.serialize_rooms();
                    SerMainController.serialize_total();
                    System.exit(0);
                }
            }
        }
    }

    public static Integer read_integer(Integer range_1, Integer range_2, Scanner scan){
        int input_int = range_1 - 1;
        while(input_int == range_1 - 1){
            try{
                input_int = Integer.parseInt(scan.nextLine());
                if (input_int<range_1 || input_int>range_2){
                    System.out.println("Wrong number");
                    input_int = range_1 - 1;
                }
            }



            catch(NumberFormatException e){
                System.out.println("Wrong number");
            }
        }

        return input_int;

    }
}
