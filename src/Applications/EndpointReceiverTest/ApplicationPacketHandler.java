package Applications.EndpointReceiverTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Protocol.PacketHelper;

public class ApplicationPacketHandler implements Runnable
{
	byte[] data;
	InetAddress ip;
	int port;

	public ApplicationPacketHandler(byte[] data, InetAddress ip, int port)
	{
		this.data = data;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void run()
	{
		PacketHelper packetHelper = new PacketHelper(data, data[0]);
		packetHelper.decodeRouterOrEndpointPacket();
		
		try 
		{
			packetHelper.createAck();
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(packetHelper.getData(), packetHelper.getData().length, ip, port);
			System.out.println("received forwarded packet from: " + port + "\n    netId: " + packetHelper.getNetIdString() + "\n    payload: " + packetHelper.getPayload());
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}	
}
