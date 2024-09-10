package Project_0;

import java.util.Scanner;

import java.util.Random;

import java.io.*;

import java.util.InputMismatchException;

public class EpidemicSim {

  // Input validation for ints:
    public static int validateInt() {
      Scanner scan = new Scanner(System.in);
      
      while(!scan.hasNextInt()) {
        System.out.println("Error: Please enter an ***integer***.");
        scan.next();
      }

      return scan.nextInt();
    }

  // Input validation for doubles:
    public static double validateDouble() {
      Scanner scan = new Scanner(System.in);

      while(!scan.hasNextDouble()) {
        System.out.println("Error: Please enter a ***double***.");
        scan.next();
      }

      return scan.nextDouble();
    }

  /* This method will throw an error if the integer argument is not a square, 
   * or if it is out of the given range 1 to 625:
   */
    public static void tryPerfectSquare(int N) throws InputMismatchException {
      if (N < 1 || N > 625 || Math.sqrt(N) != ((double) ((int) Math.sqrt(N))))
        throw new InputMismatchException("Error: Invalid input. Enter a perfect square between 1 and 625");
    }

  /* This method will color all of the letters in a char array:
   * If the char is 'I', then it will be colored red.
   * Otherwise if the char is 'R', then it will be colored blue.
   * Lastly, if the char is 'S', which is true if neither of the above are true, it will stay colored black.
   */
    public static void coloredLetters(char[][] patients, int row, int column){
      if(patients[row][column] != 'I'){
        System.out.print("\u001B[31m" + patients[row][column] + "\u001B[37m" + " ");
      }else if(patients[row][column] == 'R'){
        System.out.print("\u001B[32m" + patients[row][column] + "\u001B[37m" + " ");
      }else{
        System.out.print(patients[row][column] + " ");
      }
    }

  // Calculate the chance that an individual spreads the infection:
    public static void tryInfection(int row, int column, char[][] patients, char[][] temp, double alpha) {
      // Try to infect the individual above the infected, if there is one.
        if(row > 0) {
          if(patients[row-1][column] == 'S' && Math.random() <= alpha)  {
            temp[row-1][column] = 'I';
          }
        }
      // Try to infect the individual below the infected, if there is one.
        if(row < patients.length - 1) {
          if(patients[row+1][column] == 'S' && Math.random() <= alpha)  {
            temp[row+1][column] = 'I';
          }
        }
      // Try to infect the individual to the left of the infected, if there is one.
        if(column > 0) {
          if(patients[row][column-1] == 'S' && Math.random() <= alpha)  {
            temp[row][column-1] = 'I';
          }
        }
      // Try to infect the individual to the right of the infected, if there is one.
      if(column < patients[row].length - 1) {
        if(patients[row][column+1] == 'S' && Math.random() <= alpha)  {
          temp[row][column+1] = 'I';
        }
      }        
  }

  // Calculate the chance of recovery
    public static char tryRecovery(char patient, double beta) {
      if (Math.random() <= beta)
       return 'R';
     else
        return 'I';

  }

  // Generate the position of patient zero:
    public static void infectPatientZero(int size, char[][] patients) {
      Random randGen = new Random();

      int[] coordinates = new int[2];
      coordinates[0] = randGen.nextInt(size);
      coordinates[1] = randGen.nextInt(size);
      patients[coordinates[0]][coordinates[1]] = 'I';
    }

  // Run one time step:
    public static char[][] timeStep(char[][] patients, int size, double alpha, double beta) {
      /* numInfected: count the amount of infected individuals.
       * numRecovered: count the amount of recovered individuals.
       * infectRatio: infected individuals vs total individuals.
       */
      int numInfected = 0;
      int numRecovered = 0;
      double infectRatio = 0.0;

      // Make a copy of the array
        char[][] temp = new char[size][size];

      // Populate the copy
      for(int i = 0; i < size; i++) {
        for(int j = 0; j < size; j++) {
          temp[i][j] = patients[i][j];
        }
      }
      
      // Infected and Recovered Count
      for(int i = 0; i < size; i++) {
        for(int j = 0; j < size; j++) {
          if(temp[i][j] == 'I')
            numInfected++;
          if(temp[i][j] == 'R')
            numRecovered++;
        }
      }
      infectRatio = (double)numInfected/(Math.pow(size, 2));

      // Print out the 2D array of patients with colored chars and list out the info of this simuled timestep.
      for(int i = 0; i < size; i++) {
          for(int j = 0; j < size; j++) {
            coloredLetters(temp, i, j);
          }
          System.out.println("");
      }
      System.out.println("\nNumber of infected individuals: " + numInfected);
      System.out.println("Number of recovered individuals: " + numRecovered);
      System.out.println("Ratio of infected individuals to total #: " + infectRatio);
      System.out.println("\n=================================================");

      // If the patient is infected, then first let them infect others and then possibly recover.
      for(int i = 0; i < size; i++) {
        for(int j = 0; j < size; j++) {
          if(patients[i][j] == 'I') {
            tryInfection(i, j, patients, temp, alpha);
            temp[i][j] = tryRecovery(patients[i][j], beta);
          }
        }
      }

      return temp;
    }

  /* 
   * This is the file writer method.
   * It takes the current patient grid and saves it into a file. 
   * The files are stored on the replit folder "infectionGrids".
   * The file name includes the grid number according to current timestep.
   */
    public static void fileWriter(char[][] patients, int fileCount, int size) throws FileNotFoundException {
      File DiseaseSpreadModel = new File("infectionGrid" + (fileCount+1) + ".txt");
      PrintWriter outputFile = new PrintWriter("C:\\Users\\amirs\\Downloads\\gridDatabase\\infectionGrid" + (fileCount) + ".txt");

      // This loop goes through the 2D array, and prints each patient while leaving a newline every row.
      for(int i = 0; i < size; i++) {
        for(int j = 0; j < size; j++) {
          outputFile.print(patients[i][j] + " ");
        }
        outputFile.println();
      }
      outputFile.close();
    }

  // Main method:
    public static void main(String[] args) {
      // Scanner:
        Scanner scnr = new Scanner(System.in);
      // Variable declaration:
        int numTimeSteps = 0;
        double alpha = -1;
        double beta = -1;
        int N = -1;
        int size;
        int currentTimeStep;

      /*
       * Prompt the user to enter the number of patients to be tested in the simulation. 
       * This number must be a perfect square.
       * We decided to cap it at 625 to reduce the computation time.
       */
        System.out.println("===============================================================");
        System.out.println("PLEASE ENTER THE NUMBER OF INDIVIDUALS: ");
        System.out.println("===============================================================");

      // Number of patients input validation:
        do {   
          try {
            N = validateInt();
            tryPerfectSquare(N);
          } catch (InputMismatchException ex) {
            N = -1;
            System.out.println(ex.getMessage());
          } 
        } while (N == -1 || N > 625);

      // Prompt the user for the variable "alpha", which represents infection rate.
        System.out.println("================================================================");
        System.out.println("PLEASE ENTER THE INFECTION RATE ALPHA FROM 0 TO 1: ");
        System.out.println("================================================================");

      // Alpha input validation. Alpha must be within range 0 to 1 inclusive.
        do {
          alpha = validateDouble();
          if (alpha < 0 || alpha > 1) {
            System.out.println("Error: Alpha must be between 0 and 1.");
            alpha = -1;
          }
        } while (alpha < 0 || alpha > 1);

      // Prompt user for the variable "beta", which represents recovery rate.
        System.out.println("===============================================================");
        System.out.println("PLEASE ENTER THE RECOVERY RATE BETA FROM 0 TO 1: ");
        System.out.println("===============================================================");

      // Beta input validation. Beta must be within range 0 to 1 inclusive.
        do {
          beta = validateDouble();
          if (beta < 0 || beta > 1) {
            System.out.println("Error: Beta must be between 0 and 1.");
            beta = -1;
          }
        } while (beta < 0 || beta > 1);

      /*
       * Prompt user for number of time steps.
       * Each time step is executed in the time step method (line XX).
       */
        System.out.println("===============================================================");
        System.out.println("PLEASE ENTER THE NUMBER OF TIME STEPS FROM 1 TO 20: ");
        System.out.println("===============================================================");

      // Time step input validation. Time steps must be within range 1 to 20.
        do {
          numTimeSteps = validateInt();
          if (numTimeSteps < 1 || numTimeSteps > 20) {
            System.out.println("===============================================================");
            System.out.println("Error: Number of time steps must be between 1 and 20.");
            System.out.println("===============================================================");
            numTimeSteps = -1;
          }
        } while (numTimeSteps < 1 || numTimeSteps > 20);

      /*
       * Now we begin to set up the grid of patients as a 2D array.
       * The array's length and width are both equal to the square root of the number of patients.
       */
        size = (int)Math.sqrt(N);
        char[][] patients = new char[size][size];

      /*
       * This for loop populates the empty grid with susceptible patients 'S'
       */
        for(int i = 0; i < size; i++) {
          for(int j = 0; j < size; j++) {
            patients [i][j] = 'S';
          }
        }

      // The infectedPatientZero method (line XX) will infect one random patient in the grid.
        infectPatientZero(size, patients);

      
      /* With the basic patient grid set up, we can iterate through time steps with a while loop.
       * A do while loop is used so that the program will execute at least once.
       * fileWriter method is called to save the current grid into a file,
       * then the grid is updated with the next time step.
       */
        currentTimeStep = 0;
        System.out.println("\n================SIMULATION START================");
        do {
        try { // File exception handling.
            System.out.println("\nTime step: " + (currentTimeStep));
            fileWriter(patients, currentTimeStep, size);
            System.out.println();
          } catch(FileNotFoundException ex) {
            System.out.println("Error: invalid file name.");
          }
          patients = timeStep(patients, size, alpha, beta);

          currentTimeStep++;
        } while(currentTimeStep <= numTimeSteps);
  }
}