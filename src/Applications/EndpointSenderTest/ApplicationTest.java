package Applications.EndpointSenderTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

	@Override
	public void run()
	{
		// send a packet to the other endpoint
		PacketHelper packetHelper = new PacketHelper("tcd.scss", "payload from sendingEndpoint", (byte) 0x3);
		packetHelper.createRouterOrEndpointPacket();
		int attemptsToSend = 1;
		try
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = null;
			while (attemptsToSend < 3)
			{
				packet = new DatagramPacket(packetHelper.getData(), packetHelper.getData().length, myIp, localRouterPort);
				socket.send(packet);
				System.out.println("forwarding the packet to: " + localRouterPort + "\n    netId: " + packetHelper.getNetIdString());
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
				System.out.println("failed to send to " + localRouterPort + " after " + attemptsToSend + " attempts at sending");
			}
			else
			{
				System.out.println("received acknowledgement packet from: " + packet.getPort());
			}
			socket.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
