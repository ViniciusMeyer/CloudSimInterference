package cloudsim.interference;

import java.util.ArrayList;
import java.util.List;

import cloudsim.interference.IntContainerDataCenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.core.CloudSim;

public class IntContainerHost {
	
	/**
     * The id.
     */
    private int id;

    /**
     * The allocation policy.
     */
    private IntContainerVmScheduler containerVmScheduler;

    /**
     * The vm list.
     */
    private final List<? extends IntContainerVm> vmList = new ArrayList<>();
    
    /**
     * The pe list.
     */
    private List<? extends IntContainerVmPe> peList;

    /**
     * Tells whether this machine is working properly or has failed.
     */
    private boolean failed;

    /**
     * The vms migrating in.
     */
    private final List<IntContainerVm> vmsMigratingIn = new ArrayList<>();
    /**
     * The datacenter where the host is placed.
     */
    private IntContainerDataCenter datacenter;

/**
     * Instantiates a new host.
     *
     * @param id             the id
     * @param peList         the pe list
     * @param containerVmScheduler    the vm scheduler
     */
    public IntContainerHost(
            int id,
            List<? extends IntContainerVmPe> peList,
            IntContainerVmScheduler containerVmScheduler) {
        setId(id);
        setContainerVmScheduler(containerVmScheduler);
        setPeList(peList);
        setFailed(false);

    }

    /**
     * Requests updating of processing of cloudlets in the VMs running in this host.
     *
     * @param currentTime the current time
     * @return expected time of completion of the next cloudlet in all VMs in this host.
     * Double.MAX_VALUE if there is no future events expected in this host
     * @pre currentTime >= 0.0
     * @post $none
     */
    public double updateContainerVmsProcessing(double currentTime) {
        double smallerTime = Double.MAX_VALUE;

        for (IntContainerVm containerVm : getVmList()) {
            double time = containerVm.updateVmProcessing(currentTime, getContainerVmScheduler().getAllocatedMipsForContainerVm(containerVm));
            if (time > 0.0 && time < smallerTime) {
                smallerTime = time;
            }
        }

        return smallerTime;
    }

    /**
     * Adds the migrating in vm.
     *
     * @param containerVm the vm
     */
    public void addMigratingInContainerVm(IntContainerVm containerVm) {
        //Log.printLine("Host: addMigratingInContainerVm:......");
        containerVm.setInMigration(true);

        if (!getVmsMigratingIn().contains(containerVm)) {
           
        	getContainerVmScheduler().getVmsMigratingIn().add(containerVm.getUid());
            if (!getContainerVmScheduler().allocatePesForVm(containerVm, containerVm.getCurrentRequestedMips())) {
                Log.printConcatLine("[VmScheduler.addMigratingInContainerVm] Allocation of VM #", containerVm.getId(), " to Host #",
                        getId(), " failed by MIPS");
                System.exit(0);
            }

            getVmsMigratingIn().add(containerVm);
            getVmList().add(containerVm);
            updateContainerVmsProcessing(CloudSim.clock());
            containerVm.getHost().updateContainerVmsProcessing(CloudSim.clock());
        }
    }

        /**
         * Removes the migrating in vm.
         *
         * @param vm the vm
         */
        public void removeMigratingInContainerVm(IntContainerVm vm) {
            containerVmDeallocate(vm);
            getVmsMigratingIn().remove(vm);
            getVmList().remove(vm);
            getContainerVmScheduler().getVmsMigratingIn().remove(vm.getUid());
            vm.setInMigration(false);
        }

    /**
     * Reallocate migrating in vms.
     */
    public void reallocateMigratingInContainerVms() {
        for (IntContainerVm containerVm : getVmsMigratingIn()) {
            if (!getVmList().contains(containerVm)) {
                getVmList().add(containerVm);
            }
            if (!getContainerVmScheduler().getVmsMigratingIn().contains(containerVm.getUid())) {
                getContainerVmScheduler().getVmsMigratingIn().add(containerVm.getUid());
            }
            getContainerVmScheduler().allocatePesForVm(containerVm, containerVm.getCurrentRequestedMips());
            
        }
    }

    /**
     * Checks if is suitable for vm.
     *
     * @param vm the vm
     * @return true, if is suitable for vm
     */
    public boolean isSuitableForContainerVm(IntContainerVm vm) {
        //Log.printLine("Host: Is suitable for VM???......");
        return (getContainerVmScheduler().getPeCapacity() >= vm.getCurrentRequestedMaxMips()
                && getContainerVmScheduler().getAvailableMips() >= vm.getCurrentRequestedTotalMips());
    }

    /**
     * Allocates PEs and memory to a new VM in the Host.
     *
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     */
    public boolean containerVmCreate(IntContainerVm vm) {
        //Log.printLine("Host: Create VM???......" + vm.getId());
      
        if (!getContainerVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
            Log.printConcatLine("[VmScheduler.containerVmCreate] Allocation of VM #", vm.getId(), " to Host #", getId(),
                    " failed by MIPS");
            return false;
        }

        getVmList().add(vm);
        vm.setHost(this);
        return true;
    }

    /**
     * Destroys a VM running in the host.
     *
     * @param containerVm the VM
     * @pre $none
     * @post $none
     */
    public void containerVmDestroy(IntContainerVm containerVm) {
        //Log.printLine("Host:  Destroy Vm:.... " + containerVm.getId());
        if (containerVm != null) {
            containerVmDeallocate(containerVm);
            getVmList().remove(containerVm);
          containerVm.setHost(null);
        }
    }

    /**
     * Destroys all VMs running in the host.
     *
     * @pre $none
     * @post $none
     */
    public void containerVmDestroyAll() {
        //Log.printLine("Host: Destroy all Vms");
        containerVmDeallocateAll();
        for (IntContainerVm containerVm : getVmList()) {
            containerVm.setHost(null);
        }
        getVmList().clear();
    }

    /**
     * Deallocate all hostList for the VM.
     *
     * @param containerVm the VM
     */
    protected void containerVmDeallocate(IntContainerVm containerVm) {
        //Log.printLine("Host: Deallocated the VM:......" + containerVm.getId());
        getContainerVmScheduler().deallocatePesForVm(containerVm);
    }

    /**
     * Deallocate all hostList for the VM.
     */
    protected void containerVmDeallocateAll() {
        //Log.printLine("Host: Deallocate all the Vms......");
        getContainerVmScheduler().deallocatePesForAllContainerVms();
    }

    /**
     * Returns a VM object.
     *
     * @param vmId   the vm id
     * @param userId ID of VM's owner
     * @return the virtual machine object, $null if not found
     * @pre $none
     * @post $none
     */
    public IntContainerVm getContainerVm(int vmId, int userId) {
        //Log.printLine("Host: get the vm......" + vmId);
        //Log.printLine("Host: the vm list size:......" + getVmList().size());
        for (IntContainerVm containerVm : getVmList()) {
            if (containerVm.getId() == vmId && containerVm.getUserId() == userId) {
                return containerVm;
            }
        }
        return null;
    }

    /**
     * Gets the pes number.
     *
     * @return the pes number
     */
    public int getNumberOfPes() {
        //Log.printLine("Host: get the peList Size......" + getPeList().size());
        return getPeList().size();
    }

    /**
     * Gets the free pes number.
     *
     * @return the free pes number
     */
    public int getNumberOfFreePes() {
        //Log.printLine("Host: get the free Pes......" + ContainerVmPeList.getNumberOfFreePes(getPeList()));
        return IntContainerVmPeList.getNumberOfFreePes(getPeList());
    }

    /**
     * Gets the total mips.
     *
     * @return the total mips
     */
    public int getTotalMips() {
        //Log.printLine("Host: get the total mips......" + ContainerVmPeList.getTotalMips(getPeList()));
        return IntContainerVmPeList.getTotalMips(getPeList());
    }

    /**
     * Allocates PEs for a VM.
     *
     * @param containerVm        the vm
     * @param mipsShare the mips share
     * @return $true if this policy allows a new VM in the host, $false otherwise
     * @pre $none
     * @post $none
     */
    public boolean allocatePesForContainerVm(IntContainerVm containerVm, List<Double> mipsShare) {
        //Log.printLine("Host: allocate Pes for Vm:......" + containerVm.getId());
        return getContainerVmScheduler().allocatePesForVm(containerVm, mipsShare);
    }

    /**
     * Releases PEs allocated to a VM.
     *
     * @param containerVm the vm
     * @pre $none
     * @post $none
     */
    public void deallocatePesForContainerVm(IntContainerVm containerVm) {
        //Log.printLine("Host: deallocate Pes for Vm:......" + containerVm.getId());
        getContainerVmScheduler().deallocatePesForVm(containerVm);
    }

    /**
     * Returns the MIPS share of each Pe that is allocated to a given VM.
     *
     * @param containerVm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     * @pre $none
     * @post $none
     */
    public List<Double> getAllocatedMipsForContainerVm(IntContainerVm containerVm) {
        //Log.printLine("Host: get allocated Pes for Vm:......" + containerVm.getId());
        return getContainerVmScheduler().getAllocatedMipsForContainerVm(containerVm);
    }

    /**
     * Gets the total allocated MIPS for a VM over all the PEs.
     *
     * @param containerVm the vm
     * @return the allocated mips for vm
     */
    public double getTotalAllocatedMipsForContainerVm(IntContainerVm containerVm) {
        //Log.printLine("Host: total allocated Pes for Vm:......" + containerVm.getId());
        return getContainerVmScheduler().getTotalAllocatedMipsForContainerVm(containerVm);
    }

    /**
     * Returns maximum available MIPS among all the PEs.
     *
     * @return max mips
     */
    public double getMaxAvailableMips() {
        //Log.printLine("Host: Maximum Available Pes:......");
        return getContainerVmScheduler().getMaxAvailableMips();
    }

    /**
     * Gets the free mips.
     *
     * @return the free mips
     */
    public double getAvailableMips() {
        //Log.printLine("Host: Get available Mips");
        return getContainerVmScheduler().getAvailableMips();
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    protected void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the VM scheduler.
     *
     * @return the VM scheduler
     */
    public IntContainerVmScheduler getContainerVmScheduler() {
        return containerVmScheduler;
    }

    /**
     * Sets the VM scheduler.
     *
     * @param vmScheduler the vm scheduler
     */
    protected void setContainerVmScheduler(IntContainerVmScheduler vmScheduler) {
        this.containerVmScheduler = vmScheduler;
    }

    /**
     * Gets the pe list.
     *
     * @param <T> the generic type
     * @return the pe list
     */
    @SuppressWarnings("unchecked")
    public <T extends IntContainerVmPe> List<T> getPeList() {
        return (List<T>) peList;
    }

    /**
     * Sets the pe list.
     *
     * @param <T>    the generic type
     * @param containerVmPeList the new pe list
     */
    protected <T extends IntContainerVmPe> void setPeList(List<T> containerVmPeList) {
        this.peList = containerVmPeList;
    }

    /**
     * Gets the vm list.
     *
     * @param <T> the generic type
     * @return the vm list
     */
    @SuppressWarnings("unchecked")
    public <T extends IntContainerVm> List<T> getVmList() {
        return (List<T>) vmList;
    }

    /**
     * Checks if is failed.
     *
     * @return true, if is failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Sets the PEs of this machine to a FAILED status. NOTE: <tt>resName</tt> is used for debugging
     * purposes, which is <b>ON</b> by default. Use {@link #setFailed(boolean)} if you do not want
     * this information.
     *
     * @param resName the name of the resource
     * @param failed  the failed
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setFailed(String resName, boolean failed) {
        // all the PEs are failed (or recovered, depending on fail)
        this.failed = failed;
        IntContainerVmPeList.setStatusFailed(getPeList(), resName, getId(), failed);
        return true;
    }

    /**
     * Sets the PEs of this machine to a FAILED status.
     *
     * @param failed the failed
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setFailed(boolean failed) {
        // all the PEs are failed (or recovered, depending on fail)
        this.failed = failed;
        IntContainerVmPeList.setStatusFailed(getPeList(), failed);
        return true;
    }

    /**
     * Sets the particular Pe status on this Machine.
     *
     * @param peId   the pe id
     * @param status Pe status, either <tt>Pe.FREE</tt> or <tt>Pe.BUSY</tt>
     * @return <tt>true</tt> if the Pe status has changed, <tt>false</tt> otherwise (Pe id might not
     * be exist)
     * @pre peID >= 0
     * @post $none
     */
    public boolean setPeStatus(int peId, int status) {
        return IntContainerVmPeList.setPeStatus(getPeList(), peId, status);
    }

    /**
     * Gets the vms migrating in.
     *
     * @return the vms migrating in
     */
    public List<IntContainerVm> getVmsMigratingIn() {
        return vmsMigratingIn;
    }

    /**
     * Gets the data center.
     *
     * @return the data center where the host runs
     */
    public IntContainerDataCenter getDatacenter() {
    	return datacenter;
    }
    
    /**
     * Sets the data center.
     *
     * @param datacenter the data center from this host
     */
    public void setDatacenter(IntContainerDataCenter datacenter) {
    	this.datacenter = datacenter;
    }
         


}


