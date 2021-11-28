package Controller;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import Protocol.PacketDecoder;
import Protocol.PacketGenerator;
import Protocol.ProtocolTypes;

public class Controller
{
	Integer counter = 0;
	Integer totalRoutersOrEndpoints;
	Boolean safeToAdd = true;

	// to generate
	HashMap<String, HashMap<String, String>> routingTable;

	// to deduce from 'Hello' packets
	ArrayList<byte[]> tempQueue = new ArrayList<byte[]>();
	HashMap<String, ArrayList<Integer>> networks;
	HashMap<String, ArrayList<String>> connections; // can create adj list from this

	// manually input from controller
	HashMap<String, String> netIdOwnershipTable;
	String myIp;

	PacketDecoder decoder = new PacketDecoder();
	PacketGenerator generator = new PacketGenerator();

	public Controller(String routingTableFile, HashMap<String, String> netIdOwnershipTable, String myIp, Integer totalRoutersOrEndpoints)
	{
		this.routingTable = generateRoutingTableManually(routingTableFile);
		this.netIdOwnershipTable = netIdOwnershipTable;
		this.myIp = myIp;
		this.totalRoutersOrEndpoints = totalRoutersOrEndpoints;
	}

	public Controller(HashMap<String, String> netIdOwnershipTable, Integer totalRouterOrEndpoints)
	{
		this.myIp = new String("controller");
		this.netIdOwnershipTable = netIdOwnershipTable;
		this.totalRoutersOrEndpoints = totalRouterOrEndpoints;
	}

	public HashMap<String, HashMap<String, String>> generateRoutingTableManually(String file)
	{
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();

		try 
		{
			Scanner scanner = new Scanner(new File(file));
			String router = scanner.nextLine();
			while (!router.equals("end"))
			{
				HashMap<String, String> tempMap = new HashMap<String, String>();
				int routesNo = Integer.parseInt(scanner.nextLine());
				
				for (int i = 0; i < routesNo; i++)
				{
					String line = scanner.nextLine();
					String[] splitLine = line.split("->");
					tempMap.put(splitLine[1], splitLine[0]);
				}
				map.put(router, tempMap);
				router = scanner.nextLine();
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		

		return map;
	}

	public void createNetorkDescriptor()
	{
		for (byte[] data : tempQueue)
		{
			ArrayList<Integer> networksNumbers = new ArrayList<Integer>();
			String name = decoder.getTarget(ProtocolTypes.FROM_IP_STR, data);
			ArrayList<InetAddress> ips = decoder.getFromIpsInetAddress(data);
			for (InetAddress ip : ips)
			{
				try
				{
					byte[] n = ip.getAddress();
					networksNumbers.add(Integer.valueOf((int)n[2]));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			networks.put(name, networksNumbers);
		}
	}

	public void createNetworkConnections()
	{
		Set<String> routers = networks.keySet();
		for (String router : routers)
		{
			ArrayList<String> conns = new ArrayList<String>();
			ArrayList<Integer> networkNos = networks.get(router);
			for (Integer no : networkNos)
			{
				for (String otherRouter : routers)
				{
					if (otherRouter.equals(router)) continue;

					if (networks.get(otherRouter).contains(no))
					{
						conns.add(otherRouter);
					}
				}	
			}
			connections.put(router, conns);
		}
	}

	public void sendAllGoodPackets()
	{
		try 
		{
			DatagramSocket socket = new DatagramSocket();
			for (int i = 0; i < tempQueue.size(); i++)
			{
				byte[] temp = tempQueue.get(tempQueue.size() - i - 1);
				String routerName = decoder.getTarget(ProtocolTypes.FROM_IP_STR, temp);
				byte[] allGoodPack = generator.createGoodToGoPacket(routerName);
				DatagramPacket packet = new DatagramPacket(allGoodPack, allGoodPack.length, InetAddress.getByName(routerName), 51510);
				socket.send(packet);
			}
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void createNetwork()
	{
		System.out.println("creating the network");
		createNetorkDescriptor();
		createNetworkConnections();
		NetworkGraph graph = new NetworkGraph(connections, totalRoutersOrEndpoints);
		this.routingTable = graph.generateRoutingTable();
		sendAllGoodPackets();
		System.out.println("send all good packets");
	}

	public void start()
	{
		try
		{
			boolean forever = true;
			DatagramSocket socket = new DatagramSocket(51510, InetAddress.getByName(myIp));
			while (forever)
			{
				byte[] buf = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(buf, 0, data, 0, data.length);
				if (data[0] == ProtocolTypes.HELLO_R)
				{
					HelloHandler handle = new HelloHandler(data, tempQueue, safeToAdd, counter);
					Thread t = new Thread(handle);
					t.start();
				}
				else if (counter == totalRoutersOrEndpoints)
				{
					createNetwork();
				}
				else
				{
					PacketHandler handler = new PacketHandler(data, routingTable, netIdOwnershipTable, packet.getPort());
					Thread t = new Thread(handler);
					t.start();
				}
			}
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
