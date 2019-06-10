package io.onemfive.radio.tasks;

import io.onemfive.core.util.AppThread;
import io.onemfive.radio.RadioSensor;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Runs Radio Tasks.
 *
 * @author objectorange
 */
public class TaskRunner extends AppThread {

    private static final Logger LOG = Logger.getLogger(TaskRunner.class.getName());

    public enum Status {Running, Stopping, Shutdown}
    private static final short timeBetweenRunsMinutes = 10;

    private Status status = Status.Shutdown;
    private RadioSensor sensor;
    private Properties properties;

    public TaskRunner(RadioSensor sensor, Properties properties) {
        this.sensor = sensor;
        this.properties = properties;
    }

    @Override
    public void run() {
        status = Status.Running;
        LOG.info("RadioSensor Task Runner running...");
        while(status == Status.Running) {
            sensor.checkRouterStats();
            try {
                synchronized (this) {
                    this.wait(timeBetweenRunsMinutes * 60 * 1000);
                }
            } catch (InterruptedException ex) {
            }
        }
        LOG.info("RadioSensor Task Runner Stopped.");
        status = Status.Shutdown;
    }

    public void shutdown() {
        status = Status.Stopping;
        LOG.info("Signaled Task Runner to shutdown after all tasks complete...");
    }

}
