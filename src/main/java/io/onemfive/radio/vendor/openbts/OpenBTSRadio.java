package io.onemfive.radio.vendor.openbts;

import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.vendor.VendorRadio;

import java.util.Properties;

public class OpenBTSRadio extends VendorRadio {

    private NodeManager nodeManager;

    @Override
    public int sendMessage(RadioDatagram datagram, Properties options) {
        return 0;
    }

    @Override
    public boolean start(Properties properties) {
        if(super.start(properties)) {
            nodeManager = new NodeManager();
            return true;
        } else {
            return false;
        }
    }

}
