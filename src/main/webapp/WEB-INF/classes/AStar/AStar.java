/*
 * Alonso Martinez
 */
package AStar;

import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Set;
import AStar.Settings;
import DataAccess.DirectionBuilder;
import DataAccess.Map;
public class AStar{
	//variable to see if we have found our node
    private Settings settings; //The settings to be used for additional costs in AStar*
    //Default constructor
    public AStar()
    {}
    //AStar constructor that takes in a map and selections of settings to prepare for a new AStar search
    public AStar(Settings settings)
    {
    	this.settings = settings;
    }
    //Function to change the AStar settings
    public void setSettings(Settings settings)
    {
    	this.settings = settings;
    }
	//function to print out the path. Takes a node and will print the path if there is one to this node
	public List<Node> buildPath(Node end){
		
		//create a new array list of nodes
		List<Node> path = new LinkedList<Node>();
		
		
		//get the nodes in sequence from the end to the start
		for(Node node = end; node!=null; node = node.parent){
			path.add(node);
		}
		if (path.size() <= 1)//If there is not path
		{
			return new LinkedList<Node>(); //Return empty list
		}
		//reverse the path as it goes from end to finish and we want start to finish
		Collections.reverse(path);
		return path;
	}

	private void clearParents(List<Node> nodeList)
	{
		for(Node n : nodeList)
		{
			n.parent = null;
		}
	}

	public List<Node> findPath(Node start, Node end, List<Node> map){
		this.clearParents(map);
		
		//create a new hashSet of nodes that we have already checked
		Set<Node> checkedNodes = new HashSet<Node>();

		
		
		//this is our queue for storing the nodes that we are one. This queue will process 
		//the nodes. We are using a queue because of speed reasons
		PriorityQueue<Node> queue = new PriorityQueue<Node>(20, 
				new Comparator<Node>(){
			
			//we need this compare method to compare the costs of the nodes so that we can
			//sort our queue based on the closest node
			public int compare(Node i, Node j){
				if(i.fValue > j.fValue){
					return 1;
				}

				else if (i.fValue < j.fValue){
					return -1;
				}

				else{
					return 0;
				}
			}

		}
				);

		//set our first gValue to 0 since it is the start
		start.gValue = 0;

		//add our start node to the queue
		
		queue.add(start);
		
		
		//while our queue is not empty and we have not found a path, execute
		while(!queue.isEmpty()){
			//System.out.println(queue);
			//define a new node named current that will be the current node we are processing
			//set this equal to the top element of the queue for processing
			Node current = queue.poll();

			//add our node to the checked nodes set so that we dont check it again
			if (checkedNodes.contains(current))
			{
				current.parent = null;
			}
			checkedNodes.add(current);

			//if the node name is equal to the node we are searching for, then we are done
			//might change this so that it works off of something other than name
			//System.out.println(checkedNodes);
			//System.out.println(current);
			//System.out.println(current.neighbors);
			if(current.xPos == end.xPos && current.yPos == end.yPos){
				break;
			}
			//here we are checking every edge of every neighboring node for the node we are searching for
			
			
			for(Node neighbor : current.neighbors){ //Check the neighbor nodes
				
				if(checkedNodes.contains(neighbor)) //If neighbor has already been checked
				{
					continue;
				}
				double gScoreTemp = current.gValue + getGValue(current, neighbor); //Get the tentative G-Value
				if(!queue.contains(neighbor)) //If not in the que add the neighbor to the queue
				{
					neighbor.gValue = gScoreTemp;
					neighbor.hValue = getHValue(neighbor, end);
					neighbor.fValue = neighbor.gValue + neighbor.hValue;
					queue.add(neighbor);
				}
				else if(gScoreTemp > neighbor.gValue) 
				{
					continue; //Bad path
				}
				neighbor.parent = current; //Update the neighbors values

				neighbor.gValue = gScoreTemp;
				neighbor.hValue = getHValue(neighbor, end);
				neighbor.fValue = neighbor.gValue + neighbor.hValue;

			}

		}
		return buildPath(end);

	}
	
	public static double getGValue(Node node1, Node node2)
	{
		return getDistance(node1, node2);
	}
	public static double getHValue(Node node1, Node node2)
	{
		return 0;//getDistance(node1, node2);
	}
	//function gets the distance between two nodes. This will be used as the cost
	public static double getDistance(Node one, Node two){

		double dx = one.xPos - two.xPos;

		double dy = one.yPos - two.yPos;

		double distance = Math.sqrt(dx*dx + dy*dy);


		return distance;
	}
	
	private List<String> runAStar(Node startNode, Node endNode, Map map) {
    	if (startNode != null && endNode != null)
    	{
    		List<Node> path = Node.getPathFromNode(startNode, endNode, map);
    		System.out.println("The Path is");
    		System.out.println(path);
            return DirectionBuilder.getDirectionsList(path, 1, 1);
    	}
    	else
    		return null;
    }
	
}