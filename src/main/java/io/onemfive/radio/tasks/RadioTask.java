package io.onemfive.radio.tasks;

import io.onemfive.radio.RadioSensor;

import java.util.Properties;

/**
 * A task for the Radio Sensor.
 */
public abstract class RadioTask {
    protected RadioSensor sensor;
    protected TaskRunner taskRunner;
    protected Properties properties;
    protected long periodicity = 60 * 60 * 1000; // 1 hour as default
    protected long lastCompletionTime = 0L;
    protected boolean started = false;
    protected boolean completed = false;
    protected boolean longRunning = false;

    public RadioTask(RadioSensor sensor, TaskRunner taskRunner, Properties properties) {
        this.sensor = sensor;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.lastCompletionTime = System.currentTimeMillis();
    }

    public RadioTask(RadioSensor sensor, TaskRunner taskRunner, Properties properties, long periodicity) {
        this.sensor = sensor;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.lastCompletionTime = System.currentTimeMillis();
        this.periodicity = periodicity;
    }

    public abstract boolean runTask();

    public boolean isLongRunning() {return longRunning;}

    public void setLastCompletionTime(long lastCompletionTime) {
        this.lastCompletionTime = lastCompletionTime;
    }

    public long getLastCompletionTime() { return lastCompletionTime;}

    public long getPeriodicity() {
        return periodicity;
    }
}
