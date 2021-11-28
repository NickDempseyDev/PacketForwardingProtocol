package Protocol;

public class ProtocolTypes
{
	// Packet types
	final public static int ROUTER = 0x1;
	final public static int ENDPOINT = 0x2;
	final public static int ACK = 0x3;
	final public static int CONTROLLER_REQUEST = 0x4;
	final public static int CONTROLLER_RESPONSE = 0x5;
	final public static int HELLO_R = 0x6;
	final public static int GOOD_TO_GO = 0x7;

	// Types for the TLVs
	final public static int NET_ID = 0x1;
	final public static int COMBINATION_NET_ID = 0x2;
	final public static int NEXT_HOP = 0x3;
	final public static int PAYLOAD = 0x4;
	final public static int FROM_IP_STR = 0x5;
	final public static int FROM_IPS = 0x6;
	final public static int END_OF_PACKET = 0x7;
}
