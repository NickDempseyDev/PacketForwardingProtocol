package Applications.Router1;

import java.net.InetAddress;

import Router.Router;

public class Router1 {

	public static void main(String[] args) {
		try {
			// InetAddress ip = InetAddress.getLocalHost();
			// InetAddress toIp = InetAddress.getByName("172.20.11.2");
			// Router router = new Router("router1", ip, toIp);
			// router.send("TEST MESSAGE");
			InetAddress localhost = InetAddress.getLocalHost();
  			System.out.println(" IP Addr: " + localhost.getHostAddress());
  			// Just in case this host has multiple IP addresses....
  			InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
  			if (allMyIps != null) {
    			System.out.println(" Full list of IP addresses:");
    			for (int i = 0; i < allMyIps.length; i++) {
					System.out.println("    " + allMyIps[i]);
    			}
  			}
		} catch (Exception e) {
			System.out.println(" (error retrieving server host name)");
		}
		
	}
}