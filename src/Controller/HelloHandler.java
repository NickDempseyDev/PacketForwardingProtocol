package Controller;

import java.util.ArrayList;

import Protocol.PacketDecoder;
import Protocol.ProtocolTypes;

public class HelloHandler implements Runnable
{
	byte[] data;
	Boolean safeToAdd;
	ArrayList<byte[]> queue;
	Integer counter;
	PacketDecoder decoder = new PacketDecoder();

	public HelloHandler(byte[] data, ArrayList<byte[]> queue, Boolean safeToAdd, Integer counter)
	{
		this.data = data;
		this.safeToAdd = safeToAdd;
		this.queue = queue;
		this.counter = counter;
	}

	@Override
	public void run()
	{
		System.out.println("TRYING to add packet from: " + decoder.getTarget(ProtocolTypes.FROM_IP_STR, data) + " to the queue");
		Boolean stop = false;
		while(!stop)
		{
			if (safeToAdd)
			{
				safeToAdd = false;
				queue.add(data);
				System.out.println("ADDED packet from: " + decoder.getTarget(ProtocolTypes.FROM_IP_STR, data) + " to the queue");
				counter++;
				stop = true;
			}
		}
	}
}
