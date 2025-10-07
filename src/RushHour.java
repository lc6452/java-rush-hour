import java.util.Scanner;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
/**
 * RushHour.java
 * Purpose: Driver class for Solver.java.
 *
 * @author Lucas Chang
 * @version 2.0
 * @date 20 September 2016
 */

public class RushHour {

	private static int width, height, rows, cols, x, y;
	private static String directions;
	private static char board[][];
	private static int r, g, b;
	private static ArrayList<Vehicle> parkingSpot = new ArrayList<Vehicle>();
	private static Color colour;
	
	/**
	 * Driver for game
	 * @param args command line arguments (input file path)
	 * @throws FileNotFoundException, InterruptedException
	 */
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		
		
		Scanner file = new Scanner(new FileInputStream(args[0]));
		//Check if file has any content
		if (!(file.hasNext())){
			System.out.println("Empty file");
			System.exit(0);
		}	
		//Read file contents
		readInput(file);
		
		//Displays game window as solution is being found
		StdDraw.setCanvasSize(cols*100, rows*100);
		StdDraw.setXscale(0, cols);
		StdDraw.setYscale(0, rows);
		StdDraw.clear(StdDraw.BLACK);
	    StdDraw.setPenColor(Color.WHITE);
	    StdDraw.text(3, 3, "Loading ...");
	    StdDraw.enableDoubleBuffering();
	    StdDraw.show();
	    
	    //Finds winning state of given initial state
		Solver.getSolution(parkingSpot);
		
	}

	/**
	 * Reads initial game configuration from text file
	 * @param file the scanner for reading the input file
	 */
	private static void readInput(Scanner file){
		
		Random rnd = new Random();

		//Read row and column values from file
				
		rows = file.nextInt();
		cols = file.nextInt();
		int i = 0;
		
		try{
			while (file.hasNext()){
				
				//Give first car a red color
				if (i == 0){
					r = 255;
					g =  b = 0;
				}
				
				//Assign random color to vehicle
		
				else{
					r = rnd.nextInt(255);
					g = rnd.nextInt(255);
					b = rnd.nextInt(255);
				}
				
				colour = new Color(r, g, b);
				
				//Read text file and create a new car
				
				x = file.nextInt();
				y = file.nextInt();
				width = file.nextInt();
				height = file.nextInt();
				file.nextLine();
				directions = file.nextLine();
				String moves[] = directions.split(" ");
				Vehicle car = new Vehicle(x, y, width, height, moves, colour);
				
				//Append car to list
				if (isValid(car)){
					
					parkingSpot.add(car);
				}
				else{
					System.out.println("Invalid dimensions");
					System.exit(0);
				}
				i++;
			}
		}
		
		/**
		 * Execute catch block if the last car in the file has no moves
		 * There won't be a last line so catch exception and assign blank move to car
		 */
		
		catch (NoSuchElementException e){
			
			directions = "";
			String moves[] = directions.split(" ");
			Vehicle car = new Vehicle(x, y, width, height, moves, colour);
			
			if (isValid(car)){
				parkingSpot.add(car);
			}
			else{
				System.out.println("Invalid dimensions");
				System.exit(0);
			}
		}
	}

	/**
	 * Displays current game state
	 * @param parkingSpot arraylist representation of a game state
	 * @throws InterruptedException
	 */
	public static void display(ArrayList<Vehicle> parkingSpot) throws InterruptedException{

		StdDraw.clear();
		
		//Draws parking lot
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				StdDraw.setPenColor(0, 0, 0);
				StdDraw.square(x + 0.5, y + 0.5, 0.5);
			}
		}
		
		//Parks cars
		for (int i = 0; i < parkingSpot.size(); i++){
			
			Vehicle car = parkingSpot.get(i);
			
			for (int j = 0; j < car.getWidth(); j++){
				for (int k = 0; k < car.getHeight(); k++) {			
					StdDraw.setPenColor(car.getColour());
					StdDraw.filledSquare(car.getX() + j + 0.5, car.getY() + k + 0.5, 0.5);
				}
			}
			 
		}
		//display current game state
		StdDraw.enableDoubleBuffering();
	    StdDraw.show();
		Thread.sleep(300);
	}
	
	/**
	 * Checks if target has been reached
	 * @param parkingSpot arraylist representation of a game state
	 * @return true if red car is at the right edge of the parking lot, false otherwise
	 */
	public static boolean isGoal(ArrayList<Vehicle> parkingSpot){
		Vehicle redCar = parkingSpot.get(0);
		//Checks if car is at right edge of parking lot
		if (redCar.getX() + redCar.getWidth() == cols)
			return true;

		return false;
	}
	
	/**
	 * Checks if each car has valid attributes
	 * @param car the car object to validate
	 * @return true if all attributes are legal, false otherwise
	 */
	private static boolean isValid(Vehicle car){
		return (car.getX() >= 0 && car.getX() + car.getWidth() <= cols && car.getY() >= 0 && car.getY() + car.getHeight() <= rows 
				&& car.getHeight() > 0 && car.getWidth() > 0 && rows > 0 && height > 0);
	}

	/**
	 * Checks if at least one (red)vehicle has been provided by the input file  
	 * @return true if text file has at least one car in it, false otherwise
	 */		
	private static boolean hasVehicle(){
		return (parkingSpot.size() > 0);
	}
	
	/**
	 * Shows message to player after all the moves have been executed
	 * @param outcome the message to display
	 * @throws FileNotFoundException, InterruptedException
	 */
    public static void dialog(String outcome) throws FileNotFoundException, InterruptedException{
	    JOptionPane.showMessageDialog(null, outcome);
	    System.exit(0);
	}
	
    /**
     * @return parking lot height
     */
    public static int getRows(){
    	return rows;
    }
    
    /**
     * @return parking lot width
     */
    public static int getCols(){
    	return cols;
    }
}