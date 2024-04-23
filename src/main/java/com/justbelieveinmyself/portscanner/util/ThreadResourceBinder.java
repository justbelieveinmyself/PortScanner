package com.justbelieveinmyself.portscanner.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadResourceBinder<T> {
    private Map<Long, T> resources = new ConcurrentHashMap<>(256);

    public T bind(T resource) {
        resources.put(Thread.currentThread().getId(), resource);
        return resource;
    }

    public void close() {
        for (T resource : resources.values()) {
            close(resource);
        }
        resources.clear();
    }

    private void close(T resource) {
        if (resource instanceof DatagramSocket) {
            ((DatagramSocket) resource).close();
        } else if (resource instanceof Socket) {
            try {
                ((Socket) resource).close();
            } catch (IOException ignored) {}
        } else if (resource instanceof Closeable) {
            try {
                ((Closeable) resource).close();
            } catch (IOException ignored) {}
        }
    }

    public void closeAndUnbind(T resource) {
        close(resource);
        resources.remove(Thread.currentThread().getId());
    }

}
