package ru.sbt.mipt.oop.event;

import com.coolcompany.smarthome.events.CCSensorEvent;

import java.util.Map;

public record SensorEventAdapter(Map<String, SensorEventType> transform) {
    public SensorEvent adapt(CCSensorEvent event) {
        SensorEventType sensorEventType = transform.getOrDefault(event.getEventType(), SensorEventType.NONE);
        return new SensorEvent(sensorEventType, event.getObjectId());
    }
}
