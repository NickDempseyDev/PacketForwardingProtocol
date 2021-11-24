package Protocol;

// import java.net.InetAddress;

public class PacketHelper 
{
	byte type;
	byte[] data;
	String[] netId;
	String netIdString;
	String payload;
	String nextHop;
	String fromIp;

	public PacketHelper(byte[] data, byte type)
	{
		this.type = type;

		if (type == 0x1 /* Router */ || type == 0x2 /* Endpoint */) 
		{
			this.data = data;
			decodeRouterOrEndpointPacket();
		}
		else if (type == 0x4 /* Controller Request*/)
		{
			this.data = data;
			decodeRequestControllerpacket();
		}
		else if (type == 0x5 /* Controller Response*/)
		{
			this.data = data;
			decodeControllerResponsePacket();
		}
	}

	public PacketHelper(String netIdString, String payload, byte type) 
	{
		this.type = type;

		if (type == 0x1 /* Router */ || type == 0x2 /* Endpoint */)
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
			createRouterOrEndpointPacket();
		}
	}

	public PacketHelper(byte type, String nextHop, String netIdString, String fromIp)
	{
		if (type == 0x4 /* Controller Request */)
		{
			this.type = type;
			this.fromIp = fromIp;
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
			createControllerRequestPacket();
		}
		else /* Controller Response */
		{
			this.type = type;
			this.nextHop = nextHop;
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
			createControllerResponsePacket();
		}
	}

	// Just in case this host has multiple IP addresses....
  	// InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
  	// if (allMyIps != null) {
    // 	System.out.println(" Full list of IP addresses:");
    // 	for (int i = 0; i < allMyIps.length; i++) {
	// 		System.out.println("    " + allMyIps[i]);
    // 	}
  	// }


	/**
	 * @params void
	 * 
	 * @return void
	 * 
	 * @implNote The breakdown of the bytes for the Router packet is as follows:
	 * 			 <ul>
	 * 			 <li>if there is no combination in the net id:</li>
	 * 			 <li>[0] - type of packet (1 = Router, 2 = Endpoint, 3 = Ack, 4 = Controller)</li>
	 * 			 <li>[1] - type (network id = 1, combination = 2, next hop = 3)</li>
	 * 			 <li>[2] - length of net id</li>
	 * 			 <li>[3, a]- net id</li>
	 * 			 <li>[a+1] - payload length</li>
	 * 			 <li>[a+2, b] - payload</li>
	 * 			 </ul>
	 * 
	 * 			 <ul>
	 * 			 <li>if there is combination in the net id:</li>
	 * 			 <li>[0] - type of packet (1 = Router, 2 = Endpoint, 3 = Ack, 4 = Controller)</li>
	 * 			 <li>[1] - type (network id = 1, combination = 2, next hop = 3)</li>
	 * 			 <li>[2] - number of net ids to follow</li>
	 * 			 <li>[3] - length of net id 1</li>
	 * 			 <li>[4, a] - net id 1</li>
	 * 		     <li>... more length of net ids and net id values</li>
	 * 			 <li>[...] - payload length</li>
	 * 			 <li>[... + 1, b] - payload</li>
	 * 			 </ul>
	 * 
	 */
	public void createRouterOrEndpointPacket() 
	{
		int sizeOfData = 3 + this.netId.length + this.payload.getBytes().length;

		for (String netIdTemp : this.netId)
		{
			sizeOfData += netIdTemp.length(); 
		}

		byte typeNetIdCombo;
		byte[] payloadBytes = this.payload.getBytes();
		int currPos;

		if (this.netId.length == 1) 
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

		this.data = new byte[sizeOfData];

		this.data[0] = this.type;
		this.data[1] = typeNetIdCombo;
		this.data[2] = (currPos == 3 ? (byte) this.netId.length : (byte) this.netId[0].length());

		for (int i = 0; i < this.netId.length; i++) 
		{
			byte[] temp = this.netId[i].getBytes();
			this.data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, this.data, currPos, temp.length);
			currPos += temp.length;
		}

		this.data[currPos++] = (byte) payloadBytes.length;
		System.arraycopy(payloadBytes, 0, this.data, currPos, payloadBytes.length);

	}

	/**
	 * @params void
	 * 
	 * @return void
	 * 
	 * @implNote The breakdown of the bytes for the Router packet is as follows:
	 * 			 <ul>
	 * 			 <li>if there is no combination in the net id:</li>
	 * 			 <li>[0] - type of packet (1 = Router, 2 = Endpoint, 3 = Ack, 4 = Controller)</li>
	 * 			 <li>[1] - type (network id = 1, combination = 2, next hop = 3)</li>
	 * 			 <li>[2] - length of net id</li>
	 * 			 <li>[3, a]- net id</li>
	 * 			 <li>[a+1] - payload length</li>
	 * 			 <li>[a+2, b] - payload</li>
	 * 			 </ul>
	 * 
	 * 			 <ul>
	 * 			 <li>if there is combination in the net id:</li>
	 * 			 <li>[0] - type of packet (1 = Router, 2 = Endpoint, 3 = Ack, 4 = Controller)</li>
	 * 			 <li>[1] - type (network id = 1, combination = 2, next hop = 3)</li>
	 * 			 <li>[2] - number of net ids to follow</li>
	 * 			 <li>[3] - length of net id 1</li>
	 * 			 <li>[4, a] - net id 1</li>
	 * 		     <li>... more length of net ids and net id values</li>
	 * 			 <li>[...] - payload length</li>
	 * 			 <li>[... + 1, b] - payload</li>
	 * 			 </ul>
	 * 
	 */
	public void decodeRouterOrEndpointPacket() 
	{
		int numOfNetIds = 1;
		int currPos = 2;
		if (this.data[1] == 0x2) 
		{
			numOfNetIds = this.data[currPos];
			currPos = 3;
		}

		this.netId = new String[numOfNetIds];
		
		for (int i = 0; i < numOfNetIds; i++) 
		{
			int lengthNetId = this.data[currPos++];
			byte[] temp = new byte[lengthNetId];
			System.arraycopy(this.data, currPos, temp, 0, lengthNetId);
			currPos += lengthNetId;
			this.netId[i] = new String(temp);
		}

		this.netIdString = String.join(".", this.netId);

		int payloadLength = this.data[currPos++];

		byte[] temp = new byte[payloadLength];

		System.arraycopy(this.data, currPos, temp, 0, payloadLength);

		this.payload = new String(temp);

	}

	public void decodeRequestControllerpacket()
	{
		int numOfNetIds = 1;
		int currPos = 2;
		if (this.data[1] == 0x2) 
		{
			numOfNetIds = this.data[currPos];
			currPos = 3;
		}

		this.netId = new String[numOfNetIds];
		
		for (int i = 0; i < numOfNetIds; i++) 
		{
			int lengthNetId = this.data[currPos++];
			byte[] temp = new byte[lengthNetId];
			System.arraycopy(this.data, currPos, temp, 0, lengthNetId);
			currPos += lengthNetId;
			this.netId[i] = new String(temp);
		}

		this.netIdString = String.join(".", this.netId);

		
		byte fromIpType = this.data[currPos++];
		
		int fromIpLength = this.data[currPos++];

		byte[] temp = new byte[fromIpLength];

		System.arraycopy(this.data, currPos, temp, 0, fromIpLength);

		this.fromIp = new String(temp);
	}

	
	public void createControllerRequestPacket()
	{
		int sizeOfData = 3 + this.fromIp.length() + 1 + this.netId.length;
		
		for (String netIdTemp : this.netId)
		{
			sizeOfData += netIdTemp.length(); 
		}
		
		byte typeNetIdCombo;
		int currPos;
		
		if (this.netId.length == 1) 
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
		
		this.data = new byte[sizeOfData];
		
		this.data[0] = this.type;
		this.data[1] = typeNetIdCombo;
		this.data[2] = (currPos == 3 ? (byte) this.netId.length : (byte) this.netId[0].length());
		
		for (int i = 0; i < this.netId.length; i++) 
		{
			byte[] temp = this.netId[i].getBytes();
			this.data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, this.data, currPos, temp.length);
			currPos += temp.length;
		}
		
		this.data[currPos++] = (byte)0x5; // from ip type
		this.data[currPos++] = (byte) this.fromIp.getBytes().length; // from ip length
		System.arraycopy(this.fromIp.getBytes(), 0, this.data, currPos, this.fromIp.getBytes().length); // from ip
	}

	public void decodeControllerResponsePacket()
	{
		int numOfNetIds = 1;
		int currPos = 2;
		if (this.data[1] == 0x2) 
		{
			numOfNetIds = this.data[currPos];
			currPos = 3;
		}

		this.netId = new String[numOfNetIds];
		
		for (int i = 0; i < numOfNetIds; i++) 
		{
			int lengthNetId = this.data[currPos++];
			byte[] temp = new byte[lengthNetId];
			System.arraycopy(this.data, currPos, temp, 0, lengthNetId);
			currPos += lengthNetId;
			this.netId[i] = new String(temp);
		}

		this.netIdString = String.join(".", this.netId);

		
		byte nextHopType = this.data[currPos++];
		
		int nextHopLength = this.data[currPos++];

		byte[] temp = new byte[nextHopLength];

		System.arraycopy(this.data, currPos, temp, 0, nextHopLength);

		this.nextHop = new String(temp);
	}
	
	public void createControllerResponsePacket()
	{
		int sizeOfData = 3 + this.nextHop.length() + 1 + this.netId.length;

		for (String netIdTemp : this.netId)
		{
			sizeOfData += netIdTemp.length(); 
		}

		byte typeNetIdCombo;
		int currPos;

		if (this.netId.length == 1) 
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

		this.data = new byte[sizeOfData];

		this.data[0] = this.type;
		this.data[1] = typeNetIdCombo;
		this.data[2] = (currPos == 3 ? (byte) this.netId.length : (byte) this.netId[0].length());

		for (int i = 0; i < this.netId.length; i++) 
		{
			byte[] temp = this.netId[i].getBytes();
			this.data[currPos++] = (byte) temp.length;
			System.arraycopy(temp, 0, this.data, currPos, temp.length);
			currPos += temp.length;
		}

		this.data[currPos++] = 0x3; // next hop type
		this.data[currPos++] = (byte) this.nextHop.getBytes().length; // next hop length
		System.arraycopy(this.nextHop.getBytes(), 0, this.data, currPos, this.nextHop.getBytes().length); // next hop value
	}

	public byte[] createAck()
	{
		byte[] pack = new byte[2];
		pack[0] = (byte) 3;
		pack[1] = (byte) this.type;
		return pack;
	}
}
