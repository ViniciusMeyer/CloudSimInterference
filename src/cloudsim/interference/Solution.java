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
			// Log.printConcatLine(getCostFromCloudlet(i), " - ",getCloudRes(i), " -
			// ",getHostRes(i) );
			// if this cloudlet/container is the only one ruuning inside a given Host,
			// there will be no interference incidence, hence its interference cost is 0
			if (!runninginOnlyOneHost(i)) {
				totalCost += getCostFromCloudlet(i) / (getCloudRes(i) / getHostRes(i));
				// Log.printConcatLine(totalCost);
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

	public boolean runninginOnlyOneHost(int clId) {
		
		//Log.printLine("AQUI ======= ID: "+ clId);
		int count = 0;
		double hostId = 0;
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(clId);
		hostId = (double) cloudlet.get("hostId");

		for (int i = 1; i <= this.placement.size(); i++) {
			cloudlet = (HashMap) this.placement.get(i);

			//Log.printLine((double) cloudlet.get("hostId") + " " + hostId + " "+ i + " "+ this.placement.size());
			if ((double) cloudlet.get("hostId") == hostId) {
				count++;
			}
		}
		//Log.printLine("AQUI");

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

	/*
	 * swap cloudlets' hosts
	 */
	public void swapCloudletHost(int cloudlet1, int cloudlet2) {
		HashMap cl1 = (HashMap) this.placement.get(cloudlet1);
		HashMap cl2 = (HashMap) this.placement.get(cloudlet2);
		HashMap aux = new HashMap<String, Double>();

		aux = cl1;
		// cl1 = cl2;
		// cl2 = aux;
		//Log.printLine("ANTES:");
		//Log.printConcatLine("CL1: host", cl1.get("hostId"), " hostPe: ", cl1.get("hostPe"), " clId: ", cl1.get("clId"),		" clPe: ", cl1.get("clPe"), " clCost:", cl1.get("clCost"));
		//Log.printConcatLine("CL2: host", cl2.get("hostId"), " hostPe: ", cl2.get("hostPe"), " clId: ", cl2.get("clId")," clPe: ", cl2.get("clPe"), " clCost:", cl2.get("clCost"));

		cl1.replace("hostId", cl2.get("hostId"));
		cl1.replace("hostPe", cl2.get("hostPe"));
		// cl1.put("clId", cl2.get("clId"));
		// cl1.put("clPe", cl2.get("clPe"));
		// cl1.put("clCost", cl2.get("clCost"));

		cl2.replace("hostId", aux.get("hostId"));
		cl2.replace("hostPe", aux.get("hostPe"));
		// cl2.put("clId", aux.get("clId"));
		// cl2.put("clPe", aux.get("clPe"));
		// cl2.put("clCost", aux.get("clCost"));
		//Log.printLine("DEPOIS:");
		//Log.printConcatLine("CL1: host", cl1.get("hostId"), " hostPe: ", cl1.get("hostPe"), " clId: ", cl1.get("clId")," clPe: ", cl1.get("clPe"), " clCost:", cl1.get("clCost"));
		//Log.printConcatLine("CL2: host", cl2.get("hostId"), " hostPe: ", cl2.get("hostPe"), " clId: ", cl2.get("clId"), " clPe: ", cl2.get("clPe"), " clCost:", cl2.get("clCost"));
		this.placement.replace(cl1.get("clId"), cl1);
		this.placement.replace(cl2.get("clId"), cl2);

	}

	public boolean hasSameCloudletSize(int reference, int find) {
		HashMap cl1_ref = (HashMap) this.placement.get(reference);
		HashMap cl2_find = (HashMap) this.placement.get(find);

		if (cl1_ref.get("clPe") == cl2_find.get("clPe")) {
			return true;
		} else {
			return false;
		}
	}

	public Solution copy() {
		return this;
	}
}
