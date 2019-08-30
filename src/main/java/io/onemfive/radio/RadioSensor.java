package io.onemfive.radio;

import io.onemfive.core.Config;
import io.onemfive.core.ServiceRequest;
import io.onemfive.core.ServiceStatus;
import io.onemfive.core.notification.NotificationService;
import io.onemfive.data.DID;
import io.onemfive.data.Envelope;
import io.onemfive.data.EventMessage;
import io.onemfive.data.NetworkPeer;
import io.onemfive.data.util.*;
import io.onemfive.radio.tasks.TaskRunner;
import io.onemfive.radio.vendor.GNURadio;
import io.onemfive.sensors.*;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Manages communications across the full radio electromagnetic spectrum
 * using Software Defined Radio (SDR). Defaults to GNU Radio.
 */
public class RadioSensor extends BaseSensor implements RadioSessionListener {

    private static final Logger LOG = Logger.getLogger(RadioSensor.class.getName());
    private static String LOCAL_NODE_FILE_NAME = "sdr";

    private Properties properties;
    private Radio radio;
    private RadioSession session;
    private TaskRunner taskRunner;
    private RadioPeer localNode;
    private File localNodeFile;

    public RadioSensor(SensorManager sensorManager, Envelope.Sensitivity sensitivity, Integer priority) {
        super(sensorManager, sensitivity, priority);
    }

    @Override
    public String[] getOperationEndsWith() {
        return new String[]{".sdr"};
    }

    @Override
    public String[] getURLBeginsWith() {
        return new String[]{"sdr"};
    }

    @Override
    public String[] getURLEndsWith() {
        return new String[]{".sdr"};
    }

    /**
     * Sends UTF-8 content to a Radio Peer using Software Defined Radio (SDR).
     * @param envelope Envelope containing SensorRequest as data.
     *                 To DID must contain base64 encoded Radio destination key.
     * @return boolean was successful
     */
    @Override
    public boolean send(Envelope envelope) {
        LOG.info("Sending Radio Message...");
        SensorRequest request = (SensorRequest) DLC.getData(SensorRequest.class,envelope);
        if(request == null){
            LOG.warning("No SensorRequest in Envelope.");
            request.errorCode = ServiceRequest.REQUEST_REQUIRED;
            return false;
        }
        NetworkPeer toPeer = request.to.getPeer(NetworkPeer.Network.SDR.name());
        if(toPeer == null) {
            LOG.warning("No Peer for Radio found in toDID while sending to Radio.");
            request.errorCode = SensorRequest.TO_PEER_REQUIRED;
            return false;
        }
        if(!NetworkPeer.Network.SDR.name().equals((toPeer.getNetwork()))) {
            LOG.warning("Radio requires an SDR Peer.");
            request.errorCode = SensorRequest.TO_PEER_WRONG_NETWORK;
            return false;
        }
        NetworkPeer fromPeer = request.from.getPeer(NetworkPeer.Network.SDR.name());
        LOG.info("Content to send: "+request.content);
        if(request.content == null) {
            LOG.warning("No content found in Envelope while sending to Radio.");
            request.errorCode = SensorRequest.NO_CONTENT;
            return false;
        }
        if(request.content.length() > RadioDatagramBuilder.DATAGRAM_MAX_SIZE) {
            // Just warn for now
            // TODO: Split into multiple serialized datagrams
            LOG.warning("Content longer than "+RadioDatagramBuilder.DATAGRAM_MAX_SIZE+". May have issues.");
        }

        RadioDatagramBuilder builder = new RadioDatagramBuilder(session);
        RadioDatagram datagram = builder.makeRadioDatagram(request.content.getBytes());
        Properties options = new Properties();
        if(session.sendMessage(datagram, options)) {
            LOG.info("Radio Message sent.");
            return true;
        } else {
            LOG.warning("Radio Message sending failed.");
            request.errorCode = SensorRequest.SENDING_FAILED;
            return false;
        }
    }

    /**
     * Incoming
     * @param envelope
     * @return
     */
    @Override
    public boolean reply(Envelope envelope) {
        sensorManager.sendToBus(envelope);
        return true;
    }

    /**
     * Will be called only if you register via addSessionListener().
     *
     * After this is called, the client should call receiveMessage(msgId).
     * There is currently no method for the client to reject the message.
     * If the client does not call receiveMessage() within a timeout period
     * (currently 30 seconds), the session will delete the message and
     * log an error.
     *
     * @param session session to notify
     * @param msgId message number available
     * @param size size of the message - why it's a long and not an int is a mystery
     */
    @Override
    public void messageAvailable(RadioSession session, int msgId, long size) {
        RadioDatagram d = session.receiveMessage(msgId);
        LOG.info("Received Radio Message:\n\tFrom: " + d.from.getSDRAddress());
        Envelope e = Envelope.eventFactory(EventMessage.Type.TEXT);
        DID did = new DID();
        did.addPeer(d.from);
        e.setDID(did);
        EventMessage m = (EventMessage) e.getMessage();
        m.setName(d.from.getSDRFingerprint());
        m.setMessage(d);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_PUBLISH, e);
        LOG.info("Sending Event Message to Notification Service...");
        sensorManager.sendToBus(e);
    }

    /**
     * Notify the service that the session has been terminated.
     * All registered listeners will be called.
     *
     * @param session session to report disconnect to
     */
    @Override
    public void disconnected(RadioSession session) {
        LOG.warning("Radio Session reporting disconnection.");
        routerStatusChanged();
    }

    /**
     * Notify the client that some throwable occurred.
     * All registered listeners will be called.
     *
     * @param session session to report error occurred
     * @param message message received describing error
     * @param throwable throwable thrown during error
     */
    @Override
    public void errorOccurred(RadioSession session, String message, Throwable throwable) {
        LOG.severe("Router says: "+message+": "+throwable.getLocalizedMessage());
        routerStatusChanged();
    }

    public void checkRouterStats() {
        LOG.info("RadioSensor stats:" +
                "\n\t...");
    }

    private void routerStatusChanged() {
        String statusText;
        switch (getStatus()) {
            case NETWORK_CONNECTING:
                statusText = "Testing Radio Network...";
                break;
            case NETWORK_CONNECTED:
                statusText = "Connected to Radio Network.";
                restartAttempts = 0; // Reset restart attempts
                break;
            case NETWORK_STOPPED:
                statusText = "Disconnected from Radio Network.";
                restart();
                break;
            default: {
                statusText = "Unhandled Radio Network Status: "+getStatus().name();
            }
        }
        LOG.info(statusText);
    }

    /**
     * Sets up a {@link RadioSession}, using the Radio Destination stored on disk or creating a new Radio
     * destination if no key file exists.
     */
    private void initializeSession() throws Exception {
        LOG.info("Initializing Radio Session....");
        updateStatus(SensorStatus.INITIALIZING);

        Properties sessionProperties = new Properties();
        session = new RadioSession(radio);
        session.connect();
        session.addSessionListener(this);

        taskRunner = new TaskRunner(this, sessionProperties);
        taskRunner.start();
    }

    public RadioPeer getLocalNode() {
        return localNode;
    }

    private boolean loadLocalNode() {
        // read the local node from its file if it exists
        if(localNodeFile==null) {
            localNodeFile = new File(getDirectory(), LOCAL_NODE_FILE_NAME);
        }
        if(!localNodeFile.exists()) {
            try {
                if(!localNodeFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                LOG.warning(e.getLocalizedMessage());
                return false;
            }
        }
        if(localNodeFile.length() > 0) {
            String localNodeJSON = FileUtil.readTextFile(localNodeFile.getAbsolutePath(), 1000, true);
            Map<String, Object> m = (Map<String, Object>) JSONParser.parse(localNodeJSON);
            if(m!=null && m.size()>0) {
                localNode = new RadioPeer();
                localNode.fromMap(m);
                LOG.info("RadioSensor Local Radio peer: " + localNode.toString());
            }
        }
        if(localNode==null) {
            LOG.info("Requesting new RadioSensor local Radio Peer...");

        }
        return true;
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting Radio Sensor...");
        updateStatus(SensorStatus.STARTING);
        try {
            this.properties = Config.loadFromClasspath("radio-sensor.config", p, false);
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
        }

        String radioDef = properties.getProperty(Radio.class.getName());
        if(radioDef!=null) {
            try {
                radio = (Radio)Class.forName(radioDef).newInstance();
            } catch (Exception e) {
                LOG.warning("Unable to instantiate Radio of type: "+radioDef+"\n\tException: "+e.getLocalizedMessage());
                return false;
            }
        }
        if(radio==null) {
            // Default to GNURadio
            radio = new GNURadio();
        }

        if(!loadLocalNode()) {
            return false;
        }

        if(radio.start(properties)) {
            updateStatus(SensorStatus.NETWORK_CONNECTING);
            return true;
        } else {
            updateStatus(SensorStatus.NETWORK_ERROR);
            return false;
        }
    }

    @Override
    public boolean pause() {
        return radio.pause();
    }

    @Override
    public boolean unpause() {
        return radio.unpause();
    }

    @Override
    public boolean restart() {
        return radio.restart();
    }

    @Override
    public boolean shutdown() {
        return radio.shutdown();
    }

    @Override
    public boolean gracefulShutdown() {
        return radio.gracefulShutdown();
    }
}
