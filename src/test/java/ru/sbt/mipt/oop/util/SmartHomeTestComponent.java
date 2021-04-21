package ru.sbt.mipt.oop.util;

import org.junit.jupiter.api.Assertions;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import ru.sbt.mipt.oop.*;
import ru.sbt.mipt.oop.actions.AllDoorsAction;
import ru.sbt.mipt.oop.actions.AllLightsAction;
import ru.sbt.mipt.oop.actions.AllRoomsAction;
import ru.sbt.mipt.oop.event.processor.EventProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmartHomeTestComponent {

    static protected final AbstractApplicationContext context =
            new AnnotationConfigApplicationContext(TestConfiguration.class);

    protected SmartHome smartHome = null;
    protected EventProcessor eventProcessor = null;

    protected List<SmartHomeTestComponent> childTestComponents = new ArrayList<>();

    protected List<Door> doors = null;
    protected List<Light> lights = null;
    protected List<Room> rooms = null;

    protected final String hallRoomName = "hall";

    public void set(SmartHome smartHome, EventProcessor eventProcessor) {
        this.smartHome = smartHome;
        this.eventProcessor = eventProcessor;

        findAll();

        for (SmartHomeTestComponent component : childTestComponents) {
            component.set(smartHome, eventProcessor);
        }
    }

    protected void findAll() {
        rooms = getRooms(smartHome);
        doors = getDoors(smartHome);
        lights = getLights(smartHome);
    }

    protected List<Room> getRooms(Actionable actionable) {
        AllRoomsAction allRoomsAction = new AllRoomsAction();
        actionable.execute(allRoomsAction);

        return allRoomsAction.getRooms();
    }

    protected List<Door> getDoors(Actionable actionable) {
        AllDoorsAction allDoorsAction = new AllDoorsAction();
        actionable.execute(allDoorsAction);

        return allDoorsAction.getDoors();
    }

    protected List<Light> getLights(Actionable actionable) {
        AllLightsAction allLightsAction = new AllLightsAction();
        actionable.execute(allLightsAction);

        return allLightsAction.getLights();
    }

    protected Room getRoom(String name) {
        return rooms.stream().filter(room -> room.getName().equals(name)).findAny().orElse(null);
    }

    protected Room getHallRoom() {
        return getRoom(hallRoomName);
    }

    protected Door getDoor(String id) {
        return doors.stream().filter(door -> door.getId().equals(id)).findAny().orElse(null);
    }

    protected Light getLight(String id) {
        return lights.stream().filter(light -> light.getId().equals(id)).findAny().orElse(null);
    }

    protected List<Boolean> getLightIsOnList(List<Light> lights) {
        return lights.stream()
                .map(Light::isOn)
                .collect(Collectors.toList());
    }

    protected List<Boolean> getDoorIsOpenList(List<Door> doors) {
        return doors.stream()
                .map(Door::isOpen)
                .collect(Collectors.toList());
    }

    protected void doWithoutChanges(Runnable func) {
        List<Boolean> doorIsOpenListBefore = getDoorIsOpenList(doors);
        List<Boolean> lightIsOnListBefore = getLightIsOnList(lights);

        func.run();

        List<Boolean> doorIsOpenListAfter = getDoorIsOpenList(doors);
        List<Boolean> lightIsOnListAfter = getLightIsOnList(lights);

        Assertions.assertEquals(doorIsOpenListBefore.size(), doorIsOpenListAfter.size());
        Assertions.assertArrayEquals(doorIsOpenListBefore.toArray(), doorIsOpenListAfter.toArray());

        Assertions.assertEquals(lightIsOnListBefore.size(), lightIsOnListAfter.size());
        Assertions.assertArrayEquals(lightIsOnListBefore.toArray(), lightIsOnListAfter.toArray());
    }

}
