package Applications.EndpointReceiverTest;

import java.net.InetAddress;
import Protocol.PacketDecoder;
import Protocol.ProtocolTypes;

public class ApplicationPacketHandler implements Runnable
{
	byte[] data;
	InetAddress ip;
	int port;
	PacketDecoder decoder = new PacketDecoder();

	public ApplicationPacketHandler(byte[] data, InetAddress ip, int port)
	{
		this.data = data;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void run()
	{
		String netIdString = decoder.getNetIdString(data);
		String payload = decoder.getTarget(ProtocolTypes.PAYLOAD, data);
		System.out.println("received forwarded packet from: " + port + "\n    netId: " + netIdString + "\n    payload: " + payload);
	}	
}
