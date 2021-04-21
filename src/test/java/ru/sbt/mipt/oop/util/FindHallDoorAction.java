package ru.sbt.mipt.oop.util;

import ru.sbt.mipt.oop.Action;
import ru.sbt.mipt.oop.Door;
import ru.sbt.mipt.oop.Room;

public class FindHallDoorAction implements Action {

    private final String hallRoomName;
    private Door hallRoomDoor = null;

    public Door getHallRoomDoor() {
        return hallRoomDoor;
    }

    public FindHallDoorAction(String hallRoomName) {
        this.hallRoomName = hallRoomName;
    }

    @Override
    public void apply(Object obj) {
        if (obj instanceof Room room && room.getName().equals(hallRoomName)) {
            room.execute(element -> {
                if (element instanceof Door door) {
                    hallRoomDoor = door;
                }
            });
        }
    }

}
