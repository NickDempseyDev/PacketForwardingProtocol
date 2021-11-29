package Controller;

import java.net.InetAddress;

public class Test {
	public Test(Integer i)
	{
		i++;
	}
	public static void main(String[] args) {
		Integer i = 1;
		Test test = new Test(i);
		System.out.println(i);
	}
}
