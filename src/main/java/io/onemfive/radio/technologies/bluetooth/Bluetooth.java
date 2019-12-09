package io.onemfive.radio.technologies.bluetooth;

import io.onemfive.data.NetworkPeer;
import io.onemfive.radio.BaseRadio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
import io.onemfive.radio.tasks.TaskRunner;
import io.onemfive.sensors.SensorRequest;

import javax.bluetooth.*;
import javax.bluetooth.UUID;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Integration with JSR-82 implementation BlueCove (http://www.bluecove.org).
 * Bluecove licensed under GPL.
 */
public class Bluetooth extends BaseRadio {

    private static final Logger LOG = Logger.getLogger(Bluetooth.class.getName());

    private Properties properties;

    private Map<String, RemoteDevice> devices = new HashMap<>();
    private Map<String, List<String>> deviceServices = new HashMap<>();
    private Map<String, BluetoothPeer> peers = new HashMap<>();

    private DeviceDiscovery deviceDiscovery;
    private ServiceDiscovery serviceDiscovery;

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

    @Override
    public boolean start(Properties properties) {
        this.properties = properties;

        if(taskRunner==null) {
            taskRunner = new TaskRunner(sensor, properties);
        }

        deviceDiscovery = new DeviceDiscovery(devices, sensor, taskRunner, properties, 60 * 60 * 1000L);
        deviceDiscovery.setLongRunning(true);
        taskRunner.addTask(deviceDiscovery);

        serviceDiscovery = new ServiceDiscovery(devices, deviceServices, peers, sensor, taskRunner, properties, 30 * 1000L);
        serviceDiscovery.setLongRunning(true);
        taskRunner.addTask(serviceDiscovery);

        if(!taskRunner.isAlive()) {
            taskRunner.start();
        }
        return true;
    }

    public static void main(String[] args) {
        Bluetooth bluetooth = new Bluetooth();
        bluetooth.start(null);
        bluetooth.shutdown();
    }
}
