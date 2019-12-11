package io.onemfive.radio.technologies.bluetooth;

import io.onemfive.data.content.Text;
import io.onemfive.radio.RadioDatagram;
import io.onemfive.radio.RadioSensor;
import io.onemfive.radio.RadioSession;
import io.onemfive.radio.tasks.RadioTask;
import io.onemfive.radio.tasks.TaskRunner;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class PeerDiscovery extends RadioTask {

    private static final Logger LOG = Logger.getLogger(PeerDiscovery.class.getName());

    private BluetoothPeer localPeer;
    private Bluetooth radio;
    private Map<String, BluetoothPeer> peers;

    public PeerDiscovery(BluetoothPeer localPeer, Bluetooth radio, Map<String, BluetoothPeer> peers, RadioSensor sensor, TaskRunner taskRunner, Properties properties, long periodicity) {
        super(sensor, taskRunner, properties, periodicity);
        this.localPeer = localPeer;
        this.radio = radio;
        this.peers = peers;
        startRunning = false;
    }

    @Override
    public boolean runTask() {
        if(super.runTask()) {
            started = true;
            if(peers != null && peers.size() > 0) {
                Collection<BluetoothPeer> peersList = peers.values();
                for (BluetoothPeer peer : peersList) {
                    RadioSession session = radio.establishSession(peer, true);
                    RadioDatagram datagram = new RadioDatagram();
                    datagram.from = localPeer;
                    datagram.to = peer;
                    datagram.content = new Text(("Hola Gaia!-"+System.currentTimeMillis()+"").getBytes());
                    session.sendDatagram(datagram);
                }
            }
            lastCompletionTime = System.currentTimeMillis();
            started = false;
            return true;
        } else {
            return false;
        }
    }
}
