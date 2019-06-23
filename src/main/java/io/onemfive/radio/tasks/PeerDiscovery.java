package io.onemfive.radio.tasks;

import io.onemfive.radio.RadioPeer;
import io.onemfive.radio.RadioSensor;

import java.util.Properties;
import java.util.logging.Logger;

class PeerDiscovery extends RadioTask {

    private Logger LOG = Logger.getLogger(PeerDiscovery.class.getName());

    public PeerDiscovery(RadioSensor sensor, TaskRunner taskRunner, Properties properties, long periodicity) {
        super(sensor, taskRunner, properties, periodicity);
    }

    @Override
    boolean runTask() {
        LOG.info("Starting Radio Peer Discovery...");
        RadioPeer localNode = sensor.getLocalNode();
        if(localNode==null) {
            LOG.info("Local RadioPeer not established yet. Can't run Peer Discovery.");
            return false;
        }
        LOG.info("Completed Radio Peer Discovery.");
        return false;
    }
}
