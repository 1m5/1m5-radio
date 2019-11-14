package io.onemfive.radio;

import io.onemfive.radio.tasks.RadioTask;
import io.onemfive.radio.tasks.TaskRunner;

import java.util.Properties;
import java.util.logging.Logger;

public class EstablishSession extends RadioTask {

    private Logger LOG = Logger.getLogger(EstablishSession.class.getName());

    private RadioSession session;

    public EstablishSession(RadioSession session, RadioSensor sensor, TaskRunner taskRunner, Properties properties) {
        super(sensor, taskRunner, properties);
        this.session = session;
        periodicity = 60 * 1000; // 1 minute
    }

    public EstablishSession(RadioSession session, RadioSensor sensor, TaskRunner taskRunner, Properties properties, long periodicity) {
        super(sensor, taskRunner, properties, periodicity);
        this.session = session;
    }

    @Override
    public boolean runTask() {
        while(session.getStatus()!= RadioSession.Status.STOPPING) {
            try {
                LOG.fine("Sleeping for "+(periodicity/1000)+" seconds..");
                synchronized (this) {
                    this.wait(periodicity);
                }
            } catch (InterruptedException ex) {
            }
            if(session.getStatus()== RadioSession.Status.DISCONNECTED) {
                session.connect();
            }
        }
        return true;
    }
}
