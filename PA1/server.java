// Author: Maxwell Young
// Date: April 5, 2015

import java.net.*;
import java.io.*;
import java.util.Random;

public class server{
      
    public static void main(String args[]){  
        
	// variable for print to screen
	int verbose = 1;

    // initial port of negotiation. Unfortunately, we have to control this otherwise the failure
    // to close ports by students causes problems.
    int nport = Integer.parseInt(args[0]);
         
	// number of packets to receive
	int numPackets = 5;	 
		 
    // for the UDP component
    byte[] recBuf = new byte[1024];
    byte[] sendBuf = new byte[1024];
    DatagramSocket dserverSocket = null;
      
            try{
               
               
                // receive the msg from client using UDP socket
                String message = "";
                byte[] buffer = new byte[1024];
                dserverSocket = new DatagramSocket(nport);
		
                if(verbose == 1){ // for testing purposes
                System.out.println("Waiting for client on port " + dserverSocket.getLocalPort() + "...");
                }
    
			   
                while(numPackets > 0){  
                    recBuf = new byte[1024];
                    DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                    dserverSocket.receive(recpacket);
                    buffer = recpacket.getData();
                    
                    // you cannot directly iniatlize to string. You must specify length
                    // in order to avoid junk being printed to file.
                    message = new String(buffer, 0, recpacket.getLength());
                      
                    if(verbose == 1){ // used for testing
	                    System.out.println("Server received: " + message);                    
                    }
                    
					
                    InetAddress IPAddress = recpacket.getAddress();
                    int port = recpacket.getPort();
                    sendBuf = (message.toUpperCase()).getBytes();  // convert payload to uppercase and repack
                    DatagramPacket sendpacket = new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);              
                    dserverSocket.send(sendpacket);
					
                    if(verbose == 1){ // used for testing
	                    System.out.println("Server sent back ACK: " + message.toUpperCase());                    
                    }
					
					// decrement number of remaining packets to receive
					numPackets--;
                } // end while
                
                dserverSocket.close(); //close socket
             
        
            }// end try
            catch(SocketTimeoutException t){ // catch a timeout event
                System.out.println("Socket time out event");   
            }
            catch(IOException e){ // catch IO exception
                System.out.println("I/O exception");
            }     
        }   
}// end class
