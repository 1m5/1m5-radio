package io.onemfive.radio.vendor.gnu;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import ru.r2cloud.jradio.RtlSdrSettings;
import ru.r2cloud.jradio.source.RtlSdr;
import ru.r2cloud.jradio.source.RtlTcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
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

    private RtlSdrSettings settings;

    private RtlTcp rtl;

    public int sendMessage(RadioDatagram datagram, Properties options) {
        return 0;
    }

    public native int sendMessage(byte[] message);

    @Override
    public boolean start(Properties properties) {
        String host = "localhost";
        int port = 5000;
        settings.setFrequency(60*1000);
        settings.setSampleRate(120*1000);
        try {
            rtl = new RtlTcp(host, port, settings);
        } catch (IOException e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        }
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
        if(rtl!=null) {
            try {
                rtl.close();
            } catch (IOException e) {
                LOG.warning(e.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        return shutdown();
    }
}
