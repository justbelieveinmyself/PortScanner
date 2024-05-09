package com.justbelieveinmyself.portscanner.exporters;

import java.io.IOException;

public class TXTExporter extends AbstractExporter {
    int[] padLengths;

    @Override
    public String getFilenameExtension() {
        return "txt";
    }

    @Override
    public void nextAddressResults(Object[] results) throws IOException {
        output.write(pad(results[0], padLengths[0]));
        for (int i = 1; i < results.length; i++) {
            output.write(pad(results[i], padLengths[i]));
        }
        output.println();
    }

    @Override
    public void setFetchers(String[] fetcherNames) throws IOException {
        padLengths = new int[fetcherNames.length];

        for (int i = 0; i < fetcherNames.length; i++) {
            padLengths[i] = fetcherNames[i].length() * 3;
            output.write(pad(fetcherNames[i], padLengths[i]));
        }
        output.println();
    }

    String pad(Object o, int length) {
        if (length < 16) {
            length = 16;
        }
        String s;
        if (o == null) {
            s = "";
        } else {
            s = o.toString();
        }

        if (s.length() >= length) {
            return s;
        }
        return s + "                                                                                                                  "
                .substring(0, length - s.length());
    }

}