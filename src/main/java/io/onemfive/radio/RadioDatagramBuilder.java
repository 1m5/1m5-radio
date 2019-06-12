package io.onemfive.radio;

public class RadioDatagramBuilder {

    public static final int DATAGRAM_MAX_SIZE = 32768;
    private RadioSession session;

    public RadioDatagramBuilder(RadioSession session) {
        this.session = session;
    }

    public byte[] makeSDRDatagram(byte[] payload) {
        return payload;
    }
}
