package ru.sbt.mipt.oop;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sbt.mipt.oop.alarm.Alarm;
import ru.sbt.mipt.oop.alarm.AlarmActiveState;
import ru.sbt.mipt.oop.alarm.AlarmPanicState;
import ru.sbt.mipt.oop.remote.RemoteController;
import ru.sbt.mipt.oop.remote.command.*;

import java.util.List;

public class RemoteControllerTest extends SmartHomeTestComponent {

    private final AlarmTest alarmTest = new AlarmTest();
    private final Alarm alarm = alarmTest.getAlarm();
    private final String correctCode = alarmTest.getCorrectCode();

    private final RemoteController remoteController = new RemoteController();

    public RemoteControllerTest() {
        super();

        set(alarmTest.getSmartHome(), alarmTest.getEventProcessor());
    }

    @Before
    public void addRemoteController() {
        remoteController.bind("A", new TurnOnAllLightsCommand(smartHome));

        Door hallRoomDoor = findHallRoomDoor();
        if (hallRoomDoor != null) {
            remoteController.bind("B", new CloseHallDoorCommand(smartHome, hallRoomDoor.getId()));
        }

        remoteController.bind("C", new TurnOnHallLightsCommand(smartHome));
        remoteController.bind("D", new ActivateAlarmCommand(alarm, correctCode));
        remoteController.bind("1", new PanicAlarmCommand(alarm));
        remoteController.bind("2", new TurnOffAllLightsCommand(smartHome));
    }

    @Test
    public void pressNonBindButton() {
        doWithoutChanges(() -> {
            remoteController.onButtonPressed("UUU", null);
            remoteController.onButtonPressed("9", null);
        });
    }

    @Test
    public void pressTurnOnAllLightsButton() {
        if (lights.isEmpty()) return;

        // At least one light is off
        lights.get(0).setOn(false);

        remoteController.onButtonPressed("A", null);

        lights.forEach(light -> Assert.assertTrue(light.isOn()));
    }

    @Test
    public void pressCloseHallDoorButton() {
        if (lights.isEmpty()) return;

        Door hallRoomDoor = findHallRoomDoor();
        if (hallRoomDoor == null) return;

        // At least one light is off
        lights.get(0).setOn(false);

        remoteController.onButtonPressed("B", null);

        lights.forEach(light -> Assert.assertFalse(light.isOn()));
        Assert.assertFalse(hallRoomDoor.isOpen());
    }

    @Test
    public void pressTurnOnHallLightsButton() {
        pressTurnOffAllLightsButton();

        remoteController.onButtonPressed("C", null);

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
    public void pressActivateAlarmButton() {
        remoteController.onButtonPressed("D", null);

        Assert.assertTrue(alarm.getState() instanceof AlarmActiveState);

        alarmTest.doWithoutCode();
    }

    @Test
    public void pressPanicAlarmButton() {
        remoteController.onButtonPressed("1", null);

        Assert.assertTrue(alarm.getState() instanceof AlarmPanicState);

        alarmTest.doWithoutCode();
    }

    @Test
    public void pressTurnOffAllLightsButton() {
        if (lights.isEmpty()) return;

        // At least one light is on
        lights.get(0).setOn(true);

        remoteController.onButtonPressed("2", null);

        lights.forEach(light -> Assert.assertFalse(light.isOn()));
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

}
