import java.util.ArrayList;


public class Main_Matrice {
	private int Number_of_transactions;
	private int Number_of_sensitive_itemsets;
	private int [][] Linear_Program_Matrice;
	private ArrayList<Itemset> relevant_transactions = new ArrayList<Itemset>();
	private Solver solver = new Solver();
	public int number_of_relevant_transactions =0;
	private int [][] new_matrice;
	
	
	public void add_transaction_to_Matrice(int relevant_transaction_number,  int[] support_per_transaction){
		for (int row =0; row<support_per_transaction.length; row++){
			this.Linear_Program_Matrice[row][relevant_transaction_number] = support_per_transaction[row];
		}
	}
	
	public Main_Matrice(int Number_of_transactions, int Number_of_sensitive_itemsets){
		this.Number_of_transactions = Number_of_transactions;
		this.Number_of_sensitive_itemsets = Number_of_sensitive_itemsets;
		this.Linear_Program_Matrice = new int[this.Number_of_sensitive_itemsets][this.Number_of_transactions];
	}
	
	public void add_transaction_to_Relevant_Transactions(Itemset Transaction){
		this.relevant_transactions.add(Transaction);
	}

	public void solve() {
		//maybe need to resize the array here.
		this.resize_matrice(this.number_of_relevant_transactions);
		solver.solve_main_matrice(this.new_matrice);
		
	}	
	private void resize_matrice(int matrice_size)
	{
		this.new_matrice = new int[this.Number_of_sensitive_itemsets][matrice_size];
		for (int i = 0; i < matrice_size-1; i++){
			for (int j =0; j<this.Number_of_sensitive_itemsets; j++){
				this.new_matrice[j][i]=this.Linear_Program_Matrice[j][i];
			}
		}
	}
	
}
