package com.justbelieveinmyself.portscanner.fetchers;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.PortIterator;
import com.justbelieveinmyself.portscanner.core.ScanningResult;
import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.values.NotScanned;
import com.justbelieveinmyself.portscanner.core.values.NumericRangeList;
import com.justbelieveinmyself.portscanner.util.SequenceIterator;
import com.justbelieveinmyself.portscanner.util.ThreadResourceBinder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Сканирует TCP порты
 */
public class PortsFetcher extends AbstractFetcher {
    public static final String ID = "fetcher.ports";

    static final String PARAMETER_OPEN_PORTS = "openPorts";
    static final String PARAMETER_FILTERED_PORTS = "filteredPorts";

    private ScannerConfig config;
    private ThreadResourceBinder<Socket> sockets = new ThreadResourceBinder<>();

    private PortIterator portIterator;

    boolean displayAsRange = true; //сделать изменяемой?

    public PortsFetcher(ScannerConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getFullName() {
        int numPorts = new PortIterator(config.portString).size();
        return getId() + " [" + numPorts + (config.useRequestedPorts ? "+" : "") + "]";
    }

    @SuppressWarnings("unchecked")
    protected boolean scanPorts(ScanningSubject subject) {
        SortedSet<Integer> openPorts = getOpenPorts(subject);

        if (openPorts == null) {

            openPorts = new TreeSet<>();
            TreeSet<Integer> filteredPorts = new TreeSet<>();

            subject.setParameter(PARAMETER_OPEN_PORTS, openPorts);
            subject.setParameter(PARAMETER_FILTERED_PORTS, filteredPorts);

            int portTimeout = subject.getAdaptedPortTimeout();

            // копируем новый итератор вместо создания для каждого потока
            Iterator<Integer> portsIterator = portIterator.copy();
            if (config.useRequestedPorts && subject.isAnyPortRequested()) {
                portsIterator = new SequenceIterator<>(portsIterator, subject.requestedPortsIterator());
            }
            if (!portsIterator.hasNext()) {
                return false;
            }

            while (portsIterator.hasNext() && !Thread.currentThread().isInterrupted()) {
                Socket socket = sockets.bind(new Socket());
                int port = portsIterator.next();
                try {
                    socket.setReuseAddress(true);
                    socket.setReceiveBufferSize(32);

                    socket.connect(new InetSocketAddress(subject.getAddress(), port), portTimeout);
                    socket.setSoLinger(true, 0);
                    socket.setSendBufferSize(16);
                    socket.setTcpNoDelay(true);

                    if (socket.isConnected()) {
                        openPorts.add(port);
                    }
                } catch (SocketTimeoutException e) {
                    filteredPorts.add(port);
                } catch (IOException e) {
                    assert e instanceof ConnectException : e;
                } finally {
                    sockets.closeAndUnbind(socket);
                }
            }
        }
        return true;
    }

    @Override
    public Object scan(ScanningSubject subject) {
        boolean portsScanned = scanPorts(subject);
        if (!portsScanned) {
            return NotScanned.VALUE;
        }
        SortedSet<Integer> openPorts = getOpenPorts(subject);
        if (!openPorts.isEmpty()) {
            subject.setResultType(ScanningResult.ResultType.WITH_PORTS);
            return new NumericRangeList(openPorts, displayAsRange);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public SortedSet<Integer> getFilteredPorts(ScanningSubject subject) {
        return (SortedSet<Integer>) subject.getParameter(PARAMETER_FILTERED_PORTS);
    }

    @SuppressWarnings("unchecked")
    public SortedSet<Integer> getOpenPorts(ScanningSubject subject) {
        return (SortedSet<Integer>) subject.getParameter(PARAMETER_OPEN_PORTS);
    }

    @Override
    public void init() {
        this.portIterator = new PortIterator(config.portString);
    }

    @Override
    public void cleanUp() {
        sockets.close();
    }

}
