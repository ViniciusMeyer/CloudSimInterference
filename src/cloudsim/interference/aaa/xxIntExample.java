package cloudsim.interference.aaa;

/*
 * Title:        ContainerCloudSimExample1 Toolkit
 * Description:  ContainerCloudSimExample1 (containerized cloud simulation) Toolkit for Modeling and Simulation
 *               of Containerized Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import org.cloudbus.cloudsim.Cloudlet;
import cloudsim.interference.util;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.core.CloudSim;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

//adjusts
import cloudsim.interference.*;
import cloudsim.interference.cloudlet.IntContainerCloudlet;
import cloudsim.interference.cloudlet.IntContainerCloudletSchedulerDynamicWorkload;
import cloudsim.interference.datacenter.IntContainerDataCenter;
import cloudsim.interference.datacenter.IntContainerDataCenterBroker;
import cloudsim.interference.datacenter.IntContainerDataCenterCharacteristics;
import cloudsim.interference.pe.IntContainerPe;
import cloudsim.interference.pe.IntContainerPeProvisionerSimple;
import cloudsim.interference.vm.IntContainerVm;
import cloudsim.interference.vm.IntContainerVmAllocationPolicy;
import cloudsim.interference.vm.IntContainerVmAllocationPolicyMigrationAbstractHostSelection;
import cloudsim.interference.vm.IntContainerVmPe;
import cloudsim.interference.vm.IntContainerVmPeProvisionerSimple;
import cloudsim.interference.vm.IntContainerVmSchedulerTimeShared;
import cloudsim.interference.vm.IntContainerVmSelectionPolicy;
import cloudsim.interference.vm.IntContainerVmSelectionPolicySimple;

/**
 * A simple example showing how to create a data center with one host, one VM,
 * one container and run one cloudlet on it.
 */
public class xxIntExample {

	/**
	 * Simulation parameters including the interval and limit
	 */
	static final boolean ENABLE_OUTPUT = true;
	static final boolean OUTPUT_CSV = false;
	static final double SCHEDULING_INTERVAL = 1.0D;
	static final double SIMULATION_LIMIT = 99999999999.0D;// 601.0D;
	/**
	 * Cloudlet specs
	 */
	static final int CLOUDLET_LENGTH = 60000;
	static final int CLOUDLET_PES = 1;

	/**
	 * Startup delay for VMs and the containers are mentioned here.
	 */
	static final double CONTAINER_STARTTUP_DELAY = 0.4;// the amount is in seconds
	static final double VM_STARTTUP_DELAY = 100;// the amoun is in seconds

	/**
	 * The available virtual machine types along with the specs.
	 */

	static final int VM_TYPES = 1;
	static final double[] VM_MIPS = new double[] { 100 };
	static final int[] VM_PES = new int[] { 12 };

	/**
	 * The available types of container along with the specs.
	 */

	static final int CONTAINER_TYPES = 1;
	static final int[] CONTAINER_MIPS = new int[] { 100 };
	static final int[] CONTAINER_PES = new int[] { 3 };

	/**
	 * The available types of hosts along with the specs.
	 */

	static final int HOST_TYPES = 1;
	static final int[] HOST_MIPS = new int[] { 100 };
	static final int[] HOST_PES = new int[] { 12 };

	/**
	 *
	 *
	 * The population of hosts, containers, and VMs are specified. The containers
	 * population is equal to the cloudlets population as each cloudlet is mapped to
	 * each container. However, depending on the simualtion scenario the container's
	 * population can also be different from cloudlet's population.
	 */

	static final int NUMBER_HOSTS =3;
	static final int NUMBER_VMS = 3;
	static final int NUMBER_CLOUDLETS = 12;

	/**
	 * The cloudlet list.
	 */
	private static List<IntContainerCloudlet> cloudletList;

	/**
	 * The vmlist.
	 */
	private static List<IntContainerVm> vmList;

	/**
	 * The vmlist.
	 */

	private static List<IntContainer> containerList;

	/**
	 * The hostList.
	 */

	private static List<IntContainerHost> hostList;

	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */

	public static void main(String[] args) {

		Log.printLine("Starting Interference ContainerCloudSim...");

		try {
			/**
			 * number of cloud Users
			 */
			int num_user = 1;
			/**
			 * The fields of calender have been initialized with the current date and time.
			 */
			Calendar calendar = Calendar.getInstance();
			/**
			 * Deactivating the event tracing
			 */
			boolean trace_flag = false;
			/**
			 * 1- Like CloudSim the first step is initializing the CloudSim Package before
			 * creating any entities.
			 *
			 */

			CloudSim.init(num_user, calendar, trace_flag);
			/**
			 * 2- Defining the container allocation Policy. This policy determines how
			 * Containers are allocated to VMs in the data center.
			 *
			 */

			// ContainerAllocationPolicy containerAllocationPolicy = new
			// PowerContainerAllocationPolicySimple();
			IntContainerAllocationPolicy containerAllocationPolicy = new IntContainerAllocationPolicySimple();

			/**
			 * 3- Defining the VM selection Policy. This policy determines which VMs should
			 * be selected for migration when a host is identified as over-loaded.
			 *
			 */
			// PowerContainerVmSelectionPolicy vmSelectionPolicy = new
			// PowerContainerVmSelectionPolicyMaximumUsage();
			IntContainerVmSelectionPolicy vmSelectionPolicy = new IntContainerVmSelectionPolicySimple();

			/**
			 * 4- Defining the host selection Policy. This policy determines which hosts
			 * should be selected as migration destination.
			 *
			 */
			// HostSelectionPolicy hostSelectionPolicy = new HostSelectionPolicyFirstFit();
			IntHostSelectionPolicy hostSelectionPolicy = new IntHostSelectionPolicyFirstFit();
			/**
			 * 5- Defining the thresholds for selecting the under-utilized and over-utilized
			 * hosts.
			 */

			double overUtilizationThreshold = 0.80;
			double underUtilizationThreshold = 0.70;
			/**
			 * 6- The host list is created considering the number of hosts, and host types
			 * which are specified in the {@link ConstantsExamples}.
			 */
			hostList = new ArrayList<IntContainerHost>();
			hostList = createHostList(NUMBER_HOSTS);
			cloudletList = new ArrayList<IntContainerCloudlet>();
			vmList = new ArrayList<IntContainerVm>();

			/**
			 * 7- The container allocation policy which defines the allocation of VMs to
			 * containers.
			 */

			IntContainerVmAllocationPolicy vmAllocationPolicy = new IntContainerVmAllocationPolicyMigrationAbstractHostSelection(
					hostList, vmSelectionPolicy, hostSelectionPolicy, overUtilizationThreshold,
					underUtilizationThreshold);

			/**
			 * 8- The overbooking factor for allocating containers to VMs. This factor is
			 * used by the broker for the allocation process.
			 */

			IntContainerDataCenterBroker broker = createBroker("Broker");
			int brokerId = broker.getId();
			/**
			 * 9- Creating the cloudlet, container and VM lists for submitting to the
			 * broker.
			 */
			cloudletList = createIntContainerCloudletList(brokerId, NUMBER_CLOUDLETS);

			// imprimir um trace de interferencia
			// for(int o=0; o<cloudletList.get(0).interfMetrics.getIntLength();o++){
			// for(int h=0; h<7; h++){
			// Log.print(cloudletList.get(0).interfMetrics.getIntByLine(o)[h]+" ");
			// }
			// Log.print("\n");
			// }

			containerList = createContainerList(brokerId, NUMBER_CLOUDLETS);
			vmList = createVmList(brokerId, NUMBER_VMS);
			/**
			 * 10- The address for logging the statistics of the VMs, containers in the data
			 * center.
			 */
			String logAddress = "~/Results";

			@SuppressWarnings("unused")
			IntContainerDataCenter e = (IntContainerDataCenter) createDatacenter("datacenter",
					IntContainerDataCenter.class, hostList, vmAllocationPolicy, containerAllocationPolicy, "AA",
					SCHEDULING_INTERVAL, logAddress, VM_STARTTUP_DELAY, CONTAINER_STARTTUP_DELAY, cloudletList);

			// createDatacenter("datacenter", IntContainerDataCenter.class, hostList,
			// vmAllocationPolicy, containerAllocationPolicy, "aa", SCHEDULING_INTERVAL,
			// logAddress, VM_STARTTUP_DELAY, CONTAINER_STARTTUP_DELAY);

			/**
			 * 11- Submitting the cloudlet's , container's , and VM's lists to the broker.
			 */
			// Log.printLine("======" +containerList.size()+" ====== "+cloudletList.size());
			broker.submitCloudletList(cloudletList.subList(0, containerList.size()));
			broker.submitContainerList(containerList);
			broker.submitVmList(vmList);

			/**
			 * 12- Determining the simulation termination time according to the cloudlet's
			 * workload.
			 */

			CloudSim.terminateSimulation(SIMULATION_LIMIT);

			/**
			 * 13- Starting the simualtion.
			 */
			CloudSim.startSimulation();
			/**
			 * 14- Stopping the simualtion.
			 */
			CloudSim.stopSimulation();
			/**
			 * 15- Printing the results when the simulation is finished.
			 */
			List<IntContainerCloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);

			Log.printLine("ContainerCloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * It creates a specific name for the experiment which is used for creating the
	 * Log address folder.
	 */

	private static String getExperimentName(String... args) {
		StringBuilder experimentName = new StringBuilder();

		for (int i = 0; i < args.length; ++i) {
			if (!args[i].isEmpty()) {
				if (i != 0) {
					experimentName.append("_");
				}

				experimentName.append(args[i]);
			}
		}

		return experimentName.toString();
	}

	/**
	 * Creates the broker.
	 *
	 * @param overBookingFactor
	 * @return the datacenter broker
	 */
	private static IntContainerDataCenterBroker createBroker(String name) {

		IntContainerDataCenterBroker broker = null;

		try {
			broker = new IntContainerDataCenterBroker(name, 0); // original overBookingFactor
		} catch (Exception var2) {
			var2.printStackTrace();
			System.exit(0);
		}

		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<IntContainerCloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatusString() == "Success") {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}

	/**
	 * Create the Virtual machines and add them to the list
	 *
	 * @param brokerId
	 * @param containerVmsNumber
	 */
	private static ArrayList<IntContainerVm> createVmList(int brokerId, int containerVmsNumber) {
		ArrayList<IntContainerVm> containerVms = new ArrayList<IntContainerVm>();

		for (int i = 0; i < containerVmsNumber; ++i) {
			ArrayList<IntContainerPe> peList = new ArrayList<IntContainerPe>();
			// int vmType = i / (int) Math.ceil((double) containerVmsNumber / 4.0D);
			int vmType = 0;
			for (int j = 0; j < VM_PES[vmType]; ++j) {
				peList.add(new IntContainerPe(j, new IntContainerPeProvisionerSimple((double) VM_MIPS[vmType])));
			}
			containerVms.add(new IntContainerVm(IntIDs.pollId(IntContainerVm.class), brokerId, (double) VM_MIPS[vmType],
					new IntContainerSchedulerTimeShared(peList), peList, SCHEDULING_INTERVAL));
			// SCHEDULING_INTERVAL

		}

		// imprimir Vms criadas
		for (int i = 0; i < containerVmsNumber; ++i) {
//		Log.printLine("Vm"+containerVms.get(i).getId() +" toal mips "+containerVms.get(i).getTotalMips());
		}

		return containerVms;
	}

	/**
	 * Create the host list considering the specs listed in the
	 * {@link ConstantsInterference}.
	 *
	 * @param hostsNumber
	 * @return
	 */

	public static List<IntContainerHost> createHostList(int hostsNumber) {
		ArrayList<IntContainerHost> hostList = new ArrayList<IntContainerHost>();
		for (int i = 0; i < hostsNumber; ++i) {
			// int hostType = i / (int) Math.ceil((double) hostsNumber / 3.0D); /// para
			// deixar um tipo só, homogeneo
			int hostType = 0;
			ArrayList<IntContainerVmPe> peList = new ArrayList<IntContainerVmPe>();
			for (int j = 0; j < HOST_PES[hostType]; ++j) {
				peList.add(
						new IntContainerVmPe(j, new IntContainerVmPeProvisionerSimple((double) HOST_MIPS[hostType])));
			}

			hostList.add(new IntContainerHost(IntIDs.pollId(IntContainerHost.class), peList,
					new IntContainerVmSchedulerTimeShared(peList)));
		}

		return hostList;
	}

	/**
	 * Create the data center
	 *
	 * @param name
	 * @param datacenterClass
	 * @param hostList
	 * @param vmAllocationPolicy
	 * @param containerAllocationPolicy
	 * @param experimentName
	 * @param logAddress
	 * @return
	 * @throws Exception
	 */

	public static IntContainerDataCenter createDatacenter(String name,
			Class<? extends IntContainerDataCenter> datacenterClass, List<IntContainerHost> hostList,
			IntContainerVmAllocationPolicy vmAllocationPolicy, IntContainerAllocationPolicy containerAllocationPolicy,
			String experimentName, double schedulingInterval, String logAddress, double VMStartupDelay,
			double ContainerStartupDelay, List<IntContainerCloudlet> cloudletList) throws Exception {
		String arch = "x86";
		String os = "Linux";
		String vmm = "Xen";
		double time_zone = 10.0D;
		double cost = 3.0D;
		double costPerMem = 0.05D;
		double costPerStorage = 0.001D;
		double costPerBw = 0.0D;
		IntContainerDataCenterCharacteristics characteristics = new IntContainerDataCenterCharacteristics(arch, os, vmm,
				hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		IntContainerDataCenter datacenter = new IntContainerDataCenter(name, characteristics, vmAllocationPolicy,
				containerAllocationPolicy, new LinkedList<Storage>(), schedulingInterval, experimentName, logAddress,
				VMStartupDelay, ContainerStartupDelay, cloudletList);

		return datacenter;
	}

	/**
	 * create the containers for hosting the cloudlets and binding them together.
	 *
	 * @param brokerId
	 * @param containersNumber
	 * @return
	 */

	public static List<IntContainer> createContainerList(int brokerId, int containersNumber) {
		ArrayList<IntContainer> containers = new ArrayList<IntContainer>();

		for (int i = 0; i < containersNumber; ++i) {
			int containerType = 0; // i / (int) Math.ceil((double) containersNumber / CONTAINER_TYPES);

			containers.add(new IntContainer(IntIDs.pollId(IntContainer.class), brokerId,
					(double) CONTAINER_MIPS[containerType], CONTAINER_PES[containerType], "Xen",
					new IntContainerCloudletSchedulerDynamicWorkload(CONTAINER_MIPS[containerType],
							CONTAINER_PES[containerType]),
					SCHEDULING_INTERVAL));
		}

		return containers;
	}

	/**
	 * Creating the cloudlet list that are going to run on containers
	 *
	 * @param brokerId
	 * @param numberOfCloudlets
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<IntContainerCloudlet> createIntContainerCloudletList(int brokerId, int numberOfCloudlets)
			throws FileNotFoundException {
		// pasta com os traces de interferência
		// String inputFolderName = System.getProperty("user.dir") +
		// "/src/resources/workload/interference";//
		// ContainerCloudSimExampleInterference.class.getClassLoader().getResource("workload/interference").getPath();
		String inputFolderName = xxIntExample.class.getClassLoader().getResource("resources/workload/interference")
				.getPath();

		// String inputFolderName =
		// "/home/vinicius/git/CloudSimInterference/src/resources/workload/interference";
		// Log.printLine("========"+inputFolderName);
		ArrayList<IntContainerCloudlet> cloudletList = new ArrayList<IntContainerCloudlet>(); // criar um ArrayList de
																								// IntContainerCloudlet
		int count = 1;
		long fileSize = 300L;
		long outputSize = 300L;
		// UtilizationModelNull utilizationModelNull = new UtilizationModelNull(); //
		// Modo NULO (simples)
		UtilizationModelFull utilizationModelFull = new UtilizationModelFull();
		// UtilizationModelPlanetLabInMemory = teste new
		// UtilizationModelPlanetLabInMemory(inputPath, schedulingInterval);

		java.io.File inputFolder1 = new java.io.File(inputFolderName);
		java.io.File[] files1 = inputFolder1.listFiles();
		// Log.printLine("======== "+files1.length);
		int createdCloudlets = 0;
		for (java.io.File aFiles1 : files1) {
			java.io.File inputFolder = new java.io.File(aFiles1.toString());
			java.io.File[] files = inputFolder.listFiles();
			for (int i = 0; i < files.length; ++i) {
				if (createdCloudlets < numberOfCloudlets) {
					IntContainerCloudlet cloudlet = null;

					try {
						cloudlet = new IntContainerCloudlet(IntIDs.pollId(IntContainerCloudlet.class), CLOUDLET_LENGTH,
								1, fileSize, outputSize, utilizationModelFull, utilizationModelFull,
								utilizationModelFull, new Interference(files[i].getAbsolutePath()));
						Log.printLine(
								"Cloudlet " + count + " <- interf. traces: \"" + files[i].getAbsolutePath() + "\"");
						count++;
//						System.out.println(files[i].getAbsolutePath().toString());
//                                new UtilizationModelPlanetLabInMemoryExtended(files[i].getAbsolutePath(), 300.0D),

					} catch (Exception var13) {
						var13.printStackTrace();
						System.exit(0);
					}

					cloudlet.setUserId(brokerId);
					cloudletList.add(cloudlet);
					createdCloudlets += 1;
				} else {

					return cloudletList;
				}

			}

		}
		return cloudletList;
	}

}
