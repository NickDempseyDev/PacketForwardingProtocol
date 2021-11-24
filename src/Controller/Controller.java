package Controller;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Scanner;

public class Controller
{
	HashMap<String, HashMap<String, String>> routingTable;
	HashMap<String, String> netIdOwnershipTable;
	String myIp;

	public Controller(String routingTableFile, HashMap<String, String> netIdOwnershipTable, String myIp)
	{
		this.routingTable = generateRoutingTable(routingTableFile);
		this.netIdOwnershipTable = netIdOwnershipTable;
		this.myIp = myIp;
	}

	public HashMap<String, HashMap<String, String>> generateRoutingTable(String file)
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
				PacketHandler handler = new PacketHandler(data, routingTable, netIdOwnershipTable, packet.getPort());
				Thread t = new Thread(handler);
				t.start();
			}
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
