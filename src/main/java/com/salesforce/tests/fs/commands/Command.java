package com.salesforce.tests.fs.commands;

public class Command {

    private String commandName;
    private CommandExecutor commandExecutor;

    public Command(String commandName, CommandExecutor commandExecutor) {
        this.commandName = commandName;
        this.commandExecutor = commandExecutor;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
}
