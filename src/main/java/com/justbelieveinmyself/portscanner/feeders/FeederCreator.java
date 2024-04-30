package com.justbelieveinmyself.portscanner.feeders;

public interface FeederCreator {

    Feeder createFeeder();

    String getFeederId();

    String getFeederName();

}
