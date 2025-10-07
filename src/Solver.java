import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * Solver.java
 * Purpose: Finds a solution for a given board.
 *
 * @author Lucas Chang
 * @version 2.0
 */

public class Solver {
	
	private static int moves;
	private static boolean finished = false;
	private static ArrayList<Vehicle> childNode;
	private static Stack<ArrayList<Vehicle>> stack = new Stack<>();
	private static Queue<ArrayList<Vehicle>> queue = new LinkedList<>();
	private static Map<ArrayList<Vehicle>, ArrayList<Vehicle>> hashmap = new HashMap<>();
   
	/**
	 * Adds initial game state to hash map and queue and starts exploring nodes
	 * @param original array list representation of game state
	 * @throws InterruptedException, FileNotFoundException
	 */
	public static void getSolution(ArrayList<Vehicle> original) throws InterruptedException, FileNotFoundException{
		
		//Adds initial game state to hash map and assigns it a value of null
		enqueue(original, null);
		
		while (!(queue.isEmpty()) && !(finished)){
			
			ArrayList<Vehicle> parentNode = queue.remove();
			createGameState(parentNode);
		}
	}
	
	/**
	 * Creates child nodes from parent node
	 * @param parentNode the parent node to expand
	 * @throws InterruptedException, FileNotFoundException
	 */
	private static void createGameState(ArrayList<Vehicle> parentNode) throws InterruptedException, FileNotFoundException {
		
		childNode = getCopy(parentNode);
		
		for (int i = 0; i < parentNode.size(); i++){
			
			String direction;	
			Vehicle car = childNode.get(i);
			
			for(int j = 0; j < car.getMoves().size(); j++){
		
				direction = car.getMoves().get(j);
				//Checks if move is valid
				if (canMove(car, direction)){
					enqueue(childNode, parentNode);
				}
				
				childNode = getCopy(parentNode);
				car = childNode.get(i);
			}
		}
		
	}

	/**
	 * Checks if move is valid, discards node if not valid
	 * @param car the vehicle to move
	 * @param direction the direction to move the car
	 * @return true if car object can move in given direction, false otherwise
	 */
	private static boolean canMove(Vehicle car, String direction) {
		
		car.move(direction);
		//Checks for collisions and if car is still within bounds after each move
		if(isValid() && noCollision())
			return true;
		return false;

	}
	
	/**
	 * Checks if cars are not overlapping
	 * @return true if no cars are overlapping, false if there are collisions
	 */
	private static boolean noCollision(){
		
		boolean board[][] = new boolean[RushHour.getRows()][RushHour.getCols()];
		
		for (int i = 0; i < board.length; i++){
			
			for (int j = 0; j < board[i].length; j++)
				board[i][j] = false;
		}
		
		for (int i = 0; i < childNode.size(); i++){
			
			for (int j = 0; j < childNode.get(i).getWidth(); j++){
				for (int k = 0; k < childNode.get(i).getHeight(); k++){
					if (!(board[childNode.get(i).getY()+k][childNode.get(i).getX()+j])){
						board[childNode.get(i).getY()+k][childNode.get(i).getX()+j] = true;
					}
						
					else
						return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if all cars have valid dimensions, starting coordinates and
	 * they're inside the parking lot
	 * @return true if the cars are within bounds, false otherwise
	 */
	private static boolean isValid() {
		
		Vehicle car;
		int cols = RushHour.getCols();
		int rows = RushHour.getRows();
		
		for (int i = 0; i < childNode.size(); i++){
			
			car = childNode.get(i);
			
			if (!(car.getX() >= 0 && car.getX() + car.getWidth() <= cols && car.getY() >= 0 && car.getY() + car.getHeight() <= rows
					&& car.getHeight() > 0 && car.getWidth() > 0)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Adds node to queue and hash map
	 * uses child node as key and parent node as value
	 * @param childNode the child node to add
	 * @param parentNode the parent node
	 * @throws InterruptedException, FileNotFoundException
	 */
	private static void enqueue(ArrayList<Vehicle> childNode, ArrayList<Vehicle> parentNode) throws InterruptedException, FileNotFoundException {
		//Checks if node is not already in the hashmap
		if (!(hashmap.containsKey(childNode))){
			
			hashmap.put(childNode, parentNode);
			queue.add(childNode);
			
			//Checks if goal has been reached
			if(RushHour.isGoal(childNode)){
				finished = true;
				backTrack(childNode);
				System.exit(0);
			}	
		}
		
	}
	
	/**
	 * Takes the winning game state, adds it to a stack
	 * finds parent node of the winning game state and also adds it to a stack
	 * using child node's key. Repeats process until initial game state is reached.
	 * Then it displays each game state by popping the stack
	 * @param childNode the goal node
	 * @throws InterruptedException, FileNotFoundException
	 */
	private static void backTrack(ArrayList<Vehicle> childNode) throws InterruptedException, FileNotFoundException {
		
		moves = 0;
		stack.add(childNode);
		
		ArrayList<Vehicle> parentNode = hashmap.get(childNode);
		
		while (parentNode != null){
			stack.add(parentNode);
			moves++;
			parentNode = hashmap.get(parentNode);
		}
		//Visualizes solution
		while (!(stack.empty())){
			RushHour.display(stack.pop());
		}
		//Displays number of moves at the end of the game
		RushHour.dialog("Solved in " + moves+ " move(s)");
	}

	/**
	 * Creates a copy of an array list
	 * @param original the arraylist from which copy is made
	 * @return copy of parameter arraylist
	 */
	private static ArrayList<Vehicle> getCopy(ArrayList<Vehicle> original){
		
		ArrayList<Vehicle> copyOfList = new ArrayList<Vehicle>();
		
		for (Vehicle car: original){
			
				int x = car.getX();
				int y = car.getY();
				int width = car.getWidth();
				int height = car.getHeight();
				String[] moves = car.getMoves().toArray(new String[0]);
				Color colour = car.getColour();
				copyOfList.add(new Vehicle(x, y, width, height, moves, colour));
		}
		
		return copyOfList;
	}
}
