package io.onemfive.radio;

import io.onemfive.core.LifeCycle;
import io.onemfive.sensors.SensorRequest;
import io.onemfive.sensors.peers.PeerReport;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio extends LifeCycle {
    void setPeerReport(PeerReport peerReport);
    RadioDatagram toRadioDatagram(SensorRequest request);
    Boolean sendDatagram(RadioDatagram datagram, RadioSession session);
    RadioDatagram receiveDatagram(RadioSession session, Integer port);
    RadioSession establishSession();
    RadioSession getSession(Integer sessionId);
    Boolean closeSession(Integer sessionId);
    Boolean disconnected();
}
