package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.Device;

import java.util.List;

public interface FeederCreator {

    Feeder createFeeder();

    Feeder createRescanFeeder(List<Device> selectedItems);

    String getFeederId();

    String getFeederName();

}
