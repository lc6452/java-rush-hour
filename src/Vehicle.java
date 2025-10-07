import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Vehicle {

	private int startX, startY, endX, endY;
	private Color colour;
	private List<String> moves;
	
	/**
	 * Creates new vehicle by assigning it certain attributes provided by input file
	 * @param x x coordinate
	 * @param y y coordinate  
	 * @param width width of vehicle
	 * @param height height of vehicle
	 * @param directions array of possible directions
	 * @param colour color of car
	 */
	public Vehicle(int x, int y, int width, int height, String directions[], Color colour){
		
		this.startX = x;
		this.startY = y;
		this.colour = colour;
		this.endX = width;
		this.endY =  height;
		this.moves = Arrays.asList(directions);
		
	}
	/**
	 * @return x coordinate of car
	 */
	public int getX(){
		return startX;
	}
	/**
	 * @return y coordinate of car
	 */
	public int getY(){
		return startY;
	}
	/**
	 * @return width of car
	 */
	public int getWidth(){
		return endX;
	}
	/**
	 * @return height of car
	 */
	public int getHeight(){
		return endY;
	}
	/**
	 * @return possible moves car can make
	 */
	public List<String> getMoves(){
		return moves;
	}
	/**
	 * @return color of car
	 */
	public Color getColour() {
		return colour;
	}
	
	public void setColour(Color colour) {
		this.colour = colour;
	}
		
	/**
	 * Updates x and y coordinates after each move
	 * @param direction the direction in which car should be moved
	 */
	public void move(String direction) {
		
		switch(direction){
			
			case "up":
				startY ++;
				break;
			
			case "down":
				startY --;
				break;
			
			case "left":
				startX --;
				break;
			
			case "right":
				startX ++;
				break;
		}
		
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colour == null) ? 0 : colour.hashCode());
		result = prime * result + endX;
		result = prime * result + endY;
		result = prime * result + ((moves == null) ? 0 : moves.hashCode());
		result = prime * result + startX;
		result = prime * result + startY;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		if (colour == null) {
			if (other.colour != null)
				return false;
		} else if (!colour.equals(other.colour))
			return false;
		if (endX != other.endX)
			return false;
		if (endY != other.endY)
			return false;
		if (moves == null) {
			if (other.moves != null)
				return false;
		} else if (!moves.equals(other.moves))
			return false;
		if (startX != other.startX)
			return false;
		if (startY != other.startY)
			return false;
		return true;
	}



	
}