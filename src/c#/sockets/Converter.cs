using System;

namespace SN_Chat.sockets {
    public abstract class Converter {
        public static byte[] IntToByteArray(int v) {
            var b = new byte[4];
            b[0] = (byte) (v >> 24);
            b[1] = (byte) (v >> 16);
            b[2] = (byte) (v >> 8);
            b[3] = (byte) v;

            return b;
        }
        
        public static int ByteArrayToInt(byte[] b) {
            return (b[0] << 24) +
                   (b[1] << 16) +
                   (b[2] << 8) +
                   b[3];
        }
    }
}