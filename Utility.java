import java.io.*;
import java.util.zip.CRC32;
import java.security.MessageDigest;


class Utility {

	private String pathName = null;
	private File[] fileListing;
	private int countItems;
	private String[] fileNameExtensions;
	private String[][] duplicateFiles;

	public Utility(String pathNameProvided, int countObjects, String[] fileNameExtensions, String[][] duplicateFiles)
	{
		pathName = pathNameProvided;
		countItems = countObjects;
		fileNameExtensions = fileNameExtensions;
		duplicateFiles = duplicateFiles;
	}

	public boolean isFilePathValid() 
	{
		boolean isFilePathValid = false;
		File isThisHere = new File(pathName);

		if (!isThisHere.isDirectory()) 
		{
			isFilePathValid = false;
			System.out.println("Checking for directory");
		}
		else 
		{
			isFilePathValid = true;
		 	fileListing = isThisHere.listFiles();
		}

		return	isFilePathValid;
	}

	public static byte[] createChecksum(String filename) throws Exception 
	{
		InputStream fis =  new FileInputStream(filename);
		byte[] buffer = new byte[1024];
     	MessageDigest complete = MessageDigest.getInstance("SHA1");
     	int numRead;

     	do 
     	{	
      		numRead = fis.read(buffer);

      		if (numRead > 0) 
      		{
        		complete.update(buffer, 0, numRead);
      		}
     	} 
     	while (numRead != -1);

     	fis.close();

     	return complete.digest();
   	}

   	public static String getSHA1Checksum(String filename) throws Exception 
   	{
   		byte[] b = createChecksum(filename);
   		String result = "";

   		for (int i=0; i < b.length; i++) 
   		{
   			result +=
          	Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring(1);	
   		}
     
     	return result;
   	}

	public String[][] getSHA1AllFiles (String path, String[][] fileDuplicates) 
	{
		File root = new File (path);
		File[] list = root.listFiles();
		String[][] fileDuplicatesStore = fileDuplicates;
		String[][] fileDuplicatesStoreTemp = new String[1][4];

		if (list == null)
			return fileDuplicates;

		for (File f : list) 
		{
			if (f.isDirectory()) 
			{
				fileDuplicatesStore = getSHA1AllFiles(f.getAbsolutePath(), fileDuplicatesStore);
			}
			else 
			{
				String fileName = f.getName();
				String getFilePath = f.getAbsolutePath();
				String fileSHA1Value = "";
				boolean duplicateFileFound = false;

				try
				{
					fileSHA1Value = getSHA1Checksum(getFilePath);
					//System.out.println("SHA1Value: " + fileSHA1Value + " and filename: " + fileName);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}

				if (fileDuplicatesStore.length <= 1 && fileDuplicatesStore[0][0] == null)
				{
					fileDuplicatesStore[0][0] = fileSHA1Value;
					fileDuplicatesStore[0][1] = getFilePath;
				}
				else
				{
					fileDuplicatesStoreTemp = new String[fileDuplicatesStore.length][4];
					for (int x = 0; x < fileDuplicatesStore.length; x++)
					{
						fileDuplicatesStoreTemp[x][0] = fileDuplicatesStore[x][0];
						fileDuplicatesStoreTemp[x][1] = fileDuplicatesStore[x][1];
					}
					fileDuplicatesStore = new String[fileDuplicatesStore.length+1][4];
				
					for (int x = 0; x < fileDuplicatesStoreTemp.length; x++)
					{
						fileDuplicatesStore[x][0] = fileDuplicatesStoreTemp[x][0];
						fileDuplicatesStore[x][1] = fileDuplicatesStoreTemp[x][1];
					}
					fileDuplicatesStore[fileDuplicatesStore.length-1][0] = fileSHA1Value;
					fileDuplicatesStore[fileDuplicatesStore.length-1][1] = getFilePath;	
				}
			}
		}
		return fileDuplicatesStore;
	}

	public String [][] findDuplicateFiles (String[][] duplicateFilesArray)
	{
		String[][] duplicateFiles = duplicateFilesArray;

		//TO DO:
		//1. Figure out if the SHA1 value matches, the first match gets X in [x][3], the second match gets a D in [X][4]
		//2. If the checksum match, that its a 100% match, if the filename matches than its a 150% match
		//3. If both the checksum match and the filename matches than its a 100% match

		for (int x = 0; x < duplicateFilesArray.length; x++)
		{
			if (duplicateFilesArray[x][2] == null)
			{
				duplicateFilesArray[x][2] = "";
				duplicateFilesArray[x][3] = "";
			}
			if (!(duplicateFilesArray[x][2].equals("X")))
			{
				//System.out.println(duplicateFilesArray[x][1]);
				for (int y = 0; y < duplicateFilesArray.length; y++)
				{
					String filepath = duplicateFilesArray[x][1];
					//We don't want to compare the unique file that we have so far against 
					//itself and than mark it as a duplicate.  If the arrays are in the same 
					//position, we ignore comparing the two files
					if (x != y)
					{
						//Will this loop provide the solution for a case when a file has a 100% match found and gets marked
						//as X and D respectively.  Later in the cycle the X find another comparable which is not a SHA1 value 
						//match but the file names match

						//This will provide a 100% match to the file, by comparing the SHA1 values
						if (duplicateFilesArray[x][0].equals(duplicateFilesArray[y][0]))
						{
							duplicateFilesArray[x][2] = "X";
							duplicateFilesArray[x][3] = "100%";
							duplicateFilesArray[y][2] = "D";
							duplicateFilesArray[y][3] = "100%";
							//System.out.println(duplicateFilesArray[x][1].substring(duplicateFilesArray[x][1].lastIndexOf("/")+1));
						}
						//This will provide a file name match, which is considered a 50% match since it could be purely conincidental
						else if ((duplicateFilesArray[x][1].substring(duplicateFilesArray[x][1].lastIndexOf("/")+1)).contains(duplicateFilesArray[y][1].substring(duplicateFilesArray[y][1].lastIndexOf("/")+1)) && !duplicateFilesArray[y][1].equals("D")) 
						{
							duplicateFilesArray[y][2] = "D";
							duplicateFilesArray[y][3] = "50%";
						}
					}
				}
			}
			
		}

		return duplicateFiles;
	}

	public String formatOutput (String[][] duplicateFilesArray)
	{
		String formatttedPrint = "";
		String[][] arrayToBeFormatted = duplicateFilesArray;
		long diskSpaceSaved = 0;
		long diskSpacedSavedOverall = 0;

		for (int x = 0; x < arrayToBeFormatted.length; x++)
		{
			if (arrayToBeFormatted[x][2].equals("X"))
			{
				formatttedPrint = formatttedPrint + "+ " + arrayToBeFormatted[x][1].substring(arrayToBeFormatted[x][1].lastIndexOf("/")+1) + "\n";
				for (int y = 0; y < arrayToBeFormatted.length; y++)
				{
					if (arrayToBeFormatted[x][0].equals(arrayToBeFormatted[y][0]) && arrayToBeFormatted[y][2].equals("D"))// && arrayToBeFormatted[x][2].equals("D"))
					{
						diskSpaceSaved = diskSpaceSaved + getFileSize(arrayToBeFormatted[y][1]);
						formatttedPrint = formatttedPrint + "+--- " + arrayToBeFormatted[y][1] + "\n";
					}
				}
				formatttedPrint = formatttedPrint + " File space to be saved: " + diskSpaceSaved/1000000 + "MM\n\n";
				diskSpacedSavedOverall = diskSpacedSavedOverall + diskSpaceSaved;
				diskSpaceSaved = 0;	
			}
		}
		formatttedPrint = formatttedPrint + "\n" + "Total approximate disk space that can be saved: " + diskSpacedSavedOverall/1000000 + "MM";
		return formatttedPrint;
	}

	public long getFileSize (String path)
	{
		long filesize = 0;
		File root = new File (path);
		filesize = root.length();

		return filesize;
	}

	/*public String flattenFolders (String path, String parentPath)
	{
		File root = new File (path);
		File[] list = root.listFiles();
		String parentPath = parentPath;

		for (File f : list)
		{
			if (f.isDirectory())
			{
				path = flattenFolders(path, parentPath)
			}
			else
			{
				//move file from that folder and place it in the parent path
			}
		}

		return parentPath;
	}*/

 	public int getFileCount( String path, int countObjects) 
   	{ 
		File root = new File( path ); 
		File[] list = root.listFiles(); 
		int countItems = countObjects;

		if (list == null) 
			return countItems; 
		
		for ( File f : list ) 
		{ 
			if ( f.isDirectory() ) 
			{ 
				countItems = getFileCount(f.getAbsolutePath(), countItems); 
			} 
			else 
			{ 
				countItems++;
			} 
		} 
		return countItems;
	} 

	public String[] getExtensionTypes(String path, String[] fileExt) 
	{ 
		File root = new File( path ); 
		File[] list = root.listFiles(); 
		String[] fileTypeExt = fileExt;

		if (list == null) 
			return fileTypeExt; 
		
		for ( File f : list ) 
		{ 
			if ( f.isDirectory() ) 
			{ 
				fileTypeExt = getExtensionTypes( f.getAbsolutePath(), fileTypeExt ); 
			} 
			else 
			{ 
				String fileName = f.getName();
				String fileExtStore = "";
				boolean newFileExtension = false;

				int t = fileName.lastIndexOf('.');
				
				fileExtStore = fileName.substring(t+1);

				if (t > 0) {	
					for (int x = 0; x < fileTypeExt.length; x++) 
					{
						if (fileTypeExt[x] != null) 
						{
							if (fileTypeExt[x].equals(fileExtStore)) 
							{
	    						newFileExtension = false;
	    					}
	    					else 
	    					{
	    						newFileExtension = true;
	   						}
	    				}
	    			}	
				}

				if (fileTypeExt.length <= 1)
					newFileExtension = true;

    			if (newFileExtension) 
    			{
    				String[] fileTypeExtTemp;
    				if (fileTypeExt.length <= 1) 
    				{
    					fileTypeExtTemp = new String[fileTypeExt.length + 2];
    					for (int y = 0; y < fileTypeExt.length; y++) 
    						fileTypeExtTemp[y] = fileTypeExt[y];
    					fileTypeExtTemp[fileTypeExt.length] = fileExtStore;
    				}
    				else 
    				{
    					fileTypeExtTemp = new String[fileTypeExt.length + 1];
    					for (int y = 0; y < fileTypeExt.length; y++) 
    						fileTypeExtTemp[y] = fileTypeExt[y];
    					fileTypeExtTemp[fileTypeExt.length] = fileExtStore;
    				}
    	
    				fileTypeExt = new String[fileTypeExtTemp.length];
    				for (int y = 0; y < fileTypeExt.length; y++) {
    					fileTypeExt[y] = fileTypeExtTemp[y];
    				}
    			}
			} 
		} 
		return fileTypeExt;
	} 
}