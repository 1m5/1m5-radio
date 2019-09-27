package io.onemfive.radio.vendor.gnuradio;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Wrapper for GNU Radio.
 * https://www.gnuradio.org
 * GNU Radio is licensed under the GPLv3 as of August 2019.
 *
 * @author objectorange
 */
public class GNURadio implements Radio {

    private Logger LOG = Logger.getLogger(GNURadio.class.getName());

    public int sendMessage(RadioDatagram datagram, Properties options) {
        LOG.warning("GNURadio sendMessage not yet implemented.");
        return 0;
    }

    public native int sendMessage(byte[] message);

    @Override
    public RadioSession establishSession() {
        return null;
    }

    @Override
    public boolean start(Properties properties) {

        return true;
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

        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        return shutdown();
    }
}
