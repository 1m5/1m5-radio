package io.onemfive.radio;

import java.util.Properties;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio {

    int sendMessage(RadioDatagram datagram, Properties options);
}
