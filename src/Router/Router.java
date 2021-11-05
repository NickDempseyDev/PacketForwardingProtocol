package Router;

public class Router {
	String routerName;
	public Router(String name) {
		routerName = name;
	}

	public void printName() {
		System.out.println("Printing from the Router Class and implemented in: " + routerName);
	}
}
