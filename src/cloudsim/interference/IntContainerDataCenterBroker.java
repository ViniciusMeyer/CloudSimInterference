package cloudsim.interference;

import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.lists.ContainerList;
import org.cloudbus.cloudsim.container.lists.ContainerVmList;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.CloudletList;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by sareh on 15/07/15.
 */

public class IntContainerDataCenterBroker extends SimEntity {

	
	/**
	 * The vm list.
	 */
	protected List<? extends IntContainerVm> vmList;

	/**
	 * The vms created list.
	 */
	protected List<? extends IntContainerVm> vmsCreatedList;
	/**
	 * The containers created list.
	 */
	protected List<? extends IntContainer> containersCreatedList;

	/**
	 * The cloudlet list.
	 */
	protected List<? extends IntContainerCloudlet> cloudletList;
	/**
	 * The container list
	 */

	protected List<? extends IntContainer> containerList;

	/**
	 * The cloudlet submitted list.
	 */
	protected List<? extends IntContainerCloudlet> cloudletSubmittedList;

	/**
	 * The cloudlet received list.
	 */
	protected List<? extends IntContainerCloudlet> cloudletReceivedList;

	/**
	 * The cloudlets submitted.
	 */
	protected int cloudletsSubmitted;

	/**
	 * The vms requested.
	 */
	protected int vmsRequested;

	/**
	 * The vms acks.
	 */
	protected int vmsAcks;
	/**
	 * The containers acks.
	 */
	protected int containersAcks;
	/**
	 * The number of created containers
	 */

	protected int containersCreated;

	/**
	 * The vms destroyed.
	 */
	protected int vmsDestroyed;

	/**
	 * The datacenter ids list.
	 */
	protected List<Integer> datacenterIdsList;

	/**
	 * The datacenter requested ids list.
	 */
	protected List<Integer> datacenterRequestedIdsList;

	/**
	 * The vms to datacenters map.
	 */
	protected Map<Integer, Integer> vmsToDatacentersMap;
	/**
	 * The vms to datacenters map.
	 */
	protected Map<Integer, Integer> containersToDatacentersMap;

	/**
	 * The datacenter characteristics list.
	 */
	protected Map<Integer, IntContainerDataCenterCharacteristics> datacenterCharacteristicsList;

	/**
	 * The datacenter characteristics list.
	 */
	protected double overBookingfactor;

	protected int numberOfCreatedVMs;

	/**
	 * Created a new DatacenterBroker object.
	 *
	 * @param name name to be associated with this entity (as required by Sim_entity
	 *             class from simjava package)
	 * @throws Exception the exception
	 * @pre name != null
	 * @post $none
	 * 
	 */
	public IntContainerDataCenterBroker(String name, double overBookingfactor) throws Exception {
		super(name);
		
		//Log.printLine("\nBROKER: ==== INCIO - CRIAÇÃO OBJETO BROKER =====\n");
		setVmList(new ArrayList<IntContainerVm>());
		setContainerList(new ArrayList<IntContainer>()); // cont
		setVmsCreatedList(new ArrayList<IntContainerVm>());
		setContainersCreatedList(new ArrayList<IntContainer>()); //cont
		setCloudletList(new ArrayList<IntContainerCloudlet>());
		setCloudletSubmittedList(new ArrayList<IntContainerCloudlet>());
		setCloudletReceivedList(new ArrayList<IntContainerCloudlet>());
		cloudletsSubmitted = 0;
		setVmsRequested(0);
		setVmsAcks(0);
		setContainersAcks(0); //cont
		setContainersCreated(0); //cont
		setVmsDestroyed(0);
		//setOverBookingfactor(overBookingfactor); // cont (overbooking)
		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setContainersToDatacentersMap(new HashMap<Integer, Integer>()); // cont
		setDatacenterCharacteristicsList(new HashMap<Integer, IntContainerDataCenterCharacteristics>());
		setNumberOfCreatedVMs(0); //cont
		//Log.printLine("\nBROKER: ==== FIM - CRIAÇÃO OBJETO BROKER =====\n");
	
	}

	/**
	 * This method is used to send to the broker the list with virtual machines that
	 * must be created.
	 *
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends IntContainerVm> list) {
		//Log.printLine("BROKER:submitVmList");
		getVmList().addAll(list);
		
	}

	/**
	 * This method is used to send to the broker the list of cloudlets.
	 *
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitCloudletList(List<? extends IntContainerCloudlet> list) {
		//Log.printLine("BROKER:submitCloudletList");
		getCloudletList().addAll(list);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 *
	 * @param cloudletId ID of the cloudlet being bount to a vm
	 * @param vmId       the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId) {
		//Log.printLine("BROKER:bindCloudletToVm");
		CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
//        Log.printConcatLine("The Vm ID is ",  CloudletList.getById(getCloudletList(), cloudletId).getVmId(), "should be", vmId);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 *
	 * @param cloudletId  ID of the cloudlet being bount to a vm
	 * @param containerId the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToContainer(int cloudletId, int containerId) {
		//Log.printLine("BROKER:bindCloudletToContainer");
		CloudletList.getById(getCloudletList(), cloudletId).setContainerId(containerId);
		
	}

	/**
	 * Processes events available for this Broker.
	 *
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
		//Log.printLine("BROKER:processEvent   ===========   "+CloudSim.clock());
		switch (ev.getTag()) {
		// Resource characteristics request
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
			processResourceCharacteristicsRequest(ev);
			break;
		// Resource characteristics answer // create VMsInDatacenter
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			processResourceCharacteristics(ev);
			break;
		// VM Creation answer
		case CloudSimTags.VM_CREATE_ACK:
			processVmCreate(ev);
			break;
		// New VM Creation answer
		case containerCloudSimTags.VM_NEW_CREATE:
			processNewVmCreate(ev);
			break;
		// A finished cloudlet returned
		case CloudSimTags.CLOUDLET_RETURN:
			processCloudletReturn(ev);
			break;
		// if the simulation finishes
		case CloudSimTags.END_OF_SIMULATION:
			shutdownEntity();
			break;
		case containerCloudSimTags.CONTAINER_CREATE_ACK:
			processContainerCreate(ev);
			break;
		// other unknown tags are processed by this method
		default:
			processOtherEvent(ev);
			break;
		}
	}

	public void processContainerCreate(SimEvent ev) {
		//Log.printLine("BROKER:processContainerCreate");
		int[] data = (int[]) ev.getData();
		int vmId = data[0];
		int containerId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			if (vmId == -1) {
				Log.printConcatLine("Error : Where is the VM");
			} else {
				getContainersToVmsMap().put(containerId, vmId);
				getContainersCreatedList().add(IntContainerList.getById(getContainerList(), containerId));

				// ContainerVm p= ContainerVmList.getById(getVmsCreatedList(), vmId);
				int hostId = IntContainerVmList.getById(getVmsCreatedList(), vmId).getHost().getId();
				Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": The Container #", containerId,
						", is created on Vm #", vmId, ", On Host#", hostId);
				setContainersCreated(getContainersCreated() + 1);
			}
		} else {
			// Container container = ContainerList.getById(getContainerList(), containerId);
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Failed Creation of Container #", containerId);
		}

		incrementContainersAcks();
		if (getContainersAcks() == getContainerList().size()) {
			// Log.print(getContainersCreatedList().size() + " vs asli
			// "+getContainerList().size());
			submitCloudlets();
			getContainerList().clear();
		}

	}

	/**
	 * Process the return of a request for the characteristics of a PowerDatacenter.
	 *
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristics(SimEvent ev) {
		//Log.printLine("BROKER:processResourceCharacteristics");
		IntContainerDataCenterCharacteristics characteristics = (IntContainerDataCenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);
		
		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			
			getDatacenterCharacteristicsList().clear();
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
			createVmsInDatacenter(getDatacenterIdsList().get(0));
			
		}
		
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 *
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		//Log.printLine("BROKER:processResourceCharacteristicsRequest");
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, IntContainerDataCenterCharacteristics>());

	 Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Cloud Resource List received with ", getDatacenterIdsList().size(), " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	protected void processNewVmCreate(SimEvent ev) {
		//Log.printLine("BROKER:processNewVmCreate");
		Map<String, Object> map = (Map<String, Object>) ev.getData();
		int datacenterId = (int) map.get("datacenterID");
		int result = (int) map.get("result");
		IntContainerVm containerVm = (IntContainerVm) map.get("vm");
		int vmId = containerVm.getId();
		if (result == CloudSimTags.TRUE) {
			getVmList().add(containerVm);
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(containerVm);
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId, " has been created in Datacenter #",
					datacenterId, ", Host #", IntContainerVmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId,
					" failed in Datacenter #", datacenterId);
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 *
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
		//Log.printLine("BROKER:processVmCreate");
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];
		
		

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(IntContainerVmList.getById(getVmList(), vmId));
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId, " has been created in Datacenter #",
					datacenterId, ", Host #", IntContainerVmList.getById(getVmsCreatedList(), vmId).getHost().getId());
			setNumberOfCreatedVMs(getNumberOfCreatedVMs() + 1);
		} else {
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId,
					" failed in Datacenter #", datacenterId);
		}

		incrementVmsAcks();
//        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
//        If we have tried creating all of the vms in the data center, we submit the containers.
		if (getVmList().size() == vmsAcks) {

			submitContainers();
		}
//        // all the requested VMs have been created
//        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
//            submitCloudlets();
//        } else {
//            // all the acks received, but some VMs were not created
//            if (getVmsRequested() == getVmsAcks()) {
//                // find id of the next datacenter that has not been tried
//                for (int nextDatacenterId : getDatacenterIdsList()) {
//                    if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
//                        createVmsInDatacenter(nextDatacenterId);
//                        return;
//                    }
//                }
//
//                // all datacenters already queried
//                if (getVmsCreatedList().size() > 0) { // if some vm were created
//                    submitCloudlets();
//                } else { // no vms created. abort
//                    Log.printLine(CloudSim.clock() + ": " + getName()
//                            + ": none of the required VMs could be created. Aborting");
//                    finishExecution();
//                }
//            }
//        }
	
	}

	/**
	 * Process a cloudlet return event.
	 *
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletReturn(SimEvent ev) {
		//Log.printLine("BROKER:processCloudletReturn");
		IntContainerCloudlet cloudlet = (IntContainerCloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Cloudlet ", cloudlet.getCloudletId(), " returned");
		Log.printConcatLine(CloudSim.clock(), ": ", getName(), "The number of finished Cloudlets is:",
				getCloudletReceivedList().size());
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}

	/**
	 * Overrides this method when making a new and different type of Broker. This
	 * method is called by for incoming unknown tags.
	 *
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		//Log.printLine("BROKER:processOtherEvent");
		if (ev == null) {
			Log.printConcatLine(getName(), ".processOtherEvent(): ", "Error - an event is null.");
			return;
		}

		Log.printConcatLine(getName(), ".processOtherEvent(): Error - event unknown by this DatacenterBroker.");
	}

	/**
	 * Create the virtual machines in a datacenter.
	 *
	 * @param datacenterId Id of the chosen PowerDatacenter
	 * @pre $none
	 * @post $none
	 */
	protected void createVmsInDatacenter(int datacenterId) {
		//Log.printLine("BROKER:createVmsInDatacenter");
		// send as much vms as possible for this datacenter before trying the next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (IntContainerVm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(String.format("%s: %s: Trying to Create VM #%d in %s", CloudSim.clock(), getName(),vm.getId(), datacenterName));
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
		
		
	}

	/**
	 * Submit cloudlets to the created VMs.
	 *
	 * @pre $none
	 * @post $none
	 */
	protected void submitCloudlets() {
		//Log.printLine("BROKER:submitCloudlets");
		int containerIndex = 0;
		List<IntContainerCloudlet> successfullySubmitted = new ArrayList<>();
		for (IntContainerCloudlet cloudlet : getCloudletList()) {
			// Log.printLine("Containers Created" + getContainersCreated());
			if (containerIndex < getContainersCreated()) {
				//Log.printLine("Container Index" + containerIndex);
				//
                    int containerId = getContainersCreatedList().get(containerIndex).getId();// descomentei do original
                    bindCloudletToContainer(cloudlet.getCloudletId(), containerId);// descomentei do original
                    
				//Log.printLine("=== === " + getContainersToVmsMap().get(cloudlet.getContainerId()));
				if (getContainersToVmsMap().get(cloudlet.getContainerId()) != null) {
					int vmId = getContainersToVmsMap().get(cloudlet.getContainerId());
                    //bindCloudletToVm(cloudlet.getCloudletId(), vmId); // descomentei do original
					cloudlet.setVmId(vmId);
//                    if(cloudlet.getVmId() != vmId){
//                        Log.printConcatLine("The cloudlet Vm Id is ", cloudlet.getVmId(), "It should be", vmId);
//                    }
					containerIndex++;
					sendNow(getDatacenterIdsList().get(0), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
					cloudletsSubmitted++;
					// Log.printLine("aqui");
					getCloudletSubmittedList().add(cloudlet);
					successfullySubmitted.add(cloudlet);
				}

				// Log.printLine("Container Id" + containerId);

				//Log.printConcatLine("VM ID is: ",cloudlet.getVmId(), ", Container ID: ",cloudlet.getContainerId(), ", cloudletId: ", cloudlet.getCloudletId());

//                cloudlet.setVmId(v.getId());
				// if user didn't bind this cloudlet and it has not been executed yet
//            if (cloudlet.getContainerId() == -1) {
//                Log.print("User has forgotten to bound the cloudlet to container");
//            } else { // submit to the specific vm
//                vm = ContainerVmList.getById(getVmsCreatedList(), cloudlet.getVmId());
//                if (vm == null) { // vm was not created
//                    if (!Log.isDisabled()) {
//                        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Postponing execution of cloudlet ",
//                                cloudlet.getCloudletId(), ": bount VM not available");
//                    }
//                    continue;
//                }
//            }
//
//            if (!Log.isDisabled()) {
//                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Sending cloudlet ",
//                        cloudlet.getCloudletId(), " to VM #", cloudlet.getContainerId());
//            }

//            containerIndex = (containerIndex + 1) % getVmsCreatedList().size();

//          int vmIndex = 0;
//        List<IntContainerCloudlet> successfullySubmitted = new ArrayList<IntContainerCloudlet>();
//        for (IntContainerCloudlet cloudlet : getCloudletList()) {
//            ContainerVm vm;
//            // if user didn't bind this cloudlet and it has not been executed yet
//            if (cloudlet.getVmId() == -1) {
//                vm = getVmsCreatedList().get(vmIndex);
//            } else { // submit to the specific vm
//                vm = ContainerVmList.getById(getVmsCreatedList(), cloudlet.getVmId());
//                if (vm == null) { // vm was not created
//                    if (!Log.isDisabled()) {
//                        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Postponing execution of cloudlet ",
//                                cloudlet.getCloudletId(), ": bount VM not available");
//                    }
//                    continue;
//                }
//            }
//
//            if (!Log.isDisabled()) {cloudlet.getCloudletId()
//                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Sending cloudlet ",
//                        cloudlet.getCloudletId(), " to VM #", vm.getId());
//            }
//
//            cloudlet.setVmId(vm.getId());
//            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
//            cloudletsSubmitted++;
//            vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
//            getCloudletSubmittedList().add(cloudlet);
//            successfullySubmitted.add(cloudlet);
			}
		}

		// remove submitted cloudlets from waiting list
		getCloudletList().removeAll(successfullySubmitted);
		successfullySubmitted.clear();
	}

	/**
	 * getOverBookingfactor Destroy the virtual machines running in datacenters.
	 *
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		//Log.printLine("BROKER:clearDatacenters");
		for (IntContainerVm vm : getVmsCreatedList()) {
//            Log.printConcatLine(CloudSim.clock(), ": " + getName(), ": Destroying VM #", vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();
	}

	/**
	 *
	 */
	protected void submitContainers() {
		//Log.printLine("BROKER:submitContainers");
		List<IntContainer> successfullySubmitted = new ArrayList<>();
		int i = 0;
		for (IntContainer container : getContainerList()) {
			IntContainerCloudlet cloudlet = getCloudletList().get(i);
			// Log.printLine("Containers Created" + getContainersCreated());

			if (cloudlet.getUtilizationModelCpu() instanceof UtilizationModelPlanetLabInMemory) {
				UtilizationModelPlanetLabInMemory temp = (UtilizationModelPlanetLabInMemory) cloudlet.getUtilizationModelCpu();
				double[] cloudletUsage = temp.getData();
				Percentile percentile = new Percentile();
				double percentileUsage = percentile.evaluate(cloudletUsage, getOverBookingfactor());
				// Log.printLine("Container Index" + containerIndex);
				double newmips = percentileUsage * container.getMips();
//                    double newmips = percentileUsage * container.getMips();
//                    double maxUsage = Doubles.max(cloudletUsage);
//                    double newmips = maxUsage * container.getMips();
				container.setWorkloadMips(newmips);
//                    bindCloudletToContainer(cloudlet.getCloudletId(), container.getId());
				cloudlet.setContainerId(container.getId());
				if (cloudlet.getContainerId() != container.getId()) {
//                        Log.printConcatLine("Binding Cloudlet: ", cloudlet.getCloudletId(), "To Container: ",container.getId() , "Now it is", cloudlet.getContainerId());
				}

			}
			cloudlet.setContainerId(container.getId());
			Log.printConcatLine("Binding Cloudlet: ", cloudlet.getCloudletId(), " To Container: ",container.getId());
			i++;

		}

		for (IntContainer container : getContainerList()) {
			successfullySubmitted.add(container);

		}
		
		sendNow(getDatacenterIdsList().get(0), containerCloudSimTags.CONTAINER_SUBMIT, successfullySubmitted);

//        List<Container> successfullySubmitted = new ArrayList<>();
//        for (Container container : getContainerList()) {
//
//            if (!Log.isDisabled()) {
//                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Sending container ",
//                        container.getId(), " to Datacenter");
//            }
//            cloudletsSubmitted++;
//            vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
//            getCloudletSubmittedList().add(cloudlet);
//            successfullySubmitted.add(cloudlet);
//        }

		// remove submitted cloudlets from waiting list
	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 *
	 * @pre $none
	 * @post $none
	 */
	protected void finishExecution() {
		//Log.printLine("BROKER:finishExecution");
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		//Log.printLine("BROKER:shutdownEntity");
		Log.printConcatLine(getName(), " is shutting down...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		//Log.printLine("BROKER:startEntity");
		Log.printConcatLine(getName(), " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}

	/**
	 * Gets the vm list.
	 *
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerVm> List<T> getVmList() {
		//Log.printLine("BROKER:getVmList");
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 *
	 * @param <T>    the generic type
	 * @param vmList the new vm list
	 */
	protected <T extends IntContainerVm> void setVmList(List<T> vmList) {
		//Log.printLine("BROKER:setVmList");
		this.vmList = vmList;
	}

	/**
	 * Gets the cloudlet list.
	 *
	 * @param <T> the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerCloudlet> List<T> getCloudletList() {
		//Log.printLine("BROKER:getCloudletList");
		return (List<T>) cloudletList;
	}

	/**
	 * Sets the cloudlet list.
	 *
	 * @param <T>          the generic type
	 * @param cloudletList the new cloudlet list
	 */
	protected <T extends IntContainerCloudlet> void setCloudletList(List<T> cloudletList) {
		//Log.printLine("BROKER:setCloudletList");
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 *
	 * @param <T> the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerCloudlet> List<T> getCloudletSubmittedList() {
		//Log.printLine("BROKER:getCloudletSubmittedList");
		return (List<T>) cloudletSubmittedList;
	}

	/**
	 * Sets the cloudlet submitted list.
	 *
	 * @param <T>                   the generic type
	 * @param cloudletSubmittedList the new cloudlet submitted list
	 */
	protected <T extends IntContainerCloudlet> void setCloudletSubmittedList(List<T> cloudletSubmittedList) {
		//Log.printLine("BROKER:setCloudletSubmittedList");
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 *
	 * @param <T> the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerCloudlet> List<T> getCloudletReceivedList() {
		//Log.printLine("BROKER:getCloudletReceivedList");
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 *
	 * @param <T>                  the generic type
	 * @param cloudletReceivedList the new cloudlet received list
	 */
	protected <T extends IntContainerCloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		//Log.printLine("BROKER:setCloudletReceivedList");
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 *
	 * @param <T> the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerVm> List<T> getVmsCreatedList() {
		//Log.printLine("BROKER:getVmsCreatedList");
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 *
	 * @param <T>            the generic type
	 * @param vmsCreatedList the vms created list
	 */
	protected <T extends IntContainerVm> void setVmsCreatedList(List<T> vmsCreatedList) {
		//Log.printLine("BROKER:setVmsCreatedList");
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 *
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		//Log.printLine("BROKER:getVmsRequested");
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 *
	 * @param vmsRequested the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		//Log.printLine("BROKER:setVmsRequested");
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 *
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		//Log.printLine("BROKER:getVmsAcks");
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 *
	 * @param vmsAcks the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		//Log.printLine("BROKER:setVmsAcks");
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		//Log.printLine("BROKER:incrementVmsAcks");
		vmsAcks++;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementContainersAcks() {
		//Log.printLine("BROKER:incrementContainersAcks");
		setContainersAcks(getContainersAcks() + 1);
	}

	/**
	 * Gets the vms destroyed.
	 *
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		//Log.printLine("BROKER:getVmsDestroyed");
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 *
	 * @param vmsDestroyed the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		//Log.printLine("BROKER:setVmsDestroyed");
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 *
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		//Log.printLine("BROKER:getDatacenterIdsList");
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 *
	 * @param datacenterIdsList the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		//Log.printLine("BROKER:setDatacenterIdsList");
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 *
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		//Log.printLine("BROKER:getVmsToDatacentersMap");
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 *
	 * @param vmsToDatacentersMap the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		//Log.printLine("BROKER:setVmsToDatacentersMap");
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 *
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, IntContainerDataCenterCharacteristics> getDatacenterCharacteristicsList() {
		//Log.printLine("BROKER:getDatacenterCharacteristicsList");
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 *
	 * @param datacenterCharacteristicsList the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, IntContainerDataCenterCharacteristics> datacenterCharacteristicsList) {
		//Log.printLine("BROKER:setDatacenterCharacteristicsList");
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 *
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		//Log.printLine("BROKER:getDatacenterRequestedIdsList");
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 *
	 * @param datacenterRequestedIdsList the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		//Log.printLine("BROKER:setDatacenterRequestedIdsList");
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

//------------------------------------------------

	public <T extends IntContainer> List<T> getContainerList() {
		//Log.printLine("BROKER:getContainerList");

		return (List<T>) containerList;
	}

	public void setContainerList(List<? extends IntContainer> containerList) {
		//Log.printLine("BROKER:setContainerList");
		this.containerList = containerList;
	}

	/**
	 * This method is used to send to the broker the list with virtual machines that
	 * must be created.
	 *
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitContainerList(List<? extends IntContainer> list) {
		//Log.printLine("BROKER:submitContainerList");
		getContainerList().addAll(list);
	}

	public Map<Integer, Integer> getContainersToVmsMap() {
		//Log.printLine("BROKER:getContainersToVmsMap");
		return containersToDatacentersMap;
	}

	public void setContainersToDatacentersMap(Map<Integer, Integer> containersToDatacentersMap) {		
		//Log.printLine("BROKER:setContainersToDatacentersMap");
		this.containersToDatacentersMap = containersToDatacentersMap;
	}

	public <T extends IntContainer> List<T> getContainersCreatedList() {
		//Log.printLine("BROKER:getContainersCreatedList");
		return (List<T>) containersCreatedList;
	}

	public void setContainersCreatedList(List<? extends IntContainer> containersCreatedList) {
		//Log.printLine("BROKER:setContainersCreatedList");
		this.containersCreatedList = containersCreatedList;
	}

	public int getContainersAcks() {
		//Log.printLine("BROKER:getContainersAcks");
		return containersAcks;
	}

	public void setContainersAcks(int containersAcks) {
		//Log.printLine("BROKER:setContainersAcks");
		this.containersAcks = containersAcks;
	}

	public int getContainersCreated() {
		//Log.printLine("BROKER:getContainersCreated");
		return containersCreated;
	}

	public void setContainersCreated(int containersCreated) {
		//Log.printLine("BROKER:setContainersCreated");
		this.containersCreated = containersCreated;
	}

	public double getOverBookingfactor() {
		//Log.printLine("BROKER:getOverBookingfactor");
		return overBookingfactor;
	}

	public void setOverBookingfactor(double overBookingfactor) {
		//Log.printLine("BROKER:setOverBookingfactor");
		this.overBookingfactor = overBookingfactor;
	}

	public int getNumberOfCreatedVMs() {
		//Log.printLine("BROKER:getNumberOfCreatedVMs");
		return numberOfCreatedVMs;
	}

	public void setNumberOfCreatedVMs(int numberOfCreatedVMs) {
		//Log.printLine("BROKER:setNumberOfCreatedVMs");
		this.numberOfCreatedVMs = numberOfCreatedVMs;
	}
}
