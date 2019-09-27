package io.onemfive.radio.vendor.jradio;

import io.onemfive.core.Config;
import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
import ru.r2cloud.jradio.RtlSdrSettings;
import ru.r2cloud.jradio.source.RtlTcp;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Integration with JRadio implemented at: https://github.com/dernasherbrezon/jradio
 */
public class JRadio implements Radio {

    private Logger LOG = Logger.getLogger(JRadio.class.getName());

    private RtlSdrSettings settings;
    private Properties config;
    private String host = "127.0.0.1";
    private int port = 5000;
    private long frequency = 60*1000L;
    private long sampleRate = 120*1000L;
    private RtlTcp rtl;

    @Override
    public int sendMessage(RadioDatagram datagram, Properties options) {
        LOG.warning("JRadio sendMessage not yet implemented.");
        return 0;
    }

    @Override
    public RadioSession establishSession() {
        return null;
    }

    @Override
    public boolean start(Properties properties) {
        try {
            config = Config.loadFromClasspath("inkrypt-dcdn-dapp.config", properties, false);
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        }
        String host = properties.getProperty("io.onemfive.radio.host");
        if(host!=null) {
            this.host = host;
        }
        String portStr = properties.getProperty("io.onemfive.radio.port");
        if(portStr!=null) {
            port = Integer.parseInt(portStr);
        }
        String frequencyStr = properties.getProperty("io.onemfive.radio.frequency");
        if(frequencyStr!=null) {
            frequency = Long.parseLong(frequencyStr);
        }
        String sampleRateStr = properties.getProperty("io.onemfive.radio.sampleRate");
        if(sampleRateStr!=null) {
            sampleRate = Long.parseLong(sampleRateStr);
        }
        settings.setFrequency(frequency);
        settings.setSampleRate(sampleRate);
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
