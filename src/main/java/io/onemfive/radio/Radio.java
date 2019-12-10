package io.onemfive.radio;

import io.onemfive.core.LifeCycle;
import io.onemfive.sensors.peers.PeerReport;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio extends LifeCycle {
    void setPeerReport(PeerReport peerReport);
    RadioSession establishSession(RadioPeer peer, boolean autoConnect);
    RadioSession getSession(Integer sessionId);
    Boolean closeSession(Integer sessionId);
    Boolean disconnected();
}
