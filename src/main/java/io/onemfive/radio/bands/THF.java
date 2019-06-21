package io.onemfive.radio.bands;

/**
 * Terahertz High Frequency
 * From 0.1 THz (100 GHz) to 29,999.999.999.999 Hz (1 THz = 1000 GHz)
 */
public class THF extends ITUBand {

    public THF() {
        super(100000000000L,29999999999999L);
    }
}
