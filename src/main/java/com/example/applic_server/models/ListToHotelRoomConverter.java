package com.example.applic_server.models;

import java.util.ArrayList;
import java.util.List;

public class ListToHotelRoomConverter {
    public List<String> room_to_list(List<HotelRoom> room){
        List<String> list = new ArrayList<>();
        for (HotelRoom new_room: room){
            list.add(Integer.toString(new_room.getId()));
            list.add(new_room.getType());
            list.add(Boolean.toString(new_room.isBusy()));
            list.add(Boolean.toString(new_room.isDirty()));
            list.add(Double.toString(new_room.getCost()));
            list.add(Integer.toString(new_room.getNumber_of_beds()));
            list.add(Integer.toString(new_room.getFloor()));
            list.add(new_room.getGuest_name());
            list.add(new_room.getCleaner());
        }
        return list;
    }

    public HotelRoom list_to_room(List<String> str_list) {
        return new HotelRoom(Integer.parseInt(str_list.get(0)),str_list.get(1), Boolean.parseBoolean(str_list.get(2)),
                Boolean.parseBoolean(str_list.get(3)), Double.parseDouble(str_list.get(4)),
                Integer.parseInt(str_list.get(5)), Integer.parseInt(str_list.get(6)), str_list.get(7), str_list.get(8));
    }
}
