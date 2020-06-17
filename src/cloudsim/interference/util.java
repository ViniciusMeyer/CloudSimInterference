package cloudsim.interference;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



public class util {



	public static Interference readCsv(String csvFile, String cvsSplitBy, Interference interf) {
		BufferedReader br = null;
		String line = "";
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] aux = line.split(cvsSplitBy);
				interf.addInterference(Integer.parseInt(aux[0]), Integer.parseInt(aux[1]),
						Integer.parseInt(aux[2]), Integer.parseInt(aux[3]), Integer.parseInt(aux[4]),
						Integer.parseInt(aux[5]), Integer.parseInt(aux[6]));
				// System.out.println("Country [code= " + country[4] + " , name=" + country[5] +
				// "]");

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return interf;

	}
	
	
	public static String printDouble(double number) {
		
		return String.format("%.2f", number);
	}
	

	
	
	 

}