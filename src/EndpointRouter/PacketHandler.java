package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Protocol.PacketHelper;

public class PacketHandler implements Runnable
{

	byte[] data;
	PacketHelper packetHelper;
	int fromPort;
	InetAddress fromIp;
	InetAddress nextIp;
	int toPort;

	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, InetAddress nextIp)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.nextIp = nextIp;
		this.toPort = (data[0] == (byte) 1 ? 51511 : 51510);
	}

	public void send(String from)
	{
		packetHelper.createRouterOrEndpointPacket();
		int attemptsToSend = 1;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			while (attemptsToSend < 3)
			{
				packet = new DatagramPacket(packetHelper.getData(), packetHelper.getData().length, nextIp, toPort);
				socket.send(packet);
				System.out.println("forwarding the packet to a: " + from + "\n    PORT: " + toPort + "\n    netId: " + packetHelper.getNetIdString());
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
				System.out.println("failed to send to a: " + from + "\n    PORT: " + toPort + "\n    after " + attemptsToSend + " attempts at sending");
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

	public void forwardPacket(String from)
	{
		packetHelper.setType((byte) 0x1);
		packetHelper.createRouterOrEndpointPacket();
		send(from);
	}
	
	@Override
	public void run()
	{
		this.packetHelper = new PacketHelper(data, data[0]);
		packetHelper.decodeRouterOrEndpointPacket();
		String from = "Router";
		if (data[0] == (byte) 0x3)
		{
			from = "Endpoint";
		}
		System.out.println("received forwarded packet from a: " + from + "\n    IP: " + fromIp + "\n    netId: " + packetHelper.getNetIdString());
		
		try 
		{
			packetHelper.createAck();
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(packetHelper.getData(), packetHelper.getData().length, fromIp, fromPort);
			socket.send(packet);
			socket.close();
			forwardPacket(from);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}
}
