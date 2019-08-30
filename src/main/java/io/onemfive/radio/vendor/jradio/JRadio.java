package io.onemfive.radio.vendor.jradio;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import ru.r2cloud.jradio.RtlSdrSettings;
import ru.r2cloud.jradio.source.RtlTcp;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class JRadio implements Radio {

    private Logger LOG = Logger.getLogger(JRadio.class.getName());

    private RtlSdrSettings settings;

    private RtlTcp rtl;


    @Override
    public int sendMessage(RadioDatagram datagram, Properties options) {

        return 0;
    }

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
        return false;
    }
}
