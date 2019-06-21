package io.onemfive.radio.bands;

public abstract class ITUBand {

    protected final long begin;
    protected final long end;

    public ITUBand(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }
}
