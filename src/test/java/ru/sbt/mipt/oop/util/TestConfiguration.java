package ru.sbt.mipt.oop.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sbt.mipt.oop.Door;
import ru.sbt.mipt.oop.SmartHome;
import ru.sbt.mipt.oop.alarm.Alarm;
import ru.sbt.mipt.oop.command.TurnOffLightCommandProducer;
import ru.sbt.mipt.oop.event.handler.*;
import ru.sbt.mipt.oop.event.processor.AlarmEventProcessorDecorator;
import ru.sbt.mipt.oop.event.processor.EventProcessor;
import ru.sbt.mipt.oop.event.processor.SensorEventProcessor;
import ru.sbt.mipt.oop.io.JsonSmartHomeReader;
import ru.sbt.mipt.oop.io.SmartHomeReader;
import ru.sbt.mipt.oop.remote.RemoteController;
import ru.sbt.mipt.oop.remote.builder.RemoteControllerChainBuilder;
import ru.sbt.mipt.oop.remote.command.*;

import java.util.List;

@Configuration
public class TestConfiguration {

    @Bean
    SmartHome smartHome() {
        SmartHomeReader smartHomeReader = new JsonSmartHomeReader("smart-home-1.json");
        return smartHomeReader.read();
    }

    @Bean
    Alarm alarm() {
        return new Alarm();
    }

    @Bean
    public String correctCode() {
        return "xkcd";
    }

    @Bean
    LightEventHandler lightEventHandler(SmartHome smartHome) {
        return new LightEventHandler(smartHome);
    }

    @Bean
    DoorEventHandler getDoorEventHandler(SmartHome smartHome) {
        return new DoorEventHandler(smartHome);
    }

    @Bean
    HallDoorEventHandler getHallDoorEventHandler(SmartHome smartHome) {
        return new HallDoorEventHandler(smartHome, new TurnOffLightCommandProducer());
    }

    @Bean
    AlarmEventHandler alarmEventHandler() {
        return new AlarmEventHandler();
    }

    @Bean
    SensorEventProcessor sensorEventProcessor(SmartHome smartHome, List<EventHandler> eventHandlers) {
        return new SensorEventProcessor(smartHome, eventHandlers);
    }

    @Bean
    AlarmEventProcessorDecorator alarmEventProcessorDecorator(EventProcessor eventProcessor,
                                                              Alarm alarm,
                                                              AlarmEventHandler alarmEventHandler) {
        return new AlarmEventProcessorDecorator(eventProcessor, alarm, alarmEventHandler);
    }

    @Bean
    RemoteController remoteController() {
        RemoteControllerChainBuilder builder = new RemoteControllerChainBuilder();

        builder.setRcId("1");
        builder.bindButton("A", turnOnAllLightsCommand());
        builder.bindButton("B", turnOffAllLightsCommand());
        builder.bindButton("C", turnOnHallLightsCommand());
        builder.getRemoteControl();

        builder.setRcId("2");
        builder.bindButton("A", closeHallDoorCommand());
        builder.getRemoteControl();

        builder.setRcId("3");
        builder.bindButton("1", activateAlarmCommand());
        builder.bindButton("2", panicAlarmCommand());

        return builder.getRemoteControl();
    }


    @Bean
    TurnOnAllLightsCommand turnOnAllLightsCommand() {
        return new TurnOnAllLightsCommand(smartHome());
    }

    @Bean
    CloseHallDoorCommand closeHallDoorCommand() {
        Door door = hallRoomDoor(smartHome());
        return door == null ? null : new CloseHallDoorCommand(smartHome(), door.getId());
    }

    @Bean
    Door hallRoomDoor(SmartHome smartHome) {
        FindHallDoorAction findHallDoorAction = new FindHallDoorAction("hall");
        smartHome.execute(findHallDoorAction);

        return findHallDoorAction.getHallRoomDoor();
    }

    @Bean
    TurnOnHallLightsCommand turnOnHallLightsCommand() {
        return new TurnOnHallLightsCommand(smartHome());
    }

    @Bean
    ActivateAlarmCommand activateAlarmCommand() {
        return new ActivateAlarmCommand(alarm(), correctCode());
    }

    @Bean
    TurnOffAllLightsCommand turnOffAllLightsCommand() {
        return new TurnOffAllLightsCommand(smartHome());
    }

    @Bean
    PanicAlarmCommand panicAlarmCommand() {
        return new PanicAlarmCommand(alarm());
    }

}
