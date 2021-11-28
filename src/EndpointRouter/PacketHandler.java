package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Protocol.PacketDecoder;
import Protocol.PacketGenerator;
import Protocol.ProtocolTypes;

public class PacketHandler implements Runnable
{

	byte[] data;
	int fromPort;
	InetAddress fromIp;
	String nextRouter;
	String localApplication;
	int nextPort;
	PacketDecoder decoder = new PacketDecoder();
	PacketGenerator generator = new PacketGenerator();
	Boolean goodToGo;
	
	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, String nextRouter, int nextPort, Boolean goodToGo)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.nextRouter = nextRouter;
		this.nextPort = nextPort;
		this.localApplication = "localhost";
		this.goodToGo = goodToGo;
	}

	public void forwardToApplication()
	{
		String netIdString = decoder.getNetIdString(data);
		String payload = decoder.getTarget(ProtocolTypes.PAYLOAD, data);
		byte[] dataToForward = generator.createRouterOrEndpointPacket(netIdString, payload, "endpoint");
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(dataToForward, dataToForward.length, InetAddress.getByName(localApplication), nextPort);
			System.out.println("forwarding the packet to the application");
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void forwardToRouter()
	{
		String netIdString = decoder.getNetIdString(data);
		String payload = decoder.getTarget(ProtocolTypes.PAYLOAD, data);
		byte[] dataToForward = generator.createRouterOrEndpointPacket(netIdString, payload, "endpoint");
		int attemptsToSend = 0;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(dataToForward, dataToForward.length, InetAddress.getByName(nextRouter), nextPort);
			while (attemptsToSend < 3)
			{
				System.out.println("forwarding the packet to: " + nextRouter + "\n    PORT: " + nextPort);
				socket.send(packet);
				byte[] buffer = new byte[1500];
				DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
				try
				{
					socket.setSoTimeout(3000);
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

		if (data[0] != ProtocolTypes.GOOD_TO_GO)
		{
			while (!goodToGo) {}
		}
		else
		{
			goodToGo = true;
		}

		if (data[0] == (byte) ProtocolTypes.ROUTER)
		{
			try 
			{
				byte[] ackRes = decoder.createAck(data, ProtocolTypes.ENDPOINT);
				sendAck(ackRes);
				forwardToApplication();
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
				byte[] ackRes = decoder.createAck(data, ProtocolTypes.ENDPOINT);
				sendAck(ackRes);
				forwardToRouter();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
