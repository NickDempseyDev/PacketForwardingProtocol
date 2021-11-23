package Applications.EndpointReceiverTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Protocol.EndpointPacketData;
import Protocol.PacketHelper;
import Protocol.RouterPacketData;

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
		EndpointPacketData pack = new EndpointPacketData(data);
		System.out.println("received forwarded packet from: " + port + "\n    netId: " + pack.getNetIdString() + "\n    payload: " + pack.getPayload());
		// try 
		// {
		// 	byte[] ack = pack.createAck();
		// 	DatagramSocket socket = new DatagramSocket();
		// 	DatagramPacket packet = new DatagramPacket(ack, ack.length, ip, port);
		// 	socket.send(packet);
		// 	socket.close();
		// } 
		// catch (Exception e) 
		// {
		// 	e.printStackTrace();
		// }
	}	
}
