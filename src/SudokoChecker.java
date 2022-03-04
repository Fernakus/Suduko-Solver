/*  -------------------------------------------------------
 *  SudokoSolutionChecker.java
 *  -------------------------------------------------------
 *  Author:  Matthew Ferlaino
 *  Course:	 COSC2006A
 *  ID:      169657520
 *  Email:   mferlaino@algomau.ca
 *  Date:	 October 14th, 2019
 *  ------------------------------------------------------- */

// Imports
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SudokoChecker {
	/*
	 * -- ThreadNode Class --
	 * This is the ThreadNode class 
	 * as a private class built into the 
	 * SudokoSolutionChecker class.
	 */
	private class ThreadNode implements Runnable{
		// Variables
		public List<Character> puzzleData, numbersAndLetters;
		public boolean isValid;
		
		// Double-arg Constructor
		public ThreadNode(List<Character> puzzleData, List<Character> numbersAndLetters) {
			this.puzzleData = SudokoChecker.this.clone(puzzleData);
			this.numbersAndLetters = SudokoChecker.this.clone(numbersAndLetters);
			this.isValid = false;
		}
		
		// run()
		public void run() {
			// Comparing data from puzzle with our criteria
			for (Character ch : puzzleData) {
				if (numbersAndLetters.contains(ch)) {
					numbersAndLetters.remove(ch);
					puzzleData.remove(ch);
				}
			}
			
			// Check if isValid
			if (numbersAndLetters.isEmpty() && puzzleData.isEmpty()) isValid = true;
			else isValid = false;
		}
	}
	
	// Variables
	private char[][] puzzle;
	private List<Character> numbersAndLetters, puzzleData;
	private List<Thread> threads;
	private List<ThreadNode> threadNodes;

	// No-arg Constructor
	public SudokoChecker() {
		construct();
		createThreads();
	}
	
	/* --- Methods ---
	 * 1. construct()
	 * 2. createThreads()
	 * 3. clone()
	 * 4. check()
	 * 5. print()
	 */
	
	// construct()
	private void construct() {
		// Populate Puzzle
		char[][] puzzle = {
				{'A', 'E', 'F', '1', '9', '2', 'D', '8', 'B', 'G', '6', 'C', '5', '3', '7', '4'},
				{'3', '7', 'G', '2', '1', 'E', 'A', '6', 'F', '9', '5', '4', '8', 'D', 'C', 'B'},
				{'B', '9', '8', '5', '4', 'C', 'F', '7', 'D', '3', '1', 'E', '6', 'A', 'G', '2'},
				{'D', '4', '6', 'C', '3', 'B', '5', 'G', '8', 'A', '7', '2', 'F', 'E', '1', '9'},
				{'5', 'D', 'A', '8', 'B', 'G', 'E', '4', 'C', '6', '9', '1', '7', 'F', '2', '3'},
				{'2', '6', '9', '3', 'A', '7', 'C', '1', '5', 'F', '4', 'G', 'D', '8', 'B', 'E'},
				{'1', 'C', '7', 'E', '2', 'F', '9', '5', '3', 'D', 'B', '8', 'A', '6', '4', 'G'},
				{'4', 'G', 'B', 'F', '8', '6', '3', 'D', '7', 'E', '2', 'A', 'C', '9', '5', '1'},
				{'C', 'A', '1', '4', 'F', '5', '8', 'E', '2', 'B', 'G', '9', '3', '7', '6', 'D'},
				{'F', 'B', '5', 'G', 'D', '3', '7', '2', 'E', 'C', '8', '6', '4', '1', '9', 'A'},
				{'8', '3', 'E', '7', 'G', '1', '6', '9', 'A', '4', 'D', '5', 'B', '2', 'F', 'C'},
				{'9', '2', 'D', '6', 'C', '4', 'B', 'A', '1', '7', 'F', '3', 'E', 'G', '8', '5'},
				{'E', '1', '4', 'A', '5', 'D', '2', 'F', 'G', '8', 'C', '7', '9', 'B', '3', '6'},
				{'7', 'F', '2', 'D', '6', 'A', 'G', 'C', '9', '5', '3', 'B', '1', '4', 'E', '8'},
				{'6', '8', 'C', 'B', 'E', '9', '1', '3', '4', '2', 'A', 'F', 'G', '5', 'D', '7'},
				{'G', '5', '3', '9', '7', '8', '4', 'B', '6', '1', 'E', 'D', '2', 'C', 'A', 'F'}};
		
		// Create & populate
		List<Character> numbersAndLetters = new CopyOnWriteArrayList<Character>();
		char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
		
		for (int i = 1; i < 10; i++) 
			numbersAndLetters.add((char)(i + '0'));
		
		for (int i = 0; i < letters.length; i++) 
			numbersAndLetters.add(letters[i]);
				
		// Assign
		this.puzzle = puzzle;
		this.numbersAndLetters = numbersAndLetters;
	}
	
	// createThreads()
	private void createThreads() {
		// Instantiate Threads and ThreadNodes
		threads = new CopyOnWriteArrayList<Thread>();
		threadNodes = new CopyOnWriteArrayList<ThreadNode>();
		puzzleData = new CopyOnWriteArrayList<Character>();
		
		// Spin up row ThreadNodes
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) 
				puzzleData.add(puzzle[i][j]);
			
			threadNodes.add(new ThreadNode(puzzleData, numbersAndLetters));
			puzzleData.clear();
		}
		
		
		// Spin up column threadNodes
		for (int i = 0; i < puzzle.length; i++) {
			for (int j = 0; j < puzzle.length; j++) 
				puzzleData.add(puzzle[j][i]);
			
			threadNodes.add(new ThreadNode(puzzleData, numbersAndLetters));
			puzzleData.clear();
		}

		// Spin up 4x4 square threads
		for (int i = 0; i < 16; i += 4) {
			for (int j = i; j < i + 4; j++) {
				puzzleData.add(puzzle[0][j]);
				puzzleData.add(puzzle[1][j]);
				puzzleData.add(puzzle[2][j]);
				puzzleData.add(puzzle[3][j]);
			}	
			threadNodes.add(new ThreadNode(puzzleData, numbersAndLetters));
			puzzleData.clear();
			
			for (int j = i; j < i + 4; j++) {
				puzzleData.add(puzzle[4][j]);
				puzzleData.add(puzzle[5][j]);
				puzzleData.add(puzzle[6][j]);
				puzzleData.add(puzzle[7][j]);
			}	
			threadNodes.add(new ThreadNode(puzzleData, numbersAndLetters));
			puzzleData.clear();
			
			for (int j = i; j < i + 4; j++) {
				puzzleData.add(puzzle[8][j]);
				puzzleData.add(puzzle[9][j]);
				puzzleData.add(puzzle[10][j]);
				puzzleData.add(puzzle[11][j]);
			}	
			threadNodes.add(new ThreadNode(puzzleData, numbersAndLetters));
			puzzleData.clear();
			
			for (int j = i; j < i + 4; j++) {
				puzzleData.add(puzzle[12][j]);
				puzzleData.add(puzzle[13][j]);
				puzzleData.add(puzzle[14][j]);
				puzzleData.add(puzzle[15][j]);
			}	
			threadNodes.add(new ThreadNode(puzzleData, numbersAndLetters));
			puzzleData.clear();
		}
		
		// Add all ThreadNodes to Threads
		for (ThreadNode th : threadNodes)
			threads.add(new Thread(th));		
	} 
		
	// clone()
	private List<Character> clone(List<Character> list) {
		List<Character> newList = new CopyOnWriteArrayList<Character>();
		for (Character ch : list) 
			newList.add(ch);
		return newList;
	}
		
	// check()
	public void check() {
		try {
			// Start threads
			for (Thread th : threads) 
				th.start();
			
			// Wait for threads to finish
			for (Thread th : threads) 
				th.join();
			
			// Results
			for (ThreadNode th : threadNodes) {
				if (th.isValid) 
					continue;
				
				else {
					System.out.println("The final check() for valid rows, cols & 4x4 squares returned: false");
					return;
				}
			}
			System.out.println("The final check() for valid rows, cols & 4x4 squares returned: true");
		}
		
		catch (InterruptedException ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}
	
	// print()
	public void print() {
		// Print Top Border
		System.out.print("| ");
		for (int i = 0; i < 19; i++) System.out.print("- ");
		System.out.println("|");
			
		// Print Board Contents
		for (int i = 1; i < puzzle.length + 1; i++) {
			System.out.print("| ");
				
			for (int j = 1; j < puzzle.length + 1; j++) {
				if (j % 4 == 0) 
					System.out.print(puzzle[i - 1][j - 1] + " | ");
				else 
					System.out.print(puzzle[i - 1][j - 1] + " ");		
			}
			
			// Horizontal Divider
			if (i % 4 == 0) {
				System.out.println();
				System.out.print("| ");
				for (int j = 0; j < 19; j++) System.out.print("- ");
				System.out.print("|");
			}	
			System.out.println();
		}
		System.out.println();
	}
}
