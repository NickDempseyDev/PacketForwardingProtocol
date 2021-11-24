package Protocol;

public class ControllerPacketData
{
	private byte type;
	
	private byte[] data;
	private String netIdString;
	private String fromIp;
	private String nextHop;
	private String[] netId;
	
	public ControllerPacketData(byte[] data)
	{
		PacketHelper helper = new PacketHelper(data, data[0]);
		setData(data);
		setNetId(helper.netId);
		setNetIdString(helper.netIdString);
		setNextHop(helper.nextHop);
		setFromIp(helper.fromIp);
	}
	
	public ControllerPacketData(String netIdString, String nextHop, String fromIp, byte type)
	{
		setType(type);
		setFromIp(fromIp);
		setNetIdString(netIdString);
		setNextHop(nextHop);
		createPacket();
	}
	
	public void createPacket()
	{
		PacketHelper helper = new PacketHelper(getType(), getNextHop(), getNetIdString(), getFromIp());
		setData(helper.data);
	}
	
	public byte[] createAck()
	{
		PacketHelper helper = new PacketHelper(getData(), (byte) 0x4);
		return helper.createAck();
	}
	
	public String getNextHop()
	{
		return nextHop;
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
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	public void setNetIdString(String netIdString)
	{
		this.netIdString = netIdString;
	}
	
	public void setNextHop(String nextHop)
	{
		this.nextHop = nextHop;
	}
	
	public void setNetId(String[] netId)
	{
		this.netId = netId;
	}
	
	public String getFromIp()
	{
		return fromIp;
	}
	
	public void setFromIp(String fromIp)
	{
		this.fromIp = fromIp;
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
