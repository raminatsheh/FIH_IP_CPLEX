import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Sanitizer_Naive {
	
    /** the list of current itemsets */
    private List<int[]> itemsets ;
    private List<int[]> all_itemsets_in_T ;
    private List<Itemset> all_itemsets_in_T_Itemset = new ArrayList<Itemset>();
    private List<Itemset> all_itemsets_in_T_Itemset2 = new ArrayList<Itemset>();
    private Solver transaction_solver = new Solver();
    private ArrayList<Double> sanitizer_outcome;
    
	public void sanitize_transaction(Itemset transaction){
		

		Populate_itemsets_in_T_from_relevantitemsets(transaction);
		Sanitize_Transaction(transaction);

		//sanitizer_outcome = transaction_solver.sanitize_transaction((ArrayList<Itemset>) this.all_itemsets_in_T_Itemset2, transaction.items);
		//transaction.sanitized_in_all_itemsets = sanitizer_outcome;
		
	}
	
	private void Sanitize_Transaction(Itemset transaction) {
		ArrayList<Integer> sen_items_in_T = new ArrayList<Integer>();

		for (Itemset temp:this.all_itemsets_in_T_Itemset2){
			if (temp.get_type()=="sensitive"){
				for(Integer temp_int:temp.items){
					if (sen_items_in_T.contains(temp_int)){				
					}
					else{
						sen_items_in_T.add(temp_int);
					}
				}
			}
		}
		
		ArrayList<Integer> all_items_in_T = new ArrayList<Integer>();
		for (Integer temp:transaction.items){
			all_items_in_T.add(temp);
		}
		
		Itemset temp_itemset = new Itemset(0, all_items_in_T, "transaction");
		Integer temp_integer = 0;
		int support_integer = 0;
		if (sen_items_in_T.size() < transaction.items.size()){
			for(Integer temp:sen_items_in_T){
				temp_itemset.items.remove(temp);
			}
			this.Populate_itemsets_in_T(temp_itemset);
			int j = 0;
			ArrayList<Double> temp_sanitization_Markers = new ArrayList<Double>();
			for (Integer tt:transaction.indices){
				temp_sanitization_Markers.add(0.0);
			}
			for (Integer temp_index:transaction.indices){
				if (temp_itemset.indices.contains(temp_index)){
					temp_sanitization_Markers.set(j, 0.0);
				}
				else{
					temp_sanitization_Markers.set(j, 1.0);
				}
				j++;
			}
			j=0;
			transaction.sanitized_in_all_itemsets = temp_sanitization_Markers;
			transaction.items_after_sanitization = temp_itemset.items;
		}
		else{
			temp_itemset.items.clear();
			for (Itemset temp:Main.list_of_items_in_database){
				if (transaction.items.contains(temp.items.get(0))){
					if(support_integer < temp.support_level){
						support_integer = temp.support_level;
						temp_integer = temp.items.get(0);
					}
				}
			}
			temp_itemset.items.add(temp_integer);
			transaction.items_after_sanitization.clear();
			transaction.items_after_sanitization.add(temp_integer);
			
			for (int i = 0; i<transaction.indices.size();i++){
				transaction.sanitized_in_all_itemsets.add(i, 1.0);
			}
		}
		
		
		
		
	}

	private void Populate_itemsets_in_T_from_relevantitemsets(Itemset transaction) {
		int i = 0;
		for (Itemset temp:Main.list_of_preprocessed_itemsts){
			if (transaction.Contains(temp)){
				this.all_itemsets_in_T_Itemset2.add(temp);
				transaction.indices.add(i);
				for (Itemset temp_itemset2:Main.list_of_sensitive_itemsets){
					if (temp.Contains(temp_itemset2)){
						temp.set_type("sensitive");
					}
				}
			}
			i++;
		}
		
	}

	private void Populate_itemsets_in_T(Itemset transaction) {
		int i = 0;
		for (Itemset temp:Main.list_of_preprocessed_itemsts){
			if (transaction.Contains(temp)){
				//this.all_itemsets_in_T_Itemset2.add(temp);
				transaction.indices.add(i);
				for (Itemset temp_itemset2:Main.list_of_sensitive_itemsets){
					if (temp.Contains(temp_itemset2)){
						temp.set_type("sensitive");
					}
				}
			}
			i++;
		}
		
	}
}
