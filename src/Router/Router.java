package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Router {
	String routerName;
	InetAddress myIp;
	InetAddress toIp;

	public Router(String routerName, InetAddress myIp, InetAddress toIp) {
		this.routerName = routerName;
		this.myIp = myIp;
		this.toIp = toIp;
	}

	public void printName() {
		System.out.println("Printing from the Router Class and implemented in: " + routerName + " " + myIp);
	}

	public void send(String msg) {
		try 
		{
			System.out.println("my ip: " + myIp.getHostAddress() + "other ip: " + toIp.getHostAddress());
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, toIp, 5000);
			System.out.println("Message sent to: " + toIp + "from me: " + socket.getLocalAddress() + "\n  Message: " + msg);
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void receive() {
		try 
		{
			System.out.println("my ip: " + myIp.getHostAddress());
			byte[] buffer = new byte[1500];
			DatagramSocket socket = new DatagramSocket(5000, myIp);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);

			byte[] data = new byte[packet.getData().length];
			System.arraycopy(buffer, 0, data, 0, data.length);
			String msgDecoded = new String(data);

			System.out.println("Message received from : " + packet.getAddress() + "\n  Message: " + msgDecoded);

			socket.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
