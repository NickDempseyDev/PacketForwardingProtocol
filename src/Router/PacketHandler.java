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
	String routerName;

	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, HashMap<String, String> routingTable, String routerName)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.routingTable = routingTable;
		this.routerName = routerName;
	}

	public boolean contactController(String netIdString)
	{
		ControllerPacketData packRequest = new ControllerPacketData(netIdString, "", routerName, (byte) 0x4);
		try
		{
			byte[] buffer = new byte[1500];
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(packRequest.getData(), packRequest.getData().length, InetAddress.getByName("controller"), 51510);
			socket.send(packet);
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			socket.close();
			byte[] dataRecv = new byte[packet.getData().length];
			System.arraycopy(packet.getData(), 0, dataRecv, 0, packet.getData().length);
			ControllerPacketData packResponse = new ControllerPacketData(dataRecv);
			if (packResponse.getNextHop().equals("null"))
			{
				return false;
			}
			else
			{
				updateRoutingTable(packResponse.getNetIdString(), packResponse.getNextHop());
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
		if (!routingTable.containsKey(netIdString))
		{
			validAddressReturned = contactController(netIdString);
		}

		if (validAddressReturned)
		{
			String toIp = routingTable.get(netIdString);
			PacketSender sender = new PacketSender(data, toIp, 51510, netIdString, fromPort, fromIp);
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
		routingTable.put(netIdString, nextHop);
	}

	@Override
	public void run()
	{
		System.out.print("ROUTER PACK HANDLER AND RECEIVED A ... ");
		// for (int i = 0; i < data.length; i++) {
		// 	System.out.print((byte)data[i]);
		// }
		// System.out.println();
		if (data[0] == 0x1 /* Received a router packet */)
		{
			System.out.println(" ROUTER PACKET");
			RouterPacketData routerPacket = new RouterPacketData(data);
			forwardPacket(data, routerPacket.getNetIdString());
		}
		else if (data[0] == 0x2 /* Received an endpoint packet */)
		{
			System.out.println(" ENDPOINT PACKET");
			EndpointPacketData endpointPacket = new EndpointPacketData(data);
			endpointPacket.setType((byte) 0x1);
			endpointPacket.createPacket();
			forwardPacket(endpointPacket.getData(), endpointPacket.getNetIdString());
		}
		else if (data[0] == 0x4 || data[0] == 0x5 /* Received a controller packet */)
		{
			System.out.println(" CONTROLLER PACKET WITH DATA[0] = " + data[0]);
			ControllerPacketData controllerPacket = new ControllerPacketData(data);
			System.out.println("netID from controller: " + controllerPacket.getNetIdString() + " and next hop from controller: " + controllerPacket.getNextHop());
			updateRoutingTable(controllerPacket.getNetIdString(), controllerPacket.getNextHop());
		}
		else
		{
			System.out.println("shouldnt be happening");
		}
	}
}
