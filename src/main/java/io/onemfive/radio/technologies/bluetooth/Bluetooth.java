package io.onemfive.radio.technologies.bluetooth;

import io.onemfive.data.NetworkPeer;
import io.onemfive.radio.BaseRadio;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSession;
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

    private static final Object inquiryCompletedEvent = new Object();
    private static final Object serviceSearchCompletedEvent = new Object();

    private Properties properties;

    private List<RemoteDevice> devices = new ArrayList<>();
    private Map<RemoteDevice, List<String>> deviceServices = new HashMap<>();
    private List<BluetoothPeer> peers = new ArrayList<>();

    private BluetoothDiscovery discoveryListener;

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
        if(discoverDevices()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean discoverDevices() {
        devices.clear();
        deviceServices.clear();
        peers.clear();
        try {
            discoveryListener = new BluetoothDiscovery(devices, deviceServices, peers);
            // Discover Devices
            synchronized (inquiryCompletedEvent) {
                boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, discoveryListener);
                if (started) {
                    LOG.info("wait for device inquiry to complete...");
                    inquiryCompletedEvent.wait();
                    LOG.info(deviceServices.size() + " device(s) found.");
                }
            }
            // Discover Services
            // Now start service discovery
            UUID serviceUUID = ServiceClasses.getUUID(ServiceClasses.OBEX_OBJECT_PUSH);
//        if ((properties != null) && (properties.size() > 0)) {
//            serviceUUID = new UUID(args[0], false);
//        }

            UUID[] searchUuidSet = new UUID[] { serviceUUID };
            int[] attrIDs =  new int[] {
                    0x0100 // Service name
            };

            for(RemoteDevice device : devices) {
                try {
                    discoveryListener.setCurrent(device);
                    LOG.info("search services on " + device.getBluetoothAddress() + " " + device.getFriendlyName(false));
                    LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, device, discoveryListener);
                    serviceSearchCompletedEvent.wait();
                } catch (IOException e) {
                    LOG.warning(e.getLocalizedMessage());
                }
            }

            // Notify Peer Manager via Peer Report of discovery results
            for(NetworkPeer peer : peers) {
                peerReport.report(peer);
            }
        } catch (BluetoothStateException e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        } catch (InterruptedException e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Bluetooth bluetooth = new Bluetooth();
        bluetooth.start(null);
        bluetooth.shutdown();
    }
}
