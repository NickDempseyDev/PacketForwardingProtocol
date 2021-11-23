package Protocol;

public class EndpointPacketData
{
	private byte type;
	private byte[] data;
	private String netIdString;
	private String[] netId;
	private String payload;
	
	public EndpointPacketData(byte[] data)
	{
		setType((byte)2);
		PacketHelper helper = new PacketHelper(data, (byte) 0x2);
		setData(data);
		setNetId(helper.netId);
		setNetIdString(helper.netIdString);
		setPayload(helper.payload);
	}
	
	public EndpointPacketData(String netIdString, String payload)
	{
		setType((byte)2);
		setNetIdString(netIdString);
		setPayload(payload);
		createPacket();
	}
	
	public void createPacket()
	{
		PacketHelper helper = new PacketHelper(getNetIdString(), getPayload(), getType());
		setData(helper.data);
	}
	
	public byte[] createAck()
	{
		PacketHelper helper = new PacketHelper(getData(), getType());
		return helper.createAck();
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public String getNetIdString()
	{
		return netIdString;
	}
	
	public String[] getNetId()
	{
		return netId;
	}
	
	public String getPayload()
	{
		return payload;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	public void setNetIdString(String netIdString)
	{
		this.netIdString = netIdString;
	}
	
	public void setNetId(String[] netId)
	{
		this.netId = netId;
	}
	
	public void setPayload(String payload)
	{
		this.payload = payload;
	}

	public byte getType()
	{
		return type;
	}
	
	public void setType(byte type)
	{
		this.type = type;
	}
}
