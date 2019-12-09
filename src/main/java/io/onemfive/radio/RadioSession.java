package io.onemfive.radio;

/**
 * Define the means of sending and receiving messages using the radio electromagnetic spectrum
 * over a bidirectional Socket.
 */
public interface RadioSession {

    enum Status {CONNECTING, CONNECTED, DISCONNECTED, STOPPING, STOPPED, ERRORED}

    Integer getId();
    Radio getRadio();
    boolean connect();
    boolean disconnect();
    void addSessionListener(RadioSessionListener listener);
    void removeSessionListener(RadioSessionListener listener);
    Status getStatus();
}
