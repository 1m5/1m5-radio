package io.onemfive.radio.spread;

import io.onemfive.radio.contacts.QSignal;
import io.onemfive.radio.contacts.QTranslator;
import org.junit.Test;

public class SignalTest {

    @Test
    public void translate() {
        System.out.println(QTranslator.translateQuery(QSignal.QSX, "149.231"));
        System.out.println(QTranslator.translateResponse(QSignal.QSX, "148.526"));
        System.out.println(QTranslator.translateQuery(QSignal.QSY, "149.231"));
        System.out.println(QTranslator.translateResponse(QSignal.QSX, "149.231"));
        System.out.println(QTranslator.translateQuery(QSignal.QTC));
        System.out.println(QTranslator.translateResponse(QSignal.QTC));
        System.out.println(QTranslator.translateQuery(QSignal.QSL));
        System.out.println(QTranslator.translateResponse(QSignal.QSL));
    }
}
