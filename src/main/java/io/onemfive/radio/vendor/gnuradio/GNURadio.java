package io.onemfive.radio.vendor.gnuradio;

import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
import io.onemfive.radio.vendor.VendorRadio;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Wrapper for GNU Radio.
 * https://www.gnuradio.org
 * GNU Radio is licensed under the GPLv3 as of August 2019.
 *
 * @author objectorange
 */
public class GNURadio extends VendorRadio {

    private Logger LOG = Logger.getLogger(GNURadio.class.getName());

    @Override
    public Boolean sendDatagram(RadioDatagram datagram, RadioSession session) {
        return false;
    }

    @Override
    public RadioDatagram receiveDatagram(RadioSession session, Integer port) {
        return null;
    }

    @Override
    public boolean start(Properties properties) {
        return super.start(properties);
    }

    @Override
    public boolean shutdown() {
        return super.shutdown();
    }

    @Override
    public boolean gracefulShutdown() {
        return super.gracefulShutdown();
    }
}
