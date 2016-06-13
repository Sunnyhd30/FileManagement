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
			if (args[0] != null) 
			{
				
				if (args[0] == "-Help" || args[0] == "-h")
				{
					//Print out the help features
					System.out.println ("Moron!");
				}
				else if (args[1] != null)
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
			System.out.println("Proper Usage is: {options} file path name.  Type java DupeCheckerCMD -Help for additional information");
			//e.printStackTrace();
			System.exit(1);
		}
	}
}
