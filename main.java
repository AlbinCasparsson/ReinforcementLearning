import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.awt.geom.Line2D;

public class main {
	
	public static boolean debug = false; //prints stuff if true
	static int collisions = 0; //used to count number of collisions that occur
	
	static double width = 1000;
	static double height = 1000;
	static int n = 10; //number of skaters
	static int k = 120; //Angle resolution
	static double v = 50; //Speed (same for all skaters)
	static double r = 8; //collision radius
	static int r1 = 1; //reward for not colliding
	static int r2 = -1; //reward if skater needs to stop to avoid collision
	
	static int tLimit = 100000; //Number of "seconds" to run the simulation
	static int t = 0; //Starting time, leave at 0
	static int initPayoffs = 20; //initial cumulative payoff
	
	static int nsamples = 20; //number of samples to display from the history
	
	static ArrayList<Skater> skaters;
	static ArrayList<Double> positionsX;
	static ArrayList<Double> positionsY;
	
	static Random rand = new Random();

	public static void main(String[] args) {
		skaters = new ArrayList<Skater>(n);
		positionsX = new ArrayList<Double>(n);
		positionsY = new ArrayList<Double>(n);
		
		//create skater objects and put in a list
		for(int i = 0; i < n; i++){
			skaters.add(i, new Skater(k, initPayoffs));
			positionsX.add(i, new Double(rand.nextDouble()*width));
			positionsY.add(i, new Double(rand.nextDouble()*height));
			
			if(debug){
				System.out.println("Skater " + i + " at " + positionsX.get(i) + ", " + positionsY.get(i));
			}
		}
		
		//Run the simulation and ask the user if it should continue
		while(true){
			System.out.println("Time = " + t);
			skatingTime();
			System.out.println("The probabilities are:");
			for(int i = 0; i < n; i++){
				System.out.println("Skater " + i + ": " + skaters.get(i).printProbs());
			}
			collectHistory();
			System.out.println("Continue simulation? (y/n)");
			try{
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String s = bufferRead.readLine();
			    //System.out.println(s);
			    if (s.equals("n")){
			    	break;
			    }
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
	}
	static final void skatingTime(){
		for(int i = 0; i < tLimit; i++){
			for (int j = 0; j < n; j++){
				//let skater j choose a direction
				int action = skaters.get(j).chooseAction();
				System.out.println("Skater " + j + " moves in direction " + action);
				
				//test if that direction causes collision,
				double speedX = v*Math.cos(Math.toRadians(action));
				double speedY = v*Math.sin(Math.toRadians(action));
				double newX = positionsX.get(j)+speedX;
				double newY = positionsY.get(j)+speedY;
				boolean collision = false;
				for (int h = 0; h < n; h++){
					if(h != j){
						double dist = Math.hypot(newX - positionsX.get(h), newY - positionsY.get(h));
						if(dist < r){
							collision = true;
							if(debug){
								System.out.println(" Collision!");
							}
						}
					}
				}
				
				//if no collision occurred, update the position
				if (collision == false){
					positionsX.set(j, ((newX+width) % width));
					positionsY.set(j, ((newY+height) % height));
					if(debug){
						System.out.println(" to " + positionsX.get(j) + ", " + positionsY.get(j));;
					}
					//reward skater
					skaters.get(j).reward(r1, t);
				}
				else{
					collisions++;
					if(debug){
						System.out.println(" stays at " + positionsX.get(j) + ", " + positionsY.get(j));
					}
					//give the lower reward
					skaters.get(j).reward(r2, t);
				}
			}
			t++;
		}
	}
	
	//Sample the history so that plots can be created
	static void collectHistory(){
		System.out.println("History:");
		for(int i = 0; i <= nsamples; i++){
			int time = i*t/nsamples;
			System.out.print("Time " + time + ": ");
			for(int j = 0; j < (360/k); j++){
				Integer sum = 0;
				for(int h = 0; h < n; h++){
					//get the reward for action j of player h at time t
					sum += skaters.get(h).getHistory().get(time).get(j);
				}
				Integer mean = sum / n;
				System.out.print(mean + " ");
			}
			System.out.println();
		}
		System.out.println("t is " + t + ", collisions: " + collisions);

	}

}
