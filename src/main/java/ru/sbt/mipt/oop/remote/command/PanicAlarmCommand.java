package ru.sbt.mipt.oop.remote.command;

import ru.sbt.mipt.oop.alarm.Alarm;

public class PanicAlarmCommand implements Command {

    private final Alarm alarm;

    public PanicAlarmCommand(Alarm alarm) {
        this.alarm = alarm;
    }

    @Override
    public void execute() {
        alarm.panic();
    }

}
