package io.onemfive.radio;

import io.onemfive.core.LifeCycle;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio extends LifeCycle {
    void sendDatagram(RadioDatagram datagram);
    RadioSession getSession(Signal signal, boolean autoEstablish);
    RadioSession establishSession(Signal signal);
    Boolean closeSession(RadioSession session);
    Boolean disconnected();
}
