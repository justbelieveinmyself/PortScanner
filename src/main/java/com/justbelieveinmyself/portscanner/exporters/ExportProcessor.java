package com.justbelieveinmyself.portscanner.exporters;

import com.justbelieveinmyself.portscanner.core.ScanningResult;
import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.fetchers.Fetcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportProcessor {
    private final Exporter exporter;
    private final File file;

    public ExportProcessor(Exporter exporter, File file) {
        this.exporter = exporter;
        this.file = file;
    }

    public void process(ScanningResultList scanningResults) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);

            exporter.start(outputStream);

            List<Fetcher> fetchers = scanningResults.getFetchers();
            String[] fetcherNames = new String[fetchers.size()];
            int i = 0;
            for (Fetcher fetcher : fetchers) {
                fetcherNames[i++] = fetcher.getName();
            }
            exporter.setFetchers(fetcherNames);

            for (ScanningResult scanningResult : scanningResults) {
                exporter.nextAddressResults(scanningResult.getValues().toArray());
            }

            exporter.end();
        } catch (ExporterException e) {
            throw e;
        } catch (Exception e) {
            throw new ExporterException("Экспортирование не удалось", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignore) {}
            }
        }
    }

}
