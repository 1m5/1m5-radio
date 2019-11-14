package io.onemfive.radio.vendor;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioSession;
import io.onemfive.radio.Signal;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class VendorRadio implements Radio {

    protected Map<String,RadioSession> sessions = new HashMap<>();

    @Override
    public RadioSession getSession(Signal signal, boolean autoEstablish) {
        if(sessions.containsKey(signal.getId())) {
            return sessions.get(signal.getId());
        } else if(autoEstablish) {
            return establishSession(signal);
        } else {
            return null;
        }
    }

    @Override
    public RadioSession establishSession(Signal signal) {
        RadioSession session = new RadioSession(this, signal);
        sessions.put(signal.getId(), session);
        return session;
    }

    @Override
    public Boolean closeSession(RadioSession session) {
        if(session.disconnect()) {
            if(sessions.get(session.getSignal())!=null){
                sessions.remove(session.getSignal());
            }
            return true;
        }
        return false;
    }

    @Override
    public Boolean disconnected() {
        return sessions.size()==0;
    }

    @Override
    public boolean start(Properties properties) {
        return false;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean unpause() {
        return false;
    }

    @Override
    public boolean restart() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean gracefulShutdown() {
        return false;
    }
}
