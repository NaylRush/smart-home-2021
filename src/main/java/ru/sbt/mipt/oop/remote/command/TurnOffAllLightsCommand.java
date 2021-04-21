package ru.sbt.mipt.oop.remote.command;

import ru.sbt.mipt.oop.Light;
import ru.sbt.mipt.oop.SmartHome;

public class TurnOffAllLightsCommand implements Command {

    private final SmartHome smartHome;

    public TurnOffAllLightsCommand(SmartHome smartHome) {
        this.smartHome = smartHome;
    }

    @Override
    public void execute() {
        smartHome.execute(obj -> {
            if (obj instanceof Light light) {
                light.setOn(false);
            }
        });
    }

}
