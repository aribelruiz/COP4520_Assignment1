// Aribel Ruiz
// 01/26/2023

// =========================================
// COP4520 : Assignment 1 - Multi-Threading
// =========================================
//      Concepts of Parallel and Distributed Processing Assignment 1. 
//      This is an introductory assignment to multi-threaded programming.

import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class Assignment1 
{
    // ====================================== Class Variables ======================================

    // Variables assist in checking the number of threads running and available
    public static boolean threadParking[] = new boolean[8];
    private static int openParkingIndex = -1;

    // Variables for thread management
    public static int threadsRunning = 0;
    public static boolean executionDone = false; 

    // Variable defines upper bound when searching for all prime numbers
    public static int upperBound = (int)Math.pow(10,8);
    public static boolean printTest = false;
    public static boolean testing = false;


    // Variables for prime numbers
    private static long[] top10Primes = new long[10];
    private static int top10Counter = 0;
    private static long numOfPrimes = 0;
    private static long primeSum = 0; 
  
    // Boolean list storing whether each number is composite (number N represented by index N)
    public static boolean[] isCompositeList = new boolean[upperBound + 1]; 
    public static boolean[] isPrimeList = new boolean[upperBound + 1]; 

    // ========================================= Functions =========================================
    public static void main(String[] args)
    {
        int newUpperBound = upperBound;
        boolean checkPrintTest = printTest;
        try {
            newUpperBound = Integer.parseInt(args[0]);
        } catch (Exception e) {
            // System.out.println("DEFAULT: Upper bound = (10^8).");
        }

        try {
            checkPrintTest = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            // System.out.println("DEFAULT: Not displying info on console.");
        }

       FindPrimes(newUpperBound, checkPrintTest, testing);
    }

    // Function finds all prime numbers from 1 to N, prints output to primes.txt, and returns execution time
    public static double FindPrimes(int newUpperBound, boolean checkPrintTest, boolean checkTesting)
    {
        upperBound = newUpperBound; 
        printTest = checkPrintTest;
        testing = checkTesting;

        // Starts timer
        long start = System.nanoTime();
        
        // Checks for program finish (executionDone set to true in thread when upperBound reached)
        while (!executionDone)
        {
            // Resets openParkingIndex variable
            openParkingIndex = -1;

            // Checks if there is an opening for a new thread (-1 when no threads available)
            if ((openParkingIndex = FindOpenIndex()) != -1)
            {
                // Creates new thread at openParkingIndex
                MultiThreadThing myThing = new MultiThreadThing(openParkingIndex);
                Thread myThread = new Thread(myThing);
                myThread.start();
            }
        }  

        // Finds prime numbers (number is prime if not found in composite list (excluding 0 and 1))
        for (long i = upperBound; i >= 2; i--)
        {
            if (isCompositeList[(int)i] == false)
            {
                // Assures top 10 primes are added to top10Primes[]
                if (top10Counter < 10)
                    top10Primes[top10Counter++] = i;

                // Increments number of primes and adds primes to primeSum
                numOfPrimes++;
                primeSum += i;
            }
        }

        // Creates primes.txt file to print output
        try {
            File outputFile = new File("primes.txt");
        
            if (outputFile.createNewFile())
            {
                if (printTest == false &&  testing == false)
                    System.out.println("\nFile created: " + outputFile.getName()); 
            }
            else
            {
                if (printTest == false && testing == false)
                    System.out.println("\nFile '" + outputFile.getName() + "' already exists.");
            }

        } catch (IOException e) {
            System.out.println("\nAn error occured.");
            e.printStackTrace();
        }

        // Ends timer
        long end = System.nanoTime();

        // Prints execution time
        NumberFormat formatter = new DecimalFormat("#0.00000");

        // Writes prime number information to prime.txt file
        try {
            FileWriter myWriter = new FileWriter("primes.txt");
            
            // Printing <execution time (in seconds)> <total number of primes found> <sum of all primes found> to output 
            myWriter.write(formatter.format((end - start) / 1000000000d) + "s ");
            
            myWriter.write(numOfPrimes + " ");
            myWriter.write(primeSum + " ");

            // Printing <top 10 maximum primes, listed in order from lowest to highest> to output 
            myWriter.write("\n" + printTop10Primes(top10Primes));
            
            // Closing file writer
            myWriter.close();
            if (printTest == false && testing == false)
                System.out.println("Successfully wrote output to 'primes.txt'.\n");
                
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        if (printTest == true)
        {
            System.out.println("====== Prime Info for All Primes (between 1 and " + upperBound + ") ======");
            System.out.println("Execution time: " + formatter.format((end - start) / 1000000000d) + "s");
            System.out.println("Total Number of Primes: " + numOfPrimes);
            System.out.println("Sum of All Primes: " + primeSum + "\n");
        }
        ResetAllClassVariables();
        return ((end - start) / 1000000000d);
    }


    // Function searches for open index to start new thread
    public static int FindOpenIndex()
    {
        for (int i = 0; i < threadParking.length; i++)
        {
            // If open parking found, closes parking and returns index of opening
            if (threadParking[i] == false)
            {
                // Found Parking at index i
                threadParking[i] = true;
                return i;
            }
        }

        // Returns -1 if there are no openings in thread parking
        return -1;
    }

    // Function returns the top 10 prime numbers as a string (from lowest to highest)
    public static String printTop10Primes(long[] top10)
    {
        String top10Str = "";
        top10Str += "[";
        for (int i = top10.length - 1; i >= 0; i--)
        {
            if (i == 0)
            {
                top10Str += (top10[i]);
                break;
            }

            top10Str += (top10[i] + ", ");
        }
        top10Str += "]\n";

        return top10Str;
    }

    public static void ResetAllClassVariables()
    {
        openParkingIndex = -1;
    
        // Variables for thread management
        threadsRunning = 0;
        executionDone = false; 
    
        // Variable defines upper bound when searching for all prime numbers
        upperBound = (int)Math.pow(10,8);
        
        // Variables for prime numbers
        top10Primes = new long[10];
        top10Counter = 0;
        numOfPrimes = 0;
        primeSum = 0; 
      
        // Boolean list storing whether each number is composite (number N represented by index N)
        isCompositeList = new boolean[upperBound + 1]; 
        isPrimeList = new boolean[upperBound + 1]; 
    }
}



