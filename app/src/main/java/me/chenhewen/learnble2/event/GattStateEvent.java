package me.chenhewen.learnble2.event;

public class GattStateEvent {

    public GattStateEvent(String address, int state, int newState) {
        this.address = address;
        this.state = state;
        this.newState = newState;
    }

    public String address;
    public int state;
    public int newState;

}
