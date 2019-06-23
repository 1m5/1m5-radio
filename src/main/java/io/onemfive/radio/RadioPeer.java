package io.onemfive.radio;

import io.onemfive.data.Addressable;
import io.onemfive.data.DID;
import io.onemfive.data.JSONSerializable;
import io.onemfive.data.NetworkPeer;

/**
 * A peer on the Radio network.
 */
public class RadioPeer extends NetworkPeer implements Addressable, JSONSerializable {

    public RadioPeer() {
        this(null, null);
    }

    public RadioPeer(String username, String passphrase) {
        super(NetworkPeer.Network.SDR.name(), username, passphrase);
    }

    @Override
    public Object clone() {
        RadioPeer clone = new RadioPeer();
        clone.did = (DID)did.clone();
        clone.network = network;
        return clone;
    }
}
