package io.onemfive.radio.technologies.bluetoothle;

import io.onemfive.radio.BaseRadio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
import io.onemfive.sensors.SensorRequest;

public class BluetoothLE extends BaseRadio {

    @Override
    public RadioDatagram toRadioDatagram(SensorRequest request) {
        return null;
    }

    @Override
    public Boolean sendDatagram(RadioDatagram datagram, RadioSession session) {
        return null;
    }

    @Override
    public RadioDatagram receiveDatagram(RadioSession session, Integer port) {
        return null;
    }

    @Override
    public RadioSession establishSession() {
        return null;
    }
}
