// Name: Andrew Blake Berry

// Sources used:
// Class provided Java UDP Example
// https://stackoverflow.com/questions/17940423/send-object-over-udp-in-java

import java.net.*;
import java.io.*;

public class server{
      
    public static void main(String args[]) {

    // initial port of negotiation. Unfortunately, we have to control this otherwise the failure
    // to close ports by students causes problems.
    int nport = Integer.parseInt(args[0]);
    String filename = args[1];
		 
    // for the UDP component
    byte[] recBuf = new byte[1024];
    DatagramSocket dserverSocket = null;
      
            try{
                // receive the msg from client using UDP socket
                dserverSocket = new DatagramSocket(nport);
                DatagramSocket dSocket = new DatagramSocket();
                
                recBuf = new byte[1024];
                int squ = -1;
                String file = "";
                InetAddress ip;
                String log = "";
                packet lastAck = null;

                packet recv = new packet(0, 0, 0, "");
                while (recv.getType() != 3) {
                    recv = new packet(0, 0, 0, "");
                    DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                    dserverSocket.receive(recpacket);
                    recv = deserializePacket(recpacket.getData());
                    log += recv.getSeqNum() + "\n";
                    if (recv.getType() == 3) {
                        packet pack = new packet(2, recv.getSeqNum(), 0, "EOT");
                        byte[] buff = serializePacket(pack);
                        DatagramPacket packet = new DatagramPacket(buff, buff.length, recpacket.getAddress(), recpacket.getPort());
                        dSocket.send(packet);
                        break;
                    }
                    else {
                        if (recv.getSeqNum() == (squ+1)%8) {
                            file += recv.getData();
                            squ = recv.getSeqNum();
                            lastAck = recv;
                        }
                        byte[] buff = serializePacket(new packet(0, lastAck.getSeqNum(), 0, lastAck.getData().toUpperCase()));
                        DatagramPacket packet = new DatagramPacket(buff, buff.length, recpacket.getAddress(), recpacket.getPort());
                        dSocket.send(packet);
                    }
                }
             
                dSocket.close(); // close the UDP socket

                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                writer.write(file);
                writer.close();

                writer = new BufferedWriter(new FileWriter("arrival.log"));
                writer.write(log);
                writer.close();
            }// end try
            catch(SocketTimeoutException t){ // catch a timeout event
                System.out.println("Socket time out event");   
            }
            catch(IOException e){ // catch IO exception
                System.out.println("I/O exception");
            }     
        }

    public static packet deserializePacket(byte[] data)
    {
        try
        {
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
            packet obj = (packet) iStream.readObject();
            iStream.close();
            return obj;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    
        return null;
    }

    public static byte[] serializePacket(packet mp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(mp);
            oos.close();
            // get the byte array of the object
            byte[] obj= baos.toByteArray();
            baos.close();
            return obj;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}// end class
