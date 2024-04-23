package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.fetchers.Fetcher;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Хранит результаты сканирования для одного IP-адреса
 */
public class ScanningResult {

    public enum ResultType {
        UNKNOWN, DEAD, ALIVE, WITH_PORTS;

        public boolean matches(ResultType that) {
            if (this.ordinal() <= DEAD.ordinal()) {
                return that.ordinal() <= DEAD.ordinal();
            }
            return this.ordinal() <= that.ordinal();
        }
    }

    private InetAddress address;
    private String mac;

    /** Результаты сканирования */
    private Object[] values;
    /** Тип результата сканирования */
    private ResultType type;
    /** Ссылка на содержащий список */
    ScanningResultList resultList;

    /**
     * Создает новый объект и инициализирует первое значение этим адресом
     * @param numberOfFetchers количество доступных сборщиков
     */
    public ScanningResult(InetAddress address, int numberOfFetchers) {
        this.address = address;
        values = new Object[numberOfFetchers];
        values[0] = address.getHostAddress();
        type = ResultType.UNKNOWN;
    }

    /**
     * Сбрасывает сканированные данные: возвращает объект к только что созданному состоянию
     * Используется для повторного сканирования
     */
    public void reset() {
        values = new Object[values.length];
        values[0] = address.getHostAddress();
        type = ResultType.UNKNOWN;
    }

    /**
     * @return true, если результат готов (полностью сканировано)
     */
    public boolean isReady() {
        return type != ResultType.UNKNOWN;
    }

    public InetAddress getAddress() {
        return address;
    }

    /**
     * @return результат сканирования в виде неизменяемого списка, каждый элемент - результат получателя
     */
    public List<Object> getValues() {
        return Arrays.asList(values);
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public ResultType getType() {
        return type;
    }

    public void setValue(int fetcherIndex, Object value) {
        values[fetcherIndex] = value;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");
        StringBuilder details = new StringBuilder(1024);
        Iterator<?> iterator = getValues().iterator();
        List<Fetcher> fetchers = resultList.getFetchers();
        for (int i = 0; iterator.hasNext(); i++) {
            String fetcherName = fetchers.get(i).getFullName();
            details.append(fetcherName).append(":\t");
            Object value = iterator.next();
            details.append(values != null ? value : "");
            details.append(newLine);
        }
        return details.toString();
    }

}
