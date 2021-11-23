package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import Protocol.ControllerPacketData;
import Protocol.EndpointPacketData;
import Protocol.PacketHelper;
import Protocol.RouterPacketData;

public class PacketHandler implements Runnable
{

	byte[] data;
	int fromPort;
	InetAddress fromIp;
	HashMap<String, String> routingTable;

	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, HashMap<String, String> routingTable)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.routingTable = routingTable;
	}

	public void contactController()
	{
		System.out.println("Here I would contact the controller... but the controller has not yet been implemented");
	}

	public void forwardPacket(byte[] data, String netIdString)
	{
		if (!routingTable.containsKey(netIdString))
		{
			contactController();
		}
		else
		{
			String toIp = routingTable.get(netIdString);
			PacketSender sender = new PacketSender(data, toIp, 51510, netIdString, fromPort, fromIp);
			Thread t = new Thread(sender);
			t.start();
		}
	}

	public void updateRoutingTable(String netIdString, String nextHop)
	{

	}

	@Override
	public void run()
	{
		if (data[0] == 0x1 /* Received a router packet */)
		{
			RouterPacketData routerPacket = new RouterPacketData(data);
			forwardPacket(data, routerPacket.getNetIdString());
		}
		else if (data[0] == 0x2 /* Received an endpoint packet */)
		{
			EndpointPacketData endpointPacket = new EndpointPacketData(data);
			endpointPacket.setType((byte) 0x1);
			endpointPacket.createPacket();
			forwardPacket(endpointPacket.getData(), endpointPacket.getNetIdString());
		}
		else if (data[0] == 0x4 /* Received a controller packet */)
		{
			ControllerPacketData controllerPacket = new ControllerPacketData(data);
			updateRoutingTable(controllerPacket.getNetIdString(), controllerPacket.getNextHop());
		}
		else
		{
			System.out.println("shouldnt be happening");
		}
	}
}
