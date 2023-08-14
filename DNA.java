//Programmer: Ben Rathbone
//CS 141
//Date: 6-3-23
//Assignment: Lab 6 - DNA
//Purpose: A program that accepts a .txt file of nucleotides.
//         It will collect and calculate data from that file, and write it to an output file.

import java.util.*;  //imports Scanner
import java.io.*;    //imports File and PrintStream

public class DNA
{
   public static void main(String[] args) throws FileNotFoundException
   {
      int proteinCodons = 5;     //minimum # of codons a valid protein must have
      int proteinPercent = 30;   //% of mass from C and G for a protein to be valid
      int nucNum = 4;            //# of unique nucleotides (A, C, G, and T)   
      int nucsPerCodon = 3;      //# of nucleotides per codon
      
      Scanner console = new Scanner(System.in); //creates scanner for user input
      
      System.out.println("This program reports information about DNA\n" +
                         "nucleotide sequences that may encode proteins.\n");
      
      File inputFile = inputFile(console);               //prompts user for input file name
      File outputFile = outputFile(inputFile, console);  //prompts user for output file name
      
      Scanner input = new Scanner(inputFile);            //creates Scanner for input file
      PrintStream output = new PrintStream(outputFile);  //creates PrintStream for outputFile
      
      //various data the program will collect and calculate
      String regionName;                        //the region name
      String nucleotides;                       //the nucleotide sequence
      int[] nucCounts = new int[nucNum];        //the number of each nucleotide
      double[] nucMasses = new double[nucNum];  //the % mass of each nucleotide type
      double totalMass;                         //the total mass of all nucleotides in the sequence
      String[] codonList;                       //a list of codon triplets (nucsPerCodon)
      String proteinStatus;                     //is the sequence a protein? YES or NO
      
      //this is where the magic happens
      while (input.hasNextLine())
      {
         regionName = input.nextLine();   //stores 1st line as "regionName"
         
         if (input.hasNextLine()) //makes sure there's another line before continuing
         {
            nucleotides = input.nextLine().toUpperCase(); //stores 2nd line as "nucleotides"
            
            countNucleotides(nucCounts, nucleotides); //calls countNucleotides method
            
            totalMass = calculateMass(nucMasses, nucCounts);   //calls calculateMass method
                               
            codonList = createCodonsList(nucleotides, nucsPerCodon);  //calls createCodonsList method
            
            proteinStatus = checkProtein(codonList, proteinCodons,   //calls checkProtein method
                            nucMasses, totalMass, proteinPercent);  
            
            printToFile(output, regionName, nucleotides, nucCounts, //calls printToFile method
                        nucMasses, totalMass, codonList, proteinStatus);
         }
      }
      System.out.println("\n" + outputFile + " has been successfully created!");
   }//end of main method
      
   //prompts the user for input file name
   //if the file exists, returns the file
   //accepts console
   public static File inputFile(Scanner console)
   {
      while (true)
      {
         System.out.print("Input file name: ");
         File inputFile = new File(console.nextLine());
         
         if (inputFile.exists()) //if the file exists
         {
            return inputFile;
         }
         else  //if the file does not exist
         {
            System.out.println(inputFile + " does not exist.  Please try again.");
         }
      }
   }//end of inputFile method
   
   //prompts the user for output file name
   //if output file name is different from input file name, returns the output file
   //accepts input file name and console
   public static File outputFile(File inputFile, Scanner console)
   {
      while (true)
      {
         System.out.print("Output file name: ");          //prompts user for output file name
         File outputFile = new File(console.nextLine());  //creates new file object with that name
         
         //checks if output name is identical to input name
         if (outputFile.getName().equals(inputFile.getName())) //if they are identical
         {
           System.out.println("Output file name must be different than input file name!"); 
         }
         else  //if they are different
         {
            return outputFile;
         }
      }
   }//end of outputFile method
   
   //counts the occurrences of each nucleotide (A, C, G, and T)
   //accepts the nucCounts array and the nucleotide String
   public static void countNucleotides(int[] nucCounts, String nucleotides)
   {
      Arrays.fill(nucCounts, 0); //resets nucCounts array
      
      for (int i = 0; i < nucleotides.length(); i++)
      {
         switch (nucleotides.charAt(i))
         {
            case 'A': nucCounts[0]++;
                      break;
            case 'C': nucCounts[1]++;  
                      break;
            case 'G': nucCounts[2]++;   
                      break;
            case 'T': nucCounts[3]++;
                      break;
            default:  break;
         }
      }
   }//end of countNucleotides method
   
   //calculates the mass percentage of each nucleotide type, as well as total mass
   //accepts the nucMasses and nucCounts arrays
   //returns totalMass
   public static double calculateMass(double[] nucMasses, int[] nucCounts)
   {  
      double[] masses = {135.128, 111.103, 151.128, 125.107};
      double[] tempMasses = new double[nucMasses.length];
      double totalMass = 0.0;
      
      for (int i = 0; i < nucMasses.length; i++)
      {
         tempMasses[i] = (double) nucCounts[i] * masses[i]; //finds the mass of each nucleotide
         totalMass += tempMasses[i];                        //updates totalMass
      }
      
      totalMass = Math.round(totalMass * 10.0) / 10.0;      //rounds totalMass
      
      for (int j = 0; j < nucMasses.length; j++)
      {
         //finds mass % of each nuc.
         nucMasses[j] = Math.round(((tempMasses[j] / totalMass) * 100) * 10.0) / 10.0;
      }
      
      return totalMass;    
   }//end of calculateMass method
   
   //breaks apart the nucleotide sequence into codons
   //the length of each codone is determined by the nucsPerCodon int (top of main)
   //accepts the nucleotides String and the nucsPerCodon int
   //returns codonList
   public static String[] createCodonsList(String nucleotides, int nucsPerCodon)
   {
      int listLength = nucleotides.length() / nucsPerCodon;   //determines the length of codonList
      String[] codonList = new String[listLength];
      
      for (int i = 0; i < listLength; i++)   //fills codonList with codons
      {
         codonList[i] = nucleotides.substring(i * nucsPerCodon, i * nucsPerCodon + nucsPerCodon);
      }
      
      return codonList;
   }//end of createCodonList method
   
   //checks if the nucleotide sequence is a protein-coding gene
   //accepts codonList, proteinCodons, nucMasses, totalMass, and proteinPercent
   //returns YES if the sequence is a protein
   //returns NO if it isn't
   public static String checkProtein(String[] codonList, int proteinCodons, double[] nucMasses,
                                     double totalMass, int proteinPercent)
   {  
      int startCodon = codonSearch(codonList, "ATG"); //finds position of the start codon ATG
      int stopCodon = -1;
      String[] stopCodonStrings = {"TAA", "TAG", "TGA"}; //array of valid stop codons
      //array to store stop codon positions
      int[] stopCodonPositions = new int[stopCodonStrings.length];
      
      for (int i = 0; i < stopCodonStrings.length; i++)  //finds positions of potential stop codons
      {
         //stores position in array
         stopCodonPositions[i] = codonSearch(codonList, stopCodonStrings[i]);
      }
      
      Arrays.sort(stopCodonPositions); //sorts stop codon positions from smallest to largest
      
      for (int j = 0; j < stopCodonStrings.length; j++)  //finds the smallest position, besides -1
      {
         if (stopCodonPositions[j] >= 0)
         {
            stopCodon = stopCodonPositions[j];  //saves smallest stop codon position as "stopCodon"
            break;
         }
      }
      
      if (startCodon == 0 && stopCodon > 0 &&            //there is a valid start and stop codon
          stopCodon - startCodon + 1 >= proteinCodons && //there are the valid # of codons (5)
          nucMasses[1] + nucMasses[2] >= proteinPercent) //C&G are at least proteinPercent
      {                                                  //             (30%) of totalMass
         return "YES";
      }
      else
      {
         return "NO";
      }
   }//end of checkProtein method
   
   //searches the codonList array for the desired codon
   //if it finds it, returns the index
   //if it doesn't find it, returns -1
   public static int codonSearch(String[] codonList, String codon)
   {
      for (int i = 0; i < codonList.length; i++)
      {
         if (codonList[i].equals(codon))
         {
            return i;   //if the codon is found, return index
         }
      }
      return -1;        //if the codon is not found, return -1
   }//end of codonSearch method
   
   //prints info for a nucleotide sequence to a file
   //accepts output and all the data from main
   public static void printToFile(PrintStream output, String regionName, String nucleotides,
                                  int[] nucCounts, double[] nucMasses, double totalMass,
                                  String[] codonList, String proteinStatus)
                                  throws FileNotFoundException
   {
      //prints info to outputFile
      output.println("Region Name: " + regionName);
      output.println("Nucleotides: " + nucleotides);
      output.println("Nuc. Counts: " + Arrays.toString(nucCounts));
      output.println("Total Mass%: " + Arrays.toString(nucMasses) + " of " + totalMass);
      output.println("Codons List: " + Arrays.toString(codonList));
      output.println("Is Protein?: " + proteinStatus); 
      output.println();
   }//end of printToFile method
}//end of program