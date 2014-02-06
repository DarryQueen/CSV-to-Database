import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
	public static void main(String[] args) {
		CSVParser obj = new CSVParser();
		obj.execute();
	}
	
	public void execute() {
		//Lists for values:
		List<String[]> dArrayList = new ArrayList<String[]>();
		List<String[]> eArrayList = new ArrayList<String[]>();
		List<String[]> sArrayList = new ArrayList<String[]>();
		
		//Variables for parsing:
		String csvFile = "Employees.csv";

		try {
			loadCSV(dArrayList, eArrayList, sArrayList, csvFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Time to insert into SQL file!
		File file = new File("insert_commands.sql");
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			//Write insert lines for dArrayList:
			String dValues = getInsertDepartments(dArrayList);
			bw.write("INSERT INTO department (dep_id,dep_name) VALUES\n\t" + dValues + ";\n\n");
			
			//Write insert lines for eArrayList:
			String eValues = getInsertEmployees(eArrayList);
			bw.write("INSERT INTO employee (type,emp_id,first_name,last_name,gender,hire_date,termination_date,department1,department2,bonus,manager_start_date) VALUES\n\t" + eValues + ";\n\n");
			
			//Write insert lines for sArrayList:
			String sValues = getInsertSalaries(sArrayList);
			bw.write("INSERT INTO salary (emp_id,start_date,end_date,salary_amt) VALUES \n\t" + sValues + ";");
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	//Loads given Lists with data provided from csvFile.
	private static void loadCSV(List<String[]> dArrayList, List<String[]> eArrayList, List<String[]> sArrayList, String csvFile) throws IOException {
		BufferedReader br = null;
		String line;
		
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {

			String[] values = line.split(",");
			String header = values[0];
	 
			//Check if department type:
			if (header.length() == 1) {
				dArrayList.add(values);
			//Check if employee type:
			} else if (header.equals("MANAGER") || header.equals("EMPLOYEE")) {
				eArrayList.add(values);
			//Check if salary type:
			} else {
				sArrayList.add(values);
			}
		}
		br.close();
	}
	
	//Converts List of departments to String values.
	private static String getInsertDepartments(List<String[]> dArrayList) {
		String dValues = "";
		for (String[] dArray : dArrayList) {
			dValues = dValues + "(" + dArray[0] + ",\"" + dArray[1] + "\"),\n\t";
		}
		return dValues.substring(0, dValues.length() - 3);
	}
	
	//Converts List of employees to String values.
	private static String getInsertEmployees(List<String[]> eArrayList) throws ParseException {
		String eValues = "";
		for (String[] eArray : eArrayList) {
			eValues = eValues + "(";
			for (int i = 0; i < eArray.length; i++) {
				//For department:
				if (i == 7) {
					String[] depArray = eArray[i].split(";");
					if (depArray.length > 1) {
						eValues = eValues + depArray[0] + "," + depArray[1] + ",";
					} else {
						eValues = eValues + depArray[0] + ",NULL,";
					}
				//For dates:
				} else if (!eArray[i].equals("null") && (i == 5 || i == 6 || (eArray[0].equals("MANAGER") && i == 9))) {
					SimpleDateFormat inFormat = new SimpleDateFormat("M/d/yyyy");
					SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
					
					String date = outFormat.format(inFormat.parse(eArray[i]));
					eValues = eValues + "\"" + date + "\",";
				//All other cases:
				} else {
					if (eArray[i].equals("null")) {
						eValues = eValues + "NULL,";
					} else if (i == 8) {
						eValues = eValues + eArray[i] + ",";
					} else {
						eValues = eValues + "\"" + eArray[i] + "\",";
					}
				}
			}
			//Employees have two fewer fields than managers:
			if (eArray[0].equals("EMPLOYEE")) {
				eValues = eValues + "NULL,NULL),\n\t";
			} else {
				eValues = eValues.substring(0, eValues.length() - 1) + "),\n\t";
			}
		}
		return eValues.substring(0, eValues.length() - 3);
	}
	
	//Converts List of salaries to String values.
	private static String getInsertSalaries(List<String[]> sArrayList) throws ParseException {
		String sValues = "";
		for (String[] sArray : sArrayList) {
			sValues = sValues + "(";
			for (int i = 0; i < sArray.length; i++) {
				if (i == 0) {
					sValues = sValues + "\"" + sArray[i] + "\",";
				} else if (i == 3) {
					sValues = sValues + sArray[i] + "),\n\t";
				} else {
					if (sArray[i].equals("null")) {
						sValues = sValues + "NULL,";
					} else {
						SimpleDateFormat inFormat = new SimpleDateFormat("M/d/yyyy");
						SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
						
						String date = outFormat.format(inFormat.parse(sArray[i]));
						sValues = sValues + "\"" + date + "\",";
					}
				}
			}
		}
		return sValues.substring(0, sValues.length() - 3);
	}
}
