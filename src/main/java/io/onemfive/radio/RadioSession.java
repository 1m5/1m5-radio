package io.onemfive.radio;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class RadioSession {

    private static final Logger LOG = Logger.getLogger(RadioSession.class.getName());

    private Destination localDestination;
    private List<RadioSessionListener> listeners = new ArrayList<>();

    Destination getLocalDestination() {
        return localDestination;
    }

    Destination lookupDestination(String address) {
        Destination dest = null;

        return dest;
    }

    boolean sendMessage(Destination toDestination, byte[] payload, Properties options) {
        LOG.warning("RadioSession.sendMessage() not implemented.");
        return false;
    }

    byte[] receiveMessage(int msgId) {
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
