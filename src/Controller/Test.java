package Controller;

import java.net.InetAddress;

public class Test {
	public Test()
	{
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] ips = InetAddress.getAllByName(localhost.getCanonicalHostName());
			byte[] ip = ips[0].getAddress();
			int a = 5;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Test test = new Test();
	}
}
