// Name: Andrew Blake Berry

// Source used:
// Class provided Java UDP Example

import java.net.*;
import java.io.*;
import java.util.Random;
import java.nio.ByteBuffer;

public class server{
      
    public static void main(String args[]){

    // initial port of negotiation. Unfortunately, we have to control this otherwise the failure
    // to close ports by students causes problems.
    int nport = Integer.parseInt(args[0]);
		 
    // for the UDP component
    byte[] recBuf = new byte[1024];
    byte[] sendBuf = new byte[1024];
    DatagramSocket dserverSocket = null;
    Random rand = new Random();
      
            try{
                // receive the msg from client using UDP socket
                String message = "";
                byte[] buffer = new byte[1024];
                dserverSocket = new DatagramSocket(nport);
                
                recBuf = new byte[1024];
                DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                dserverSocket.receive(recpacket);
                buffer = recpacket.getData();
                
                // you cannot directly iniatlize to string. You must specify length
                // in order to avoid junk being printed to file.
                message = new String(buffer, 0, recpacket.getLength());
                
                InetAddress IPAddress = recpacket.getAddress();
                int port = recpacket.getPort();
                int newPort = rand.nextInt(64512) + 1024;
                sendBuf = ByteBuffer.allocate(4).putInt(newPort).array();
                DatagramPacket sendpacket = new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);              
                dserverSocket.send(sendpacket);
                System.out.println("The random port chosen is " + newPort);
                dserverSocket.close(); //close socket

                dserverSocket = new DatagramSocket(newPort);
                String file = "";

                while (ByteBuffer.wrap(buffer).getInt() != 200) {
                    recpacket = new DatagramPacket(recBuf, recBuf.length);
                    dserverSocket.receive(recpacket);
                    buffer = recpacket.getData();
                    message = new String(buffer, 0, recpacket.getLength());
                    sendBuf = (message.toUpperCase()).getBytes();  // convert payload to uppercase and repack
                    sendpacket = new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);              
                    dserverSocket.send(sendpacket);
                    if (ByteBuffer.wrap(buffer).getInt() != 200) {
                        file += message;
                    }
                }
                
                dserverSocket.close(); //close socket

                BufferedWriter writer = new BufferedWriter(new FileWriter("blah.txt"));
                writer.write(file);
                
                writer.close();
             
        
            }// end try
            catch(SocketTimeoutException t){ // catch a timeout event
                System.out.println("Socket time out event");   
            }
            catch(IOException e){ // catch IO exception
                System.out.println("I/O exception");
            }     
        }   
}// end class
