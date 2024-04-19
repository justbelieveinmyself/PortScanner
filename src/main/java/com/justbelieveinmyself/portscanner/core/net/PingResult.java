package com.justbelieveinmyself.portscanner.core.net;

import java.net.InetAddress;

/**
 * Результат пингования
 */
public class PingResult {
    InetAddress address;
    private int ttl;
    private long totalTime;
    private long longestTime;
    private int packetCount;
    private int replyCount;
    private boolean timeoutAdaptationAllowed;

    public PingResult(InetAddress address, int packetCount) {
        this.address = address;
        this.packetCount = packetCount;
    }

    public void addReply(long time) {
        replyCount++;
        if (time > longestTime) {
            totalTime = time;
        }
        totalTime += time;

        timeoutAdaptationAllowed = replyCount > 2;
    }

    public int getAverageTime() {
        return (int) (totalTime / replyCount);
    }

    public int getPacketLoss() {
        return packetCount - replyCount;
    }

    public int getPacketLossPercent() {
        if (replyCount > 0) {
            return (this.getPacketLoss() * 100) / packetCount;
        } else {
            return 100;
        }
    }

    /**
     * @return true, если хотя бы один ответ был получен
     */
    public boolean isAlive() {
        return replyCount > 0;
    }

    public void enableTimeoutAdaptation() {
        if (isAlive()) timeoutAdaptationAllowed = true;
    }

    PingResult merge(PingResult result) {
        this.packetCount += result.packetCount;
        this.replyCount += result.replyCount;
        return this;
    }

    public boolean isTimeoutAdaptationAllowed() {
        return  timeoutAdaptationAllowed;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getLongestTime() {
        return (int) longestTime;
    }

}
