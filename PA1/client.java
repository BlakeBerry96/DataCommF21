// Name: Andrew Blake Berry

// Sources used:
// Class provided Java UDP Example
// https://www.w3schools.com/java/java_files_read.asp

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class client{
    
    public static void main(String [] args){
                     
    // UDP variables
    DatagramSocket dSocket = null;
    InetAddress address = null;
        
    // server name
    String serverName = args[0];
        
    // port to send (on server)
    int port = Integer.parseInt(args[1]);

    // file name
    String fileName = args[2];
	
	// number of packets to send
    int numPackets = 0;
    
    String message = "";
    byte[] buffer = new byte[1024];
            
            try{						       
                // Setup to transfer over UDP socket
                address = InetAddress.getByName(serverName);
                dSocket = new DatagramSocket();            
            
                char[] payload = new char[16]; // define char array to hold text
                
                byte[] sendBuf = new byte[4];  // declare byte array for sending
                sendBuf = "1234".getBytes();   // populate byte array
                DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, port); // create datagram
                dSocket.send(packet); // send the message to the server  

                // Receive the ACK from the server
                byte[] recBuf = new byte[1024];
                DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                dSocket.receive(recpacket);

                int newPort = ByteBuffer.wrap(recpacket.getData()).getInt();
                System.out.println(newPort);
                
                String file = Read(fileName);
                System.out.println(file);
                System.out.println(file.getBytes().length);
                numPackets = file.getBytes().length / 4 + 1;

                while (numPackets > 0) {
                    if (numPackets > 1) {
                        sendBuf = file.substring(0, 4).getBytes();   // populate byte array
                        file = file.substring(4);
                    }
                    else {
                        sendBuf = file.getBytes(); // populate byte array
                    }
                    packet = new DatagramPacket(sendBuf, sendBuf.length, address, newPort); // create datagram
                    dSocket.send(packet); // send the message to the server  

                    recBuf = new byte[1024];
                    recpacket = new DatagramPacket(recBuf, recBuf.length);
                    dSocket.receive(recpacket);
                    buffer = recpacket.getData();
                    message = new String(buffer, 0, recpacket.getLength());
                    System.out.println(message);

                    numPackets--;
                }

                sendBuf = ByteBuffer.allocate(4).putInt(200).array();
                packet = new DatagramPacket(sendBuf, sendBuf.length, address, newPort); // create datagram
                dSocket.send(packet); // send the message to the server  

                recBuf = new byte[1024];
                recpacket = new DatagramPacket(recBuf, recBuf.length);
                dSocket.receive(recpacket);
                buffer = recpacket.getData();
                message = new String(buffer, 0, recpacket.getLength());
                System.out.println(message);

                dSocket.close(); // close the UDP socket          
            } // end try
            catch(UnknownHostException e){
                System.out.println("Unknown host.");   
            }
            catch(IOException e){
                System.out.println("I/O error");    
            }       
    } // end main

    // Source for Read:
    // https://www.w3schools.com/java/java_files_read.asp
    public static String Read(String fileName) {
        String output = "";
        try {
          File myObj = new File(fileName);
          Scanner myReader = new Scanner(myObj);
          if (myReader.hasNextLine()) {
              output += myReader.nextLine();
          }
          while (myReader.hasNextLine()) {
            output += "\n" + myReader.nextLine();
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        return output;
      }
} // end class
