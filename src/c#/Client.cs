using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace SN_Chat {
    class Client {
        private Socket socket;
        public Client(String ipAddress, int port) {
            IPHostEntry ipHostInfo = Dns.GetHostEntry(ipAddress);
            IPAddress address = ipHostInfo.AddressList[0];
            IPEndPoint ipe = new IPEndPoint(address, port);

            try {
                socket = new Socket(address.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
                socket.Connect(ipe);

            } catch (ArgumentNullException ane) {
                Console.WriteLine("ArgumentNullException : {0}", ane.ToString());
            } catch (SocketException se) {
                Console.WriteLine("SocketException : {0}", se.ToString());
            } catch (Exception e) {
                Console.WriteLine("Unexpected exception : {0}", e.ToString());
            }

        }

        public void SendMessage(String msg) {
            byte[] byteCount = new byte[2];
            int length = Encoding.UTF8.GetByteCount(msg);
            
            byteCount[0] = (byte)(length >> 8);
            byteCount[1] = (byte)(length >> 0);

            byte[] bytes = new byte[length + 2];

            byteCount.CopyTo(bytes, 0);
            Encoding.UTF8.GetBytes(msg).CopyTo(bytes, byteCount.Length);

            socket.Send(bytes);
        }

        public String ReceiveMessage(){
            byte[] byteCount = new byte[2];
            int lenght = 0;

            socket.Receive(byteCount);
            for (int i = 0; i < byteCount.Length; ++i) {
                lenght = lenght << 8;
                lenght += byteCount[i];
            }

            byte[] bytes = new byte[lenght];
            socket.Receive(bytes);

            string msg = Encoding.UTF8.GetString(bytes, 0, bytes.Length);
          
            return msg;
        }

        public void Stop() {
            socket.Close();
        }


    }
}
