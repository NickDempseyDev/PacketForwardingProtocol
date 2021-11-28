package Applications.ControllerTest;

import java.util.HashMap;
import Controller.Controller;

public class ControllerTest
{
	public ControllerTest()
	{
		HashMap<String, String> netIdOwnershipTable = new HashMap<String, String>();
		netIdOwnershipTable.put("tcd.scss", "endpointreceiver");
		Controller controller = new Controller(netIdOwnershipTable, 4);
		controller.start();
	}

	public static void main(String[] args) 
	{
		try 
		{
			ControllerTest controllerTest = new ControllerTest();	
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
