package ru.sbt.mipt.oop.remote;

import rc.RemoteControl;
import ru.sbt.mipt.oop.remote.command.Command;

import java.util.Map;
import java.util.Objects;

public class RemoteController implements RemoteControl {

    private final Map<String, Command> buttonCodeToCommand;
    private final RemoteController nextRc;
    private final String rcId;

    public RemoteController(String rcId, Map<String, Command> buttonCodeToCommand, RemoteController nextRc) {
        this.rcId = rcId;
        this.buttonCodeToCommand = buttonCodeToCommand;
        this.nextRc = nextRc;
    }

    @Override
    public void onButtonPressed(String buttonCode, String rcId) {
        if (Objects.equals(this.rcId, rcId)) {
            Command command = buttonCodeToCommand.get(buttonCode);
            if (command != null) {
                command.execute();
            }
        } else {
            if (nextRc != null) {
                nextRc.onButtonPressed(buttonCode, rcId);
            }
        }
    }

}
