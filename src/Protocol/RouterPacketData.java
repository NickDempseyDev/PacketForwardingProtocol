package Protocol;

public class RouterPacketData
{
	private byte[] data;
	
	private String netIdString;
	private String[] netId;
	private String payload;
	
	public RouterPacketData(byte[] data)
	{
		PacketHelper helper = new PacketHelper(data, (byte) 0x1);
		setData(data);
		setNetId(helper.netId);
		setNetIdString(helper.netIdString);
		setPayload(helper.payload);
	}
	
	public RouterPacketData(String netIdString, String payload)
	{
		setNetIdString(netIdString);
		setPayload(payload);
		createPacket();
	}

	public void createPacket()
	{
		PacketHelper helper = new PacketHelper(getNetIdString(), getPayload(), (byte) 0x1);
		setData(helper.data);
	}

	public byte[] createAck()
	{
		PacketHelper helper = new PacketHelper(getData(), (byte) 0x1);
		return helper.createAck();
	}
	
	public String getNetIdString()
	{
		return netIdString;
	}
	
	public void setNetIdString(String netIdString)
	{
		this.netIdString = netIdString;
	}
	
	public String[] getNetId()
	{
		return netId;
	}
	
	public void setNetId(String[] netId)
	{
		this.netId = netId;
	}
	
	public String getPayload()
	{
		return payload;
	}
	
	public void setPayload(String payload)
	{
		this.payload = payload;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
}
