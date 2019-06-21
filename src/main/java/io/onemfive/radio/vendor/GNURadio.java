package io.onemfive.radio.vendor;

import io.onemfive.radio.Radio;
import io.onemfive.radio.RadioDatagram;

public class GNURadio implements Radio {

    public int sendMessage(RadioDatagram datagram) {
        return 0;
    }

    public native int sendMessage(byte[] message);

}
