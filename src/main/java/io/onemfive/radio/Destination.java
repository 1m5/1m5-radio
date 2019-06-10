package io.onemfive.radio;

import io.onemfive.core.util.data.Base64;
import io.onemfive.data.Hash;
import io.onemfive.data.NetworkPeer;

public class Destination extends NetworkPeer {

    private String address;
    private Hash hash;

    public Destination() {
        super(NetworkPeer.Network.SDR.name(), null, null);
    }

    public String toBase64() {
        return Base64.encode(address);
    }

    public Hash getHash() {
        return hash;
    }

}
