package ru.sbt.mipt.oop.remote;

import org.junit.Assert;
import org.junit.Test;
import ru.sbt.mipt.oop.Door;
import ru.sbt.mipt.oop.Light;
import ru.sbt.mipt.oop.Room;
import ru.sbt.mipt.oop.SmartHome;
import ru.sbt.mipt.oop.alarm.Alarm;
import ru.sbt.mipt.oop.alarm.AlarmActiveState;
import ru.sbt.mipt.oop.alarm.AlarmPanicState;
import ru.sbt.mipt.oop.alarm.AlarmTest;
import ru.sbt.mipt.oop.event.processor.AlarmEventProcessorDecorator;
import ru.sbt.mipt.oop.util.SmartHomeTestComponent;

import java.util.List;

public class RemoteControllerTest extends SmartHomeTestComponent {

    private final AlarmTest alarmTest = new AlarmTest();

    private final Alarm alarm;
    private final RemoteController remoteController;

    private final Door hallRoomDoor;

    public RemoteControllerTest() {
        alarm = context.getBean(Alarm.class);

        remoteController = context.getBean(RemoteController.class);

        hallRoomDoor = (Door) context.getBean("hallRoomDoor");

        childTestComponents.add(alarmTest);
        set(context.getBean(SmartHome.class), context.getBean(AlarmEventProcessorDecorator.class));
    }

    @Test
    public void pressNonBindButton() {
        doWithoutChanges(() -> {
            remoteController.onButtonPressed("A", null);
            remoteController.onButtonPressed("A", "3");
        });
    }

    @Test
    public void pressTurnOnAllLightsButton() {
        if (lights.isEmpty()) return;

        // At least one light is off
        lights.get(0).setOn(false);

        remoteController.onButtonPressed("A", "1");

        lights.forEach(light -> Assert.assertTrue(light.isOn()));
    }

    @Test
    public void pressTurnOffAllLightsButton() {
        if (lights.isEmpty()) return;

        // At least one light is on
        lights.get(0).setOn(true);

        remoteController.onButtonPressed("B", "1");

        lights.forEach(light -> Assert.assertFalse(light.isOn()));
    }

    @Test
    public void pressTurnOnHallLightsButton() {
        pressTurnOffAllLightsButton();

        remoteController.onButtonPressed("C", "1");

        List<Light> lights = getLights(getHallRoom());
        lights.forEach(light -> Assert.assertTrue(light.isOn()));

        Room notHallRoom = rooms.stream()
                .filter(room -> !room.getName().equals(hallRoomName))
                .findAny().orElse(null);

        if (notHallRoom == null) return;

        lights = getLights(notHallRoom);
        lights.forEach(light -> Assert.assertFalse(light.isOn()));
    }

    @Test
    public void pressCloseHallDoorButton() {
        if (lights.isEmpty() || hallRoomDoor == null) return;

        // At least one light is off
        lights.get(0).setOn(false);

        remoteController.onButtonPressed("A", "2");

        lights.forEach(light -> Assert.assertFalse(light.isOn()));
        Assert.assertFalse(hallRoomDoor.isOpen());
    }

    @Test
    public void pressActivateAlarmButton() {
        remoteController.onButtonPressed("1", "3");

        checkActiveState();

        alarmTest.doWithoutCode();

        alarmTest.correctCode();

        checkInactiveState();
    }

    @Test
    public void pressPanicAlarmButton() {
        remoteController.onButtonPressed("2", "3");

        checkPanicState();

        alarmTest.doWithoutCode();
    }

    private void checkActiveState() {
        Assert.assertTrue(alarm.getState() instanceof AlarmActiveState);
    }

    private void checkInactiveState() {
        Assert.assertTrue(alarm.getState() instanceof AlarmInactiveState);
    }

    private void checkPanicState() {
        Assert.assertTrue(alarm.getState() instanceof AlarmPanicState);
    }

}
