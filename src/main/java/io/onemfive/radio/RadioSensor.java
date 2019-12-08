package io.onemfive.radio;

import io.onemfive.core.Config;
import io.onemfive.core.ServiceRequest;
import io.onemfive.core.notification.NotificationService;
import io.onemfive.data.DID;
import io.onemfive.data.Envelope;
import io.onemfive.data.EventMessage;
import io.onemfive.data.NetworkPeer;
import io.onemfive.data.util.*;
import io.onemfive.radio.signals.SignalBase;
import io.onemfive.radio.tasks.TaskRunner;
import io.onemfive.radio.technologies.RadioTech;
import io.onemfive.sensors.*;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static io.onemfive.sensors.SensorStatus.NETWORK_CONNECTED;
import static io.onemfive.sensors.SensorStatus.NETWORK_STOPPED;

/**
 * Manages communications across the full radio electromagnetic spectrum
 * using Software Defined Radio (SDR). Defaults to GNU Radio.
 */
public class RadioSensor extends BaseSensor implements RadioSessionListener {

    private static final Logger LOG = Logger.getLogger(RadioSensor.class.getName());
    private static String LOCAL_NODE_FILE_NAME = "sdr";
    private static String SIGNALS_FILE_NAME = "signals.json";

    private Properties properties;
    private Radio radio;
    private TaskRunner taskRunner;
    private RadioPeer localNode;
    private File localNodeFile;
    private File signalsFile;
    private List<Signal> signals;
    private Map<String, RadioTech> techMap = new HashMap<>();

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

        RadioPeer toRPeer = (RadioPeer)toPeer;
        Signal signal = toRPeer.mostAvailableSignal();
        if(signal==null) {
            LOG.warning("Unhandled issue #1 here.");
            return false;
        }
        RadioSession session = radio.getSession(signal, true);
        if(session==null) {
            LOG.warning("Unhandled issue #2 here.");
            return false;
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
     * @param port message port available
     */
    @Override
    public void messageAvailable(RadioSession session, Integer port) {
        RadioDatagram d = session.receiveMessage(port);
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

    @Override
    public void connected(RadioSession session) {
        LOG.info("Radio Session reporting connection.");
        updateStatus(NETWORK_CONNECTED);
        routerStatusChanged();
    }

    /**
     * Notify the service that the session has been terminated.
     * All registered listeners will be called.
     *
     * @param session session to report disconnect to
     */
    @Override
    public void disconnected(RadioSession session) {
        LOG.info("Radio Session reporting disconnection.");
        if(radio.disconnected()){
            updateStatus(NETWORK_STOPPED);
        }
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
        LOG.warning("Router says: "+message+": "+throwable.getLocalizedMessage());
        routerStatusChanged();
    }

    public void checkRouterStats() {
        LOG.info("RadioSensor status:\n\t"+getStatus().name());
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
                break;
            default: {
                statusText = "Unhandled Radio Network Status: "+getStatus().name();
            }
        }
        LOG.info(statusText);
    }

    /**
     * Sets up a list of {@link RadioSession}, using the list of active Radio Signals stored on disk or creating a new Radio
     * Signals file if no file exists.
     */
    private void initializeSessions() throws Exception {
        LOG.info("Initializing Radio Sessions....");
        updateStatus(SensorStatus.INITIALIZING);
        taskRunner = new TaskRunner(this, properties);
        if(signalsFile==null || signals==null) {
            loadSignals();
        }
        RadioSession rs;
        for(Signal s : signals) {
            rs = radio.establishSession(s);
            rs.addSessionListener(this);
            taskRunner.addTask(new EstablishSession(rs, this, taskRunner, properties));
        }
        taskRunner.start();
    }

    private boolean loadSignals() {
        signals = new ArrayList<>();
        if(signalsFile==null) {
            signalsFile = new File(getDirectory(), SIGNALS_FILE_NAME);
        }
        if(!signalsFile.exists()) {
            try {
                if(!signalsFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                LOG.warning(e.getLocalizedMessage());
                return false;
            }
        }
        if(signalsFile.length() > 0) {
            String json = FileUtil.readTextFile(signalsFile.getAbsolutePath(), 100000, true);
            Map<String, Object> mS = (Map<String, Object>) JSONParser.parse(json);
            List<Map<String,Object>> mL = (List<Map<String,Object>>)mS.get("signals");
            SignalBase s;
            for(Map<String,Object> m : mL) {
                String t = (String)m.get("type");
                try {
                    s = (SignalBase)Class.forName(t).getConstructor().newInstance();
                } catch (Exception e) {
                    LOG.warning(e.getLocalizedMessage());
                    return false;
                }
                s.fromMap(m);
                signals.add(s);
                LOG.info("RadioSensor Signal: " + s.toString());
            }
        }
        return true;
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
                radio = (Radio)Class.forName(radioDef).getConstructor().newInstance();
            } catch (Exception e) {
                LOG.warning("Unable to instantiate Radio of type: "+radioDef+"\n\tException: "+e.getLocalizedMessage());
                return false;
            }
        }

        if(!loadLocalNode()) {
            return false;
        }

        if(radio.start(properties)) {
            updateStatus(SensorStatus.NETWORK_CONNECTING);
            try {
                initializeSessions();
            } catch (Exception e) {
                LOG.warning(e.getLocalizedMessage());
            }
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
