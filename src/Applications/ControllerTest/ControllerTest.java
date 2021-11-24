package Applications.ControllerTest;

import java.net.InetAddress;
import java.util.HashMap;

import Controller.Controller;

public class ControllerTest
{
	public ControllerTest()
	{
		HashMap<String, String> netIdOwnershipTable = new HashMap<String, String>();
		netIdOwnershipTable.put("tcd.scss", "endpointreceiver");
		Controller controller = new Controller("./Applications/ControllerTest/paths.txt", netIdOwnershipTable, "controller");
		controller.start();
	}

	public static void main(String[] args) 
	{
		try 
		{
			System.out.println("MY IP IS " + InetAddress.getByName("controller"));
			ControllerTest controllerTest = new ControllerTest();	
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
