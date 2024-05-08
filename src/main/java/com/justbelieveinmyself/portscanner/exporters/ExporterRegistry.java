package com.justbelieveinmyself.portscanner.exporters;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExporterRegistry implements Iterable<Exporter> {
    private Map<String, Exporter> exporters;

    public ExporterRegistry(List<Exporter> exporters) {
        this.exporters = new LinkedHashMap<>();

        for (Exporter exporter : exporters) {
            this.exporters.put(exporter.getFilenameExtension(), exporter);
        }
    }

    @Override
    public Iterator<Exporter> iterator() {
        return exporters.values().iterator();
    }

    public Exporter createExporter(String filename) {
        int extensionPos = filename.lastIndexOf('.') + 1;
        String extension = filename.substring(extensionPos);

        Exporter exporter = exporters.get(extension);
        if (exporter == null) {
            throw new ExporterException("Неизвестный способ экспортирования");
        }
        try {
            return (Exporter) exporter.clone();
        } catch (CloneNotSupportedException e) {
            //никогда
            throw new RuntimeException(e);
        }
    }

}
