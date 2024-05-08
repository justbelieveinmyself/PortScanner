package com.justbelieveinmyself.portscanner.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public abstract class AbstractExporter implements Exporter {
    protected PrintWriter output;

    public void start(OutputStream outputStream) {
        output = new PrintWriter(new OutputStreamWriter(outputStream));
    }

    public void end() throws IOException {
        if (output.checkError()) {
            throw new IOException();
        }
    }

    @Override
    public Exporter clone() {
        try {
            return (Exporter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
