package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Protocol.RouterPacketData;

public class PacketHandler implements Runnable
{

	byte[] data;
	int fromPort;
	InetAddress fromIp;
	String nextIp;
	int nextPort;
	
	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, String nextIp, int nextPort)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.nextIp = nextIp;
		this.nextPort = nextPort;
	}

	public void send(String from)
	{
		int attemptsToSend = 0;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			while (attemptsToSend < 3)
			{
				packet = new DatagramPacket(data, data.length, InetAddress.getByName(nextIp), nextPort);
				socket.send(packet);
				System.out.println("forwarding the packet to a: " + from + "\n    PORT: " + nextPort);
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
				System.out.println("failed to send to a: " + from + "\n    PORT: " + nextPort + "\n    after " + attemptsToSend + " attempts at sending");
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
		this.data[0] = (byte) 1;
		send(from);
	}
	
	@Override
	public void run()
	{
		String from = "Router";
		if (data[0] == (byte) 0x2)
		{
			from = new String("Endpoint");
		}
		System.out.println("received forwarded packet from a: " + from + "\n    IP: " + fromIp);
		
		try 
		{
			if (from.equalsIgnoreCase("Router"))
			{
				RouterPacketData pack = new RouterPacketData(data);
				byte[] ackRes = pack.createAck();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(ackRes, ackRes.length, fromIp, fromPort);
				socket.send(packet);
				socket.close();
			}
			forwardPacket(from);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}
}
