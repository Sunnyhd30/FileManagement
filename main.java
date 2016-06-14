/*
TO DO
1. Figure out the fize sizes to be saved - DONE
2. Figure out a simpler way to compare file names
3. Flatten file directories or move names by artist
4. Delete duplicate files
*/
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

class DupeCheckerCMD {
	public static void main (String[] args) {
		/*if (args.length >= 1) {
				//Do something
				System.out.println("We got a parameter and the parameter is: " + args[0]);
		}
		else
		{
			System.out.println("Proper Usage is: {options} file path name.  Type java DupeCheckerCMD -Help for additional information");
			System.exit(0);
		}*/

		try 
		{
			if ((args.length >= 1))
			{
				if (args[0].equals("-Help") || args[0].equals("-h") || args[0].equals("-H"))
				{
					//Print out the help features
					System.out.println ("TO DO: Put a help menu in place");
				}
				else if (args.length > 1)
				{
					String[] fileNameExt = new String[1];
					String[][] duplicateFiles = new String[1][4];
					Utility checkout = new Utility(args[1], 0, fileNameExt, duplicateFiles);
					int countDuplicates = 0;

					if (checkout.isFilePathValid())
					{
						if (args[0].equals("-c") || args[0].equals("-count"))
						{
							System.out.println("Total files in the folder: " + checkout.getFileCount(args[1], 0));
						}
						else if (args[0].equals("-d") || args[0].equals("-duplicates"))
						{
							duplicateFiles = checkout.getSHA1AllFiles(args[1], duplicateFiles);
							duplicateFiles = checkout.findDuplicateFiles(duplicateFiles);
							System.out.println(checkout.formatOutput(duplicateFiles));

							int count100 = 0;
							int count50 = 0;	
							for (int x = 0; x < duplicateFiles.length; x++)
							{
								if (duplicateFiles[x][3].equals("100%") && duplicateFiles[x][2].equals("D"))	
								{
									count100++;
								}
								else if (duplicateFiles[x][3].equals("50%") && duplicateFiles[x][2].equals("D"))	
								{
									count50++;
								}
							}
							System.out.println("100% match: " + count100);
							System.out.println("50% match: " + count50);

							System.out.println("\nDo you want to process the duplicates? Y or N");
							Scanner in = new Scanner(System.in);
							String userInput = in.nextLine();
							if (userInput.equals("Y") || (userInput.equals("y")))	
							{
								for (int x = 0; x < duplicateFiles.length; x++)
								{
									if (duplicateFiles[x][3].equals("100%") && duplicateFiles[x][2].equals("D"))	
									{
										//Delete code
										System.out.println("File to keep: " + duplicateFiles[x][1]);
										System.out.println("File to be deleted: " + duplicateFiles[x][1]);
										System.out.println("Delete? Y or N");
										in = new Scanner(System.in);
										userInput = in.nextLine();
										if (userInput.equals("Y") || userInput.equals("y"))
										{
											File fileToBeDeleted = new File(duplicateFiles[x][1]);
											fileToBeDeleted.delete();
										}
										userInput = "";
									}
									else if (duplicateFiles[x][3].equals("50%") && duplicateFiles[x][2].equals("D"))	
									{
										//Delete code
									}
								}
							}
							else
							{
								System.out.println("Good Bye!");
								System.exit(1);
							}
						}
					}
					else
					{
						System.out.println("The file path you provided is invalid");
					}
				}
				else
				{
					//Since all other arguments require a minimum of two arguments, we will
					//reject the input and produce the error
					System.out.println("Proper Usage is: {options} file path name.  Type java DupeCheckerCMD -Help for additional information");
					System.exit(1);
				}
			}
		} 
		catch (IndexOutOfBoundsException e) 
		{
			e.printStackTrace(System.out);
			System.out.println("IndexOutofBounds: Proper Usage is: {options} file path name.  Type java DupeCheckerCMD -Help for additional information");
			System.exit(1);
		}
	}
}
