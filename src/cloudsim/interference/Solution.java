package cloudsim.interference;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Log;

public class Solution {

	private HashMap placement = new HashMap<Integer, HashMap<String, Double>>();
	private int ttime;
	private int start;
	private int end;

	public void Solution() {

	}

	// add a new cloudlet conf. to the Solution
	public void addCloudletToSolution(double hostId, double hostPe, double clId, double clPe, double clCost) {
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet.put("hostId", hostId);
		cloudlet.put("hostPe", hostPe);
		cloudlet.put("clId", clId);
		cloudlet.put("clPe", clPe);
		cloudlet.put("clCost", clCost);

		if (!this.placement.containsKey((int) clId)) {
			this.placement.put((int) clId, cloudlet);
		} else {
			Log.printLine(
					"@@@@ ERROR (addCloudletToSolution): Atempting to add a repited cloudlet entry in some Solution()");
		}

	}

	public double getCostFromCloudlet(int clId) {
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(clId);

		return (double) cloudlet.get("clCost");
	}

	public int getSize() {

		return this.placement.size();

	}

	public double getTotalInterferenceCost() {
		double totalCost = 0;
		for (int i = 1; i <= getSize(); i++) {
			//Log.printConcatLine(getCostFromCloudlet(i), " - ",getCloudRes(i), " - ",getHostRes(i) );
			// if this cloudlet/container is the only one ruuning inside a given Host,
			// there will be no interference incidence, hence its interference cost is 0
			if (!runninginOnlyOneHost(i)) {
				totalCost += getCostFromCloudlet(i) / (getCloudRes(i) / getHostRes(i));
				//Log.printConcatLine(totalCost);
			}
		}

		return (totalCost * (end - start)) / ttime;
	}

	private double getCloudRes(int clId) {
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(clId);

		return (double) cloudlet.get("clPe");
	}

	private double getHostRes(int clId) {
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(clId);

		return (double) cloudlet.get("hostPe");
	}

	private boolean runninginOnlyOneHost(int clId) {
		int count = 0;
		double hostId = 0;
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(clId);
		hostId = (double) cloudlet.get("hostId");

		for (int i = 1; i <= getSize(); i++) {
			cloudlet = (HashMap) this.placement.get(i);
			if ((double) cloudlet.get("hostId") == hostId) {
				count++;
			}
		}

		if (count == 1) {
			return true;
		} else {
			return false;
		}
	}

	public void setAdditionalParameters(int start, int end, int ttime) {
		this.start = start;
		this.end = end;
		this.ttime = ttime;

	}

}
