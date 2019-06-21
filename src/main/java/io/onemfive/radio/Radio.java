package io.onemfive.radio;

/**
 * Interface to use for all Radio calls.
 */
public interface Radio {

    int sendMessage(RadioDatagram message);
}
