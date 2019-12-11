package io.onemfive.radio.technologies.gnu;

import io.onemfive.radio.BaseRadio;
import io.onemfive.radio.RadioPeer;
import io.onemfive.radio.RadioSession;

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
    public RadioSession establishSession(RadioPeer peer, Boolean autoConnect) {
        return null;
    }
}
