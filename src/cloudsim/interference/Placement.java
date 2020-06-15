package cloudsim.interference;

import java.util.concurrent.ThreadLocalRandom;

import org.cloudbus.cloudsim.Log;

public class Placement {

	// public static void main(String[] args) {

	// System.out.println(randomInt(0, 16));

	// }

	/**
	 * It executes the hill climbing algorithm and returns the best local solution
	 * 
	 * @param solution initial solution
	 * @return best solution found
	 */

	public static Solution HillClimbing(Solution solution) {
		int iterations = 5000;
		int maxNoChange = 2000;
		int numOp = 0;
		int noChange = 0;

		Solution best = solution.copy();

		while (iterations > 1 && noChange < maxNoChange) {
			numOp++;
			Solution newSolution = best.copy();

			newSolution = randomSwap(newSolution);
			//Log.printLine("RANDOM");
			double bestCost = best.getTotalInterferenceCost();
			double newCost = newSolution.getTotalInterferenceCost();

			if (newCost < bestCost) {
				best = newSolution.copy();
				noChange = 0;
			}

			noChange++;
			iterations--;
		}

		return best;

	}

	/**
	 * Generate a random modification of the current solution. 50% of chance of
	 * swaping two cloudlets 50% of change of moving a cloudlet to a new PM
	 */

	private static Solution randomSwap(Solution solution) {

		double rand = Math.random();

		if (rand > 0.5) {
			return swapping(solution);
		} else {
			return moving(solution);
		}
	}

	/**
	 * Swap two random cloudlets from two different Hosts(VMs)
	 */

	private static Solution swapping(Solution solution) {

		int cloudlet1 = randomInt(1, solution.getSize());
		int cloudlet2;

		while (solution.runninginOnlyOneHost(cloudlet1)) {
			cloudlet1 = randomInt(1, solution.getSize());
		}

		cloudlet2 = cloudlet1;

		while (cloudlet2 == cloudlet1) { // || solution.runninginOnlyOneHost(cloudlet2) ||
											// solution.hasSameCloudletSize(cloudlet1, cloudlet2)) {

			cloudlet2 = randomInt(1, solution.getSize());
			//Log.printLine("AAA");

		}
		//Log.printLine("CL1: " + cloudlet1 + " CL2: " + cloudlet2);

		solution.swapCloudletHost(cloudlet1, cloudlet2);

		return solution;
	}

	/**
	 * Move a random cloudlet of the Host(VM) with highest cost to the Host(VM) with
	 * lowest cost
	 */
	private static Solution moving(Solution solution) {

		return solution;
	}

	private static int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

}
