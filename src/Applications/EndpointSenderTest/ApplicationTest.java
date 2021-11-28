package Applications.EndpointSenderTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Protocol.PacketGenerator;

public class ApplicationTest implements Runnable
{
	InetAddress myIp;
	int localRouterPort;
	PacketGenerator generator = new PacketGenerator();

	public ApplicationTest(InetAddress myIp)
	{
		this.myIp = myIp;
		this.localRouterPort = 51510;
	}

	public void sendPacket()
	{
		// send a packet to the other endpoint
		byte[] packData = generator.createRouterOrEndpointPacket("tcd.scss", "Hey endpointreceiver!", "endpoint");
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			packet = new DatagramPacket(packData, packData.length, InetAddress.getLocalHost(), localRouterPort);
			System.out.println("endpoint sender application forwarding the packet to: " + localRouterPort + "\n    netId: " + "tcd.scss" + "\n    payload: " + "Hey endpointreceiver!");
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
		sendPacket();
	}
}
