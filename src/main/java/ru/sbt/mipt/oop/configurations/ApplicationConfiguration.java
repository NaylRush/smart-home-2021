package ru.sbt.mipt.oop.configurations;

import com.coolcompany.smarthome.events.SensorEventsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sbt.mipt.oop.SmartHome;
import ru.sbt.mipt.oop.command.TurnOffLightCommandProducer;
import ru.sbt.mipt.oop.event.handler.*;
import ru.sbt.mipt.oop.io.JsonSmartHomeReader;
import ru.sbt.mipt.oop.io.SmartHomeReader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ApplicationConfiguration {

    @Bean
    SmartHome smartHome() {
        SmartHomeReader smartHomeReader = new JsonSmartHomeReader("smart-home-1.json");
        return smartHomeReader.read();
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
    List<com.coolcompany.smarthome.events.EventHandler> adaptedSensorEventHandlers(List<EventHandler> eventHandlers) {
        return eventHandlers.stream().map(SensorEventHandlerAdapter::new).collect(Collectors.toList());
    }

    @Bean
    SensorEventsManager sensorEventsManager(List<com.coolcompany.smarthome.events.EventHandler> eventHandlers) {
        SensorEventsManager eventsManager = new SensorEventsManager();
        eventHandlers.forEach(eventsManager::registerEventHandler);
        return eventsManager;
    }

}
