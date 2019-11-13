package io.onemfive.radio.signals;

import io.onemfive.radio.Signal;

public abstract class SignalBase implements Signal {

    public static Long MIN_FREQUENCY_HZ = 3L;
    public static Long MAX_FREQUENCY_HZ = 3000000000000L; // 3 THz

    protected String name;
    protected String description;
    protected String governingBody;
    protected Long floorFrequencyHz = MIN_FREQUENCY_HZ;
    protected Long ceilingFrequencyHz = MAX_FREQUENCY_HZ;
    protected Long lastKnownFrequencyHz;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoverningBody() {
        return governingBody;
    }

    public void setGoverningBody(String governingBody) {
        this.governingBody = governingBody;
    }

    public Long getFloorFrequencyHz() {
        return floorFrequencyHz;
    }

    public void setFloorFrequencyHz(Long floorFrequencyHz) {
        this.floorFrequencyHz = floorFrequencyHz;
    }

    public Long getCeilingFrequencyHz() {
        return ceilingFrequencyHz;
    }

    public void setCeilingFrequencyHz(Long ceilingFrequencyHz) {
        this.ceilingFrequencyHz = ceilingFrequencyHz;
    }

    public Long getLastKnownFrequencyHz() {
        return lastKnownFrequencyHz;
    }

    public void setLastKnownFrequencyHz(Long lastKnownFrequencyHz) {
        this.lastKnownFrequencyHz = lastKnownFrequencyHz;
    }
}
