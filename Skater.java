import java.util.ArrayList;
import java.util.Random;

public class Skater{
	
	int k; //Angle resolution
	private int lastAction = 0;
	
	Random rand = new Random();
	
	//Data structure to save cumulative payoffs
	private ArrayList<Integer> payoffs;
	private long sum;
	//Data structure for cumulative probability of choosing actions
	private ArrayList<Double> prob;
	
	//To save the history of rewards, so that it can be plotted
	private ArrayList<ArrayList<Integer>> history;
	
	public Skater(int k, int initPayoffs){
		this.k = k;
		sum = initPayoffs*(360/k);
		
		payoffs = new ArrayList<Integer>(k);
		prob = new ArrayList<Double>(k);
		for(int i = 0; i < (360/k); i++){
			payoffs.add(i, initPayoffs);
			prob.add(i, (((double) i + 1.0) / (360.0 /((double) k))));
			if(main.debug){
				System.out.println(prob.get(i));
			}
		}
		history = new ArrayList<ArrayList<Integer>>();
		history.add(0, payoffs);
	}
	
	public int chooseAction() {
		//Choose action according to probability distribution
		double randn = Math.random();
		if(main.debug){
			System.out.println("The random value is " + randn);
		}
		for(int i = 0; i < (360/k); i++){
			if(main.debug){
				System.out.println(i + ". Is " + randn + " < " + prob.get(i));
			}
			if(randn < prob.get(i)){
				lastAction = i;
				if(main.debug){
					System.out.println("The action was: " + lastAction);
				}
				return (k*i);
			}
		}
		return (k*(360/k - 1));
		
	}
	
	public void reward(int r, int t){
		if(main.debug){
			System.out.println(" Got reward " + r + " !");
		
			System.out.println("The action was: " + lastAction);
		}
		//Add the payoff to the cumulative payoff
		payoffs.set(lastAction, (payoffs.get(lastAction) + r));
		
		//Update probabilities
		sum += r;
		double c = 0;
		for(int i = 0; i < (360/k); i++){
			c += ((double) payoffs.get(i)) / ((double) sum);
			prob.set(i, c);
			if(main.debug){
				System.out.println("Cumulative payoff is " + payoffs.get(i));
				System.out.println("prob is " + c);
			}
		}
		
		//Update history
		history.add(t, new ArrayList<Integer>(payoffs));
	}
	
	public String printProbs(){
		String s = "";
		double n = 0;
		for(int i = 0; i < (360/k); i++){
			s += " For action " + i+ ": " + (prob.get(i) - (double)n);
			n = prob.get(i);
		}
		return s;
	}
	
	public ArrayList<ArrayList<Integer>> getHistory(){
		return history;
	}

}
