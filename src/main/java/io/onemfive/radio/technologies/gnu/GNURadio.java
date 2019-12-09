package io.onemfive.radio.technologies.gnu;

import io.onemfive.radio.BaseRadio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
import io.onemfive.sensors.SensorRequest;

import java.util.logging.Logger;

/**
 * Wrapper for GNU Radio.
 * https://www.gnuradio.org
 * GNU Radio is licensed under the GPLv3 as of August 2019.
 *
 * @author objectorange
 */
public class GNURadio extends BaseRadio {

    private Logger LOG = Logger.getLogger(GNURadio.class.getName());

    @Override
    public RadioDatagram toRadioDatagram(SensorRequest request) {
        return null;
    }

    @Override
    public Boolean sendDatagram(RadioDatagram datagram, RadioSession session) {
        return null;
    }

    @Override
    public RadioDatagram receiveDatagram(RadioSession session, Integer port) {
        return null;
    }

    @Override
    public RadioSession establishSession() {
        return null;
    }
}
