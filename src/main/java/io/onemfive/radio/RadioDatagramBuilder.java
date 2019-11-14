package io.onemfive.radio;

public class RadioDatagramBuilder {

    public static final int DATAGRAM_MAX_SIZE = 32768;
    private RadioSession session;

    public RadioDatagramBuilder(RadioSession session) {
        this.session = session;
    }

    public RadioDatagram makeRadioDatagram(byte[] payload) {

        return null;
    }

    public byte[] serializeRadioDatagram(RadioDatagram datagram) {

        return null;
    }
}
