package com.caseyellow.server.central.domain.webSite.model;

public class Role {

    private String identifier;
    private Command command;
    private boolean mono;

    public Role() {

    }

    public Role(String identifier, Command command, boolean mono) {
        this.identifier = identifier;
        this.command = command;
        this.mono = mono;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean isMono() {
        return mono;
    }

    public void setMono(boolean mono) {
        this.mono = mono;
    }

}
