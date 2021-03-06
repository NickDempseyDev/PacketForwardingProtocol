package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NetworkGraph
{
	HashMap<String, Integer> indexLookUp = new HashMap<String, Integer>();
	HashMap<Integer, String> nameLookUp = new HashMap<Integer, String>();
	HashMap<String, ArrayList<String>> connections;
	DirEdge[][] adjList;

	public NetworkGraph(HashMap<String, ArrayList<String>> connections, int totalNoOfRouters)
	{
		this.connections = connections;
		adjList = new DirEdge[totalNoOfRouters][];
		Set<String> keys = connections.keySet();
		Integer counter = 0;
		for (String router : keys)
		{
			indexLookUp.put(router, counter);
			nameLookUp.put(counter, router);
			counter++;
		}

		for (int i = 0; i < adjList.length; i++)
		{
			String currRouterName = nameLookUp.get(i);	
			adjList[i] = new DirEdge[connections.get(currRouterName).size()];
			int count = 0;
			for (String to : connections.get(currRouterName))
			{
				adjList[i][count++] = new DirEdge(to, indexLookUp.get(to));
			}
		}
	}

	private class DirEdge
	{
		String name;
		int indexInDirEdge;

		public DirEdge(String name, int index)
		{
			this.name = name;
			this.indexInDirEdge = index;
		}
	}

	// single source, every destination
	// code taken from 2nd year algorithms text book: Algorithms, 4th Edition by Robert Sedgewick and Kevin Wayne
	public HashMap<String, String> dijkstra(int source)
	{
		boolean[] visited = new boolean[adjList.length];
		int[] distanceTo = new int[adjList.length];
		String[] path = new String[adjList.length];

		for(int i = 0; i < visited.length; i++)
		{
			visited[i] = false;
			distanceTo[i] = Integer.MAX_VALUE;
			path[i] = "";
		}
		distanceTo[source] = 0;

		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(source);

		while (!queue.isEmpty())
		{
			int currentVertex = findMin(queue, distanceTo);

			for (DirEdge edge : adjList[currentVertex])
			{
				if (!visited[edge.indexInDirEdge])
				{
					queue.add(edge.indexInDirEdge);
					visited[edge.indexInDirEdge] = true;
				}

				if (distanceTo[edge.indexInDirEdge] > distanceTo[currentVertex] + 1)
				{
					distanceTo[edge.indexInDirEdge] = distanceTo[currentVertex] + 1;
					String curr = (path[currentVertex].length() == 0 ? "" : path[currentVertex] + ":");
					path[edge.indexInDirEdge] = curr + edge.name;
				}
			}
		}
		HashMap<String, String> ret = new HashMap<String, String>();
		for (int i = 0; i < path.length; i++)
		{
			String[] paths = path[i].split(":");
			if (path[i].length() != 0)
			{
				String dest = paths[paths.length - 1];
				String sourcePlusOne = paths[0];
				ret.put(dest, sourcePlusOne);
			}
		}			
		return ret;
	}

	private int findMin(ArrayList<Integer> queue, int[] distanceTo)
	{
		int min = Integer.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < queue.size(); i++)
		{
			int index = queue.get(i);
			if (min > distanceTo[index])
			{
				minIndex = i;
				min = distanceTo[index];
			}
		}
		return queue.remove(minIndex);
	}

	public HashMap<String, HashMap<String, String>> generateRoutingTable()
	{
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		Set<String> keys = indexLookUp.keySet();
		for (String s : keys)
		{
			map.put(s, dijkstra(indexLookUp.get(s)));
		}
		return map;
	}
}
