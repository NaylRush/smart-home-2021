package ru.sbt.mipt.oop.remote.command;

import ru.sbt.mipt.oop.Light;
import ru.sbt.mipt.oop.Room;
import ru.sbt.mipt.oop.SmartHome;

public class TurnOnHallLightsCommand implements Command {

    private final SmartHome smartHome;
    private final String hallRoomName = "hall";

    public TurnOnHallLightsCommand(SmartHome smartHome) {
        this.smartHome = smartHome;
    }

    @Override
    public void execute() {
        smartHome.execute(obj -> {
            if (obj instanceof Room room) {
                if (room.getName().equals(hallRoomName)) {
                    room.execute(obj1 -> {
                                if (obj1 instanceof Light light) {
                                    light.setOn(true);
                                }
                            }
                    );
                }
            }
        });
    }

}
