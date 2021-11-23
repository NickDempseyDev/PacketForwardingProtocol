package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Protocol.EndpointPacketData;
import Protocol.RouterPacketData;

public class PacketHandler implements Runnable
{

	byte[] data;
	int fromPort;
	InetAddress fromIp;
	String nextRouter;
	String localApplication;
	int nextPort;
	
	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, String nextRouter, int nextPort)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.nextRouter = nextRouter;
		this.nextPort = nextPort;
		this.localApplication = "localhost";
	}

	public void forwardToApplication(RouterPacketData rPack)
	{
		EndpointPacketData newEPack = new EndpointPacketData(rPack.getNetIdString(), rPack.getPayload());
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(newEPack.getData(), newEPack.getData().length, InetAddress.getByName(localApplication), nextPort);
			System.out.println("forwarding the packet to the application");
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void forwardToRouter(EndpointPacketData ePack)
	{
		RouterPacketData newRPack = new RouterPacketData(ePack.getNetIdString(), ePack.getPayload());
		int attemptsToSend = 0;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(newRPack.getData(), newRPack.getData().length, InetAddress.getByName(nextRouter), nextPort);
			while (attemptsToSend < 3)
			{
				socket.send(packet);
				System.out.println("forwarding the packet to: " + nextRouter + "\n    PORT: " + nextPort);
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
				System.out.println("failed to send to: " + nextRouter + "\n    PORT: " + nextPort + "\n    after " + attemptsToSend + " attempts at sending");
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
	
	public void sendAck(byte[] ack)
	{
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(ack, ack.length, fromIp, fromPort);
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{		
		if (data[0] == (byte) 1)
		{
			try 
			{
				RouterPacketData rPack = new RouterPacketData(data);
				byte[] ackRes = rPack.createAck();
				sendAck(ackRes);
				forwardToApplication(rPack);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();	
			}
		}
		else
		{
			try
			{
				EndpointPacketData ePack = new EndpointPacketData(data);
				byte[] ackRes = ePack.createAck();
				sendAck(ackRes);
				forwardToRouter(ePack);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
