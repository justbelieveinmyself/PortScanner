package com.justbelieveinmyself.portscanner.util;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Collections.list;

public class InetAddressUtils {
    private static final Logger LOG = Logger.getLogger(InetAddressUtils.class.getSimpleName());

    public static InterfaceAddress getLocalInterface() {
        InterfaceAddress anyAddress = null;
        try {

            List<NetworkInterface> interfaces = getNetworkInterfaces()
                    .stream()
                    .filter(i -> i.getParent() == null && !i.isVirtual())
                    .toList();

            for (NetworkInterface networkInterface : interfaces) {

                try {
                    if (networkInterface.getHardwareAddress() == null) {
                        continue;
                    }
                } catch (SocketException ignore) {}

                for (InterfaceAddress ifAddr : networkInterface.getInterfaceAddresses()) {
                    anyAddress = ifAddr;
                    InetAddress address = ifAddr.getAddress();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return ifAddr;
                    }
                }

            }
        } catch (SocketException e) {
            LOG.info("Невозможно определить сетевой интерфейс " + e);
        }

        return anyAddress;
    }

    private static List<NetworkInterface> getNetworkInterfaces() throws SocketException {
        ArrayList<NetworkInterface> interfaces = list(NetworkInterface.getNetworkInterfaces());
        return interfaces;
    }

    public static NetworkInterface getInterface(InterfaceAddress address) {
        try {
            if (address == null) return null;
            return NetworkInterface.getByInetAddress(address.getAddress());
        } catch (SocketException e) {
            return null;
        }
    }

    public static NetworkInterface getInterface(InetAddress address, Stream<NetworkInterface> interfaceStream) {
        try {
            if (address == null) return null;
            return interfaceStream.filter(i -> i.getInterfaceAddresses().stream().anyMatch(ifAddr -> {
                InetAddress netmask = parseNetmask(ifAddr.getNetworkPrefixLength());
                return startRangeByNetmask(address, netmask).equals(startRangeByNetmask(ifAddr.getAddress(), netmask));
            })).findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static InetAddress startRangeByNetmask(InetAddress address, InetAddress netmask) {
        byte[] addressBytes = address.getAddress();
        byte[] netmaskBytes = netmask.getAddress();
        for (int i = 0; i < addressBytes.length; i++) {
            addressBytes[i] = i < netmaskBytes.length ? (byte) (addressBytes[i] & netmaskBytes[i]) : 0;
        }
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (
                UnknownHostException e) {    // this should never happen as we are modifying the same bytes received from the InetAddress
            throw new IllegalArgumentException(e);
        }
    }

    public static InetAddress endRangeByNetmask(InetAddress address, InetAddress netmask) {
        byte[] netmaskBytes = netmask.getAddress();
        byte[] addressBytes = address.getAddress();
        for (int i = 0; i < addressBytes.length; i++) {
            addressBytes[i] = (byte) (i < netmaskBytes.length ? (addressBytes[i] | ~(netmaskBytes[i])) : 255);
        }
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            //никогда не выйдет
            throw new IllegalArgumentException(e);
        }
    }

    public static InetAddress parseNetmask(String netmaskString) throws UnknownHostException {
        if (netmaskString.startsWith("/")) {
            int totalBits = Integer.parseInt(netmaskString.substring(1));
            return parseNetmask(totalBits);
        }

        netmaskString = netmaskString.replaceAll("\\.\\.", ".255.");
        netmaskString = netmaskString.replaceAll("\\.\\.", ".255.");
        return InetAddress.getByName(netmaskString);
    }

    public static InetAddress parseNetmask(int prefixBits) {
        byte[] mask = new byte[prefixBits > 32 ? 16 : 4];
        for (int i = 0; i < mask.length; i++) {
            int curByteBits = Math.min(prefixBits, 8);
            prefixBits -= curByteBits;
            mask[i] = (byte) ((((1 << curByteBits) - 1) << (8 - curByteBits)) & 0xFF);
        }
        try {
            return InetAddress.getByAddress(mask);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetworkInterface getInterface(InetAddress address) {
        try {
            return getInterface(address, NetworkInterface.networkInterfaces());
        } catch (SocketException e) {
            return null;
        }
    }

    public static boolean isLikelyBroadcast(InetAddress address, InterfaceAddress ifAddr) {
        byte[] bytes = address.getAddress();
        int last = bytes.length - 1;
        if (ifAddr != null) {
            return address.equals(ifAddr.getBroadcast()) || bytes[last] == 0 && Arrays.equals(bytes, 0, last, ifAddr.getAddress().getAddress(), 0, last);
        }
        return bytes[last] == 0 || bytes[last] == (byte) 0xFF;
    }

    public static InterfaceAddress matchingAddress(NetworkInterface netIf, Class<? extends InetAddress> addressClass) {
        if (netIf == null) return null;
        return netIf.getInterfaceAddresses().stream().filter(i -> i.getAddress().getClass() == addressClass).findFirst().orElse(null);
    }

    public static boolean greaterThan(InetAddress inetAddress1, InetAddress inetAddress2) {
        byte[] address1 = inetAddress1.getAddress();
        byte[] address2 = inetAddress2.getAddress();
        for (int i = 0; i < address1.length; i++) {
            if ((address1[i] & 0xFF) > (address2[i] & 0xFF)) {
                return true;
            } else if ((address1[i] & 0xFF) < (address2[i] & 0xFF)) {
                break;
            }
        }
        return false;
    }

    public static InetAddress increment(InetAddress address) {
        return modifyInetAddress(address, true);
    }

    public static InetAddress decrement(InetAddress address) {
        return modifyInetAddress(address, false);
    }

    /**
     * Увеличивает или уменьшает IP адрес на 1
     * @return увеличенный/уменьшенный IP адрес
     */
    private static InetAddress modifyInetAddress(InetAddress address, boolean isIncrement) {
        try {
            byte[] newAddress = address.getAddress();
            for (int i = newAddress.length - 1; i >= 0; i--) {
                if (isIncrement) {
                    if (++newAddress[i] != 0x00) {
                        break;
                    }
                } else {
                    if (--newAddress[i] != 0x00) {
                        break;
                    }
                }
            }
            return InetAddress.getByAddress(newAddress);
        } catch (UnknownHostException e) {
            //никогда не случится
            assert false : e;
            return null;
        }
    }

}
