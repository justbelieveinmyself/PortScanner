package com.justbelieveinmyself.portscanner.core.net;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

/**
 * JNA привязка к iphlpapi.dll для поддержки ICMP и ARP в Windows
 */
public interface WinIpHlpDll extends Library {
    WinIpHlpDll dll = Loader.load();
    class Loader {
        public static WinIpHlpDll load() {
            try {
                return Native.loadLibrary("iphlpapi", WinIpHlpDll.class);
            }
            catch (UnsatisfiedLinkError e) {
                return Native.loadLibrary("icmp", WinIpHlpDll.class);
            }
        }
    }

    class AutoOrderedStructure extends Structure {
        //необходимо для последних версий
        @Override
        protected List<String> getFieldOrder() {
            ArrayList<String> fields = new ArrayList<>();
            for (Field field : getClass().getFields()) {
                if (!isStatic(field.getModifiers())) {
                    fields.add(field.getName());
                }
            }
            return fields;
        }
    }

/*    Pointer IcmpCreateFile(); TODO: ping

    boolean IcmpCloseHandle(Pointer hIcmp);

    int IcmpSendEcho(
            Pointer hIcmp,
            IpAddrByVal destinationAddress,
            Pointer requestData,
            Pointer requestSize,
            IpOptionInformationByRef requestOptions,
            Pointer replyBuffer,
            int replySize,
            int timeout
    );*/

    int SendARP(
            IpAddrByVal destIP,
            int srcIP,
            Pointer pMacAddr,
            Pointer pPhyAddrLen
    );

    class IpAddr extends AutoOrderedStructure {
        public byte[] bytes = new byte[4];
    }

    class IpAddrByVal extends IpAddr implements Structure.ByValue {}

    class IpOptionInformation extends AutoOrderedStructure {
        public byte ttl;
        public byte tos;
        public byte flags;
        public byte optionsSize;
        public Pointer optionsData;
    }

    class IpOptionInformationByVal extends IpOptionInformation implements Structure.ByValue {}

    class IpOptionInformationByRef extends IpOptionInformation implements Structure.ByReference {}

/*
    class IcmpEchoReply extends AutoOrderedStructure {
        public IpAddrByVal address;
        public int status;
        public int roundTripTime;
        public short dataSize;
        public short reversed;
        public Pointer data;
        public IpOptionInformationByVal options;

        public IcmpEchoReply() {}

        public IcmpEchoReply(Pointer p) {
            useMemory(p);
            read();
        }

    }
*/

}
