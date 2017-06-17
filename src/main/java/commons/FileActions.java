package commons;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class is used to perform various files related actions.
 * @author Yoav
 */
public class FileActions {

	/**
	 * Read all text from a file
	 * 
	 * @param filename
	 * @return If file exist, it will then read all content and return it line by line as an arrayList If file does not exit, returns null.
	 */
	public ArrayList<String> readAllTextFromFile(String filename) {
		ArrayList<String> fileContent = new ArrayList<>();

		File file = new File(filename);

		// Check if file exists:
		if (!file.exists()) {
			System.out.println("Error: File \"" + filename
					+ "\" does not exist");
			return null;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = br.readLine()) != null) {
				fileContent.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContent;
	}


	/**
	 * create a new file given a correct directory and file name
	 * @param directory Directory path
	 * @param filename file name (name.ext)
	 * @return true is successful, false if not.
	 */
	public boolean createNewFile(String directory, String filename) {
		File f = new File(directory + "/" + filename);		
		createFolder(directory);
		if (!f.isDirectory() && !f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
		return (f.exists());
	}


	/**
	 * Add a line to a file 
	 * @param fileName the file name to which you would like to add a String/Record
	 * @param record The new line/String/record to add at the end of the text file
	 */
	public void appendRecord(String fileName, String record) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write(record);
			bw.newLine();
			bw.flush();
		} catch (IOException ioE) {
			ioE.printStackTrace();
		} finally { // close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// do nothing...the file should be closed by now.
				}
		} // end try/catch/finally
	}

	/**
	 * This method renames a file if it's already exist.
	 * @param directory Directory path
	 * @param filename file name (name.ext)
	 */
	public void renameIfExist(String directory, String filename) {
		String millis;
		File f = new File(directory + "/" + filename);
		if (f.exists()) {
			millis = String.valueOf((new Date()).getTime());
			try {
				renameIfExist(directory , filename + millis + "_old");
				File buf = new File(directory + "/" + filename + millis + "_old");
				f.renameTo(buf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 	
	}

	/**
	 * Create folder given a valid directory name as a string
	 * @param directory - The folder/directory to create
	 * @return true if creation of directory was successful
	 */
	public boolean createFolder(String directory){		
		try {
			if ((new File(directory)).mkdirs()) {
			    return true;
			}
		} catch (Exception e) {

		}
		return false;
	}
	
	
}
