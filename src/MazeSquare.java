/**
 * Author: Gines Moratalla
 */

import java.awt.Point;
import java.util.ArrayList;

public class MazeSquare extends GameSquare
{
	private GameBoard board;						// A reference to the GameBoard this square is part of.
	private boolean target;							// true if this square is the target of the search.

	private MazeSquare target_square = null;		// MazeSquare that will be used as the target
	private MazeSquare source_square = null;		// MazeSquare that will be used as the source

	private ArrayList<ArrayList<Point>> all_paths;	// List of lists (will contain all paths, from here, we will find the shortest)

	/**
	 * Create a new GameSquare, which can be placed on a GameBoard.
	 * 
	 * @param x the x co-ordinate of this square on the game board.
	 * @param y the y co-ordinate of this square on the game board.
	 * @param board the GameBoard that this square resides on.
	 */

	// MazeSquare constructor
	public MazeSquare(int x, int y, GameBoard board)
	{
		super(x, y);
		this.board = board;
	}

	// Setter method to get and later retreive target later
	private void setTargetSquare() {

		this.target = true;
		target_square = this;
	}

	/**
	 * A method that is invoked when a user clicks on this square.
	 * This defines the end point for the search.
	 * 
	 */	

    public void leftClicked()
	{
		
		// reset highlighted squares (in case this not being the first path calculation)
		target = false;
		reset(0);
		board.getSquareAt(getXLocation(), getYLocation()).setHighlight(true);
		this.setTargetSquare();						// Set left clicked square to the most recently clicked one
	}
    
    /**
	 * A method that is invoked when a user clicks on this square.
	 * This defines the start point for the search. 
	 */	

	public void rightClicked()
	{	
		// Jump to findTarget method bellow to find the position of the target square
		target_square = findTarget();
		source_square = this;						// Set source square to the right clicked square

		// Statement to check if the same square was clicked
		if(target_square == source_square) {
			System.out.println("\nTASK 3: You clicked on the same Square, try another combination.");
			reset(1);

		// This is where the magic happens (different squares detected)
		} else if (target_square != null && target_square.target) {

			// Print current squares
			System.out.println("\nTASK 3:\nSource   x = " + getXLocation() + "; y = " + getYLocation());
			System.out.println("Target   x = " + target_square.getXLocation() + "; y = " + target_square.getYLocation() + "\n\nCalculating Shortest Path:\nThis might take some seconds...\n");

			// Create temporal (null) Array of Points (MazeSquares) to call the method DFS
			ArrayList<Point> shortest = new ArrayList<Point>();
			// Create a new instance of all_paths List of Lists defined above
			all_paths = new ArrayList<ArrayList<Point>>();

			// call DFS as list of list (above) and with current null shortest list (DFS stack)
			all_paths = DFS(getXLocation(), getYLocation(), shortest, all_paths);
			// Use list with all the paths to remove the non-shortest paths
			all_paths = findShortestPath(all_paths);
			// print shortest paths
			printAllShortPaths(all_paths);


			target_square.target = false;
		// Exclusive case where you didn't click a left (target) sqiare first
		} else {
			// Program did not find a target square, this means you didn't click left first
			System.out.println("\nTASK 3: Click left square first.");
		}

	}


	private MazeSquare findTarget()
	{
		// Iterate throught the board (two nested loops) to see if one of the squares has set target to true
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				// Upcasting to make gamesquare.getSquareAt a MazeSquare (with a target boolean variable)
				target_square = (MazeSquare) board.getSquareAt(i, j);
				if(target_square.target && target_square != null) {
					// Return true if this MazeSquare is the targer
					return target_square;
				}
			}
		}

		// Means no target square was found
		return null;
    }

	private void printAllShortPaths(ArrayList<ArrayList<Point>> current) {

		// In case there was no path between the nodes
		if(current.isEmpty()) {
			System.out.println("No shortest path found between the squares.");


		} else if (!current.isEmpty()) {

			// If list is not empty, print size and iterate printing all possible shortest paths
			System.out.println("Found " + current.size() + " shortest paths.\nThe highlighted path shown is one of the examples.");
			int i = 1;																	// Initialize path counter
			for (ArrayList<Point> array : current) {
				System.out.println("\nShortest Path " + i + " of length: " + array.size() + "\n");
				for(Point point : array) {
					System.out.println("(x = " + point.x + "; y = " + point.y + ")");

					// Only highlight the first shortest path as example
					if(i == 1) {
						board.getSquareAt(point.x, point.y).setHighlight(true);
					}
				}
				i++;
			}
		}
	}


	private ArrayList<ArrayList<Point>> findShortestPath(ArrayList<ArrayList<Point>> bigList) {
		// Set a big value as minimum to iterate the list and find the smallest size
		int min = 1000;
		for (ArrayList<Point> array : bigList) {
			if(array.size() <= min) {
				min = array.size();
			}
		}
		// Create a temporal list of lists
		ArrayList<ArrayList<Point>> shortestPaths = new ArrayList<>();
		// Iterate though big list again to add the paths that are of size min
		for (ArrayList<Point> array : bigList) {
			if(array.size() == min) {
				shortestPaths.add(array);
			}
		}
		// return list of lists with shortest paths
		return shortestPaths;
	}

	// Depth First Search Algorithm that will find the shortest path
	private ArrayList<ArrayList<Point>> DFS(int sour_x, int sour_y, ArrayList<Point> copy_list, ArrayList<ArrayList<Point>> current_path_list) {

		// Create a Point (x, y) for the current square we are on
		Point current_point = new Point(sour_x, sour_y);

		// Add element to our stack
		copy_list.add(current_point);

		// Check if the current square is the target
		if(target_square.getXLocation() == current_point.x && target_square.getYLocation() == current_point.y) {
			current_path_list.add(new ArrayList<>(copy_list));											// Check to find shortest path (method bellow)
			copy_list.remove(current_point);															// Remove the found target and return
			return current_path_list;
		}
		
		// Check what direction we can traverse in the board

		// SQUARE RIGHT (IF NO WALL AT THE RIGHT, NOT VISITED YET AND NO BOARD LIMIT YET)
		if(current_point.x <= 8 && !board.getSquareAt(current_point.x, current_point.y).getWall(1)) {

			Point point_xplus = new Point(current_point.x + 1, current_point.y);						// Create point at the right of current square

			if(!copy_list.contains(point_xplus)) {														// If this point is not on the list
				current_path_list = DFS(point_xplus.x, point_xplus.y, copy_list, current_path_list);	// jump recursively to DFS method again
			}
		}
	
		// SQUARE LEFT (IF NO WALL AT THE RIGHT, NOT VISITED YET AND NO BOARD LIMIT YET)
		if(current_point.x >= 1 && !board.getSquareAt(current_point.x, current_point.y).getWall(0)) {

			Point point_xminus = new Point(current_point.x - 1, current_point.y);						// Create point at the left of current square

			if(!copy_list.contains(point_xminus)) {														// If this point is not on the list
				current_path_list = DFS(point_xminus.x, point_xminus.y, copy_list, current_path_list);	// jump recursively to DFS method again
			}

		} 
		// SQUARE UP (IF NO WALL AT THE RIGHT, NOT VISITED YET AND NO BOARD LIMIT YET)
		if(current_point.y <= 8 && !board.getSquareAt(current_point.x, current_point.y).getWall(3)) {

			Point point_yplus = new Point(current_point.x, current_point.y + 1);						// Create point up from current square

			if(!copy_list.contains(point_yplus)) {														// If this point is not on the list
				current_path_list = DFS(point_yplus.x, point_yplus.y, copy_list, current_path_list);	// jump recursively to DFS method again
			}
		}
		// SQUARE DOWN (IF NO WALL AT THE RIGHT, NOT VISITED YET AND NO BOARD LIMIT YET)
		if(current_point.y >= 1 && !board.getSquareAt(current_point.x, current_point.y).getWall(2)) {

			Point point_yminus = new Point(current_point.x, current_point.y - 1);						// Create point down from current square

			if(!copy_list.contains(point_yminus)) {														// If this point is not on the list
				current_path_list = DFS(point_yminus.x, point_yminus.y, copy_list, current_path_list);	// jump recursively to DFS method again
			}
		}
		copy_list.remove(current_point);																// Square fully traversed, remove from Stack
		return current_path_list;
	}
	/**
	 * A method that is invoked when a reset() method is called on GameBoard.
	 * 
	 * @param n An unspecified value that matches that provided in the call to GameBoard reset()
	 */
	public void reset(int n)
	{
		for(int j = 0; j < 10; j++) {
			for(int y = 0; y < 10; y++) {
				// Reset highlight and reset target square if you find the target square by casting MazeSquare
				MazeSquare square = (MazeSquare) board.getSquareAt(j, y);
				square.target = false;
				board.getSquareAt(j, y).setHighlight(false);
				
			}
		}
	}
}
