package com.example.applic_server.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListToBookingConverter {
    public List<String> booking_to_list(List<Booking> booking){
        List<String> list = new ArrayList<>();
        for (Booking new_booking: booking){
            list.add(Integer.toString(new_booking.getId()));
            list.add(Integer.toString(new_booking.getRoom()));
            list.add(new_booking.getStartDate().toString());
            list.add(new_booking.getEndDate().toString());
            list.add(new_booking.getGuestName());
        }
        return list;
    }

    public Booking list_to_booking(List<String> str_list) {
        return new Booking(Integer.parseInt(str_list.get(0)), Integer.parseInt(str_list.get(1)),
                LocalDate.parse(str_list.get(2)), LocalDate.parse(str_list.get(3)),
                str_list.get(4));
    }
}
