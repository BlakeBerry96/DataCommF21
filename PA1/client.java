// Author: Maxwell Young
// Date: April 5, 2015
// Updated on Aug. 29, 2021

import java.io.*;
import java.net.*;

public class client{
    
    public static void main(String [] args){
                     
    // UDP variables
    DatagramSocket dSocket = null;
    InetAddress address = null;
        
    // server name
    String serverName = args[0];
        
    // port to send (on server)
    int port = Integer.parseInt(args[1]);
        
    // this is the message we'll send
	String fullmsg = "abcdef"; 
	
	// number of packets to send
	int numPackets = 5;
            
            try{
                         
				System.out.println("This is an example client code for Java. There are no guarantees attached to this code.\n");
						       
                // Setup to transfer over UDP socket
                address = InetAddress.getByName(serverName);
                dSocket = new DatagramSocket();            
            
                char[] payload = new char[16]; // define char array to hold text              
				
                while(numPackets>0){ // we'll send 5 packets
                       			
                    byte[] sendBuf = new byte[20];  // declare byte array for sending
                    sendBuf = fullmsg.getBytes();   // populate byte array
                    DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, port); // create datagram
                    dSocket.send(packet); // send the message to the server  

                    // Receive the ACK from the server
                    byte[] recBuf = new byte[1024];
                    DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
					dSocket.receive(recpacket);
                    String returnedMsg = new String(recpacket.getData());
                   					
                    // output ack to the terminal in a convoluted way, why not?
                    System.out.println("Received back from server the following: " + returnedMsg.substring(0,returnedMsg.length()));
					
					// decrement number of remaining packets to send
					numPackets--;
                    
                } // end while
                dSocket.close(); // close the UDP socket          
            } // end try
            catch(UnknownHostException e){
                System.out.println("Unknown host.");   
            }
            catch(IOException e){
                System.out.println("I/O error");    
            }       
    } // end main
} // end class
