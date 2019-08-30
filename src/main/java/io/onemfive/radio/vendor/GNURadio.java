package io.onemfive.radio.vendor;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioPeer;

import java.util.Properties;

/**
 * Wrapper for GNU Radio.
 * https://www.gnuradio.org
 * GNU Radio is licensed under the GPLv3 as of August 2019.
 *
 * @author objectorange
 */
public class GNURadio implements Radio {

    public int sendMessage(RadioDatagram datagram, Properties options) {
        return 0;
    }

    public native int sendMessage(byte[] message);

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
