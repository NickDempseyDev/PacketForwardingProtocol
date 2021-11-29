package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import Protocol.PacketGenerator;

public class Router
{
	int listeningPort = 51510;
	String routerName;
	InetAddress[] myIps;
	Boolean goodToGo = false;
	PacketGenerator generator = new PacketGenerator();
	HashMap<String, String> routingTable = new HashMap<String, String>();

	public Router(String routerName, InetAddress[] myIps)
	{
		this.routerName = routerName;
		this.myIps = myIps;
	}

	// public void simulateForwarding(String netId, String msg)
	// {
	// 	PacketHelper packetHelper = new PacketHelper(netId, msg, (byte) 1);
	// 	InetAddress toIp = routingTable.get(netId);
	// 	PacketSender sender = new PacketSender(packetHelper, toIp, 51510);
	// 	Thread t = new Thread(sender);
	// 	t.start();
	// }

	public void sendHello()
	{
		try
		{
			Thread.sleep(1000);
			DatagramSocket socket = new DatagramSocket();
			byte[] bytes = generator.createHelloPacket(routerName, myIps);
			byte[] buf = new byte[1500];
			DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("controller"), 51510);
			System.out.println(InetAddress.getByName("controller").getHostAddress());
			boolean tryAgain = true;
			while (tryAgain) {
				socket.send(packetS);
				try
				{
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.setSoTimeout(1000);
					socket.receive(packet);
					tryAgain = false;
				}
				catch (Exception e)
				{
					System.out.println("TIMEOUT");
					tryAgain = true;
				}
			}
			System.out.println("received ack from controller for hello");
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void start()
	{
		try
		{
			System.out.println("\nSending Hello...");

			sendHello();

			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort);
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				PacketHandler packetHandler = new PacketHandler(this, packet);
				Thread t = new Thread(packetHandler);
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
