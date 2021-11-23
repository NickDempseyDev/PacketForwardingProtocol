package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Protocol.PacketHelper;
import Protocol.RouterPacketData;

public class PacketSender implements Runnable
{
	InetAddress fromIp;
	int fromPort;
	byte[] data;
	String toIp;
	int toPort;
	String netIdString;

	public PacketSender(byte[] data, String toIp, int toPort, String netIdString, int fromPort, InetAddress fromIp)
	{
		this.data = data;
		this.toIp = toIp;
		this.toPort = toPort;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.netIdString = netIdString;
	}

	@Override
	public void run()
	{
		boolean recv = true;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			InetAddress ip = InetAddress.getByName(toIp);
			packet = new DatagramPacket(data, data.length, ip, toPort);
			socket.send(packet);
			System.out.println("forwarding the packet to: " + toIp + "\n    netId: " + netIdString);
			byte[] buffer = new byte[1500];
			DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
			try 
			{
				socket.setSoTimeout(500);
				socket.receive(recvPacket);	
			} 
			catch (Exception e) 
			{
				recv = false;
			}
			if (recv) 
			{
				System.out.println("received acknowledgement packet from: " + packet.getAddress() + " forwarding it to: " + fromIp);
				byte[] ackData = new byte[recvPacket.getData().length];
				System.arraycopy(recvPacket.getData(), 0, ackData, 0, recvPacket.getData().length);
				ackData[0] = (byte) 1;
				DatagramPacket forwardAck = new DatagramPacket(ackData, ackData.length, fromIp, fromPort);
				socket.send(forwardAck);
			}
			else
			{
				System.out.println("failed to send to " + toIp + " - never received acknowledgement");
			}
			socket.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
