﻿using System;
using System.IO;
using System.Net.Sockets;
using System.Threading;
using SN_Chat.sockets;

namespace SN_Chat.chat {
    internal static class MyClient {
        private static Client _client;

        private static void Main() {
            Console.WriteLine("Enter the ip address of the Server:");
            var ipAddress = Console.ReadLine();

            _client = new Client(ipAddress, 42069);

            Console.WriteLine("Enter your Username:");
            var username = Console.ReadLine();

            _client.SendMessage(username);

            ShowInstructions();

            var reader = new Thread(Reader);
            var writer = new Thread(Writer);
            reader.Start();
            writer.Start();
            reader.Join();
            writer.Join();
        }

        private static void ShowInstructions() {
            Console.WriteLine("*****************INSTRUCTIONS*****************");
            Console.WriteLine("Write \"/leave\" to leave the chat");
            Console.WriteLine("Write \"/file\" to send a file");
            Console.WriteLine("You have to type in the directory of the file");
            Console.WriteLine("**********************************************");
        }

        private static void StopThread() {
            _client.Stop();
        }

        private static void Reader() {
            while (true) {
                string msg;
                
                try {
                    msg = _client.ReceiveMessage();
                }
                catch (SocketException) {
                    Console.Error.WriteLine("Socket was closed");
                    return;
                }


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

            while (true) {
                var msg = Console.ReadLine();

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
                        _client.SendMessage(msg);
                        break;
                }
            }
        }

        private static void SendFile() {
            Console.WriteLine("Enter the file path:");
            var filename = Console.ReadLine();

            if (!File.Exists(filename)) {
                Console.WriteLine("File not found");
                return;
            }

            _client.SendMessage("/file");

            _client.SendFile(filename);
        }

        private static void ReceiveFile() {
            Directory.CreateDirectory("download");

            var filename = _client.ReceiveFile(Directory.Exists("download") ? "download/" : "");

            Console.WriteLine("The file \"{0}\" was downloaded", filename);
        }
    }
}