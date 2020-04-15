using System;
using System.ComponentModel;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Net.Sockets;
using System.Text;

namespace SN_Chat.sockets {
    internal class Client {
        private readonly Socket _socket;

        public Client(string ipAddress, int port) {
            var ipHostInfo = Dns.GetHostEntry(ipAddress);
            var address = ipHostInfo.AddressList[0];
            var ipe = new IPEndPoint(address, port);

            _socket = new Socket(address.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            _socket.Connect(ipe);
        }

        public void SendMessage(string msg) {
            var length = Encoding.UTF8.GetByteCount(msg);
            var byteLength = new byte[2];
            var bytes = new byte[length + byteLength.Length];

            byteLength[0] = (byte) (length >> 8);
            byteLength[1] = (byte) length;

            byteLength.CopyTo(bytes, 0);
            Encoding.UTF8.GetBytes(msg).CopyTo(bytes, byteLength.Length);

            _socket.Send(bytes);
        }

        public string ReceiveMessage() {
            var byteLength = new byte[2];

            _socket.Receive(byteLength);

            var length = (byteLength[0] << 8) + byteLength[1];
            var bytes = new byte[length];

            _socket.Receive(bytes);

            return Encoding.UTF8.GetString(bytes, 0, bytes.Length);
        }

        public void SendFile(string path) {
            var bytes = File.ReadAllBytes(path);
            SendMessage(Path.GetFileName(path));

            var byteLength = Converter.IntToByteArray(bytes.Length);
            _socket.Send(byteLength, byteLength.Length, SocketFlags.None);

            _socket.Send(bytes, bytes.Length, SocketFlags.None);
        }

        public string ReceiveFile(string path) {
            var filename = ReceiveMessage();

            var byteLength = new byte[4];
            _socket.Receive(byteLength, byteLength.Length, SocketFlags.None);
            var length = Converter.ByteArrayToInt(byteLength);

            var bytes = new byte[length];

            _socket.Receive(bytes, length, SocketFlags.None);

            File.WriteAllBytes(path + filename, bytes);

            return filename;
        }

        public void Stop() {
            _socket.Close();
        }
    }
}