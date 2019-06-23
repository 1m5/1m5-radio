package io.onemfive.radio;

import io.onemfive.core.ServiceRequest;
import io.onemfive.core.notification.NotificationService;
import io.onemfive.data.DID;
import io.onemfive.data.Envelope;
import io.onemfive.data.EventMessage;
import io.onemfive.data.NetworkPeer;
import io.onemfive.data.util.DLC;
import io.onemfive.data.util.DataFormatException;
import io.onemfive.radio.tasks.TaskRunner;
import io.onemfive.sensors.BaseSensor;
import io.onemfive.sensors.SensorManager;
import io.onemfive.sensors.SensorRequest;
import io.onemfive.sensors.SensorStatus;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class RadioSensor extends BaseSensor implements RadioSessionListener {

    private static final Logger LOG = Logger.getLogger(RadioSensor.class.getName());

    private RadioSession session;
    private TaskRunner taskRunner;
    private RadioPeer localNode;

    public RadioSensor(SensorManager sensorManager, Envelope.Sensitivity sensitivity, Integer priority) {
        super(sensorManager, sensitivity, priority);
    }

    @Override
    public String[] getOperationEndsWith() {
        return new String[]{".rad"};
    }

    @Override
    public String[] getURLBeginsWith() {
        return new String[]{"rad"};
    }

    @Override
    public String[] getURLEndsWith() {
        return new String[]{".rad"};
    }

    /**
     * Sends UTF-8 content to a Destination using Software Defined Radio (SDR).
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

        Destination toDestination = session.lookupDestination(toPeer.getAddress());
        if(toDestination == null) {
            LOG.warning("Radio Peer To Destination not found.");
            request.errorCode = SensorRequest.TO_PEER_NOT_FOUND;
            return false;
        }
        RadioDatagramBuilder builder = new RadioDatagramBuilder(session);
        RadioDatagram datagram = builder.makeRadioDatagram(request.content.getBytes());
        Properties options = new Properties();
        if(session.sendMessage(toDestination, datagram, options)) {
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
     * Will be called only if you register via
     * setSessionListener() or addSessionListener().
     * And if you are doing that, just use I2PSessionListener.
     *
     * If you register via addSessionListener(),
     * this will be called only for the proto(s) and toport(s) you register for.
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
        LOG.info("Message received by Radio Sensor...");
        byte[] msg = session.receiveMessage(msgId);

        LOG.info("Loading Radio Datagram...");
        RadioDatagramExtractor d = new RadioDatagramExtractor();
        d.extractRadioDatagram(msg);
        LOG.info("Radio Datagram loaded.");
        byte[] payload = d.getPayload();
        String strPayload = new String(payload);
        LOG.info("Getting sender as Radio Destination...");
        Destination sender = d.getSender();
        String address = sender.toBase64();
        String fingerprint = null;
        try {
            fingerprint = sender.getHash().toBase64();
        } catch (DataFormatException e) {
            LOG.warning(e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.warning(e.getLocalizedMessage());
        }
        LOG.info("Received Radio Message:\n\tFrom: " + address +"\n\tContent: " + strPayload);

        Envelope e = Envelope.eventFactory(EventMessage.Type.TEXT);
        NetworkPeer from = new NetworkPeer(NetworkPeer.Network.SDR.name());
        from.setAddress(address);
        from.setFingerprint(fingerprint);
        DID did = new DID();
        did.addPeer(from);
        e.setDID(did);
        EventMessage m = (EventMessage) e.getMessage();
        m.setName(fingerprint);
        m.setMessage(strPayload);
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
        session = new RadioSession();
        session.connect();

        Destination localDestination = session.getLocalDestination();
        String address = localDestination.toBase64();
        String fingerprint = localDestination.getHash().toBase64();
        LOG.info("RadioSensor Local destination key in base64: " + address);
        LOG.info("RadioSensor Local destination fingerprint (hash) in base64: " + fingerprint);

        session.addSessionListener(this);

        NetworkPeer np = new NetworkPeer(NetworkPeer.Network.SDR.name());
        np.getDid().getPublicKey().setFingerprint(fingerprint);
        np.getDid().getPublicKey().setAddress(address);

        DID localDID = new DID();
        localDID.addPeer(np);

        // Publish local Radio address
        LOG.info("Publishing Radio Network Peer's DID...");
        Envelope e = Envelope.eventFactory(EventMessage.Type.STATUS_DID);
        EventMessage m = (EventMessage) e.getMessage();
        m.setName(fingerprint);
        m.setMessage(localDID);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_PUBLISH, e);
        sensorManager.sendToBus(e);
        taskRunner = new TaskRunner(this, sessionProperties);
        taskRunner.start();
    }

    public RadioPeer getLocalNode() {
        return localNode;
    }

    @Override
    public boolean start(java.util.Properties properties) {
        return false;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean unpause() {
        return false;
    }

    @Override
    public boolean restart() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean gracefulShutdown() {
        return false;
    }
}
