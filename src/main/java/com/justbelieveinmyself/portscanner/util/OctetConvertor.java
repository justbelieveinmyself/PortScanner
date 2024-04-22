package com.justbelieveinmyself.portscanner.util;

public final class OctetConvertor {
    private OctetConvertor() {}

    /**
     * Конвертирует набор IPv4 октет к 32-битному
     * @param octets битовый массив содержащий IPv4 октеты
     * @param offset смещение в массиве, с которого начинаются октеты
     * @return 32-битное представление IPv4 адреса
     */
    public static int octetsToInt(byte[] octets, int offset) {
        return (((octets[offset] & 0xff) << 24) |
                ((octets[offset + 1] & 0xff) << 16) |
                ((octets[offset + 2] & 0xff) << 8) |
                (octets[offset + 3] & 0xff));
    }

}
