package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.*;
import static com.justbelieveinmyself.portscanner.util.OctetConvertor.octetsToInt;

public class RangeFeeder extends AbstractFeeder {
    private InetAddress startIP;
    private InetAddress endIP;
    private InetAddress originalEndIP;
    private InetAddress currentIP;
    boolean isReverse;

    double percentageComplete;
    double percentageIncrement;

    public RangeFeeder() {}

    public RangeFeeder(String startIP, String endIP) {
        try {
            this.startIP = this.currentIP = InetAddress.getByName(startIP);
            this.endIP = this.originalEndIP = InetAddress.getByName(endIP);
            initInterfaces(this.startIP);
            this.isReverse = false;
        } catch (UnknownHostException e) {
            throw new FeederException("malformedIP");
        }
        if (this.startIP.getClass() != this.endIP.getClass()) {
            throw new FeederException("differentProtocols");
        }
        if (greaterThan(this.startIP, this.endIP)) {
            this.isReverse = true;
            this.endIP = decrement(decrement(this.endIP));
        }
        initPercentageIncrement();
        this.endIP = increment(this.endIP);
    }

    @Override
    public String getId() {
        return "feeder.range";
    }

    public void initPercentageIncrement() {
        byte[] endAddress = this.endIP.getAddress();
        long rawEndIP = octetsToInt(endAddress, endAddress.length - 4);
        long rawStartIP = octetsToInt(this.startIP.getAddress(), endAddress.length - 4);

        rawEndIP = rawStartIP >= 0 ? rawStartIP : rawEndIP + Integer.MAX_VALUE;
        rawStartIP = rawStartIP >= 0 ? rawStartIP : rawStartIP + Integer.MAX_VALUE;

        percentageIncrement = Math.abs(100.0 / (rawEndIP - rawStartIP + 1));
        percentageComplete = 0;
    }

    @Override
    public boolean hasNext() {
        return !currentIP.equals(endIP);
    }

    @Override
    public ScanningSubject next() {
        percentageComplete += percentageIncrement;
        InetAddress prevIP = this.currentIP;
        if (this.isReverse) {
            this.currentIP = decrement(prevIP);
        } else {
            this.currentIP = increment(prevIP);
        }
        return subject(prevIP);
    }

    @Override
    public int percentageComplete() {
        return (int) Math.round(percentageComplete);
    }

    @Override
    public String getInfo() {
        return startIP.getHostAddress() + " - " + originalEndIP.getHostAddress();
    }

}
