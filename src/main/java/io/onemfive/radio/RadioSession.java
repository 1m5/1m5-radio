package io.onemfive.radio;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class RadioSession {

    private static final Logger LOG = Logger.getLogger(RadioSession.class.getName());

    private List<RadioSessionListener> listeners = new ArrayList<>();
    private Radio radio;

    public RadioSession(Radio radio) {
        this.radio = radio;
    }

    boolean sendMessage(RadioDatagram datagram, Properties options) {
        radio.sendMessage(datagram, options);
        return false;
    }

    public RadioDatagram receiveMessage(int msgId) {
        return null;
    }

    boolean connect() {
        return false;
    }

    void addSessionListener(RadioSessionListener listener) {
        listeners.add(listener);
    }

    void removeSessionListener(RadioSessionListener listener) {
        listeners.remove(listener);
    }
}
