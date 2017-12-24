import java.util.ArrayList;
import ilog.concert.*;
import ilog.cplex.*;

import lpsolve.*;

public class Solver {
	
	//private LpSolve solver;
	public int sigma_min= Main.minimum_support_level_int;

	public double[] solve_main_matrice(int[][] linear_Program_Matrice) {
		sigma_min = Main.minimum_support_level_int;
		
		double[] var = new double[linear_Program_Matrice.length];
		
		try {
			
			this.build_main_matrice(linear_Program_Matrice);
			
			//Define the cplex object
			IloCplex cplex = new IloCplex();
			IloNumVar[] x = cplex.intVarArray(linear_Program_Matrice[0].length, 0, 1);
			IloLinearNumExpr expr = cplex.linearNumExpr();
			IloLinearNumExpr expr_obj = cplex.linearNumExpr();
			cplex.setParam(IloCplex.IntParam.BarItLim, 10);
			cplex.setParam(IloCplex.DoubleParam.TiLim, 86400);
			
			//solver = LpSolve.makeLp(0, linear_Program_Matrice[0].length);
			
			String temp_string = "";
			
			//rename variables
			//for (int i =0; i<Main.list_of_preprocessed_itemsts.size(); i++){
			//	int t = i+1;
			//	String temp = "x"+t;
			//	solver.setColName(t, temp);
			//}
			//solver.setAddRowmode(true);  /* makes building the model faster if it is done rows by row */
			
			int index = Main.list_of_relevant_transactions.size();
			int index2 = 0;
			for (Itemset temp_itemset:Main.list_of_preprocessed_itemsts){
				
				for (Itemset temp_sen:Main.list_of_sensitive_itemsets){
					if (temp_itemset.items.equals(temp_sen.items)){
						temp_itemset.set_type("sensetive");
					}
				}
				if (temp_itemset.get_type() == "sensetive"){
					
					for (int j=0; j< Main.list_of_relevant_transactions.size();j++){
						if (Main.list_of_relevant_transactions.get(j).Contains(temp_itemset)){
							linear_Program_Matrice[index2][j]=1;
						}
					}
					

					
					
					for (int j=0; j<linear_Program_Matrice[index2].length;j++)
					{
						if (j==linear_Program_Matrice[index2].length){
							temp_string = temp_string + linear_Program_Matrice[index2][j];

						}
						else{
						temp_string = temp_string + linear_Program_Matrice[index2][j]+" ";
						}
						expr.addTerm(linear_Program_Matrice[index2][j], x[j]);
					} 
					//solver.strAddConstraint(temp_string, LpSolve.GE, temp_itemset.support_level-sigma_min+1);
					cplex.addGe(expr,(temp_itemset.support_level-sigma_min+1));
					expr.clear();
					System.out.println(temp_string+"GE" + (temp_itemset.support_level-sigma_min+1));
				}
				else{
					int sum_items = 0;
					for (int j = 0; j<linear_Program_Matrice[index2].length;j++){
						sum_items = sum_items+linear_Program_Matrice[index2][j];
					}
					if (sum_items>=sigma_min){
					linear_Program_Matrice[index2][Main.list_of_relevant_transactions.size()+index2] = -1*sigma_min;
					}
					else{
						linear_Program_Matrice[index2][Main.list_of_relevant_transactions.size()+index2] = -1*sum_items;
					}
						
					
					for (int j=0; j<linear_Program_Matrice[index2].length;j++)
					{
						if (j==linear_Program_Matrice[index2].length){
							temp_string = temp_string + linear_Program_Matrice[index2][j];

						}
						else{
						temp_string = temp_string + linear_Program_Matrice[index2][j]+" ";
						}

						expr.addTerm(linear_Program_Matrice[index2][j], x[j]);
					} 
					if (temp_itemset.support_level-sigma_min >=0){
						//solver.strAddConstraint(temp_string, LpSolve.LE, temp_itemset.support_level-sigma_min);
						cplex.addLe(expr,(temp_itemset.support_level-sigma_min));
						expr.clear();
						System.out.println(temp_string + "LE" + (temp_itemset.support_level-sigma_min));
					}
					else{
						//solver.strAddConstraint(temp_string, LpSolve.LE, 0);
						cplex.addLe(expr,0);
						expr.clear();
						System.out.println(temp_string+ "LE" + "0");
					}
						
					
				}
				
				index++;
				index2++;
				temp_string = "";

			}

			
			//for (int j2 =0;j2<linear_Program_Matrice[0].length;j2++ ){
			//	solver.setBinary((j2+1), true);
			//}
			
			String temp_string1 = "0";
			for (int j2 =1;j2<Main.list_of_relevant_transactions.size();j2++ ){
				temp_string1 = temp_string1 + " 0";
			}
			for (int j2 =Main.list_of_relevant_transactions.size();j2<linear_Program_Matrice[0].length;j2++ ){
				temp_string1 = temp_string1 + " 1";
				expr_obj.addTerm(1.0, x[j2]);
			}
			
			IloObjective obj = cplex.minimize(expr_obj);
			cplex.add(obj);
			if (cplex.solve()){
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				Main.objective_value = cplex.getObjValue();
				Main.CPLEX_Running_Time = cplex.getCplexTime();
				var = cplex.getValues(x);
			}
			
			//solver.setAddRowmode(false);			
		      // set objective function
		      //solver.strSetObjFn(temp_string1);
		      //solver.setMinim();

		//solver.solve();
      // print solution

      //System.out.println("Value of objective function: " + solver.getObjective());
      //var = solver.getPtrVariables();
      //double[] row =  new double[linear_Program_Matrice[0].length];
      //solver.getVariables(row);
      //double[] var = solver.getPtrPrimalSolution();
      for (int i1 = 0; i1 < var.length; i1++) {
        System.out.println("Value of var[" + i1 + "] = " + var[i1]);
      }
      
      
      // delete the problem and free memory
      //solver.deleteLp();
      cplex.end();
			
      
      
      
		}/*catch (LpSolveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
			}
		return var;
		
	}

	private void build_main_matrice(int[][] linear_Program_Matrice) {
		int transaction_number = 0;
		for (Itemset temp_transaction : Main.list_of_relevant_transactions){
		
			
			// for this transaction populate the constraints from the 
			// non sensitive itemset portion of the MFIH
			// first transaction number is size of sensitive itemsets
			
			for (int i = 0; i<linear_Program_Matrice.length; i++ ){
				if (temp_transaction.indices.contains(i)){
					int j = temp_transaction.indices.indexOf(i);
					if (temp_transaction.sanitized_in_all_itemsets.get(j) > 0.5){
						linear_Program_Matrice[i][transaction_number] = 1;
					}
					else{
						linear_Program_Matrice[i][transaction_number] = 0;
					}
				}
				else{
					linear_Program_Matrice[i][transaction_number] = 0;
				}
			}
			
			
			
			transaction_number++;

		}
		
	}

}
