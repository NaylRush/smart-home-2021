package ru.sbt.mipt.oop.alarm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sbt.mipt.oop.SmartHome;
import ru.sbt.mipt.oop.SmartHomeSimulator;
import ru.sbt.mipt.oop.event.AlarmEvent;
import ru.sbt.mipt.oop.event.AlarmEventType;
import ru.sbt.mipt.oop.event.handler.SmartHomeTest;
import ru.sbt.mipt.oop.event.processor.AlarmEventProcessorDecorator;
import ru.sbt.mipt.oop.util.SmartHomeTestComponent;

public class AlarmTest extends SmartHomeTestComponent {

    private final SmartHomeTest smartHomeTest = new SmartHomeTest();

    private final Alarm alarm;
    private final String correctCode;

    public AlarmTest() {
        childTestComponents.add(smartHomeTest);

        alarm = context.getBean(Alarm.class);
        correctCode = (String) context.getBean("correctCode");

        set(context.getBean(SmartHome.class), context.getBean(AlarmEventProcessorDecorator.class));
    }

    @BeforeEach
    public void activateAlarm() {
        alarm.deactivate(correctCode);
        alarm.activate(correctCode);
    }

    @Test
    public void correctCode() {
        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, correctCode));
        checkInactiveState();

        runSmartHomeTests();
    }

    @Test
    public void doWithoutCode() {
        doWithoutChanges(() -> SmartHomeSimulator.simulateWork(eventProcessor));
        checkPanicState();
    }

    @Test
    public void incorrectCode() {
        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, "123"));
        checkPanicState();

        doWithoutCode();
    }

    @Test
    public void correctCodeAfterIncorrectTest() {
        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, "123"));
        checkPanicState();

        doWithoutCode();

        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, correctCode));
        checkInactiveState();

        runSmartHomeTests();
    }

    @Test
    public void deactivateAndActivate() {
        String code1 = correctCode;

        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, code1));
        checkInactiveState();

        runSmartHomeTests();

        String code2 = correctCode + correctCode;

        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_ACTIVATE, code2));
        checkActiveState();

        doWithoutCode();

        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, code1));
        checkPanicState();

        // like incorrect code
        doWithoutCode();

        eventProcessor.processEvent(new AlarmEvent(AlarmEventType.ALARM_DEACTIVATE, code2));
        checkInactiveState();

        runSmartHomeTests();
    }

    private void runSmartHomeTests() {
        smartHomeTest.doorEvents();
        smartHomeTest.lightEvents();
        smartHomeTest.hallDoorClosed();
    }

    private void checkActiveState() {
        Assertions.assertTrue(alarm.getState() instanceof AlarmActiveState);
    }

    private void checkInactiveState() {
        System.out.println(alarm.getState().getClass().getName());
        System.out.println(alarm.getState() instanceof AlarmInactiveState);
        Assertions.assertTrue(alarm.getState() instanceof AlarmInactiveState);
    }

    private void checkPanicState() {
        Assertions.assertTrue(alarm.getState() instanceof AlarmPanicState);
    }

}
