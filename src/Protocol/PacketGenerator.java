package Protocol;

import java.net.InetAddress;

public class PacketGenerator extends ProtocolTypes
{
	public String[] createNetIdArray(String netIdString)
	{
		String[] netId;
		if (netIdString.split("\\.").length == 0)
		{
			netId = new String[1];
			netId[0] = netIdString;
		}
		else
		{
			netId = netIdString.split("\\.");
		}
		return netId;
	}

	public byte[] createRouterOrEndpointPacket(String netIdString, String payload, String routerOrEndpoint)
	{
		String[] netId = createNetIdArray(netIdString);
		
		int sizeOfData = 3 + netId.length + payload.getBytes().length + 1 + 1;

		for (String netIdTemp : netId)
		{
			sizeOfData += netIdTemp.length(); 
		}

		byte type;
		byte[] payloadBytes = payload.getBytes();
		int currPos;

		if (netId.length == 1) 
		{
			type = NET_ID;
			currPos = 2;
		}
		else 
		{
			currPos = 3;
			type = COMBINATION_NET_ID;
			sizeOfData++;
		}

		byte[] data = new byte[sizeOfData];

		data[0] = (routerOrEndpoint.equalsIgnoreCase("router")) ? (byte) ROUTER : (byte) ENDPOINT;
		data[1] = type;
		data[2] = (currPos == 3 ? (byte) netId.length : (byte) netId[0].length());

		for (int i = 0; i < netId.length; i++)
		{
			byte[] temp = netId[i].getBytes();
			data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, data, currPos, temp.length);
			currPos += temp.length;
		}

		data[currPos++] = PAYLOAD;
		data[currPos++] = (byte) payloadBytes.length;
		System.arraycopy(payloadBytes, 0, data, currPos, payloadBytes.length);

		currPos = currPos + payloadBytes.length;

		data[currPos] = END_OF_PACKET;

		return data;
	}

	public byte[] createControllerRequestPacket(String netIdString, String fromIp, InetAddress[] fromIps)
	{
		String[] netId = createNetIdArray(netIdString);

		int sizeOfData = 3 + fromIp.length() + 1 + netId.length + (fromIps.length * 4) + (2 * fromIps.length) + 1;
		
		for (String netIdTemp : netId)
		{
			sizeOfData += netIdTemp.length(); 
		}
		
		byte type;
		int currPos;
		
		if (netId.length == 1) 
		{
			type = NET_ID;
			currPos = 2;
		}
		else 
		{
			currPos = 3;
			type = COMBINATION_NET_ID;
			sizeOfData++;
		}
		
		byte[] data = new byte[sizeOfData];
		
		data[0] = CONTROLLER_REQUEST;
		data[1] = type;
		data[2] = (currPos == 3 ? (byte) netId.length : (byte) netId[0].length());
		
		for (int i = 0; i < netId.length; i++) 
		{
			byte[] temp = netId[i].getBytes();
			data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, data, currPos, temp.length);
			currPos += temp.length;
		}
		
		data[currPos++] = (byte) FROM_IP_STR; // from ip type
		data[currPos++] = (byte) fromIp.getBytes().length; // from ip length
		System.arraycopy(fromIp.getBytes(), 0, data, currPos, fromIp.getBytes().length); // from ip
		currPos = currPos + fromIp.getBytes().length;
		
		for (InetAddress ip : fromIps)
		{
			data[currPos++] = FROM_IPS;
			data[currPos++] = (byte) ip.getAddress().length;
			System.arraycopy(ip.getAddress(), 0, data, currPos, ip.getAddress().length);
			currPos = currPos + ip.getAddress().length;
		}

		data[currPos] = END_OF_PACKET;

		return data;
	}

	public byte[] createControllerResponsePacket(String netIdString, String nextHop)
	{
		String[] netId = createNetIdArray(netIdString);

		int sizeOfData = 3 + nextHop.length() + 1 + netId.length + 1;

		for (String netIdTemp : netId)
		{
			sizeOfData += netIdTemp.length(); 
		}

		byte type;
		int currPos;

		if (netId.length == 1) 
		{
			type = NET_ID;
			currPos = 2;
		}
		else 
		{
			currPos = 3;
			type = COMBINATION_NET_ID;
			sizeOfData++;
		}

		byte[] data = new byte[sizeOfData];

		data[0] = CONTROLLER_RESPONSE;
		data[1] = type;
		data[2] = (currPos == 3 ? (byte) netId.length : (byte) netId[0].length());

		for (int i = 0; i < netId.length; i++) 
		{
			byte[] temp = netId[i].getBytes();
			data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, data, currPos, temp.length);
			currPos += temp.length;
		}

		data[currPos++] = NEXT_HOP; // next hop type
		data[currPos++] = (byte) nextHop.getBytes().length; // next hop length
		System.arraycopy(nextHop.getBytes(), 0, data, currPos, nextHop.getBytes().length); // next hop value

		currPos = currPos + nextHop.getBytes().length;

		data[currPos] = END_OF_PACKET;
	
		return data;
	}

	public byte[] createHelloPacket(String fromIpStr, InetAddress[] ips)
	{
		int sizeOfData = 1 + 1 + 1 + fromIpStr.getBytes().length + (ips.length * 4) + (ips.length * 2) + 1;
		int currPos = 1;
		byte[] data = new byte[sizeOfData];
		
		data[0] = HELLO_R;
		
		data[currPos++] = (byte) FROM_IP_STR; // from ip type
		data[currPos++] = (byte) fromIpStr.getBytes().length; // from ip length
		System.arraycopy(fromIpStr.getBytes(), 0, data, currPos, fromIpStr.getBytes().length); // from ip
		currPos = currPos + fromIpStr.getBytes().length;
		
		for (InetAddress ip : ips)
		{
			data[currPos++] = FROM_IPS;
			data[currPos++] = (byte) ip.getAddress().length;
			System.arraycopy(ip.getAddress(), 0, data, currPos, ip.getAddress().length);
			currPos = currPos + ip.getAddress().length;
		}

		data[currPos] = END_OF_PACKET;

		return data;
	}

	public byte[] createGoodToGoPacket(String fromIpStr)
	{
		int sizeOfData = 1 + 1 + 1 + fromIpStr.getBytes().length + 1;

		int currPos = 1;
		byte[] data = new byte[sizeOfData];
		
		data[0] = GOOD_TO_GO;
		
		data[currPos++] = (byte) FROM_IP_STR; // from ip type
		data[currPos++] = (byte) fromIpStr.getBytes().length; // from ip length
		System.arraycopy(fromIpStr.getBytes(), 0, data, currPos, fromIpStr.getBytes().length); // from ip
		currPos = currPos + fromIpStr.getBytes().length;

		data[currPos] = END_OF_PACKET;

		return data;
	}
}
