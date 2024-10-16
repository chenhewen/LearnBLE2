package me.chenhewen.learnble2.event;

public class GattStateEvent {

    public GattStateEvent(int state, int newState) {
        this.state = state;
        this.newState = newState;
    }

    public int state;
    public int newState;
}
