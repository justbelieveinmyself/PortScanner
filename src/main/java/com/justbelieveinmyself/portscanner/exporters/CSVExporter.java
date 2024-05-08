package com.justbelieveinmyself.portscanner.exporters;

import java.io.IOException;

public class CSVExporter extends AbstractExporter {
    static final char DELIMITER = ',';
    static final char DELIMITER_ESCAPED = '.'; //

    @Override
    public void setFetchers(String[] fetcherNames) throws IOException {
        output.write(fetcherNames[0]);
        for (int i = 1; i < fetcherNames.length; i++) {
            output.write(DELIMITER);
            output.write(csvString(fetcherNames[i]));
        }
        output.println();
    }

    @Override
    public String getFilenameExtension() {
        return "csv";
    }

    @Override
    public void nextAddressResults(Object[] results) {
        output.write(csvString(results[0]));
        for (int i = 1; i < results.length; i++) {
            Object result = results[i];
            output.write(DELIMITER);
            output.write(csvString(result));
        }
        output.println();
    }

    /**
     * @return безопасную строку, которая будет как единичная запись в .csv (не содержит разделителя запятой)
     */
    String csvString(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString().replace(DELIMITER, DELIMITER_ESCAPED); //заменяется все запятые на другой знак
    }

}
/*

IP,MAC Address,Ports,Filtered Ports
192.168.56.1,0A:00:27:00:00:07,445,440-444.446-450
192.168.56.2,[n/a],[n/a],440-450
192.168.56.3,[n/a],[n/a],440-450
192.168.56.4,[n/a],[n/a],440-450
192.168.56.5,[n/a],[n/a],440-450
192.168.56.6,[n/a],[n/a],440-450

 */