package ru.sbt.mipt.oop.remote.command;

import ru.sbt.mipt.oop.SmartHome;
import ru.sbt.mipt.oop.actions.CloseDoorAction;

public class CloseHallDoorCommand implements Command {

    private final SmartHome smartHome;
    private final String hallDoorId;

    public CloseHallDoorCommand(SmartHome smartHome, String hallDoorId) {
        this.smartHome = smartHome;
        this.hallDoorId = hallDoorId;
    }

    @Override
    public void execute() {
        smartHome.execute(new CloseDoorAction(hallDoorId));

        new TurnOffAllLightsCommand(smartHome).execute();
    }

}
