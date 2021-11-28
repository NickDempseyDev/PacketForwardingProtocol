package Protocol;

import java.net.InetAddress;
import java.util.ArrayList;

public class PacketDecoder extends ProtocolTypes
{
	public String getNetIdString(byte[] data)
	{
		int currPos = 1;
		int numberOfNetIds = 1;

		if (data[currPos++] == COMBINATION_NET_ID)
		{
			numberOfNetIds = data[currPos];
			currPos = 3;
		}

		String[] netId = new String[numberOfNetIds];
		
		for (int i = 0; i < numberOfNetIds; i++) 
		{
			int lengthNetId = data[currPos++];
			byte[] temp = new byte[lengthNetId];
			System.arraycopy(data, currPos, temp, 0, lengthNetId);
			currPos += lengthNetId;
			netId[i] = new String(temp);
		}

		return String.join(".", netId);
	}

	public String getFromIps(byte[] data)
	{
		int currPos = 1;

		while (data[currPos] != FROM_IPS)
		{
			if (data[currPos] == COMBINATION_NET_ID)
			{
				int numberOfNetIds = data[currPos];
				currPos = 3;
				for (int i = 0; i < numberOfNetIds; i++) 
				{
					int lengthNetId = data[currPos++];
					currPos += lengthNetId;
				}
			}
			else
			{
				currPos = currPos + 1;
				int length = data[currPos++];
				currPos = currPos + length;
			}
		}

		ArrayList<String> ips = new ArrayList<String>();
		while (data[currPos] != END_OF_PACKET)
		{
			if (data[currPos] != FROM_IPS)
			{
				break;
			}
			
			currPos++;
			byte[] ipBytes = new byte[data[currPos++]];
			System.arraycopy(data, currPos, ipBytes, 0, ipBytes.length);
			currPos = currPos + ipBytes.length;
			String ip = new String(ipBytes);
			ips.add(ip);
		}

		return String.join("#", ips);
	}

	public ArrayList<InetAddress> getFromIpsInetAddress(byte[] data)
	{
		int currPos = 1;

		while (data[currPos] != FROM_IPS)
		{
			if (data[currPos] == COMBINATION_NET_ID)
			{
				int numberOfNetIds = data[currPos];
				currPos = 3;
				for (int i = 0; i < numberOfNetIds; i++) 
				{
					int lengthNetId = data[currPos++];
					currPos += lengthNetId;
				}
			}
			else
			{
				currPos = currPos + 1;
				int length = data[currPos++];
				currPos = currPos + length;
			}
		}

		ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
		while (data[currPos] != END_OF_PACKET)
		{
			if (data[currPos] != FROM_IPS)
			{
				break;
			}
			
			currPos++;
			byte[] ipBytes = new byte[data[currPos++]];
			System.arraycopy(data, currPos, ipBytes, 0, ipBytes.length);
			currPos = currPos + ipBytes.length;
			try
			{
				InetAddress ip = InetAddress.getByAddress(ipBytes);
				ips.add(ip);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return ips;
	}

	public String getTarget(int target, byte[] data)
	{
		int currPos = 1;

		while (data[currPos] != target && data[currPos] != END_OF_PACKET)
		{
			if (data[currPos] == COMBINATION_NET_ID)
			{
				int numberOfNetIds = data[currPos];
				currPos = 3;
				for (int i = 0; i < numberOfNetIds; i++) 
				{
					int lengthNetId = data[currPos++];
					currPos += lengthNetId;
				}
			}
			else
			{
				currPos = currPos + 1;
				int length = data[currPos++];
				currPos = currPos + length;
			}
		}

		if (data[currPos] == END_OF_PACKET)
		{
			return null;
		}

		currPos++;
		byte[] targetBytes = new byte[data[currPos++]];
		System.arraycopy(data, currPos, targetBytes, 0, targetBytes.length);

		String payload = new String(targetBytes);

		return payload;
	}

	public String decodeRouterOrEndpointPacket(byte[] data)
	{
		ArrayList<String> val = new ArrayList<String>();

		String netIdString = getNetIdString(data);
		if (netIdString.split("\\.").length == 1)
		{
			netIdString = NET_ID + netIdString;
		}
		else
		{
			netIdString = COMBINATION_NET_ID + netIdString;
		}

		String payload = getTarget(PAYLOAD, data);
		payload = PAYLOAD + payload;

		val.add(netIdString);
		val.add(payload);
		
		return String.join("#", val);
	}

	public String decodeControllerRequestPacket(byte[] data)
	{
		ArrayList<String> val = new ArrayList<String>();

		String netIdString = getNetIdString(data);
		if (netIdString.split("\\.").length == 1)
		{
			netIdString = NET_ID + netIdString;
		}
		else
		{
			netIdString = COMBINATION_NET_ID + netIdString;
		}

		String fromIpStr = getTarget(FROM_IP_STR, data);
		fromIpStr = FROM_IP_STR + fromIpStr;
		
		val.add(netIdString);
		val.add(fromIpStr);

		String fromIps = getFromIps(data);
		String[] fromIpsArr = fromIps.split("#");
		for (String ip : fromIpsArr)
		{
			String temp = new String(FROM_IPS + ip);
			val.add(temp);
		}
		
		return String.join("#", val);
	}

	public String decodeControllerResponsePacket(byte[] data)
	{
		ArrayList<String> val = new ArrayList<String>();

		String netIdString = getNetIdString(data);
		if (netIdString.split("\\.").length == 1)
		{
			netIdString = NET_ID + netIdString;
		}
		else
		{
			netIdString = COMBINATION_NET_ID + netIdString;
		}

		String nextHop = getTarget(NEXT_HOP, data);
		nextHop = NEXT_HOP + nextHop;

		val.add(netIdString);
		val.add(nextHop);

		return String.join("#", val);
	}

	public byte[] createAck(byte[] data, int typeFrom)
	{
		byte[] pack = new byte[2];
		pack[0] = (byte) ACK;
		pack[1] = (byte) typeFrom;
		return pack;
	}
}
