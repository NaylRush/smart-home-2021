package ru.sbt.mipt.oop.remote.builder;

import ru.sbt.mipt.oop.remote.RemoteController;
import ru.sbt.mipt.oop.remote.command.Command;

import java.util.HashMap;
import java.util.Map;

public class RemoteControllerChainBuilder implements RemoteControlBuilder<RemoteController> {

    private RemoteController prevRc = null;
    private String rcId = null;
    private Map<String, Command> buttonCommand = new HashMap<>();

    @Override
    public void setRcId(String rcId) {
        this.rcId = rcId;
    }

    @Override
    public void bindButton(String buttonCode, Command command) {
        buttonCommand.put(buttonCode, command);
    }

    @Override
    public RemoteController getRemoteControl() {
        prevRc = new RemoteController(rcId, buttonCommand, prevRc);
        buttonCommand = new HashMap<>();
        return prevRc;
    }

}
