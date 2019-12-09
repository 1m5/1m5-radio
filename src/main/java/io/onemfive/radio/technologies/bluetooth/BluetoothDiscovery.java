package io.onemfive.radio.technologies.bluetooth;

import io.onemfive.radio.RadioPeer;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BluetoothDiscovery implements DiscoveryListener {

    private static final Logger LOG = Logger.getLogger(BluetoothDiscovery.class.getName());

    private List<RemoteDevice> devices;
    private Map<RemoteDevice, List<String>> deviceServices;
    private List<BluetoothPeer> peers;

    private RemoteDevice current;

    public BluetoothDiscovery(List<RemoteDevice> devices, Map<RemoteDevice, List<String>> deviceServices, List<BluetoothPeer> peers) {
        this.devices = devices;
        this.deviceServices = deviceServices;
        this.peers = peers;
    }

    public void setCurrent(RemoteDevice current) {
        this.current = current;
    }

    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        String msg = "Device " + remoteDevice.getBluetoothAddress() + " found.";
        devices.add(remoteDevice);
        try {
            msg += "\r\nName: "+remoteDevice.getFriendlyName(false);
        } catch (IOException e) {
            LOG.warning(e.getLocalizedMessage());
        }
        LOG.info(msg);
    }

    @Override
    public void inquiryCompleted(int discType) {
        LOG.info("Bluetooth inquiry completed.");
    }

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] serviceRecords) {
        LOG.info("Services returned for transID: "+transID);
        for (int i = 0; i < serviceRecords.length; i++) {
            String url = serviceRecords[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) {
                LOG.info("Not a NoAuthN-NoEncrypt service.");
                continue;
            }
            if(deviceServices.get(current)==null) {
                deviceServices.put(current, new ArrayList<>());
            }
            deviceServices.get(current).add(url);
            DataElement serviceName = serviceRecords[i].getAttributeValue(0x0100);
            if (serviceName != null) {
                LOG.info("service " + serviceName.getValue() + " found " + url);
                if("1M5".equals(serviceName.getValue())) {
                    BluetoothPeer peer = new BluetoothPeer();
                    peer.setAddress(url);
                    peer.setLocal(false);
                    peers.add(peer);
                }
            } else {
                LOG.info("service found " + url);
            }
        }
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        LOG.info("Bluetooth services search completed.");
    }

}
