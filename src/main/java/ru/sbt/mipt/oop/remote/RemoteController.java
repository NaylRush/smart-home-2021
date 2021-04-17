package ru.sbt.mipt.oop.remote;

import rc.RemoteControl;
import ru.sbt.mipt.oop.remote.command.Command;

import java.util.HashMap;
import java.util.Map;

public class RemoteController implements RemoteControl {

    private final Map<String, Command> buttonCodeToCommand = new HashMap<>();

    @Override
    public void onButtonPressed(String buttonCode, String rcId) {
        Command command = buttonCodeToCommand.get(buttonCode);
        if (command != null) {
            command.execute();
        }
    }

    public void bind(String buttonCode, Command command) {
        buttonCodeToCommand.put(buttonCode, command);
    }

    public void unbind(String buttonCode) {
        buttonCodeToCommand.remove(buttonCode);
    }

}
