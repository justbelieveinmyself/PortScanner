package com.justbelieveinmyself.portscanner.core;

import java.util.Iterator;

/**
 * Класс для итератора портов, определенный в формате: 2,6-10,40-100
 */
public final class PortIterator implements Iterator<Integer>, Cloneable {
    private int[] portRangeStart;
    private int[] portRangeEnd;

    private int rangeCountMinus1;
    private int rangeIndex;
    private int currentPort;

    private boolean hasNext;

    /**
     * @param portString строка с портом для парсинга
     */
    public PortIterator(String portString) {
        if (portString != null && (portString = portString.trim()).length() > 0) {
            String[] portRanges = portString.split("[\\s\t\n\r,;]+");

            portRangeStart = new int[portRanges.length + 1]; // +1 нужен для предотвращения ArrayIndexOutOfBoundsException в next методе
            portRangeEnd = new int[portRanges.length];

            for (int i = 0; i < portRanges.length; i++) {
                String range = portRanges[i];
                int dashPos = range.indexOf("-") + 1;
                int endPort = Integer.parseInt(range.substring(dashPos));
                portRangeEnd[i] = endPort;
                portRangeStart[i] = dashPos == 0 ? endPort : Integer.parseInt(range.substring(0, dashPos - 1));
                if (endPort <= 0 || endPort >= 65536) {
                    throw new NumberFormatException(endPort + " порт вне диапазона");
                }
            }

            currentPort = portRangeStart[0];
            rangeCountMinus1 = portRanges.length - 1;
            hasNext = rangeCountMinus1 >= 0;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Integer next() {
        int returnPort = currentPort++;

        if (currentPort > portRangeEnd[rangeIndex]) {
            hasNext = rangeIndex < rangeCountMinus1;
            rangeIndex++;
            currentPort = portRangeStart[rangeIndex];
        }

        return returnPort;
    }

    public int size() {
        int size = 0;

        if (portRangeStart != null) {
            for (int i = 0; i < rangeCountMinus1; i++) {
                size += portRangeEnd[i] - portRangeStart[i];
            }
        }

        return size;
    }

    /**
     * Клонирует объект
     * @return поверхностная копия
     */
    public PortIterator copy() {
        try {
            return (PortIterator) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
