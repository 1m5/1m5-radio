package io.onemfive.radio.technologies.satellite;

import io.onemfive.radio.BaseRadio;
import io.onemfive.radio.RadioPeer;
import io.onemfive.radio.RadioSession;

public class Satellite extends BaseRadio {

    @Override
    public RadioSession establishSession(RadioPeer peer, Boolean autoConnect) {
        return null;
    }
}
