package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Protocol.PacketDecoder;
import Protocol.PacketGenerator;
import Protocol.ProtocolTypes;

public class PacketHandler implements Runnable
{

	byte[] data;
	Router router;
	DatagramPacket packet;
	PacketDecoder decoder = new PacketDecoder();
	PacketGenerator generator = new PacketGenerator();

	public PacketHandler(Router router, DatagramPacket packet)
	{
		this.router = router;
		this.packet = packet;
	}

	public boolean contactController(String netIdString)
	{
		byte[] data = generator.createControllerRequestPacket(netIdString, router.routerName, router.myIps);
		try
		{
			byte[] buffer = new byte[1500];
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("controller"), 51510);
			socket.send(packet);
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			socket.close();
			byte[] dataRecv = new byte[packet.getData().length];
			System.arraycopy(packet.getData(), 0, dataRecv, 0, packet.getData().length);
			String nextHop = decoder.getTarget(ProtocolTypes.NEXT_HOP, dataRecv);
			if (nextHop.equals("null"))
			{
				return false;
			}
			else
			{
				updateRoutingTable(netIdString, nextHop);
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void forwardPacket(byte[] data, String netIdString)
	{
		boolean validAddressReturned = true;
		if (!router.routingTable.containsKey(netIdString))
		{
			validAddressReturned = contactController(netIdString);
		}

		if (validAddressReturned)
		{
			String toIp = router.routingTable.get(netIdString);
			PacketSender sender = new PacketSender(data, toIp, 51510, netIdString, packet.getPort(), packet.getAddress());
			Thread t = new Thread(sender);
			t.start();
		}
		else
		{
			System.out.println("INVALID NET ID RECEIVED...CANNOT FIND DESTINATION IP");
		}
	}

	public void updateRoutingTable(String netIdString, String nextHop)
	{
		router.routingTable.put(netIdString, nextHop);
	}

	@Override
	public void run()
	{
		data = new byte[packet.getLength()];
		System.arraycopy(packet.getData(), 0, data, 0, data.length);

		if (data[0] == ProtocolTypes.ROUTER /* Received a router packet */)
		{
			while (!router.goodToGo) {}
			String netIdString = decoder.getNetIdString(data);
			forwardPacket(data, netIdString);
		}
		else if (data[0] == ProtocolTypes.ENDPOINT /* Received an endpoint packet */)
		{
			while (!router.goodToGo) {}
			String netIdString = decoder.getNetIdString(data);
			data[0] = ProtocolTypes.ROUTER;
			forwardPacket(data, netIdString);
		}
		else if (/* data[0] == ProtocolTypes.CONTROLLER_REQUEST || */data[0] == ProtocolTypes.CONTROLLER_RESPONSE /* Received a controller packet */)
		{
			String netIdString = decoder.getNetIdString(data);
			String nextHop = decoder.getTarget(ProtocolTypes.NEXT_HOP, data);
			System.out.println("netID from controller: " + netIdString + " and next hop from controller: " + nextHop);
			updateRoutingTable(netIdString, nextHop);
		}
		else if (data[0] == ProtocolTypes.GOOD_TO_GO)
		{
			String name = decoder.getTarget(ProtocolTypes.FROM_IP_STR, data);
			if (name.equals(router.routerName))
			{
				router.goodToGo = true;
				System.out.println("received good to go from controller");
			}
		}
		else
		{
			System.out.println("shouldnt be happening: " + data[0]);
		}
	}
}
