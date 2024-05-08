package com.justbelieveinmyself.portscanner.exporters;

import java.io.IOException;
import java.io.OutputStream;

public interface Exporter extends Cloneable {

    String getFilenameExtension();

    void nextAddressResults(Object[] results) throws IOException;

    void start(OutputStream outputStream) throws IOException;

    void end() throws IOException;

    Object clone() throws CloneNotSupportedException;

    void setFetchers(String[] fetcherNames) throws IOException;

}
