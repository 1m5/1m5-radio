package io.onemfive.radio.jamming;

public class BaseJammer implements Jammer {

    protected JammerStatus status;

    public JammerStatus getStatus() {
        return status;
    }
}
