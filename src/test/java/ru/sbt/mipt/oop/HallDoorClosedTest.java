package ru.sbt.mipt.oop;

import org.junit.Assert;
import org.junit.Test;
import ru.sbt.mipt.oop.actions.AllDoorsAction;
import ru.sbt.mipt.oop.event.SensorEvent;
import ru.sbt.mipt.oop.event.SensorEventType;

import java.util.List;

public class HallDoorClosedTest extends SmartHomeTestComponent {

    public HallDoorClosedTest() {
        super();

        SmartHomeTest smartHomeTest = new SmartHomeTest();
        set(smartHomeTest.getSmartHome(), smartHomeTest.getEventProcessor());
    }

    @Test
    public void closeHallDoor() {
        if (doors.isEmpty() || lights.isEmpty()) return;

        // At least one light is on
        eventProcessor.processEvent(new SensorEvent(SensorEventType.LIGHT_ON, lights.get(0).getId()));

        Door hallRoomDoor = findHallRoomDoor();

        if (hallRoomDoor == null) return;

        String hallRoomDoorId = hallRoomDoor.getId();

        // Close hall door, so all lights will be turned off
        eventProcessor.processEvent(new SensorEvent(SensorEventType.DOOR_CLOSED, hallRoomDoorId));

        lights.forEach(light -> Assert.assertEquals(Boolean.FALSE, light.isOn()));
    }

    @Test
    public void closeNotHallDoor() {
        if (doors.isEmpty() || lights.isEmpty()) return;

        // At least one light is on
        eventProcessor.processEvent(new SensorEvent(SensorEventType.LIGHT_ON, lights.get(0).getId()));

        Door notHallRoomDoor = findNotHallRoomDoor();

        if (notHallRoomDoor == null) return;

        String notHallRoomDoorId = notHallRoomDoor.getId();

        List<Boolean> lightIsOnListBefore = getLightIsOnList(lights);

        // Close not hall door, so it doesn't make anything ot lights
        eventProcessor.processEvent(new SensorEvent(SensorEventType.DOOR_CLOSED, notHallRoomDoorId));

        List<Boolean> lightIsOnListAfter = getLightIsOnList(lights);

        Assert.assertEquals(lightIsOnListBefore, lightIsOnListAfter);
    }

    private Door findHallRoomDoor() {
        Room hallRoom = getHallRoom();
        if (hallRoom != null) {
            List<Door> doors = getDoors(hallRoom);
            if (!doors.isEmpty()) {
                return doors.get(0);
            }
        }
        return null;
    }

    private Door findNotHallRoomDoor() {
        for (Room room : rooms) {
            if (!room.getName().equals(hallRoomName)) {
                List<Door> doors = getDoors(room);
                if (!doors.isEmpty()) {
                    return doors.get(0);
                }
            }
        }
        return null;
    }

}
