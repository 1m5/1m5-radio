package io.onemfive.radio;

public interface RadioSessionListener {
    void messageAvailable(RadioSession session, int var2, long var3);

    void disconnected(RadioSession session);

    void errorOccurred(RadioSession session, String message, Throwable throwable);
}
