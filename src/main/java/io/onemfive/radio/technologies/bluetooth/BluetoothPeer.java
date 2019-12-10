package io.onemfive.radio.technologies.bluetooth;

import io.onemfive.radio.RadioPeer;

public class BluetoothPeer extends RadioPeer {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
