package cloudsim.interference;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Degradation {

	
	public static HashMap<String, Double> deg_cpu = new HashMap<String, Double>();
	public static HashMap<String, Double> deg_mem = new HashMap<String, Double>();
	public static HashMap<String, Double> deg_disk = new HashMap<String, Double>();
	public static HashMap<String, Double> deg_cache = new HashMap<String, Double>();
	public static HashMap<String, Double> deg_net = new HashMap<String, Double>();

	public static double getCpu(String level) {
		deg_cpu.put("abs", 1.00);
		deg_cpu.put("low", 1.03);
		deg_cpu.put("mod", 1.15);
		deg_cpu.put("hig", 1.33);

		return deg_cpu.get(level);
	}

	public static double getMem(String level) {
		deg_mem.put("abs", 1.00);
		deg_mem.put("low", 1.07);
		deg_mem.put("mod", 1.64);
		deg_mem.put("hig", 1.74);

		return deg_mem.get(level);
	}

	public static double getDisk(String level) {
		deg_disk.put("abs", 1.00);
		deg_disk.put("low", 1.12);
		deg_disk.put("mod", 1.82);
		deg_disk.put("hig", 2.25);

		return deg_disk.get(level);
	}

	public static double getCache(String level) {
		deg_cache.put("abs", 1.00);
		deg_cache.put("low", 1.07);
		deg_cache.put("mod", 1.18);
		deg_cache.put("hig", 1.26);

		return deg_cache.get(level);
	}

	public static double getNet(String level) {
		deg_net.put("abs", 1.00);
		deg_net.put("low", 1.05);
		deg_net.put("mod", 1.32);
		deg_net.put("hig", 1.57);

		return deg_net.get(level);
	}

}
