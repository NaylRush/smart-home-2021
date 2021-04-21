package ru.sbt.mipt.oop.event.handler;

import org.junit.jupiter.api.Test;
import ru.sbt.mipt.oop.SmartHome;
import ru.sbt.mipt.oop.event.processor.SensorEventProcessor;
import ru.sbt.mipt.oop.util.SmartHomeTestComponent;

public class SmartHomeTest extends SmartHomeTestComponent {

    private final DoorEventsTest doorEventsTest = new DoorEventsTest();
    private final HallDoorClosedTest hallDoorClosedTest = new HallDoorClosedTest();
    private final LightEventsTest lightEventsTest = new LightEventsTest();

    public SmartHomeTest() {
        childTestComponents.add(doorEventsTest);
        childTestComponents.add(hallDoorClosedTest);
        childTestComponents.add(lightEventsTest);

        set(context.getBean(SmartHome.class), context.getBean(SensorEventProcessor.class));
    }

    @Test
    public void doorEvents() {
        doorEventsTest.applyOnExistingDoor();
        doorEventsTest.applyOnNonExistingDoor();
    }

    @Test
    public void hallDoorClosed() {
        hallDoorClosedTest.closeHallDoor();
        hallDoorClosedTest.closeNotHallDoor();
    }

    @Test
    public void lightEvents() {
        lightEventsTest.applyOnExistingLight();
        lightEventsTest.applyOnNonExistingLight();
    }

}
