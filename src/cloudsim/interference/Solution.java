package cloudsim.interference;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.cloudbus.cloudsim.Log;

public class Solution implements Cloneable {

	private HashMap placement = new HashMap<Integer, HashMap<String, Double>>();
	private int ttime;
	private int start;
	private int end;
	private int nHosts;
	private int nMig;

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
	
	public void updateCloudletInterferenceCost(int cloudlet, double cost) {
		HashMap cl =  (HashMap) this.placement.get(cloudlet);
	
		
		//Log.printLine(" ===== "+cloudlet + "    "+ cost);
		cl.replace("clCost", cost);
		
		this.placement.replace(cloudlet, cl);
		
		
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
		int nHosts = countHosts();
		double totalCost = 0;

		for (int i = 1; i <= nHosts; i++) {
			totalCost += getCostFromHost(i);

			//Log.printLine("AQUI - host: "+i+ " cost: "+ getCostFromHost(i));
		}
		//Log.printLine(totalCost);
		return (totalCost * (end - start)) / ttime;

	}

	public double getCostFromHost(int host) {
		double hostCost = 1;
		HashMap cloudlet = new HashMap<String, Double>();

		for (int i = 1; i <= getSize(); i++) {
			cloudlet = (HashMap) this.placement.get(i);
			// if this cloudlet/container is the only one ruuning inside a given Host,
			// there will be no interference incidence, hence its interference cost is 1
			if ((double) cloudlet.get("hostId") == host && !runninginOnlyOneHost(i)) {
				hostCost *= getCostFromCloudlet(i) / (getCloudRes(i) / getHostRes(i));
				// Log.printConcatLine(getCostFromCloudlet(i), " - ",getCloudRes(i), " -
				// ",getHostRes(i) );
			}
		}

		return hostCost == 1 ? 0 : hostCost;

	}

	private int countHosts() {
		int count = 0;

		HashMap cloudlet = new HashMap<String, Double>();
		ArrayList<Double> hosts = new ArrayList<Double>();

		for (int i = 1; i <= getSize(); i++) {
			cloudlet = (HashMap) this.placement.get(i);
			if (!hosts.contains((double) cloudlet.get("hostId"))) {
				hosts.add((double) cloudlet.get("hostId"));
				count++;
			}
		}

		// Log.printLine(count);
		return count;

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

		// Log.printLine("AQUI ======= ID: "+ clId);
		int count = 0;
		double hostId = 0;
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(clId);
		hostId = (double) cloudlet.get("hostId");

		for (int i = 1; i <= this.placement.size(); i++) {
			cloudlet = (HashMap) this.placement.get(i);

			// Log.printLine((double) cloudlet.get("hostId") + " " + hostId + " "+ i + " "+
			// this.placement.size());
			if ((double) cloudlet.get("hostId") == hostId) {
				count++;
			}
		}
		// Log.printLine("AQUI");

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

		aux.put("hostId", cl1.get("hostId"));
		aux.put("hostPe", cl1.get("hostPe"));

		cl1.replace("hostId", cl2.get("hostId"));
		cl1.replace("hostPe", cl2.get("hostPe"));

		cl2.replace("hostId", aux.get("hostId"));
		cl2.replace("hostPe", aux.get("hostPe"));

		this.placement.replace(cl1.get("clId"), cl1);
		this.placement.replace(cl2.get("clId"), cl2);

	}

	public boolean hasSameCloudletSize(int reference, int find) {
		HashMap cl1_ref = (HashMap) this.placement.get(reference);
		HashMap cl2_find = (HashMap) this.placement.get(find);

		if (cl1_ref.get("clPe") == cl2_find.get("clPe")) {
			// Log.printLine(cl1_ref.get("clPe") + " " + cl2_find.get("clPe"));
			return true;
		} else {
			return false;
		}
	}

	public Solution copy() {
		Solution copy = new Solution();
		for (int i = 1; i <= this.placement.size(); i++) {
			HashMap cloudlet = (HashMap) this.placement.get(i);
			copy.addCloudletToSolution((double) cloudlet.get("hostId"), (double) cloudlet.get("hostPe"),
					(double) cloudlet.get("clId"), (double) cloudlet.get("clPe"), (double) cloudlet.get("clCost"));
			copy.setAdditionalParameters(this.getStart(), this.getEnd(), this.getTtime());

		}
		return copy;
	}

	public void print() {
		Log.printConcatLine("\n\n======================================");
		Log.printConcatLine("Cloudlet ", "Host ", " Hpe ", " Cpe ", "CloudletCost");
		for (int i = 1; i <= this.placement.size(); i++) {
			HashMap cloudlet = (HashMap) this.placement.get(i);

			Log.printConcatLine("   ", cloudlet.get("clId"), "   ", cloudlet.get("hostId"), "  ",
					cloudlet.get("hostPe"), "  ", cloudlet.get("clPe"), "    ",
					String.format("%.2f", cloudlet.get("clCost")));

		}
		Log.printConcatLine("======================================\n\n");
	}

	public void printPlacement() {
		Log.printConcatLine("\n\n======================================");

		for (int i = 1; i <= this.placement.size(); i++) {
			HashMap cloudlet = (HashMap) this.placement.get(i);

			Log.printConcatLine(cloudlet.get("clId"), " - ", cloudlet.get("hostId"));

		}
		Log.printConcatLine("======================================\n\n");
	}

	public int getHostWithHighestCost() {
		double higherCost = 0;
		int host = 0, hostNumber = 0;
		hostNumber = countHosts();
		for (int i = 1; i <= hostNumber; i++) {
			if (getCostFromHost(i) > higherCost) {
				higherCost = getCostFromHost(i);
				host = i;
			}
		}

		return host;
	}

	public int getHostWithLowerCost() {
		double lowerCost = Double.MAX_VALUE;
		int host = 0, hostNumber = 0;
		hostNumber = countHosts();
		for (int i = 1; i <= hostNumber; i++) {
			if (getCostFromHost(i) < lowerCost) {
				lowerCost = getCostFromHost(i);
				host = i;
			}
		}

		return host;
	}

	public int randomCloudletInHost(int min, int max, int host) {
		int cloudlet = ThreadLocalRandom.current().nextInt(min, max + 1);
		while (!isInHost(cloudlet, host)) {
			cloudlet = ThreadLocalRandom.current().nextInt(min, max + 1);
		}

		return cloudlet;
	}

	private boolean isInHost(int cloudletN, int hostN) {
		double hostId = 0;
		
		HashMap cloudlet = new HashMap<String, Double>();
		cloudlet = (HashMap) this.placement.get(cloudletN);
		
		hostId = (double) cloudlet.get("hostId");

		if (hostId == (double)hostN) {
			return true;
		} else {
			return false;
		}
	}

	private int getTtime() {
		return ttime;
	}

	private int getStart() {
		return start;
	}

	private int getEnd() {
		return end;
	}
	
	public HashMap getPlacement() {
		
		return this.placement;
	}
	
	public int getNumberOfMigrations(Solution solutionAux) {
		int cont =0;
		
		for(int i = 1; i<this.placement.size()+1;i++) {
			HashMap cloudletAux = (HashMap) solutionAux.getPlacement().get(i);
			HashMap cloudlet = (HashMap) this.placement.get(i);
			
			
			if(!cloudlet.get("hostId").equals(cloudletAux.get("hostId"))) {
				cont++;
			}
		}
		return cont;
	}
}
