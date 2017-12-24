import java.util.ArrayList;


public class New_Transaction_Creator {

	public void create(double[] solution) {
		int index = 0;
		
		System.out.println("Debug Debug");
		for(Itemset transaction:Main.list_of_relevant_transactions){
			ArrayList<Integer> current_items = new ArrayList<Integer>();
			for(Integer temp_integer:transaction.items){
				current_items.add(temp_integer);
			}

			if(solution[index] == 0){
				Main.list_of_relevant_transactions_after_sanitization.add(transaction);
			}
			else{
				if (transaction.Contains(Main.list_of_preprocessed_itemsts.get(7))){
					
					System.out.println(transaction.items);
				}

				Itemset new_transaction = new Itemset(transaction.support_level, transaction.items_after_sanitization, "transaction");
				Main.list_of_relevant_transactions_after_sanitization.add(new_transaction);
				
			}
			
			index++;

			
		}
		
	}

}
