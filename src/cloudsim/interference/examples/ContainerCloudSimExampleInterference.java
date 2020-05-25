package cloudsim.interference.examples;

/*
 * Title:        ContainerCloudSimExample1 Toolkit
 * Description:  ContainerCloudSimExample1 (containerized cloud simulation) Toolkit for Modeling and Simulation
 *               of Containerized Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerPe;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerRamProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.CotainerPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPe;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmRamProvisionerSimple;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicy;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicyFirstFit;
import org.cloudbus.cloudsim.container.resourceAllocatorMigrationEnabled.PowerContainerVmAllocationPolicyMigrationAbstractHostSelection;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerAllocationPolicySimple;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.container.schedulers.ContainerSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.schedulers.ContainerVmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.utils.IDs;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicy;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicyMaximumUsage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G3PentiumD930;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3550XeonX5670;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

//adjusts
import cloudsim.interference.*;

/**
 * A simple example showing how to create a data center with one host, one VM,
 * one container and run one cloudlet on it.
 */
public class ContainerCloudSimExampleInterference {

	/**
	 * Simulation parameters including the interval and limit
	 */
	static final boolean ENABLE_OUTPUT = true;
	static final boolean OUTPUT_CSV = false;
	static final double SCHEDULING_INTERVAL = 300.0D;
	static final double SIMULATION_LIMIT = 87400.0D;
	/**
	 * Cloudlet specs
	 */
	static final int CLOUDLET_LENGTH = 30;
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
	static final double[] VM_MIPS = new double[] { 400 };
	static final int[] VM_PES = new int[] { 2 };
	static final float[] VM_RAM = new float[] { (float) 1024 };// **MB*
	static final int VM_BW = 100000;
	static final int VM_SIZE = 2500;

	/**
	 * The available types of container along with the specs.
	 */

	static final int CONTAINER_TYPES = 1;
	static final int[] CONTAINER_MIPS = new int[] { 100 };
	static final int[] CONTAINER_PES = new int[] { 1 };
	static final int[] CONTAINER_RAM = new int[] { 256 };
	static final int CONTAINER_BW = 2500;

	/**
	 * The available types of hosts along with the specs.
	 */

	static final int HOST_TYPES = 1;
	static final int[] HOST_MIPS = new int[] { 400 };
	static final int[] HOST_PES = new int[] { 2 };
	static final int[] HOST_RAM = new int[] { 2048 };
	static final int HOST_BW = 1000000;
	static final int HOST_STORAGE = 1000000;
	static final PowerModel[] HOST_POWER = new PowerModel[] { new PowerModelSpecPowerHpProLiantMl110G3PentiumD930() };

	/**
	 * The population of hosts, containers, and VMs are specified. The containers
	 * population is equal to the cloudlets population as each cloudlet is mapped to
	 * each container. However, depending on the simualtion scenario the container's
	 * population can also be different from cloudlet's population.
	 */

	static final int NUMBER_HOSTS = 1;
	static final int NUMBER_VMS = 1;
	static final int NUMBER_CLOUDLETS = 2;

	/**
	 * The cloudlet list.
	 */
	private static List<IntContainerCloudlet> cloudletList;

	/**
	 * The vmlist.
	 */
	private static List<ContainerVm> vmList;

	/**
	 * The vmlist.
	 */

	private static List<Container> containerList;

	/**
	 * The hostList.
	 */

	private static List<ContainerHost> hostList;

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
			ContainerAllocationPolicy containerAllocationPolicy = new IntContainerAllocationPolicySimple();

			/**
			 * 3- Defining the VM selection Policy. This policy determines which VMs should
			 * be selected for migration when a host is identified as over-loaded.
			 *
			 */
			//PowerContainerVmSelectionPolicy vmSelectionPolicy = new PowerContainerVmSelectionPolicyMaximumUsage();
			 IntContainerVmSelectionPolicy vmSelectionPolicy = new IntContainerVmSelectionPolicySimple();

			/**
			 * 4- Defining the host selection Policy. This policy determines which hosts
			 * should be selected as migration destination.
			 *
			 */
			//HostSelectionPolicy hostSelectionPolicy = new HostSelectionPolicyFirstFit();
			 HostSelectionPolicy hostSelectionPolicy = new IntHostSelectionPolicyFirstFit();
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
			hostList = new ArrayList<ContainerHost>();
			hostList = createHostList(NUMBER_HOSTS);
			cloudletList = new ArrayList<IntContainerCloudlet>();
			vmList = new ArrayList<ContainerVm>();
			/**
			 * 7- The container allocation policy which defines the allocation of VMs to
			 * containers.
			 */
			//ContainerVmAllocationPolicy vmAllocationPolicy = new PowerContainerVmAllocationPolicyMigrationAbstractHostSelection(
			//		hostList, vmSelectionPolicy, hostSelectionPolicy, overUtilizationThreshold,
			//		underUtilizationThreshold);
			ContainerVmAllocationPolicy vmAllocationPolicy = new IntContainerVmAllocationPolicyMigrationAbstractHostSelection(
					hostList, vmSelectionPolicy, hostSelectionPolicy, overUtilizationThreshold,
					underUtilizationThreshold);
			/**
			 * 8- The overbooking factor for allocating containers to VMs. This factor is
			 * used by the broker for the allocation process.
			 */
			int overBookingFactor = 80;
			ContainerDatacenterBroker broker = createBroker(overBookingFactor);
			int brokerId = broker.getId();
			/**
			 * 9- Creating the cloudlet, container and VM lists for submitting to the
			 * broker.
			 */
			cloudletList = createIntContainerCloudletList(brokerId, NUMBER_CLOUDLETS);
			containerList = createContainerList(brokerId, NUMBER_CLOUDLETS);
			vmList = createVmList(brokerId, NUMBER_VMS);
			/**
			 * 10- The address for logging the statistics of the VMs, containers in the data
			 * center.
			 */
			String logAddress = "~/Results";

			@SuppressWarnings("unused")
			PowerContainerDatacenter e = (PowerContainerDatacenter) createDatacenter("datacenter",
					PowerContainerDatacenterCM.class, hostList, vmAllocationPolicy, containerAllocationPolicy,
					getExperimentName("ContainerCloudSimExample-1", String.valueOf(overBookingFactor)),
					SCHEDULING_INTERVAL, logAddress, VM_STARTTUP_DELAY, CONTAINER_STARTTUP_DELAY);

			/**
			 * 11- Submitting the cloudlet's , container's , and VM's lists to the broker.
			 */
			broker.submitCloudletList(cloudletList.subList(0, containerList.size()));
			broker.submitContainerList(containerList);
			broker.submitVmList(vmList);
			/**
			 * 12- Determining the simulation termination time according to the cloudlet's
			 * workload.
			 */
			CloudSim.terminateSimulation(86400.00);
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
			List<ContainerCloudlet> newList = broker.getCloudletReceivedList();
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
	private static ContainerDatacenterBroker createBroker(int overBookingFactor) {

		ContainerDatacenterBroker broker = null;

		try {
			broker = new ContainerDatacenterBroker("Broker", overBookingFactor);
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
	private static void printCloudletList(List<ContainerCloudlet> list) {
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
	private static ArrayList<ContainerVm> createVmList(int brokerId, int containerVmsNumber) {
		ArrayList<ContainerVm> containerVms = new ArrayList<ContainerVm>();

		for (int i = 0; i < containerVmsNumber; ++i) {
			ArrayList<ContainerPe> peList = new ArrayList<ContainerPe>();
			int vmType = i / (int) Math.ceil((double) containerVmsNumber / 4.0D);
			for (int j = 0; j < VM_PES[vmType]; ++j) {
				peList.add(new ContainerPe(j, new CotainerPeProvisionerSimple((double) VM_MIPS[vmType])));
			}
			containerVms.add(new PowerContainerVm(IDs.pollId(ContainerVm.class), brokerId, (double) VM_MIPS[vmType],
					(float) VM_RAM[vmType], VM_BW, VM_SIZE, "Xen",
					new ContainerSchedulerTimeSharedOverSubscription(peList),
					new ContainerRamProvisionerSimple(VM_RAM[vmType]), new ContainerBwProvisionerSimple(VM_BW), peList,
					SCHEDULING_INTERVAL));

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

	public static List<ContainerHost> createHostList(int hostsNumber) {
		ArrayList<ContainerHost> hostList = new ArrayList<ContainerHost>();
		for (int i = 0; i < hostsNumber; ++i) {
			int hostType = i / (int) Math.ceil((double) hostsNumber / 3.0D);
			ArrayList<ContainerVmPe> peList = new ArrayList<ContainerVmPe>();
			for (int j = 0; j < HOST_PES[hostType]; ++j) {
				peList.add(new ContainerVmPe(j, new ContainerVmPeProvisionerSimple((double) HOST_MIPS[hostType])));
			}

			hostList.add(new PowerContainerHostUtilizationHistory(IDs.pollId(ContainerHost.class),
					new ContainerVmRamProvisionerSimple(HOST_RAM[hostType]),
					new ContainerVmBwProvisionerSimple(1000000L), 1000000L, peList,
					new ContainerVmSchedulerTimeSharedOverSubscription(peList), HOST_POWER[hostType]));
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

	public static ContainerDatacenter createDatacenter(String name,
			Class<? extends ContainerDatacenter> datacenterClass, List<ContainerHost> hostList,
			ContainerVmAllocationPolicy vmAllocationPolicy, ContainerAllocationPolicy containerAllocationPolicy,
			String experimentName, double schedulingInterval, String logAddress, double VMStartupDelay,
			double ContainerStartupDelay) throws Exception {
		String arch = "x86";
		String os = "Linux";
		String vmm = "Xen";
		double time_zone = 10.0D;
		double cost = 3.0D;
		double costPerMem = 0.05D;
		double costPerStorage = 0.001D;
		double costPerBw = 0.0D;
		ContainerDatacenterCharacteristics characteristics = new ContainerDatacenterCharacteristics(arch, os, vmm,
				hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		ContainerDatacenter datacenter = new PowerContainerDatacenterCM(name, characteristics, vmAllocationPolicy,
				containerAllocationPolicy, new LinkedList<Storage>(), schedulingInterval, experimentName, logAddress,
				VMStartupDelay, ContainerStartupDelay);

		return datacenter;
	}

	/**
	 * create the containers for hosting the cloudlets and binding them together.
	 *
	 * @param brokerId
	 * @param containersNumber
	 * @return
	 */

	public static List<Container> createContainerList(int brokerId, int containersNumber) {
		ArrayList<Container> containers = new ArrayList<Container>();

		for (int i = 0; i < containersNumber; ++i) {
			int containerType = i / (int) Math.ceil((double) containersNumber / CONTAINER_TYPES);

			containers.add(new PowerContainer(IDs.pollId(Container.class), brokerId,
					(double) CONTAINER_MIPS[containerType], CONTAINER_PES[containerType], CONTAINER_RAM[containerType],
					CONTAINER_BW, 0L, "Xen", new ContainerCloudletSchedulerDynamicWorkload(
							CONTAINER_MIPS[containerType], CONTAINER_PES[containerType]),
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
		String inputFolderName = System.getProperty("user.dir")+"/src/resources/workload/interference";//ContainerCloudSimExampleInterference.class.getClassLoader().getResource("workload/interference").getPath();
		System.out.println(inputFolderName);
		ArrayList<IntContainerCloudlet> cloudletList = new ArrayList<IntContainerCloudlet>();
		long fileSize = 300L;
		long outputSize = 300L;
		UtilizationModelNull utilizationModelNull = new UtilizationModelNull();
		java.io.File inputFolder1 = new java.io.File(inputFolderName);
		java.io.File[] files1 = inputFolder1.listFiles();
		int createdCloudlets = 0;
		for (java.io.File aFiles1 : files1) {
			java.io.File inputFolder = new java.io.File(aFiles1.toString());
			java.io.File[] files = inputFolder.listFiles();
			for (int i = 0; i < files.length; ++i) {
				if (createdCloudlets < numberOfCloudlets) {
					IntContainerCloudlet cloudlet = null;

					try {
						cloudlet = new IntContainerCloudlet(IDs.pollId(IntContainerCloudlet.class), CLOUDLET_LENGTH, 1,
								fileSize, outputSize, utilizationModelNull, utilizationModelNull, utilizationModelNull,
								new Interference(files[i].getAbsolutePath()));
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
