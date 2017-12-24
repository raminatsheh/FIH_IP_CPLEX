import java.util.ArrayList;




public class Preprocessor {
	private ArrayList<Itemset> all_itemsets;
	private ArrayList<Itemset> all_sen_itemsets;
	private ArrayList<Integer> all_sen_items;
	private ArrayList<Itemset> all_relevant_transactions_itemsets;
	public ArrayList<Itemset> pre_processed_itemsets = new ArrayList<Itemset>();
	
	public ArrayList<Itemset> pre_process_itemsets(ArrayList<Itemset> passed_list_of_all_itemsets, ArrayList<Itemset> passed_list_of_itemsets_in_relevant_transactions, ArrayList<Itemset> passed_list_of_sensitive_itemsets, ArrayList<Integer> passed_list_of_sensitive_items) {
		this.all_itemsets = passed_list_of_all_itemsets;
		this.all_relevant_transactions_itemsets = passed_list_of_itemsets_in_relevant_transactions;
		this.all_sen_itemsets = passed_list_of_sensitive_itemsets;
		this.all_sen_items = passed_list_of_sensitive_items;
		
		this.remove_itemsets_with_no_sen_items();
		return pre_processed_itemsets;
	}
	
	private void remove_itemsets_that_are_supersets_of_sensitive_itemsets(Itemset temp_itemset) {

		boolean does_support_sen_itemset = false;
		
		for (Itemset temp_sen : this.all_sen_itemsets)
		{
			if (temp_itemset.Contains(temp_sen) && temp_itemset.items.size()>temp_sen.items.size()){
				does_support_sen_itemset = true;
			}
		}
		if (!does_support_sen_itemset){
			//this.pre_processed_itemsets.add(temp_itemset);
			this.remove_itemsets_with_enough_support_in_non_relevant_transactions(temp_itemset);
		}
		
		does_support_sen_itemset = false;
	}

	private void remove_itemsets_with_enough_support_in_non_relevant_transactions(Itemset temp_itemset) {
		int support_in_relevant_transactions = 0;
		int support_in_all_transactions = 0;
		
		for (Itemset temp2_itemset: this.all_relevant_transactions_itemsets)
		{
			if (temp2_itemset.Contains(temp_itemset) && temp2_itemset.items.size() == temp_itemset.items.size()){
				support_in_relevant_transactions = this.all_relevant_transactions_itemsets.indexOf(temp2_itemset);
				support_in_relevant_transactions = this.all_relevant_transactions_itemsets.get(support_in_relevant_transactions).support_level;
			}
		}
		
		
		support_in_all_transactions = this.all_itemsets.indexOf(temp_itemset);
		support_in_all_transactions = this.all_itemsets.get(support_in_all_transactions).support_level;
		
		if ((support_in_all_transactions - support_in_relevant_transactions) >= Main.minimum_support_level_value_double)
		{}
		else{
			this.pre_processed_itemsets.add(temp_itemset);
		}
	}

	private void remove_itemsets_with_no_sen_items() {
		boolean does_contain = false;
		for (Itemset temp_itemset : this.all_itemsets)
		{
			for (Integer temp_item : this.all_sen_items){
				if (temp_itemset.items.contains(temp_item)) does_contain = true;					
			}
			if (does_contain){
				//this.pre_processed_itemsets.add(temp_itemset);
				this.remove_itemsets_that_are_supersets_of_sensitive_itemsets(temp_itemset);
			}
			does_contain = false;
		}

	}
	

}
