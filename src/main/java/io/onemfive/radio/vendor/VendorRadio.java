package io.onemfive.radio.vendor;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioSession;

import java.util.Properties;

public abstract class VendorRadio implements Radio {

    protected RadioSession session;

    @Override
    public RadioSession establishSession() {
        return session;
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
