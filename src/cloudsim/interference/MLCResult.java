package cloudsim.interference;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Log;

public class MLCResult {

	// String[][] result = new String[2][5];
	Map<String, String> result = new HashMap<String, String>();

	public MLCResult(Map<String, String> result) {
		this.result = result;
	}

	public MLCResult() {

	}

	public int getNumberOfResources() {
		return this.result.size();
	}

	public String getCpu() {
		return this.result.get("cpu");
	}
	
	public String getNetwork() {
		return this.result.get("net");
	}
	
	public String getMemory() {
		return this.result.get("mem");
	}
	
	public String getCache() {
		return this.result.get("cache");
	}
	
	public String getDisk() {
		return this.result.get("disk");
	}

	public double getCloudletCost() {
		double cost = 0;
		cost = Degradation.getCpu(getCpu()) * Degradation.getMem(getMemory()) * Degradation.getDisk(getDisk()) * Degradation.getCache(getCache()) * Degradation.getNet(getNetwork());
		//Log.printLine("CPU: "+getCpu()+" Mem: "+getMemory()+" Disk: "+getDisk()+" Cache: "+getCache()+" Net: "+getNetwork());			
		
		
		return cost>1 ? cost : 1;
		
	}
	
	/*
	 * public String getCpu() { for (int i = 0; i < getNumberOfResources(); i++) {
	 * if (this.result[0][i].equals("cpu")) {
	 * 
	 * return this.result[1][i]; } } return "null"; }
	 * 
	 * public String getMem() { for (int i = 0; i < getNumberOfResources(); i++) {
	 * if (this.result[0][i].equals("mem")) {
	 * 
	 * return this.result[1][i]; } } return "null"; }
	 * 
	 * public String getDisk() { for (int i = 0; i < getNumberOfResources(); i++) {
	 * if (this.result[0][i].equals("disk")) {
	 * 
	 * return this.result[1][i]; } } return "null"; }
	 * 
	 * public String getNet() { for (int i = 0; i < getNumberOfResources(); i++) {
	 * if (this.result[0][i].equals("net")) {
	 * 
	 * return this.result[1][i]; } } return "null"; }
	 * 
	 * public String getCache() { for (int i = 0; i < getNumberOfResources(); i++) {
	 * if (this.result[0][i].equals("cache")) {
	 * 
	 * return this.result[1][i]; } } return "null"; }
	 */
}
