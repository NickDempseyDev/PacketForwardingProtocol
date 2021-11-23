package Applications.EndpointSenderTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Protocol.EndpointPacketData;
import Protocol.PacketHelper;

public class ApplicationTest implements Runnable
{
	InetAddress myIp;
	int localRouterPort;

	public ApplicationTest(InetAddress myIp)
	{
		this.myIp = myIp;
		this.localRouterPort = 51510;
	}

	public void sendPacket()
	{
		// send a packet to the other endpoint
		EndpointPacketData packData = new EndpointPacketData("tcd.scss", "payload message");
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			packet = new DatagramPacket(packData.getData(), packData.getData().length, InetAddress.getLocalHost(), localRouterPort);
			System.out.println("endpoint sender application forwarding the packet to: " + localRouterPort + "\n    netId: " + packData.getNetIdString() + "\n    payload: " + packData.getPayload());
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
