package io.onemfive.radio.technologies.wifi;

import io.onemfive.core.LifeCycle;
import io.onemfive.radio.technologies.RadioTech;

import java.util.Properties;

public class WiFi implements LifeCycle, RadioTech {

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
