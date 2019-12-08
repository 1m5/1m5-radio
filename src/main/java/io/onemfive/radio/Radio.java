package io.onemfive.radio;

import io.onemfive.core.LifeCycle;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio extends LifeCycle {
    Boolean sendDatagram(RadioDatagram datagram, RadioSession session);
    RadioDatagram receiveDatagram(RadioSession session, Integer port);
    RadioSession getSession(Signal signal, boolean autoEstablish);
    RadioSession establishSession(Signal signal);
    Boolean closeSession(RadioSession session);
    Boolean disconnected();
}
