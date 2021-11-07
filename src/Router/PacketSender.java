package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Protocol.PacketHelper;

public class PacketSender implements Runnable
{
	
	PacketHelper packetHelper;
	InetAddress toIp;
	int toPort;

	public PacketSender(PacketHelper packetHelper, InetAddress toIp, int toPort)
	{
		this.packetHelper = packetHelper;
		this.toIp = toIp;
		this.toPort = toPort;
	}

	@Override
	public void run()
	{
		packetHelper.createRouterPacket();
		int attemptsToSend = 1;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			while (attemptsToSend < 3)
			{
				packet = new DatagramPacket(packetHelper.getData(), packetHelper.getData().length, toIp, toPort);
				socket.send(packet);
				byte[] buffer = new byte[1500];
				DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
				try 
				{
					socket.setSoTimeout(500);
					socket.receive(recvPacket);
					attemptsToSend = 99;
				} 
				catch (Exception e) 
				{
					attemptsToSend++;
				}
			}
			if (attemptsToSend == 3) 
			{
				System.out.println("failed to send to " + toIp + " after " + attemptsToSend + " attempts at sending");
			}
			else
			{
				System.out.println("received acknowledgement packet from: " + packet.getAddress());
			}
			socket.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
