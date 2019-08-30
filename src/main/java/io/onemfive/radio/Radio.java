package io.onemfive.radio;

import io.onemfive.core.LifeCycle;

import java.util.Properties;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio extends LifeCycle {

    int sendMessage(RadioDatagram datagram, Properties options);
}
