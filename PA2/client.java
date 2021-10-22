// Name: Andrew Blake Berry

// Sources used:
// Class provided Java UDP Example
// https://www.w3schools.com/java/java_files_read.asp
// https://stackoverflow.com/questions/17940423/send-object-over-udp-in-java
// https://github.com/marcoszh/GBN-java/blob/master/GBNSender/src/GBNClient.java

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class client{
    
    static int port = 0;
    static int acks = -1;
    static DatagramSocket dSocket = null;
    static int numPackets;
    static String squNumLog = "";
    static String tAckLog = "";
    static int outstandingCount = 0;
    static LinkedList<byte[]> buffers = new LinkedList<byte[]>();
    
    public static void main(String [] args){
                     
    // UDP variables
    InetAddress address = null;
        
    // server name
    String serverName = args[0];
        
    // port to send (on server)
    port = Integer.parseInt(args[1]);

    // file name
    String fileName = args[2];

    int numCharacters = 30;
    LinkedList<packet> packets = new LinkedList<packet>();
    Timer[] timers = new Timer[8];
    
    String file = Read(fileName);
    numPackets = 0;
    while (true) {
        try {
            packets.add(new packet(1, numPackets%8, numCharacters, file.substring(0, numCharacters)));
            file = file.substring(numCharacters);
            numPackets++;
        }
        catch (Exception e) {
            packets.add(new packet(1, numPackets%8, file.length(), file));
            numPackets++;
            break;
        }
    }

    packets.add(new packet(3, numPackets%8, 0, "EOT"));

    while (packets.size() > 0) {
        buffers.add(serializePacket(packets.removeFirst()));
    }
            try{						       
                // Setup to transfer over UDP socket
                address = InetAddress.getByName(serverName);
                client.dSocket = new DatagramSocket();
                byte[] recBuf = new byte[1024];

                Thread thread = new Thread(){
                    public void run(){
                        while (client.outstandingCount > 0 || client.buffers.size() > 1) {
                            DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                            try {
                                client.dSocket.receive(recpacket);
                                packet recv = deserializePacket(recpacket.getData());
                                client.tAckAdd(recv.getSeqNum());
                                if (recv.getSeqNum() != acks && recv.getType() == 0) {
                                    timers[recv.getSeqNum()].stop();
                                    if (recv.getSeqNum() > acks) {
                                        client.outstandingCount = client.outstandingCount - (recv.getSeqNum() - client.acks);
                                        client.acks = recv.getSeqNum();
                                    }
                                    else {
                                        client.outstandingCount = client.outstandingCount - (recv.getSeqNum() + 8 - client.acks);
                                        client.acks = recv.getSeqNum();
                                    }
                                }
                            } catch (IOException e) { }
                        }
                    }
                };
                
                thread.start();
                int sendSqu = -1;

                while (buffers.size() > 1) {
                    if (outstandingCount < 7) {
                        byte[] buff = buffers.removeFirst();
                        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);
                        sendSqu = (sendSqu+1)%8;
                        ActionListener taskPerformer = new myActionListener(client.dSocket, packet, sendSqu);
                        timers[sendSqu] = new Timer(2000, taskPerformer);
                        timers[sendSqu].start();
                        outstandingCount++;
                    }
                    else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) { }
                    }
                }

                while (outstandingCount > 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) { }
                }

                byte[] endBuff = buffers.removeFirst();
                DatagramPacket packet = new DatagramPacket(endBuff, endBuff.length, address, port);
                client.squNumLog += (numPackets%8) + "\n";
                client.dSocket.send(packet);

                DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                client.dSocket.receive(recpacket);
                packet recv = deserializePacket(recpacket.getData());
                client.tAckAdd(recv.getSeqNum());

                client.dSocket.close(); // close the UDP socket          

                FileWriter writer = new FileWriter("clientseqnum.log");
                writer.write(client.squNumLog);
                writer.close();
                
                writer = new FileWriter("clienttack.log");
                writer.write(client.tAckLog);
                writer.close();
            } // end try
            catch(UnknownHostException e){
                System.out.println("Unknown host.");   
            }
            catch(IOException e){
                System.out.println("I/O error");    
            }       
    } // end main

    public static void tAckAdd(int num) {
        client.tAckLog += num + "\n";
    }

    public static void seqNumAdd(int num) {
        client.squNumLog += num + "\n";
    }

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
}

class myActionListener implements ActionListener {
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	int k;

	public myActionListener(DatagramSocket clientsocket, DatagramPacket sendpacket, int i) {
		clientSocket = clientsocket;
		sendPacket = sendpacket;
		k = i;
	}

	public void actionPerformed(ActionEvent e) {
		try {

			clientSocket.send(sendPacket);
            client.squNumLog += k + "\n";
		} 
		catch (Exception exception) {
		}
	};
}