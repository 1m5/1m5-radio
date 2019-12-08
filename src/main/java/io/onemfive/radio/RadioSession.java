package io.onemfive.radio;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Define the means of sending and receiving messages using the radio electromagnetic spectrum
 * over a bidirectional Signal Socket.
 */
public class RadioSession {

    private static final Logger LOG = Logger.getLogger(RadioSession.class.getName());

    public enum Status {CONNECTING, CONNECTED, DISCONNECTED, STOPPING, STOPPED, ERRORED}

    private List<RadioSessionListener> listeners = new ArrayList<>();

    private Status status = Status.DISCONNECTED;
    private Radio radio;
    private Signal signal;
    private SignalSocket socket;

    public RadioSession(Radio radio, Signal signal) {
        this.radio = radio;
        this.signal = signal;
    }

    public boolean connect() {
        socket = new SignalSocket(radio, signal);
        status = Status.CONNECTING;
        if(socket.connect()) {
            status = Status.CONNECTED;
            for(RadioSessionListener l : listeners) {
                l.connected(this);
            }
            return true;
        } else {
            status = Status.ERRORED;
            return false;
        }
    }

    public boolean disconnect() {
        socket.close();
        status = Status.STOPPING;
        for(RadioSessionListener l : listeners) {
            l.disconnected(this);
        }
        socket = null;
        return true;
    }

    public void addSessionListener(RadioSessionListener listener) {
        listeners.add(listener);
    }

    public void removeSessionListener(RadioSessionListener listener) {
        listeners.remove(listener);
    }

    public Signal getSignal() {
        return signal;
    }

    public Status getStatus(){
        return status;
    }
}
