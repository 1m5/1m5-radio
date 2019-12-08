package io.onemfive.radio.vendor.jradio;

import io.onemfive.core.Config;
import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
import io.onemfive.radio.vendor.VendorRadio;
import ru.r2cloud.jradio.Context;
import ru.r2cloud.jradio.RtlSdrSettings;
import ru.r2cloud.jradio.source.RtlTcp;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Integration with JRadio implemented at: https://github.com/dernasherbrezon/jradio
 */
public class JRadio extends VendorRadio {

    private Logger LOG = Logger.getLogger(JRadio.class.getName());

    private Properties config;
    private String host = "127.0.0.1";
    private int port = 5000;
    private RtlSdrSettings rtlSdrSettings;
    private Context radioContext;
    private RtlTcp tcp;

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
        rtlSdrSettings = new RtlSdrSettings();
        String frequencyStr = properties.getProperty("io.onemfive.radio.frequency");
        if(frequencyStr!=null) {
            rtlSdrSettings.setFrequency(Long.parseLong(frequencyStr));
        }
        String sampleRateStr = properties.getProperty("io.onemfive.radio.sampleRate");
        if(sampleRateStr!=null) {
            rtlSdrSettings.setSampleRate(Long.parseLong(sampleRateStr));
        }
        try {
            tcp = new RtlTcp(host, port, rtlSdrSettings);
            radioContext = tcp.getContext();

        } catch (IOException e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        if(tcp !=null) {
            try {
                tcp.close();
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
