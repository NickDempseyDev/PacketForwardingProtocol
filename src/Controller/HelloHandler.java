package Controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import Protocol.PacketDecoder;
import Protocol.ProtocolTypes;

public class HelloHandler implements Runnable
{
	Controller controller;
	byte[] data;
	DatagramPacket packetF = null;
	PacketDecoder decoder = new PacketDecoder();

	public HelloHandler(Controller controller, DatagramPacket packetF)
	{
		this.controller = controller;
		this.packetF = packetF;
	}

	public void sendAck()
	{
		try
		{
			byte[] ack = decoder.createAck(data, ProtocolTypes.ACK);
			DatagramSocket sock = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(ack, ack.length, packetF.getAddress(), packetF.getPort());
			sock.send(packet);
			sock.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		data = new byte[packetF.getLength()];
		System.arraycopy(packetF.getData(), 0, data, 0, data.length);
		sendAck();
		System.out.println("received Hello from " + decoder.getTarget(ProtocolTypes.FROM_IP_STR, data));
		Boolean stop = false;
		while(!stop)
		{
			if (controller.safeToAdd)
			{
				controller.safeToAdd = false;
				controller.tempQueue.add(data);
				controller.counter++;
				controller.safeToAdd = true;
				stop = true;
			}
		}
		if (controller.counter == controller.totalRoutersOrEndpoints)
		{
			controller.createNetwork();
		}
	}
}
