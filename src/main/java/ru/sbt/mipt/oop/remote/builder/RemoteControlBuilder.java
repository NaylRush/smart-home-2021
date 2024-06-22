package ru.sbt.mipt.oop.remote.builder;

import ru.sbt.mipt.oop.remote.command.Command;

public interface RemoteControlBuilder<T> {
    void setRcId(String rcId);

    void bindButton(String buttonCode, Command command);

    T getRemoteControl();
}
