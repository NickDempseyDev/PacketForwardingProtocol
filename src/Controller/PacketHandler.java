package Controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import Protocol.ControllerPacketData;

public class PacketHandler implements Runnable
{
	byte[] data;
	int fromPort;
	HashMap<String, HashMap<String, String>> routingTable;
	HashMap<String, String> netIdOwnershipTable;

	public PacketHandler(byte[] data, HashMap<String, HashMap<String, String>> routingTable, HashMap<String, String> netIdOwnershipTable, int fromPort)
	{
		this.data = data;
		this.routingTable = routingTable;
		this.netIdOwnershipTable = netIdOwnershipTable;
		this.fromPort = fromPort;
	}

	public LinkedList<NextHopPair> generateListToSend(String netId, String fromIp)
	{
		LinkedList<NextHopPair> list = new LinkedList<NextHopPair>();
		System.out.println("contacted by: " + fromIp);
		String netIdOwner = netIdOwnershipTable.get(netId);
		String current = new String(fromIp);

		while (true)
		{
			HashMap<String, String> tempMap = routingTable.get(current);
			String nextHop = tempMap.get(netIdOwner);
			if (nextHop.equals(netIdOwner))
			{
				list.push(new NextHopPair(current, nextHop));
				break;
			}
			list.push(new NextHopPair(current, nextHop));
			current = new String(nextHop);
		}

		return list;
	}

	public void sendNextHop(String fromIp, String nextHopIp, String netId, int fromPort)
	{
		ControllerPacketData dataN = new ControllerPacketData(netId, nextHopIp, "", (byte) 0x5);
		try 
		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(dataN.getData(), dataN.getData().length, InetAddress.getByName(fromIp), fromPort);
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void sendNextHops(String netId, String fromIp)
	{
		LinkedList<NextHopPair> list = generateListToSend(netId, fromIp);

		while (!list.isEmpty())
		{
			NextHopPair next = list.pop();
			if (next.fromIp.equals(fromIp))
			{
				sendNextHop(next.fromIp, next.nextHopIp, netId, fromPort);
			}
			else
			{
				sendNextHop(next.fromIp, next.nextHopIp, netId, 51510);
			}
			try
			{
				Thread.sleep(100);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run()
	{
		ControllerPacketData packet = new ControllerPacketData(data);
		sendNextHops(packet.getNetIdString(), packet.getFromIp());
	}

	protected class NextHopPair
	{
		String fromIp;
		String nextHopIp;

		public NextHopPair(String fromIp, String nextHopIp)
		{
			this.fromIp = fromIp;
			this.nextHopIp = nextHopIp;
		}
	}
}
