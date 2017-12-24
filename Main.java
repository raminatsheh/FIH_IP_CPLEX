import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;









public class Main {


	public static ArrayList<Itemset> list_of_sensitive_itemsets = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_all_itemsets = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_items_in_database = new ArrayList<Itemset>();
	//public static ArrayList<Itemset> list_of_relevant_itemsets = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_relevant_transactions = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_relevant_transactions_after_sanitization = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_itemsets_in_relevant_transactions = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_itemsets_in_relevant_transactions_after_Sanitization = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_preprocessed_itemsts = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_all_transactions = new ArrayList<Itemset>();
	public static ArrayList<Integer> list_of_sensitive_items = new ArrayList<Integer>();
	public static ArrayList<Itemset> list_of_itemsets_that_were_affected_by_sanitization = new ArrayList<Itemset>();
	public static ArrayList<Itemset> list_of_itemsets_that_were_hidden = new ArrayList<Itemset>();
	public static double[] solution;
	public static int number_of_sensitive_itemsets = 5; 
	public static int number_of_transactions = 88166;
	public int number_of_relevant_transactions = 0;
	public static Main_Matrice my_matrice = new Main_Matrice(number_of_transactions, number_of_sensitive_itemsets);
	//public String database_path = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\IP_Data.txt";
	public String database_path = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\retail.dat";
	//public String sensitive_itemsets_path = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\IP_Sensitive.txt";
	public static String sensitive_itemsets_path = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\Rretail05_125.rul";
	public String relevant_transactions_database = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\Rretail_Relevant.txt";
	public String relevant_transactions_database_after_sanitization = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\Rretail_Relevant_Sanitized.txt";
	public static Solver my_solver = new Solver();
	public static double minimum_support_double= 88.0/88166;
	public String minimum_support_level = String.valueOf(minimum_support_double);
	public static int minimum_support_level_int = 88;
	public static double minimum_support_level_value_double = 88.0;
	public static Preprocessor my_preprocessor = new Preprocessor();
	public static int[][] main_matrice;
	public boolean run_apriori = false;
	public String apriori_data_path = "C:\\Users\\Rami\\workspace\\Minimizing_Non_Frequent_Itemsets_CPLEX\\retail_apriori.txt";
	public static int index_test = 0;
	public static String log_file_name = "Log_File.csv";
	public static double objective_value = 0;
	
	public static double CPLEX_Running_Time = 0;
	public static double preprocessing_time = 0;
	public static double transaction_sanitization_time = 0;
	public static int number_of_transactions_modified;
	public double time1 = 0;
	public double time2 = 0;
	
	//public Sanitizer my_sanitizer = new Sanitizer();
	
	
	public static void main(String[] args) throws Exception {
		if (args.length >=1){
		
			Main Main_obj = new Main();

			
			String main_directory = args[0];
			Main_obj.database_path = main_directory+"current_database.txt";
			Main_obj.sensitive_itemsets_path = main_directory+"sensitive_itemsests.txt";
			Main_obj.relevant_transactions_database = main_directory+"relevant_transaction.txt";
			Main_obj.relevant_transactions_database_after_sanitization = main_directory+"relevant_transactins_after_sanitization_CPLEX.txt";
			Main.minimum_support_double = Double.valueOf(args[1]);
			Main.minimum_support_level_value_double = Double.valueOf(args[2]);
			Main.minimum_support_level_int = Integer.valueOf(args[3]);
			Main.minimum_support_level_value_double = Double.valueOf(args[4]);
			if (args[5].contains("true")){
				Main_obj.run_apriori = true;
			}
			Main_obj.apriori_data_path = main_directory+"apriori_data.txt";
			Main.number_of_sensitive_itemsets = Integer.valueOf(args[6]);
			Main.number_of_transactions = Integer.valueOf(args[7]);
			Main.sensitive_itemsets_path = args[8];
			Main_obj.minimum_support_level = String.valueOf(minimum_support_double);
			Main_obj.Main_Program();			
			
		}
		else{
				System.out.println("Please input correct variables for the program to run");

		}
		
		//Main.minimum_support_level_int = 88;
			
	}
	
	public void Main_Program() throws Exception{
		//this.populate_sensitive_itemsets();
		this.read_sensitive_itemsets(sensitive_itemsets_path);
		Main.list_of_sensitive_items = this.find_sensitive_items(Main.list_of_sensitive_itemsets);
		this.populate(database_path);
		this.populate_relevant_transactions_database(relevant_transactions_database);
		if (run_apriori){
			list_of_all_itemsets = this.find_all_itemsets(database_path, minimum_support_level, true);
			this.populate_apriori_data(list_of_all_itemsets);
		}
		else{
			list_of_all_itemsets = this.read_all_itemsets(apriori_data_path);
		}
		
		//append to main log
		this.append_results_to_log("databse, number of sensitive itemsets, number of itemsets hidden, number of items removed, Preprocessing Time, CPLEX Run Time, Transaction Sanitization Time, Number of Transactions Modified");

		//minimum_support_double = (double) (88/list_of_relevant_transactions.size());
		//minimum_support_integer = (int) (minimum_support_integer/list_of_relevant_transactions.size());
		//this.sanitize_transactions();
		double new_ratio = minimum_support_level_value_double/(double)Main.list_of_relevant_transactions.size();
		String new_ratio_text = String.valueOf(new_ratio);
		System.out.println("===========================================================================");
		System.out.println("Starting Relevant Database apriori");
		list_of_itemsets_in_relevant_transactions = this.find_all_itemsets(relevant_transactions_database, new_ratio_text, false);//Double.toString(minimum_support_double));
	
		time1 = System.currentTimeMillis();
		list_of_preprocessed_itemsts = Main.my_preprocessor.pre_process_itemsets(list_of_all_itemsets, list_of_itemsets_in_relevant_transactions, list_of_sensitive_itemsets, list_of_sensitive_items);
		time2 = System.currentTimeMillis();
		
		Main.preprocessing_time = (time2-time1)/1000;
		
		// Debug why this item is sanitized.
		//38
		//48
		//389
		
		ArrayList<Integer> temp_list = new ArrayList<Integer>();
		temp_list.add(48);
		temp_list.add(288);
		//temp_list.add(105);
		Itemset temp_one = new Itemset(1, temp_list, "test");
		int iii = 0;
		for (Itemset temp_itemset:Main.list_of_preprocessed_itemsts){
			if (temp_itemset.Contains(temp_one)){
				Main.index_test = iii;
			}
			iii++;
		}
		
		//print_array_list(Main.my_preprocessor.pre_processed_itemsets);
		//this.sanitize_transactions();
		time1 = System.currentTimeMillis();
		this.sanitize_transactions_naive();
		time2 = System.currentTimeMillis();
		Main.transaction_sanitization_time = (time2-time1)/1000;
		this.solve_Main_Probelm();
		
		New_Transaction_Creator transaction_creator = new New_Transaction_Creator();
		transaction_creator.create(Main.solution);
		
		this.populate_relevant_transactions_database_after_sanitization(relevant_transactions_database_after_sanitization);
	
		System.out.println("===========================================================================");
		System.out.println("Starting Sanitized Relevant Database apriori");
		//list_of_itemsets_in_relevant_transactions_after_Sanitization = this.find_all_itemsets(relevant_transactions_database_after_sanitization, new_ratio_text, false);
		
		//this.find_itemsets_that_were_affected_by_sanitization();
		//this.find_itemsets_that_were_affected_by_sanitization2();
		
		// find number of items that were removed
		
		int items_removed = count_items_removed(Main.list_of_itemsets_in_relevant_transactions_after_Sanitization,Main.list_of_relevant_transactions);
		
		System.out.println("number of items removed: "+items_removed);
		this.find_number_of_transactions_modified();
		String temp_output = database_path + "," + Main.number_of_sensitive_itemsets + ","+ Double.toString(Main.objective_value) + "," + items_removed+"," + Main.preprocessing_time+"," + Main.CPLEX_Running_Time+"," + Main.transaction_sanitization_time+","+Main.number_of_transactions_modified;
		this.append_results_to_log(temp_output);
		
	}
	
	private void find_number_of_transactions_modified() {
		int[] number_of_occurenece = new int[Main.list_of_relevant_transactions.size()];
		int[] number_of_occurenece2 = new int[Main.list_of_relevant_transactions.size()];
		
		
		for(int i =0; i< Main.list_of_relevant_transactions.size(); i++){
			int j = Main.list_of_relevant_transactions.get(i).items.size();
			number_of_occurenece[j]++;
		}
		
		for(int i =0; i< Main.list_of_relevant_transactions.size(); i++){
			int j = Main.list_of_relevant_transactions_after_sanitization.get(i).items.size();
			number_of_occurenece[j]--;
		}
		
		
		for(int i =0; i< Main.list_of_relevant_transactions.size(); i++){
			if(number_of_occurenece[i] > 0){
			Main.number_of_transactions_modified = Main.number_of_transactions_modified+number_of_occurenece[i];
			}
			
		}
		
	}
	
	private int count_items_removed(
			ArrayList<Itemset> sanitized_transactions,
			ArrayList<Itemset> relative_transactions) {

		int number_of_items_after_sanitization = 0;
		int number_of_items_before_sanitization = 0;
		
		for (Itemset temp: sanitized_transactions){
			number_of_items_after_sanitization = temp.get_items().size()+number_of_items_after_sanitization;
		}
		
		for (Itemset temp2: relative_transactions){
			number_of_items_before_sanitization = temp2.get_items().size()+number_of_items_before_sanitization;
		}
		
		return (number_of_items_before_sanitization-number_of_items_after_sanitization);
	}
	
	private void populate_apriori_data(ArrayList<Itemset> list_of_all_itemsets2) {
		String string = this.apriori_data_path;	
		BufferedWriter bufferedWriter = null;
		ArrayList<Itemset> apriori_itemsets = list_of_all_itemsets2;

		try {

			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(string));

			for (Itemset temp_itemset : apriori_itemsets){
				
				for (Integer temp : temp_itemset.items){
					bufferedWriter.write(temp+" ");
				}
				bufferedWriter.write(temp_itemset.support_level+" ");

				bufferedWriter.newLine();

			}



						
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}
	
	}

	// function that will populate the Main_Matrice


	private void find_itemsets_that_were_affected_by_sanitization2() {
		
		
	}

	private void find_itemsets_that_were_affected_by_sanitization() {
		System.out.println("==========================================================================");
		System.out.println("itmesets that were hidden:");
		

	
		ArrayList<Itemset> itemsets_in_relevant_transactions_after_sanitization = new ArrayList<Itemset>();
		boolean first_itemset = true;	
		for (Itemset temp_transaction:Main.list_of_relevant_transactions_after_sanitization){
			for(Itemset temp_itemset: Main.list_of_preprocessed_itemsts){
				if (temp_transaction.Contains(temp_itemset)){
					if (first_itemset){
						ArrayList<Integer> temp_array = new ArrayList<Integer>();
						for (Integer temp_integer:temp_itemset.items){
							temp_array.add(temp_integer);
						}
						Itemset new_itemset = new Itemset(1, temp_array, "itemset"); 
						itemsets_in_relevant_transactions_after_sanitization.add(new_itemset);
						first_itemset = false;
					}
					else{
						boolean contains_itemset = false;
						for (Itemset temp_itemset2:itemsets_in_relevant_transactions_after_sanitization){
							if (temp_itemset2.Contains(temp_itemset) && (temp_itemset2.items.size() == temp_itemset.items.size())){
								contains_itemset = true;
								temp_itemset2.support_level++;
							}
	
						}
						
						if (!contains_itemset){
							ArrayList<Integer> temp_array = new ArrayList<Integer>();
							for (Integer temp_integer:temp_itemset.items){
								temp_array.add(temp_integer);
							}
							Itemset new_itemset = new Itemset(1, temp_array, "itemset"); 
							itemsets_in_relevant_transactions_after_sanitization.add(new_itemset);
						}
						contains_itemset = false;
					}

				}
			}
		}
		
		/*for (Itemset tempp: itemsets_in_relevant_transactions_after_sanitization){
			if (tempp.Contains(Main.list_of_preprocessed_itemsts.get(40))){
				System.out.println(tempp.support_level);
			}
		}*/
		
		ArrayList<Itemset> itemsets_in_relevant_transactions_before_sanitization = new ArrayList<Itemset>();
		boolean first_itemset_2 = true;	
		for (Itemset temp_transaction:Main.list_of_relevant_transactions){
			for(Itemset temp_itemset: Main.list_of_preprocessed_itemsts){
				if (temp_transaction.Contains(temp_itemset)){
					if (first_itemset_2){
						ArrayList<Integer> temp_array = new ArrayList<Integer>();
						for (Integer temp_integer:temp_itemset.items){
							temp_array.add(temp_integer);
						}
						Itemset new_itemset = new Itemset(1, temp_array, "itemset"); 
						itemsets_in_relevant_transactions_before_sanitization.add(new_itemset);
						first_itemset_2 = false;
					}
					else{
						boolean contains_itemset = false;
						for (Itemset temp_itemset2:itemsets_in_relevant_transactions_before_sanitization){
							if (temp_itemset2.Contains(temp_itemset) && (temp_itemset2.items.size() == temp_itemset.items.size())){
								contains_itemset = true;
								temp_itemset2.support_level++;
							}
	
						}
						
						if (!contains_itemset){
							ArrayList<Integer> temp_array = new ArrayList<Integer>();
							for (Integer temp_integer:temp_itemset.items){
								temp_array.add(temp_integer);
							}
							Itemset new_itemset = new Itemset(1, temp_array, "itemset"); 
							itemsets_in_relevant_transactions_before_sanitization.add(new_itemset);
						}
						contains_itemset = false;
					}

				}
			}
		}
		
		for (Itemset temp_itemset:itemsets_in_relevant_transactions_before_sanitization){
			for (Itemset temp_itemset2:itemsets_in_relevant_transactions_after_sanitization){
				if (temp_itemset.Contains(temp_itemset2) && (temp_itemset.items.size() == temp_itemset2.items.size())){
					if (true){
						// if support is reduced
						for (Itemset temp_itemset3: Main.list_of_all_itemsets){
							if (temp_itemset3.Contains(temp_itemset2) && (temp_itemset3.items.size() == temp_itemset2.items.size())){
								if (temp_itemset3.support_level-temp_itemset.support_level + temp_itemset2.support_level < minimum_support_level_int){
									boolean test = false;
									for (Itemset temp_itemset4:Main.list_of_sensitive_itemsets){
										if ((temp_itemset2.Contains(temp_itemset4)) && (temp_itemset4.items.size()!=temp_itemset2.items.size())) test = true;
									}
									if (!test){
									System.out.println(temp_itemset2.items +",Current_Support" +temp_itemset2.support_level + ", original_support"+temp_itemset3.support_level+", preprocessed support" + temp_itemset.support_level);
									for (Itemset temp_itemset5:Main.list_of_preprocessed_itemsts){
										if((temp_itemset5.Contains(temp_itemset2)) && (temp_itemset5.items.size()==temp_itemset2.items.size())){
											//System.out.println("itemsets included in preprocessed itemsets");
										}
									}
									}
									 test = false;
								}
							}
						}
					}
				}
			}
		}
		
	
		
	}

	private ArrayList<Itemset> read_all_itemsets(String apriori_data_path2) {
		ArrayList<Itemset> temp_itemsets = new ArrayList<Itemset>();
		try {
			String Database = apriori_data_path2;
			// Start reading the file line by line
			BufferedReader Database_Reader_Sensitive = new BufferedReader(new FileReader(
					Database));
			// Temp string and Itemset
			String transaction_in;

			// get all the items from string and store them in the arraylist as
			// integers
			while ((transaction_in = Database_Reader_Sensitive.readLine()) != null) {
				
				ArrayList<Integer> items_integer = new ArrayList<Integer>();
				String[] items_strings = transaction_in.split(" ");
				for (String item_string : items_strings) {
					items_integer.add(Integer.valueOf(item_string));
				}
				
				int last_item_index = items_integer.size()-1;
				int temp_support = items_integer.get(last_item_index);
				items_integer.remove(last_item_index);
				Itemset temp_itemset = new Itemset(temp_support, items_integer, "Normal");
				if (last_item_index == 1){
					Main.list_of_items_in_database.add(temp_itemset);
				}
				else{
					
					// Create the new itemset
					//Itemset_In = new Itemset(0, items_integer, "Normal");
					Itemset temp_itemset2 = new Itemset(temp_support, items_integer, "Normal");
					temp_itemsets.add(temp_itemset2);
				}
			}
			Database_Reader_Sensitive.close();
			Main.number_of_sensitive_itemsets = Main.list_of_sensitive_itemsets.size();
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
		
		return temp_itemsets;
	}

	private void solve_Main_Probelm() {

		Main.main_matrice = new int[Main.list_of_preprocessed_itemsts.size()][Main.list_of_preprocessed_itemsts.size()+Main.list_of_relevant_transactions.size()];
		solution = my_solver.solve_main_matrice(main_matrice);
	}

/*	private void sanitize_transactions() {
		int i = 0;
		for (Itemset transaction:list_of_relevant_transactions){
			if (i ==112){
				System.out.println("test");
			}
			Sanitizer my_sanitizer = new Sanitizer();
			my_sanitizer.sanitize_transaction(transaction);
			my_sanitizer = null;
			i++;
		}
	}*/

	private void sanitize_transactions_naive() {
		int i = 0;
		for (Itemset transaction:list_of_relevant_transactions){
			Sanitizer_Naive my_sanitizer = new Sanitizer_Naive();
			my_sanitizer.sanitize_transaction(transaction);
			my_sanitizer = null;
			i++;
			
		}
	}
	
	private void print_array_list(ArrayList<Itemset> pre_processed_itemsets) {
		for (Itemset temp : pre_processed_itemsets)
		{
			System.out.println(temp.items);
		}
		
	}

	private ArrayList<Itemset> find_all_itemsets(String database_path2,
			String Support_Level, boolean first_scan) throws Exception {
		String[] args={database_path2, Support_Level};
		//new Apriori(args, this);
		if (first_scan){
			Apriori my_apriori = new Apriori(args, true, true);
			ArrayList<Itemset> all_itemsets = my_apriori.Apriori_Itemset(args);
			return all_itemsets;
		}
		else
		{
			Apriori my_apriori = new Apriori(args, true, false);
			ArrayList<Itemset> all_itemsets = my_apriori.Apriori_Itemset(args);
			return all_itemsets;
		}

		
		
		
	}

	public Boolean populate(String passed_database_file_path) {
		try {
			String Database = passed_database_file_path;
			// Start reading the file line by line
			BufferedReader Database_Reader = new BufferedReader(new FileReader(
					Database));
			// Temp string and Itemset
			String transaction_in;
			Itemset Itemset_In;


			
			// get all the items from string and store them in the arraylist as
			// integers
			while ((transaction_in = Database_Reader.readLine()) != null) {

				// Temp array list to hold items for the itemsets that will be
				// created
				ArrayList<Integer> items_integer = new ArrayList<Integer>();
				
				String[] items_strings = transaction_in.split(" ");
				for (String item_string : items_strings) {
					items_integer.add(Integer.valueOf(item_string));
				}
				// Create the new itemset
				Itemset_In = new Itemset(0, items_integer, "transaction");
				
				//add transaction to list of all transactions
				Main.list_of_all_transactions.add(Itemset_In);
				
				//if itemset contains sensitive itemsets add it to relevant transactions
				
				for (Itemset temp : Main.list_of_sensitive_itemsets){
					if (Itemset_In.Contains(temp)){
					//if (Itemset_In.items.contains(temp)){
						Main.list_of_relevant_transactions.add(Itemset_In);
						break;
					}
				}
				
//				for (Integer temp_integer: Itemset_In.items){
//					if (Main.list_of_items_in_database.contains(temp_integer)){
//						
//					}
//				}
			
			}
			Database_Reader.close();
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
		return null;
	}


	public void read_sensitive_itemsets(String passed_sensitive_itemsets_path){
		try {
			String Database = passed_sensitive_itemsets_path;
			// Start reading the file line by line
			BufferedReader Database_Reader_Sensitive = new BufferedReader(new FileReader(
					Database));
			// Temp string and Itemset
			String transaction_in;

			// get all the items from string and store them in the arraylist as
			// integers
			while ((transaction_in = Database_Reader_Sensitive.readLine()) != null) {
				
				ArrayList<Integer> items_integer = new ArrayList<Integer>();
				String[] items_strings = transaction_in.split(" ");
				for (String item_string : items_strings) {
					items_integer.add(Integer.valueOf(item_string));
					
				}
				// Create the new itemset
				//Itemset_In = new Itemset(0, items_integer, "Normal");
				Itemset temp_itemset = new Itemset(0, items_integer, "Normal");
				Main.list_of_sensitive_itemsets.add(temp_itemset);
			}
			Database_Reader_Sensitive.close();
			Main.number_of_sensitive_itemsets = Main.list_of_sensitive_itemsets.size();
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}

	}
	
	public ArrayList<Integer> find_sensitive_items(ArrayList<Itemset> sensitive_itemsets){
		ArrayList<Integer> sensitive_items = new ArrayList<Integer>();
		for (Itemset sen_itemset: sensitive_itemsets){
			for (Integer sen_item: sen_itemset.items){
				if (sensitive_items.contains(sen_item)){}
				else{
					sensitive_items.add(sen_item);
				}
			}
		}
		return sensitive_items;
	}
	
	public void populate_relevant_transactions_database(String string) {
		BufferedWriter bufferedWriter = null;

		try {

			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(string));

			for (Itemset temp_itemset : Main.list_of_relevant_transactions){
				
				for (Integer temp : temp_itemset.items){
					bufferedWriter.write(temp+" ");
				}
				bufferedWriter.newLine();

			}



						
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}
		
	}

	public void populate_relevant_transactions_database_after_sanitization(String string) {
		BufferedWriter bufferedWriter = null;

		try {

			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(string));

			for (Itemset temp_itemset : Main.list_of_relevant_transactions_after_sanitization){
				
				for (Integer temp : temp_itemset.items){
					bufferedWriter.write(temp+" ");
				}
				bufferedWriter.newLine();

			}



						
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}
		
	}


	public void append_results_to_log(String string) {
	

		  try{
			  // Create file 
			  FileWriter fstream = new FileWriter(Main.log_file_name,true);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(string);
			  out.newLine();
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
	}


}
