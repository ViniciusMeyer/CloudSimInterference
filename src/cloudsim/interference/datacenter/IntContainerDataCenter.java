package cloudsim.interference.datacenter;

import org.cloudbus.cloudsim.container.core.containerCloudSimTags;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import cloudsim.interference.IntContainer;
import cloudsim.interference.IntContainerAllocationPolicy;
import cloudsim.interference.IntContainerHost;
import cloudsim.interference.Interference;
import cloudsim.interference.MLCResult;
import cloudsim.interference.MLClassifier;
import cloudsim.interference.Placement;
import cloudsim.interference.Solution;
import cloudsim.interference.util;
import cloudsim.interference.cloudlet.IntContainerCloudlet;
import cloudsim.interference.vm.IntContainerVm;
import cloudsim.interference.vm.IntContainerVmAllocationPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sareh on 10/07/15.
 */
public class IntContainerDataCenter extends SimEntity {

	private MLClassifier MLC = new MLClassifier(); // adapt
	private MLCResult MLCR = new MLCResult(); // adapt
	private List<IntContainerCloudlet> cloudletList; // adapt
	private boolean classify = false; // adapt
	private List<Solution> solutionList = new ArrayList<Solution>(); // adapt

	/**
	 * The characteristics.
	 */
	private IntContainerDataCenterCharacteristics characteristics;

	/**
	 * The regional cis name.
	 */
	private String regionalCisName;

	/**
	 * The vm provisioner.
	 */
	private IntContainerVmAllocationPolicy vmAllocationPolicy;
	/**
	 * The container provisioner.
	 */
	private IntContainerAllocationPolicy containerAllocationPolicy;

	/**
	 * The last process time.
	 */
	private double lastProcessTime;

	/**
	 * The storage list.
	 */
	private List<Storage> storageList;

	/**
	 * The vm list.
	 */
	private List<? extends IntContainerVm> containerVmList;
	/**
	 * The container list.
	 */
	private List<? extends IntContainer> containerList;

	/**
	 * The scheduling interval.
	 */
	private double schedulingInterval;
	/**
	 * The scheduling interval.
	 */
	private String experimentName;
	/**
	 * The log address.
	 */
	private String logAddress;

	private double vmStartupDelay; // cont
	private double containerStartupDelay; // cont

	/**
	 * Allocates a new PowerDatacenter object.
	 * 
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param containerAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param experimentName
	 * @param logAddress
	 * @throws Exception
	 */
	public IntContainerDataCenter(String name, IntContainerDataCenterCharacteristics characteristics,
			IntContainerVmAllocationPolicy vmAllocationPolicy, IntContainerAllocationPolicy containerAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, String experimentName, String logAddress,
			Double vmStartupDelay, Double containerStartupDelay, List<IntContainerCloudlet> cloudletList)
			throws Exception {
		super(name);
		// Log.printLine("\nDC: ==== INCIO - CRIAÇÃO OBJETO DC =====\n");
		setCharacteristics(characteristics);
		setVmAllocationPolicy(vmAllocationPolicy);
		setContainerAllocationPolicy(containerAllocationPolicy);
		setLastProcessTime(0.0);
		setStorageList(storageList);
		setContainerVmList(new ArrayList<IntContainerVm>());
		setContainerList(new ArrayList<IntContainer>());
		setSchedulingInterval(schedulingInterval);
		setExperimentName(experimentName);
		setLogAddress(logAddress);

		for (IntContainerHost host : getCharacteristics().getHostList()) {
			host.setDatacenter(this);
		}

		// If this resource doesn't have any PEs then no useful at all
		if (getCharacteristics().getNumberOfPes() == 0) {
			throw new Exception(
					super.getName() + " : Error - this entity has no PEs. Therefore, can't process any Cloudlets.");
		}

		// stores id of this class
		getCharacteristics().setId(super.getId());

		this.vmStartupDelay = vmStartupDelay; // cont
		this.containerStartupDelay = containerStartupDelay; // cont

		this.cloudletList = cloudletList;

		// Log.printLine("\nDC: ==== FIM - CRIAÇÃO OBJETO DC =====\n");
	}

	/**
	 * Overrides this method when making a new and different type of resource. <br>
	 * <b>NOTE:</b> You do not need to override {} method, if you use this method.
	 *
	 * @pre $none
	 * @post $none
	 */
	protected void registerOtherEntity() {
		// empty. This should be override by a child class
	}

	/**
	 * Processes events or services that are available for this PowerDatacenter.
	 *
	 * @param ev a Sim_event object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
		// Log.printLine("DC:processEvent =========== " + CloudSim.clock());
		int srcId = -1;

		switch (ev.getTag()) {
		// Resource characteristics inquiry
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			srcId = ((Integer) ev.getData()).intValue();
			sendNow(srcId, ev.getTag(), getCharacteristics());
			break;

		// Resource dynamic info inquiry
		case CloudSimTags.RESOURCE_DYNAMICS:
			srcId = ((Integer) ev.getData()).intValue();
			sendNow(srcId, ev.getTag(), 0);
			break;

		case CloudSimTags.RESOURCE_NUM_PE:
			srcId = ((Integer) ev.getData()).intValue();
			int numPE = getCharacteristics().getNumberOfPes();
			sendNow(srcId, ev.getTag(), numPE);
			break;

		case CloudSimTags.RESOURCE_NUM_FREE_PE:
			srcId = ((Integer) ev.getData()).intValue();
			int freePesNumber = getCharacteristics().getNumberOfFreePes();
			sendNow(srcId, ev.getTag(), freePesNumber);
			break;

		// New Cloudlet arrives
		case CloudSimTags.CLOUDLET_SUBMIT:
			processCloudletSubmit(ev, false);
			break;

		// New Cloudlet arrives, but the sender asks for an ack
		case CloudSimTags.CLOUDLET_SUBMIT_ACK:
			processCloudletSubmit(ev, true);
			break;

		// Cancels a previously submitted Cloudlet
		case CloudSimTags.CLOUDLET_CANCEL:
			processCloudlet(ev, CloudSimTags.CLOUDLET_CANCEL);
			break;

		// Pauses a previously submitted Cloudlet
		case CloudSimTags.CLOUDLET_PAUSE:
			processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE);
			break;

		// Pauses a previously submitted Cloudlet, but the sender
		// asks for an acknowledgement
		case CloudSimTags.CLOUDLET_PAUSE_ACK:
			processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE_ACK);
			break;

		// Resumes a previously submitted Cloudlet
		case CloudSimTags.CLOUDLET_RESUME:
			processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME);
			break;

		// Resumes a previously submitted Cloudlet, but the sender
		// asks for an acknowledgement
		case CloudSimTags.CLOUDLET_RESUME_ACK:
			processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME_ACK);
			break;

		// Moves a previously submitted Cloudlet to a different resource
		case CloudSimTags.CLOUDLET_MOVE:
			processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE);
			break;

		// Moves a previously submitted Cloudlet to a different resource
		case CloudSimTags.CLOUDLET_MOVE_ACK:
			processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE_ACK);
			break;

		// Checks the status of a Cloudlet
		case CloudSimTags.CLOUDLET_STATUS:
			processCloudletStatus(ev);
			break;

		// Ping packet
		case CloudSimTags.INFOPKT_SUBMIT:
			processPingRequest(ev);
			break;

		case CloudSimTags.VM_CREATE:
			processVmCreate(ev, false);
			break;

		case CloudSimTags.VM_CREATE_ACK:
			processVmCreate(ev, true);
			break;

		case CloudSimTags.VM_DESTROY:
			processVmDestroy(ev, false);
			break;

		case CloudSimTags.VM_DESTROY_ACK:
			processVmDestroy(ev, true);
			break;

		case CloudSimTags.VM_MIGRATE:
			processVmMigrate(ev, false);
			break;

		case CloudSimTags.VM_MIGRATE_ACK:
			processVmMigrate(ev, true);
			break;

		case CloudSimTags.VM_DATA_ADD:
			processDataAdd(ev, false);
			break;

		case CloudSimTags.VM_DATA_ADD_ACK:
			processDataAdd(ev, true);
			break;

		case CloudSimTags.VM_DATA_DEL:
			processDataDelete(ev, false);
			break;

		case CloudSimTags.VM_DATA_DEL_ACK:
			processDataDelete(ev, true);
			break;

		case CloudSimTags.VM_DATACENTER_EVENT:
			updateCloudletProcessing();
			checkCloudletCompletion();
			break;
		case containerCloudSimTags.CONTAINER_SUBMIT:
			processContainerSubmit(ev, true);
			break;

		case containerCloudSimTags.CONTAINER_MIGRATE:
			processContainerMigrate(ev, false);
			// other unknown tags are processed by this method
			break;

		default:
			processOtherEvent(ev);
			break;
		}
	}

	public void processContainerSubmit(SimEvent ev, boolean ack) {
		// processamento do evento para alocar VMs para Containers
		// Log.printLine("DC:processContainerSubmit -> VM para Container");

		List<IntContainer> containerList = (List<IntContainer>) ev.getData();

		for (IntContainer container : containerList) {

			// alocação de container em vm (inicial ao menos)
			// manda container para as politicas de alocação (VM)
			// de acordo com IntContainerAllocationPolicySimple (allocateVmForContainer)
			boolean result = getContainerAllocationPolicy().allocateVmForContainer(container, getContainerVmList());

			if (ack) {
				int[] data = new int[3];
				data[1] = container.getId();
				if (result) {
					data[2] = CloudSimTags.TRUE;
				} else {
					data[2] = CloudSimTags.FALSE;
				}
				if (result) {

					IntContainerVm containerVm = getContainerAllocationPolicy().getContainerVm(container);
					data[0] = containerVm.getId();

					if (containerVm.getId() == -1) {

						Log.printConcatLine("The ContainerVM ID is not known (-1) !");
					}
//                    Log.printConcatLine("Assigning the container#" + container.getUid() + "to VM #" + containerVm.getUid());
					getContainerList().add(container);
					if (container.isBeingInstantiated()) {
						container.setBeingInstantiated(false);
					}
					// processa o cloudlet tbm
					container.updateContainerProcessing(CloudSim.clock(), getContainerAllocationPolicy()
							.getContainerVm(container).getContainerScheduler().getAllocatedMipsForContainer(container));
					// Log.printLine("CLOUDSIM TIME: " + CloudSim.clock());
				} else {
					data[0] = -1;
					// notAssigned.add(container);
					Log.printLine(String.format("Couldn't find a vm to host the container #%s", container.getUid()));

				}

				send(ev.getSource(), CloudSim.getMinTimeBetweenEvents(), containerCloudSimTags.CONTAINER_CREATE_ACK,
						data);

			}

		}

	}

	/**
	 * Process data del.
	 *
	 * @param ev  the ev
	 * @param ack the ack
	 */
	protected void processDataDelete(SimEvent ev, boolean ack) {
		Log.printLine("DC:processDataDelete");
		if (ev == null) {
			return;
		}

		Object[] data = (Object[]) ev.getData();
		if (data == null) {
			return;
		}

		String filename = (String) data[0];
		int req_source = ((Integer) data[1]).intValue();
		int tag = -1;

		// check if this file can be deleted (do not delete is right now)
		int msg = deleteFileFromStorage(filename);
		if (msg == DataCloudTags.FILE_DELETE_SUCCESSFUL) {
			tag = DataCloudTags.CTLG_DELETE_MASTER;
		} else { // if an error occured, notify user
			tag = DataCloudTags.FILE_DELETE_MASTER_RESULT;
		}

		if (ack) {
			// send back to sender
			Object pack[] = new Object[2];
			pack[0] = filename;
			pack[1] = Integer.valueOf(msg);

			sendNow(req_source, tag, pack);
		}
	}

	/**
	 * Process data add.
	 *
	 * @param ev  the ev
	 * @param ack the ack
	 */
	protected void processDataAdd(SimEvent ev, boolean ack) {
		Log.printLine("DC:processDataAdd");
		if (ev == null) {
			return;
		}

		Object[] pack = (Object[]) ev.getData();
		if (pack == null) {
			return;
		}

		File file = (File) pack[0]; // get the file
		file.setMasterCopy(true); // set the file into a master copy
		int sentFrom = ((Integer) pack[1]).intValue(); // get sender ID

		/******
		 * // DEBUG Log.printLine(super.get_name() + ".addMasterFile(): " +
		 * file.getName() + " from " + CloudSim.getEntityName(sentFrom));
		 *******/

		Object[] data = new Object[3];
		data[0] = file.getName();

		int msg = addFile(file); // add the file

		if (ack) {
			data[1] = Integer.valueOf(-1); // no sender id
			data[2] = Integer.valueOf(msg); // the result of adding a master file
			sendNow(sentFrom, DataCloudTags.FILE_ADD_MASTER_RESULT, data);
		}
	}

	/**
	 * Processes a ping request.
	 *
	 * @param ev a Sim_event object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processPingRequest(SimEvent ev) {
		Log.printLine("DC:processPingRequest");
		InfoPacket pkt = (InfoPacket) ev.getData();
		pkt.setTag(CloudSimTags.INFOPKT_RETURN);
		pkt.setDestId(pkt.getSrcId());

		// sends back to the sender
		sendNow(pkt.getSrcId(), CloudSimTags.INFOPKT_RETURN, pkt);
	}

	/**
	 * Process the event for an User/Broker who wants to know the status of a
	 * Cloudlet. This PowerDatacenter will then send the status back to the
	 * User/Broker.
	 *
	 * @param ev a Sim_event object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processCloudletStatus(SimEvent ev) {
		Log.printLine("DC:processCloudletStatus");
		int cloudletId = 0;
		int userId = 0;
		int vmId = 0;
		int containerId = 0;
		int status = -1;

		try {
			// if a sender using cloudletXXX() methods
			int data[] = (int[]) ev.getData();
			cloudletId = data[0];
			userId = data[1];
			vmId = data[2];
			containerId = data[3];
			// Log.printLine("Data Center is processing the cloudletStatus Event ");
			status = getVmAllocationPolicy().getHost(vmId, userId).getContainerVm(vmId, userId)
					.getContainer(containerId, userId).getContainerCloudletScheduler().getCloudletStatus(cloudletId);
		}

		// if a sender using normal send() methods
		catch (ClassCastException c) {
			try {
				IntContainerCloudlet cl = (IntContainerCloudlet) ev.getData();
				cloudletId = cl.getCloudletId();
				userId = cl.getUserId();
				containerId = cl.getContainerId();

				status = getVmAllocationPolicy().getHost(vmId, userId).getContainerVm(vmId, userId)
						.getContainer(containerId, userId).getContainerCloudletScheduler()
						.getCloudletStatus(cloudletId);
			} catch (Exception e) {
				Log.printConcatLine(getName(), ": Error in processing CloudSimTags.CLOUDLET_STATUS");
				Log.printLine(e.getMessage());
				return;
			}
		} catch (Exception e) {
			Log.printConcatLine(getName(), ": Error in processing CloudSimTags.CLOUDLET_STATUS");
			Log.printLine(e.getMessage());
			return;
		}

		int[] array = new int[3];
		array[0] = getId();
		array[1] = cloudletId;
		array[2] = status;

		int tag = CloudSimTags.CLOUDLET_STATUS;
		sendNow(userId, tag, array);
	}

	/**
	 * Here all the method related to VM requests will be received and forwarded to
	 * the related method.
	 *
	 * @param ev the received event
	 * @pre $none
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		Log.printLine("DC:processOtherEvent");
		if (ev == null) {
			Log.printConcatLine(getName(), ".processOtherEvent(): Error - an event is null.");
		}
	}

	/**
	 * Process the event for a User/Broker who wants to create a VM in this
	 * PowerDatacenter. This PowerDatacenter will then send the status back to the
	 * User/Broker.
	 *
	 * @param ev  a Sim_event object
	 * @param ack the ack
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev, boolean ack) {
		// Log.printLine("DC:processVmCreate");
		IntContainerVm containerVm = (IntContainerVm) ev.getData();

		boolean result = getVmAllocationPolicy().allocateHostForVm(containerVm);

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = containerVm.getId();

			if (result) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			send(containerVm.getUserId(), CloudSim.getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, data);
		}

		if (result) {
			getContainerVmList().add(containerVm);

			if (containerVm.isBeingInstantiated()) {
				containerVm.setBeingInstantiated(false);
			}

			containerVm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(containerVm)
					.getContainerVmScheduler().getAllocatedMipsForContainerVm(containerVm));
		}

	}

	/**
	 * Process the event for a User/Broker who wants to destroy a VM previously
	 * created in this PowerDatacenter. This PowerDatacenter may send, upon request,
	 * the status back to the User/Broker.
	 *
	 * @param ev  a Sim_event object
	 * @param ack the ack
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmDestroy(SimEvent ev, boolean ack) {
		Log.printLine("DC:processVmDestroy");
		IntContainerVm containerVm = (IntContainerVm) ev.getData();
		getVmAllocationPolicy().deallocateHostForVm(containerVm);

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = containerVm.getId();
			data[2] = CloudSimTags.TRUE;

			sendNow(containerVm.getUserId(), CloudSimTags.VM_DESTROY_ACK, data);
		}

		getContainerVmList().remove(containerVm);
	}

	/**
	 * Process the event for a User/Broker who wants to migrate a VM. This
	 * PowerDatacenter will then send the status back to the User/Broker.
	 *
	 * @param ev a Sim_event object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmMigrate(SimEvent ev, boolean ack) {
		Log.printLine("DC:processVmMigrate");
		Object tmp = ev.getData();
		if (!(tmp instanceof Map<?, ?>)) {
			throw new ClassCastException("The data object must be Map<String, Object>");
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> migrate = (HashMap<String, Object>) tmp;

		IntContainerVm containerVm = (IntContainerVm) migrate.get("vm");
		IntContainerHost host = (IntContainerHost) migrate.get("host");

		getVmAllocationPolicy().deallocateHostForVm(containerVm);
		host.removeMigratingInContainerVm(containerVm);
		boolean result = getVmAllocationPolicy().allocateHostForVm(containerVm, host);
		if (!result) {
			Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
			System.exit(0);
		}

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = containerVm.getId();

			if (result) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			sendNow(ev.getSource(), CloudSimTags.VM_CREATE_ACK, data);
		}

		Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), containerVm.getId(),
				host.getId());
		containerVm.setInMigration(false);
	}

	/**
	 * Process the event for a User/Broker who wants to migrate a VM. This
	 * PowerDatacenter will then send the status back to the User/Broker.
	 *
	 * @param ev a Sim_event object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processContainerMigrate(SimEvent ev, boolean ack) {
		Log.printLine("DC:processContainerMigrate");

		Object tmp = ev.getData();
		if (!(tmp instanceof Map<?, ?>)) {
			throw new ClassCastException("The data object must be Map<String, Object>");
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> migrate = (HashMap<String, Object>) tmp;

		IntContainer container = (IntContainer) migrate.get("container");
		IntContainerVm containerVm = (IntContainerVm) migrate.get("vm");

		getContainerAllocationPolicy().deallocateVmForContainer(container);
		if (containerVm.getContainersMigratingIn().contains(container)) {
			containerVm.removeMigratingInContainer(container);
		}
		boolean result = getContainerAllocationPolicy().allocateVmForContainer(container, containerVm);
		if (!result) {
			Log.printLine("[Datacenter.processContainerMigrate]Container allocation to the destination vm failed");
			System.exit(0);
		}
		if (containerVm.isInWaiting()) {
			containerVm.setInWaiting(false);

		}

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = container.getId();

			if (result) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			sendNow(ev.getSource(), containerCloudSimTags.CONTAINER_CREATE_ACK, data);
		}

		Log.formatLine("%.2f: Migration of container #%d to Vm #%d is completed", CloudSim.clock(), container.getId(),
				container.getVm().getId());
		container.setInMigration(false);
	}

	/**
	 * Processes a Cloudlet based on the event type.
	 *
	 * @param ev   a Sim_event object
	 * @param type event type
	 * @pre ev != null
	 * @pre type > 0
	 * @post $none
	 */
	protected void processCloudlet(SimEvent ev, int type) {
		Log.printLine("DC:processCloudlet");
		int cloudletId = 0;
		int userId = 0;
		int vmId = 0;
		int containerId = 0;

		try { // if the sender using cloudletXXX() methods
			int data[] = (int[]) ev.getData();
			cloudletId = data[0];
			userId = data[1];
			vmId = data[2];
			containerId = data[3];
		}

		// if the sender using normal send() methods
		catch (ClassCastException c) {
			try {
				IntContainerCloudlet cl = (IntContainerCloudlet) ev.getData();
				cloudletId = cl.getCloudletId();
				userId = cl.getUserId();
				vmId = cl.getVmId();
				containerId = cl.getContainerId();
			} catch (Exception e) {
				Log.printConcatLine(super.getName(), ": Error in processing Cloudlet");
				Log.printLine(e.getMessage());
				return;
			}
		} catch (Exception e) {
			Log.printConcatLine(super.getName(), ": Error in processing a Cloudlet.");
			Log.printLine(e.getMessage());
			return;
		}

		// begins executing ....
		switch (type) {
		case CloudSimTags.CLOUDLET_CANCEL:
			processCloudletCancel(cloudletId, userId, vmId, containerId);
			break;

		case CloudSimTags.CLOUDLET_PAUSE:
			processCloudletPause(cloudletId, userId, vmId, containerId, false);
			break;

		case CloudSimTags.CLOUDLET_PAUSE_ACK:
			processCloudletPause(cloudletId, userId, vmId, containerId, true);
			break;

		case CloudSimTags.CLOUDLET_RESUME:
			processCloudletResume(cloudletId, userId, vmId, containerId, false);
			break;

		case CloudSimTags.CLOUDLET_RESUME_ACK:
			processCloudletResume(cloudletId, userId, vmId, containerId, true);
			break;
		default:
			break;
		}

	}

	/**
	 * Process the event for a User/Broker who wants to move a Cloudlet.
	 *
	 * @param receivedData information about the migration
	 * @param type         event tag
	 * @pre receivedData != null
	 * @pre type > 0
	 * @post $none
	 */
	protected void processCloudletMove(int[] receivedData, int type) {
		Log.printLine("DC:processCloudletMove");
		updateCloudletProcessing();

		int[] array = receivedData;
		int cloudletId = array[0];
		int userId = array[1];
		int vmId = array[2];
		int containerId = array[3];
		int vmDestId = array[4];
		int containerDestId = array[5];
		int destId = array[6];

		// get the cloudlet
		Cloudlet cl = getVmAllocationPolicy().getHost(vmId, userId).getContainerVm(vmId, userId)
				.getContainer(containerId, userId).getContainerCloudletScheduler().cloudletCancel(cloudletId);

		boolean failed = false;
		if (cl == null) {// cloudlet doesn't exist
			failed = true;
		} else {
			// has the cloudlet already finished?
			if (cl.getCloudletStatusString().equals("Success")) {// if yes, send it back to user
				int[] data = new int[3];
				data[0] = getId();
				data[1] = cloudletId;
				data[2] = 0;
				sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, data);
				sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
			}

			// prepare cloudlet for migration
			cl.setVmId(vmDestId);

			// the cloudlet will migrate from one vm to another does the destination VM
			// exist?
			if (destId == getId()) {
				IntContainerVm containerVm = getVmAllocationPolicy().getHost(vmDestId, userId).getContainerVm(vmDestId,
						userId);
				if (containerVm == null) {
					failed = true;
				} else {
					// time to transfer the files
					double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());
					containerVm.getContainer(containerDestId, userId).getContainerCloudletScheduler().cloudletSubmit(cl,
							fileTransferTime);
				}
			} else {// the cloudlet will migrate from one resource to another
				int tag = ((type == CloudSimTags.CLOUDLET_MOVE_ACK) ? CloudSimTags.CLOUDLET_SUBMIT_ACK
						: CloudSimTags.CLOUDLET_SUBMIT);
				sendNow(destId, tag, cl);
			}
		}

		if (type == CloudSimTags.CLOUDLET_MOVE_ACK) {// send ACK if requested
			int[] data = new int[3];
			data[0] = getId();
			data[1] = cloudletId;
			if (failed) {
				data[2] = 0;
			} else {
				data[2] = 1;
			}
			sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, data);
		}
	}

	/**
	 * Processes a Cloudlet submission.
	 *
	 * @param ev  a SimEvent object
	 * @param ack an acknowledgement
	 * @pre ev != null
	 * @post $none
	 */
	protected void processCloudletSubmit(SimEvent ev, boolean ack) {
		// Log.printLine("DC:processCloudletSubmit");
		updateCloudletProcessing();

		try {
			IntContainerCloudlet cl = (IntContainerCloudlet) ev.getData();

			// checks whether this Cloudlet has finished or not
			if (cl.isFinished()) {
				String name = CloudSim.getEntityName(cl.getUserId());
				Log.printConcatLine(getName(), ": Warning - Cloudlet #", cl.getCloudletId(), " owned by ", name,
						" is already completed/finished.");
				Log.printLine("Therefore, it is not being executed again");
				Log.printLine();

				// NOTE: If a Cloudlet has finished, then it won't be processed.
				// So, if ack is required, this method sends back a result.
				// If ack is not required, this method don't send back a result.
				// Hence, this might cause CloudSim to be hanged since waiting
				// for this Cloudlet back.
				if (ack) {
					int[] data = new int[3];
					data[0] = getId();
					data[1] = cl.getCloudletId();
					data[2] = CloudSimTags.FALSE;

					// unique tag = operation tag
					int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
					sendNow(cl.getUserId(), tag, data);
				}

				sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);

				return;
			}

			// process this Cloudlet to this CloudResource
			cl.setResourceParameter(getId(), getCharacteristics().getCostPerSecond(),
					getCharacteristics().getCostPerBw());

			// imprimir trace de interferencia de dada cloudlet
			/*
			 * for (int o = 0; o < cl.getInterferenceMetrics().getIntLength(); o++) {
			 * Log.print(o +" - "); for (int h = 0; h < 7; h++) {
			 * Log.print(+cl.getInterferenceMetrics().getIntByLine(o)[h] + " "); }
			 * Log.print("\n"); } Log.printLine( "     ===============    ");
			 */

			int userId = cl.getUserId();
			int vmId = cl.getVmId();
			int containerId = cl.getContainerId();

			// time to transfer the files
			double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

			IntContainerHost host = getVmAllocationPolicy().getHost(vmId, userId);
			IntContainerVm vm = host.getContainerVm(vmId, userId);
			IntContainer container = vm.getContainer(containerId, userId);
			double estimatedFinishTime = container.getContainerCloudletScheduler().cloudletSubmit(cl, fileTransferTime);
			// if this cloudlet is in the exec queue

			Log.printLine(cl.getCloudletId() + " === " + CloudSim.clock() + " === === == " + estimatedFinishTime);
			if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
				estimatedFinishTime += fileTransferTime;
				send(getId(), estimatedFinishTime, CloudSimTags.VM_DATACENTER_EVENT);
			}
			Log.printLine(cl.getCloudletId() + " === " + CloudSim.clock() + " === === == " + estimatedFinishTime);

			if (ack) {
				int[] data = new int[3];
				data[0] = getId();
				data[1] = cl.getCloudletId();
				data[2] = CloudSimTags.TRUE;

				// unique tag = operation tag
				int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
				sendNow(cl.getUserId(), tag, data);
			}
		} catch (ClassCastException c) {
			Log.printLine(String.format("%s.processCloudletSubmit(): ClassCastException error.", getName()));
			c.printStackTrace();
		} catch (Exception e) {
			Log.printLine(String.format("%s.processCloudletSubmit(): Exception error.", getName()));
			e.printStackTrace();
		}

		checkCloudletCompletion();
	}

	/**
	 * Predict file transfer time.
	 *
	 * @param requiredFiles the required files
	 * @return the double
	 */
	protected double predictFileTransferTime(List<String> requiredFiles) {
		Log.printLine("DC:predictFileTransferTime");
		double time = 0.0;

		for (String fileName : requiredFiles) {
			for (int i = 0; i < getStorageList().size(); i++) {
				Storage tempStorage = getStorageList().get(i);
				File tempFile = tempStorage.getFile(fileName);
				if (tempFile != null) {
					time += tempFile.getSize() / tempStorage.getMaxTransferRate();
					break;
				}
			}
		}
		return time;
	}

	/**
	 * Processes a Cloudlet resume request.
	 *
	 * @param cloudletId resuming cloudlet ID
	 * @param userId     ID of the cloudlet's owner
	 * @param ack        $true if an ack is requested after operation
	 * @param vmId       the vm id
	 * @pre $none
	 * @post $none
	 */
	protected void processCloudletResume(int cloudletId, int userId, int vmId, int containerId, boolean ack) {
		Log.printLine("DC:processCloudletResume");
		double eventTime = getVmAllocationPolicy().getHost(vmId, userId).getContainerVm(vmId, userId)
				.getContainer(containerId, userId).getContainerCloudletScheduler().cloudletResume(cloudletId);

		boolean status = false;
		if (eventTime > 0.0) { // if this cloudlet is in the exec queue
			status = true;
			if (eventTime > CloudSim.clock()) {
				schedule(getId(), eventTime, CloudSimTags.VM_DATACENTER_EVENT);
			}
		}

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = cloudletId;
			if (status) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			sendNow(userId, CloudSimTags.CLOUDLET_RESUME_ACK, data);
		}
	}

	/**
	 * Processes a Cloudlet pause request.
	 *
	 * @param cloudletId resuming cloudlet ID
	 * @param userId     ID of the cloudlet's owner
	 * @param ack        $true if an ack is requested after operation
	 * @param vmId       the vm id
	 * @pre $none
	 * @post $none
	 */
	protected void processCloudletPause(int cloudletId, int userId, int vmId, int containerId, boolean ack) {
		Log.printLine("DC:processCloudletPause");
		boolean status = getVmAllocationPolicy().getHost(vmId, userId).getContainerVm(vmId, userId)
				.getContainer(containerId, userId).getContainerCloudletScheduler().cloudletPause(cloudletId);

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = cloudletId;
			if (status) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			sendNow(userId, CloudSimTags.CLOUDLET_PAUSE_ACK, data);
		}
	}

	/**
	 * Processes a Cloudlet cancel request.
	 *
	 * @param cloudletId resuming cloudlet ID
	 * @param userId     ID of the cloudlet's owner
	 * @param vmId       the vm id
	 * @pre $none
	 * @post $none
	 */
	protected void processCloudletCancel(int cloudletId, int userId, int vmId, int containerId) {
		Log.printLine("DC:processCloudletCancel");
		Cloudlet cl = getVmAllocationPolicy().getHost(vmId, userId).getContainerVm(vmId, userId)
				.getContainer(containerId, userId).getContainerCloudletScheduler().cloudletCancel(cloudletId);
		sendNow(userId, CloudSimTags.CLOUDLET_CANCEL, cl);
	}

	/**
	 * Updates processing of each cloudlet running in this PowerDatacenter. It is
	 * necessary because Hosts and VirtualMachines are simple objects, not entities.
	 * So, they don't receive events and updating cloudlets inside them must be
	 * called from the outside.
	 *
	 * @pre $none
	 * @post $none
	 */
	protected void updateCloudletProcessing() {
		// Log.printLine("DC:updateCloudletProcessing");

		if (!classify) {
			InterferenceClassifier();
			classify = true;
		}

		// if some time passed since last processing
		// R: for term is to allow loop at simulation start. Otherwise, one initial
		// simulation step is skipped and schedulers are not properly initialized
		if (CloudSim.clock() < 0.111 || CloudSim.clock() > getLastProcessTime() + CloudSim.getMinTimeBetweenEvents()) {
			List<? extends IntContainerHost> list = getVmAllocationPolicy().getContainerHostList();
			double smallerTime = Double.MAX_VALUE;
			// for each host...
			for (int i = 0; i < list.size(); i++) {
				IntContainerHost host = list.get(i);
				// inform VMs to update processing

				double time = host.updateContainerVmsProcessing(CloudSim.clock());
				// what time do we expect that the next cloudlet will finish?
				if (time < smallerTime) {
					smallerTime = time;
				}
			}

			// gurantees a minimal interval before scheduling the event
			if (smallerTime < CloudSim.clock() + CloudSim.getMinTimeBetweenEvents() + 0.01) {
				smallerTime = CloudSim.clock() + CloudSim.getMinTimeBetweenEvents() + 0.01;
			}
			if (smallerTime != Double.MAX_VALUE) {
				schedule(getId(), (smallerTime - CloudSim.clock()), CloudSimTags.VM_DATACENTER_EVENT);
			}
			setLastProcessTime(CloudSim.clock());
		}
	}

	void InterferenceClassifier() {
		double migvalue = 10; // oversized value
		String approach = "IASA"; // "IASA", "EVEN", "CIAPA"
		String algorithm; // RR HC SA GA SAO
		// SA or SAO start PCA_OCP automatically
		long startT = System.currentTimeMillis();
		boolean first = true;
		List<Integer> nMig = new ArrayList<Integer>();

		List<Solution> solutionList1 = new ArrayList<Solution>(); // adapt
		int interval = 600, start = 1, end = 0, total = 7200, count = 1;

		Solution nextSolution = new Solution();
		// find the best intervals to make placement decisions.
		// first, we run a PCA over every trace

		// transforming cloudletList into CloudletTraces list
		List<Interference> cloudletTraces = new ArrayList<>(); // list with all container information
		List<Integer> intervals = new ArrayList<Integer>(); // list with OCP intervals
		List<? extends IntContainerHost> list = getVmAllocationPolicy().getContainerHostList();
		// for each host...
		for (int i = 0; i < list.size(); i++) {
			long startT1 = System.currentTimeMillis();
			IntContainerHost host = list.get(i);
			// for each container/cloudlet (in given VM)
			for (int x = 0; x < host.getVmList().get(0).getContainerList().size(); x++) {
				IntContainer container = host.getVmList().get(0).getContainerList().get(x);
				IntContainerCloudlet cloudlet = cloudletList.get(container.getId() - 1);
				cloudletTraces.add(cloudlet.getInterferenceMetrics());
			}
		}

		// IASA Scheduler
		if (approach.equals("IASA")) {
			algorithm = "SAO";
			// second, send this information to R to find the best instervals with
			// OnlineCPModel

			// sending cloudletTraces list to R function
			// intervals = MLC.getIntervalsOCPM(cloudletTraces, start, total);

			// manual
			intervals.add(1);
			intervals.add(311);
			intervals.add(622);
			intervals.add(940);
			intervals.add(1267);
			intervals.add(1575);
			intervals.add(1878);
			intervals.add(2183);
			intervals.add(2491);
			intervals.add(2812);
			intervals.add(3130);
			intervals.add(3448);
			intervals.add(3758);
			intervals.add(4070);
			intervals.add(4371);
			intervals.add(4678);
			intervals.add(4982);
			intervals.add(5287);
			intervals.add(5591);
			intervals.add(5902);
			intervals.add(6217);
			intervals.add(6534);
			intervals.add(6850);
			intervals.add(7164);
			// print found intervals
			// for (int i = 0; i < intervals.size(); i++) {
			// System.out.println(intervals.get(i));
			// }

			// System.out.println("fim");
			// System.exit(0);
			// third, ###############################################

			int nextInterval = 1;

			for (int second = 1; second <= total; second++) {

				if (second == intervals.get(nextInterval)) {
					interval = intervals.get(nextInterval);
					// Log.print("interval ..."+interval );

				}

				// last interval
				if (second == total) {
					// start = total - (total % interval);
					end = total;

					// if the interval is equal to total time
					if (solutionList.size() < 1) {
						Log.printLine(algorithm + " Intervalo1: " + start + " - " + end);
						solutionList.add(fillInitialSolution(start, end, interval, total));

					} else {
						Log.printLine(algorithm + " Intervalo3: " + start + " - " + end);
						nextSolution = Placement.run(solutionList.get(solutionList.size() - 1), algorithm).copy();
						solutionList.add(classifier(nextSolution, start, end, interval, total));

					}
					solutionList.get(solutionList.size() - 1).print();

					// find out how many migrations were done
					Log.printLine(solutionList.get(solutionList.size() - 1)
							.getNumberOfMigrations(solutionList.get(solutionList.size() - 2)));
					nMig.add(solutionList.get(solutionList.size() - 1)
							.getNumberOfMigrations(solutionList.get(solutionList.size() - 2)));

					break;
				}

				// middle intervals
				if (second % interval == 0 && !first) {
					end += interval;

					Log.printLine(algorithm + " Intervalo2: " + start + " - " + end);
					nextSolution = Placement.run(solutionList.get(solutionList.size() - 1), algorithm).copy();

					solutionList.add(classifier(nextSolution, start, end, interval, total));

					solutionList.get(solutionList.size() - 1).print();

					// find out how many migrations were done
					Log.printLine(solutionList.get(solutionList.size() - 1)
							.getNumberOfMigrations(solutionList.get(solutionList.size() - 2)));
					nMig.add(solutionList.get(solutionList.size() - 1)
							.getNumberOfMigrations(solutionList.get(solutionList.size() - 2)));

					start += interval;
					count++;
				}

				// first interval
				if (second % interval == 0 && first) {
					end += interval;

					Log.printLine(algorithm + " Intervalo1: " + start + " - " + end);
					solutionList.add(fillInitialSolution(start, end, interval, total));

					solutionList.get(solutionList.size() - 1).print();

					start += interval;
					count++;
					first = false;

				}

			}

			Log.printLine("Algorithm: " + algorithm);
			for (int i = 0; i < solutionList.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(util.printDouble(solutionList.get(i).getTotalInterferenceCost()));

			}

			Log.printLine("\nMigrations:\n");
			for (int i = 0; i < nMig.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(nMig.get(i));

			}

			Log.printLine("\ninterf with mig :\n");
			Log.printConcatLine(util.printDouble(solutionList.get(0).getTotalInterferenceCost()));
			for (int i = 1; i < solutionList.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(util
						.printDouble(solutionList.get(i).getTotalInterferenceCost() + (nMig.get(i - 1) * migvalue))); // interference
																														// index
			}
		}

		// Even Scheduler
		if (approach.equals("EVEN")) {
			algorithm = "RR";
			// there are no intervals set, the entire execution is done through RR algorithm
			start = 1;
			end = total;

			Log.printLine(algorithm + " Intervalo1: " + start + " - " + end);
			solutionList.add(fillInitialSolution(start, end, interval, total));

			solutionList.get(solutionList.size() - 1).print();

			Log.printLine("Algorithm: " + algorithm);
			for (int i = 0; i < solutionList.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(util.printDouble(solutionList.get(i).getTotalInterferenceCost()));

			}

		}

		// CIAPA Scheduler
		if (approach.equals("CIAPA")) {
			algorithm = "SA";
			// there are only one interval to analyze data, after that the placement does
			// not change
			// this solution uses SimulatedAnnealing algorithm

			// analysis interval (first)
			interval = 600;
			end += interval;

			Log.printLine(algorithm + " Intervalo1: " + start + " - " + end);
			solutionList.add(fillInitialSolution(start, end, interval, total));

			solutionList.get(solutionList.size() - 1).print();

			start += interval;

			// Next interval (until the end)
			end = total;

			Log.printLine(algorithm + " Intervalo3: " + start + " - " + end);
			nextSolution = Placement.run(solutionList.get(solutionList.size() - 1), algorithm).copy();
			solutionList.add(classifier(nextSolution, start, end, interval, total));

			solutionList.get(solutionList.size() - 1).print();

			// find out how many migrations were done
			Log.printLine(solutionList.get(solutionList.size() - 1)
					.getNumberOfMigrations(solutionList.get(solutionList.size() - 2)));
			nMig.add(solutionList.get(solutionList.size() - 1)
					.getNumberOfMigrations(solutionList.get(solutionList.size() - 2)));

			
			
			
			Log.printLine("Algorithm: " + algorithm);
			for (int i = 0; i < solutionList.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(util.printDouble(solutionList.get(i).getTotalInterferenceCost()));

			}

			Log.printLine("\nMigrations:\n");
			for (int i = 0; i < nMig.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(nMig.get(i));

			}

			Log.printLine("\ninterf with mig :\n");
			Log.printConcatLine(util.printDouble(solutionList.get(0).getTotalInterferenceCost()));
			for (int i = 1; i < solutionList.size(); i++) {
				// Log.printLine((i+1) + " - "+algorithm+" " +
				// util.printDouble(solutionList.get(i).getTotalInterferenceCost()));
				Log.printConcatLine(util
						.printDouble(solutionList.get(i).getTotalInterferenceCost() + (nMig.get(i - 1) * migvalue))); // interference
																														// index
			}
			
		}

		long totalTime = System.currentTimeMillis() - startT;
		Log.printLine("End of Simulation ... (" + totalTime / 1000 / 60 + " min - " + totalTime / 1000 % 60 + " sec)");
		System.exit(0);

	}

	public Solution classifier(Solution solution, int start, int end, int interval, int ttime) {
		// solution.print();
		// for each container/cloudlet (in given VM)
		for (int i = 0; i < solution.getSize(); i++) {
			HashMap cl = (HashMap) solution.getPlacement().get(i);

			IntContainerCloudlet cloudlet = cloudletList.get(i);

			MLCR = MLC.getMLClass(cloudlet.getInterferenceMetrics(), start, end);

			solution.updateCloudletInterferenceCost((i + 1), MLCR.getCloudletCost());

		}
		// solution.print();
		solution.setAdditionalParameters(start, end, ttime);

		return solution;
	}

	public Solution fillInitialSolution(int start, int end, int interval, int ttime) {

		Solution solution = new Solution();

		List<? extends IntContainerHost> list = getVmAllocationPolicy().getContainerHostList();
		// for each host...
		for (int i = 0; i < list.size(); i++) {

			IntContainerHost host = list.get(i);
			// for each container/cloudlet (in given VM)
			for (int x = 0; x < host.getVmList().get(0).getContainerList().size(); x++) {
				// Log.printLine(i+ " " + x);
				IntContainer container = host.getVmList().get(0).getContainerList().get(x);
				IntContainerCloudlet cloudlet = cloudletList.get(container.getId() - 1);

				MLCR = MLC.getMLClass(cloudlet.getInterferenceMetrics(), start, end);
				solution.addCloudletToSolution(host.getId(), host.getNumberOfPes(), cloudlet.getCloudletId(),
						container.getNumberOfPes(), MLCR.getCloudletCost());

			}
		}
		solution.setAdditionalParameters(start, end, ttime);

		return solution;
	}

	public double getInterferenceCost(int start, int end) {
		long startT = System.currentTimeMillis();
		double hostcost = 0;
		double totalcost = 0;

		List<? extends IntContainerHost> list = getVmAllocationPolicy().getContainerHostList();
		// for each host...
		for (int i = 0; i < list.size(); i++) {
			long startT1 = System.currentTimeMillis();
			IntContainerHost host = list.get(i);
			// for each container/cloudlet (in given VM)
			for (int x = 0; x < host.getVmList().get(0).getContainerList().size(); x++) {
				IntContainer container = host.getVmList().get(0).getContainerList().get(x);
				IntContainerCloudlet cloudlet = cloudletList.get(container.getId() - 1);

				MLCR = MLC.getMLClass(cloudlet.getInterferenceMetrics(), start, end);

				hostcost += MLCR.getCloudletCost()
						/ ((double) container.getNumberOfPes() / (double) host.getNumberOfPes());
				Log.printLine("Host" + host.getId() + " " + String.format("%.2f", hostcost) + " cloudlet"
						+ cloudlet.getCloudletId() + " " + String.format("%.2f", MLCR.getCloudletCost()
								/ ((double) container.getNumberOfPes() / (double) host.getNumberOfPes())));
			}
			totalcost += hostcost;
			Log.printLine("Total cost: " + String.format("%.2f", totalcost));
			long totalTime1 = System.currentTimeMillis() - startT1;
			Log.printLine("Host #" + (i + 1) + " " + String.format("%.2f", hostcost) + "  :" + totalTime1 / 1000 / 60
					+ " min - " + totalTime1 / 1000 % 60 + " sec");
			hostcost = 0;
		}

		long totalTime = System.currentTimeMillis() - startT;
		Log.printLine("Total time .. " + totalTime / 1000 / 60 + " min - " + totalTime / 1000 % 60 + " sec");

		return totalcost;
	}

	public double getInterferenceCost(int start, int end, int ttime, int interval) {
		long startT = System.currentTimeMillis();
		double hostcost = 0;
		double totalcost = 0;

		List<? extends IntContainerHost> list = getVmAllocationPolicy().getContainerHostList();
		// for each host...
		for (int i = 0; i < list.size(); i++) {
			int justOne = 0;
			long startT1 = System.currentTimeMillis();
			IntContainerHost host = list.get(i);
			// for each container/cloudlet (in given VM)
			for (int x = 0; x < host.getVmList().get(0).getContainerList().size(); x++) {
				IntContainer container = host.getVmList().get(0).getContainerList().get(x);
				IntContainerCloudlet cloudlet = cloudletList.get(container.getId() - 1);

				MLCR = MLC.getMLClass(cloudlet.getInterferenceMetrics(), start, end);
				hostcost += MLCR.getCloudletCost()
						/ ((double) container.getNumberOfPes() / (double) host.getNumberOfPes());
				Log.printLine("Host" + host.getId() + " " + String.format("%.2f", hostcost) + " cloudlet"
						+ cloudlet.getCloudletId() + " " + String.format("%.2f", MLCR.getCloudletCost()
								/ ((double) container.getNumberOfPes() / (double) host.getNumberOfPes())));
				justOne++;
			}

			if (justOne < 1) {
				Log.printLine(" @@@@ ERROR (getInterferenceCost) : LESS THAN ONE CLOUDLET PER CONTAINER");
			}
			// if there is only one cloudlet(container) inside a given host(vm),
			// there will be no interference incidence, hence the host cost is lead to 0.
			if (justOne == 1) {
				hostcost = 0;
			}

			totalcost += hostcost;

			Log.printLine("Total cost: " + String.format("%.2f", totalcost));
			long totalTime1 = System.currentTimeMillis() - startT1;
			Log.printLine("Host #" + (i + 1) + " " + String.format("%.2f", hostcost) + "  :" + totalTime1 / 1000 / 60
					+ " min - " + totalTime1 / 1000 % 60 + " sec");
			hostcost = 0;
		}

		long totalTime = System.currentTimeMillis() - startT;
		Log.printLine("Total time .. " + totalTime / 1000 / 60 + " min - " + totalTime / 1000 % 60 + " sec");

		return (totalcost * interval) / ttime;
	}

	/**
	 * Verifies if some cloudlet inside this PowerDatacenter already finished. If
	 * yes, send it to the User/Broker
	 *
	 * @pre $none
	 * @post $none
	 */

	protected void checkCloudletCompletion() {
		// Log.printLine("DC:checkCloudletCompletion");
		List<? extends IntContainerHost> list = getVmAllocationPolicy().getContainerHostList();
		for (int i = 0; i < list.size(); i++) {
			IntContainerHost host = list.get(i);
			for (IntContainerVm vm : host.getVmList()) {
				for (IntContainer container : vm.getContainerList()) {
					while (container.getContainerCloudletScheduler().isFinishedCloudlets()) {
						Cloudlet cl = container.getContainerCloudletScheduler().getNextFinishedCloudlet();
						if (cl != null) {
							sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds a file into the resource's storage before the experiment starts. If the
	 * file is a master file, then it will be registered to the RC when the
	 * experiment begins.
	 *
	 * @param file a DataCloud file
	 * @return a tag number denoting whether this operation is a success or not
	 */
	public int addFile(File file) {
		// Log.printLine("DC:addFile");
		if (file == null) {
			return DataCloudTags.FILE_ADD_ERROR_EMPTY;
		}

		if (contains(file.getName())) {
			return DataCloudTags.FILE_ADD_ERROR_EXIST_READ_ONLY;
		}

		// check storage space first
		if (getStorageList().size() <= 0) {
			return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
		}

		Storage tempStorage = null;
		int msg = DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;

		for (int i = 0; i < getStorageList().size(); i++) {
			tempStorage = getStorageList().get(i);
			if (tempStorage.getAvailableSpace() >= file.getSize()) {
				tempStorage.addFile(file);
				msg = DataCloudTags.FILE_ADD_SUCCESSFUL;
				break;
			}
		}

		return msg;
	}

	/**
	 * Checks whether the resource has the given file.
	 *
	 * @param file a file to be searched
	 * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
	 */
	protected boolean contains(File file) {
		// Log.printLine("DC:contains");
		if (file == null) {
			return false;
		}
		return contains(file.getName());
	}

	/**
	 * Checks whether the resource has the given file.
	 *
	 * @param fileName a file name to be searched
	 * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
	 */
	protected boolean contains(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return false;
		}

		Iterator<Storage> it = getStorageList().iterator();
		Storage storage = null;
		boolean result = false;

		while (it.hasNext()) {
			storage = it.next();
			if (storage.contains(fileName)) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * Deletes the file from the storage. Also, check whether it is possible to
	 * delete the file from the storage.
	 *
	 * @param fileName the name of the file to be deleted
	 * @return the error message
	 */
	private int deleteFileFromStorage(String fileName) {
		// Log.printLine("DC:deleteFileFromStorage");
		Storage tempStorage = null;
		File tempFile = null;
		int msg = DataCloudTags.FILE_DELETE_ERROR;

		for (int i = 0; i < getStorageList().size(); i++) {
			tempStorage = getStorageList().get(i);
			tempFile = tempStorage.getFile(fileName);
			tempStorage.deleteFile(fileName, tempFile);
			msg = DataCloudTags.FILE_DELETE_SUCCESSFUL;
		} // end for

		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		// Log.printLine("DC:shutdownEntity");
		Log.printConcatLine(getName(), " is shutting down...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		// Log.printLine("DC:startEntity");
		Log.printConcatLine(getName(), " is starting...");
		// this resource should register to regional GIS.
		// However, if not specified, then register to system GIS (the
		// default CloudInformationService) entity.
		int gisID = CloudSim.getEntityId(regionalCisName);
		if (gisID == -1) {
			gisID = CloudSim.getCloudInfoServiceEntityId();
		}

		// send the registration to GIS
		sendNow(gisID, CloudSimTags.REGISTER_RESOURCE, getId());
		// Below method is for a child class to override
		registerOtherEntity();
	}

	/**
	 * Gets the host list.
	 *
	 * @return the host list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerHost> List<T> getHostList() {
		// Log.printLine("DC:getHostList");
		return (List<T>) getCharacteristics().getHostList();
	}

	/**
	 * Gets the characteristics.
	 *
	 * @return the characteristics
	 */
	protected IntContainerDataCenterCharacteristics getCharacteristics() {
		// Log.printLine("DC:getCharacteristics");
		return characteristics;
	}

	/**
	 * Sets the characteristics.
	 *
	 * @param characteristics the new characteristics
	 */
	protected void setCharacteristics(IntContainerDataCenterCharacteristics characteristics) {
		// Log.printLine("DC:setCharacteristics");
		this.characteristics = characteristics;
	}

	/**
	 * Gets the regional cis name.
	 *
	 * @return the regional cis name
	 */
	protected String getRegionalCisName() {
		// Log.printLine("DC:getRegionalCisName");
		return regionalCisName;
	}

	/**
	 * Sets the regional cis name.
	 *
	 * @param regionalCisName the new regional cis name
	 */
	protected void setRegionalCisName(String regionalCisName) {
		// Log.printLine("DC:setRegionalCisName");
		this.regionalCisName = regionalCisName;
	}

	/**
	 * Gets the vm allocation policy.
	 *
	 * @return the vm allocation policy
	 */
	public IntContainerVmAllocationPolicy getVmAllocationPolicy() {
		// Log.printLine("DC:getVmAllocationPolicy");
		return vmAllocationPolicy;
	}

	/**
	 * Sets the vm allocation policy.
	 *
	 * @param vmAllocationPolicy the new vm allocation policy
	 */
	protected void setVmAllocationPolicy(IntContainerVmAllocationPolicy vmAllocationPolicy) {
		// Log.printLine("DC:setVmAllocationPolicy");
		this.vmAllocationPolicy = vmAllocationPolicy;
	}

	/**
	 * Gets the last process time.
	 *
	 * @return the last process time
	 */
	protected double getLastProcessTime() {
		return lastProcessTime;
	}

	/**
	 * Sets the last process time.
	 *
	 * @param lastProcessTime the new last process time
	 */
	protected void setLastProcessTime(double lastProcessTime) {
		this.lastProcessTime = lastProcessTime;
	}

	/**
	 * Gets the storage list.
	 *
	 * @return the storage list
	 */
	protected List<Storage> getStorageList() {
		return storageList;
	}

	/**
	 * Sets the storage list.
	 *
	 * @param storageList the new storage list
	 */
	protected void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}

	/**
	 * Gets the vm list.
	 *
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntContainerVm> List<T> getContainerVmList() {
		return (List<T>) containerVmList;
	}

	/**
	 * Sets the vm list.
	 *
	 * @param containerVmList the new vm list
	 */
	protected <T extends IntContainerVm> void setContainerVmList(List<T> containerVmList) {
		this.containerVmList = containerVmList;
	}

	/**
	 * Gets the scheduling interval.
	 *
	 * @return the scheduling interval
	 */
	protected double getSchedulingInterval() {
		return schedulingInterval;
	}

	/**
	 * Sets the scheduling interval.
	 *
	 * @param schedulingInterval the new scheduling interval
	 */
	protected void setSchedulingInterval(double schedulingInterval) {
		this.schedulingInterval = schedulingInterval;
	}

	public IntContainerAllocationPolicy getContainerAllocationPolicy() {
		return containerAllocationPolicy;
	}

	public void setContainerAllocationPolicy(IntContainerAllocationPolicy containerAllocationPolicy) {
		this.containerAllocationPolicy = containerAllocationPolicy;
	}

	public <T extends IntContainer> List<T> getContainerList() {
		return (List<T>) containerList;
	}

	public void setContainerList(List<? extends IntContainer> containerList) {
		this.containerList = containerList;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public String getLogAddress() {
		return logAddress;
	}

	public void setLogAddress(String logAddress) {
		this.logAddress = logAddress;
	}
}
