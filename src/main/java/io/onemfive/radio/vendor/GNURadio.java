package io.onemfive.radio.vendor;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioPeer;

import java.util.Properties;

public class GNURadio implements Radio {

    public int sendMessage(RadioDatagram datagram, Properties options) {
        return 0;
    }

    public native int sendMessage(byte[] message);

}
