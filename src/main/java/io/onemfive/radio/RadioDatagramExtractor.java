package io.onemfive.radio;

public class RadioDatagramExtractor {

    private byte[] payload;
    private Destination sender;

    void extractRadioDatagram(byte[] datagram) {
        // Extract payload
        this.payload = null;
        // Extract sender
        this.sender = null;
    }

    byte[] getPayload() {
        return payload;
    }

    Destination getSender() {
        return sender;
    }
}
