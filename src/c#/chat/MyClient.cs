using System;
using System.IO;
using System.Threading;

namespace SN_Chat {
    class MyClient {
        private static Client client;
        private static bool stop = false;

        static void Main(string[] args) {
            String ipAddress;
            String username;

            Console.WriteLine("Enter the ip address of the Server:");
            ipAddress = Console.ReadLine();

            client = new Client(ipAddress, 42069, MessageType.MUTF8);

            Console.WriteLine("Enter your Username:");
            username = Console.ReadLine();

            client.SendMessage(username);

            ShowInstructions();

            Thread reader = new Thread(Reader);
            Thread writer = new Thread(Writer);
            reader.Start();
            writer.Start();


        }

        private static void ShowInstructions() {
            Console.WriteLine("*****************INSTRUCTIONS*****************");
            Console.WriteLine("Write \"/leave\" to leave the chat");
            Console.WriteLine("Write \"/file\" to send a file");
            Console.WriteLine("You have to type in the directory of the file");
            Console.WriteLine("**********************************************");
        }

        private static void StopThread() {
            stop = true;
            client.Stop();
        }

        private static void Reader() {
            String msg;
            while (true) {
                msg = client.ReceiveMessage();

                if (stop)
                    return;

                switch (msg) {
                    case "/file":
                        ReceiveFile();
                        break;
                    default:
                        Console.WriteLine(msg);
                        break;
                }
            }
        }

        private static void Writer() {
            Console.WriteLine("You can now write messages to the server");

            String msg;

            while (true) {
                msg = Console.ReadLine();

                switch (msg) {
                    case "/leave":
                        Console.WriteLine("Stopping...");
                        StopThread();
                        return;
                    case "/file":
                        SendFile();
                        break;
                    case "/instruction":
                        ShowInstructions();
                        break;
                    default:
                        client.SendMessage(msg);
                        break;
                }
            }
        }

        private static void ReceiveFile() {
            String filename;

            Directory.CreateDirectory("download");

            if (Directory.Exists("download")) {
                filename = client.ReceiveFile("download");
            } else {
                filename = client.ReceiveFile("");
            }

            Console.WriteLine("The file \"{0}\" was downloaded");
        }

        private static void SendFile() {
            Console.WriteLine("Enter the file path:");
            String filename = Console.ReadLine();

            if (!File.Exists(filename)) {
                Console.WriteLine("File not found");
                return;
            }
            client.SendMessage("/file");

            client.SendFile(filename);
        }
    }
}
