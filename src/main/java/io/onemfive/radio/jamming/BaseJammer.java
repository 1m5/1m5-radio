package io.onemfive.radio.jamming;

public abstract class BaseJammer implements Jammer {

    protected JammerStatus status;

    public JammerStatus getStatus() {
        return status;
    }
}
