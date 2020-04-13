using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace SN_Chat.sockets {
    internal class Client {
        private readonly Socket _socket;
        private readonly MessageType _msgType;
        public Client(string ipAddress, int port, MessageType msgType) {
            IPHostEntry ipHostInfo = Dns.GetHostEntry(ipAddress);
            IPAddress address = ipHostInfo.AddressList[0];
            IPEndPoint ipe = new IPEndPoint(address, port);

            _socket = new Socket(address.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            _socket.Connect(ipe);
            this._msgType = msgType;
        }

        public void SendMessage(String msg) {
            byte[] byteCount = new byte[2];
            int length = Encoding.UTF8.GetByteCount(msg);

            byteCount[0] = (byte)(length >> 8);
            byteCount[1] = (byte)(length);

            byte[] bytes = new byte[length + 2];

            byteCount.CopyTo(bytes, 0);
            Encoding.UTF8.GetBytes(msg).CopyTo(bytes, byteCount.Length);

            _socket.Send(bytes);
        }

        public string ReceiveMessage() {
            byte[] byteCount = new byte[2];
            int length = 0;

            _socket.Receive(byteCount);
            foreach (var b in byteCount) {
                length = length << 8;
                length += b;
            }

            byte[] bytes = new byte[length];
            _socket.Receive(bytes);

            string msg = Encoding.UTF8.GetString(bytes, 0, bytes.Length);

            return msg;
        }

        public void SendFile(String path) {
            byte[] bytes = File.ReadAllBytes(path);
            byte[] length = new byte[4];

            SendMessage(Path.GetFileName(path));

            length[0] = (byte)(bytes.Length >> 24);
            length[1] = (byte)(bytes.Length >> 16);
            length[2] = (byte)(bytes.Length >> 8);
            length[3] = (byte)(bytes.Length);
            _socket.Send(length, length.Length, SocketFlags.None);

            _socket.Send(bytes, bytes.Length, SocketFlags.None);

        }

        public string ReceiveFile(string path) {
            string filename = ReceiveMessage();
            byte[] length = new byte[4];

            _socket.Receive(length, length.Length, SocketFlags.None);
            int fileSize = (length[0] << 24) + (length[1] << 16) + (length[2] << 8) + (length[3]);

            byte[] bytes = new byte[fileSize];

            _socket.Receive(bytes, fileSize, SocketFlags.None);

            File.WriteAllBytes(path + filename, bytes);

            return filename;
        }

        //create methods for java dataoutputstream

        public void Stop() {
            _socket.Close();
        }


    }
}
