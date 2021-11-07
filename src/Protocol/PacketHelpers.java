package Protocol;

// import java.net.InetAddress;

public class PacketHelpers 
{
	byte[] data;
	String[] netId;
	String netIdString;
	byte type;
	// InetAddress fromIp;
	// int fromPort;
	String payload;

	public PacketHelpers(byte[] data, byte type) 
	{
		this.data = data;
		this.type = type;
		// use type to decide which handler to call
		decodeRouterPacket();
	}

	public PacketHelpers(String netIdString, String payload, byte type) 
	{
		this.payload = payload;
		this.netIdString = netIdString;
		if (netIdString.split("\\.").length == 0) 
		{
			this.netId = new String[1];
			this.netId[0] = netIdString;
		}
		else 
		{
			this.netId = netIdString.split("\\.");
		}
		// use type to decide which handler to call
		createRouterPacket();
	}

	public void createRouterPacket() 
	{
		int sizeOfData = 3 + netId.length + payload.getBytes().length;

		for (String netIdTemp : netId)
		{
			sizeOfData += netIdTemp.length(); 
		}

		byte typeNetIdCombo;
		byte[] payloadBytes = payload.getBytes();
		int currPos;

		if (netId.length == 1) 
		{
			typeNetIdCombo = 1;
			currPos = 2;
		}
		else 
		{
			currPos = 3;
			typeNetIdCombo = 2;
			sizeOfData++;
		}

		data = new byte[sizeOfData];

		data[0] = this.type;
		data[1] = typeNetIdCombo;
		data[2] = (currPos == 3 ? (byte) netId.length : (byte) netId[0].length());

		for (int i = 0; i < netId.length; i++) 
		{
			byte[] temp = netId[i].getBytes();
			data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, data, currPos, temp.length);
			currPos += temp.length;
		}

		data[currPos++] = (byte) payloadBytes.length;
		System.arraycopy(payloadBytes, 0, data, currPos, payloadBytes.length);

	}

	/**
	 * @params void
	 * 
	 * @return void
	 * 
	 * @implNote The breakdown of the bytes for the Router packet is as follows:
	 * 		     [0] - type of packet (1 = Router)
	 * 			 [1] - type (network id = 1, combination = 2)
	 * 			 [2] - length of net id || number of net ids to follow
	 * 			 [3, a || 3] - net id || length of net id 1
	 * 			 [N/A || 4, a] - N/A || net id 1
	 * 		     ... more length of net ids and net id values
	 * 			 [...] - payload length
	 * 			 [... + 1] - payload
	 * 
	 */
	public void decodeRouterPacket() 
	{
		int numOfNetIds = 1;
		int currPos = 2;
		if (data[1] == 0x2) 
		{
			numOfNetIds = data[currPos];
			currPos = 3;
		}

		netId = new String[numOfNetIds];
		
		for (int i = 0; i < numOfNetIds; i++) 
		{
			int lengthNetId = data[currPos++];
			byte[] temp = new byte[lengthNetId];
			System.arraycopy(data, currPos, temp, 0, lengthNetId);
			currPos += lengthNetId;
			netId[i] = new String(temp);
		}

		netIdString = String.join(".", netId);

		int payloadLength = data[currPos++];

		byte[] temp = new byte[payloadLength];

		System.arraycopy(data, currPos, temp, 0, payloadLength);

		payload = new String(temp);

	}
}