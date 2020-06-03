package cloudsim.interference;

import java.util.List;
import java.util.Map;

public abstract class IntContainerVmAllocationPolicy {

    /**
     * The host list.
     */
    private List<? extends IntContainerHost> containerHostList;

    /**
     * Allocates a new VmAllocationPolicy object.
     *
     * @param containerHostList Machines available in this Datacentre
     * @pre $none
     * @post $none
     */
    public IntContainerVmAllocationPolicy(List<? extends IntContainerHost> containerHostList) {
        setContainerHostList(containerHostList);
    }

    /**
     * Allocates a host for a given VM. The host to be allocated is the one that was already
     * reserved.
     *
     * @param vm virtual machine which the host is reserved to
     * @return $true if the host could be allocated; $false otherwise
     * @pre $none
     * @post $none
     */
    public abstract boolean allocateHostForVm(IntContainerVm vm);

    /**
     * Allocates a specified host for a given VM.
     *
     * @param vm virtual machine which the host is reserved to
     * @return $true if the host could be allocated; $false otherwise
     * @pre $none
     * @post $none
     */
    public abstract boolean allocateHostForVm(IntContainerVm vm, IntContainerHost host);

    /**
     * Optimize allocation of the VMs according to current utilization.
     * <p>
     * //     * @param vmList           the vm list
     * //     * @param utilizationBound the utilization bound
     * //     * @param time             the time
     *
     * @return the array list< hash map< string, object>>
     */
    public abstract List<Map<String, Object>> optimizeAllocation(List<? extends IntContainerVm> vmList);

    /**
     * Releases the host used by a VM.
     *
     * @param containerVm the vm
     * @pre $none
     * @post $none
     */
    public abstract void deallocateHostForVm(IntContainerVm containerVm);

    /**
     * Get the host that is executing the given VM belonging to the given user.
     *
     * @param containerVm the vm
     * @return the Host with the given vmID and userID; $null if not found
     * @pre $none
     * @post $none
     */
    public abstract IntContainerHost getHost(IntContainerVm containerVm);

    /**
     * Get the host that is executing the given VM belonging to the given user.
     *
     * @param vmId   the vm id
     * @param userId the user id
     * @return the Host with the given vmID and userID; $null if not found
     * @pre $none
     * @post $none
     */
    public abstract IntContainerHost getHost(int vmId, int userId);

    /**
     * Sets the host list.
     *
     * @param containerHostList the new host list
     */
    protected void setContainerHostList(List<? extends IntContainerHost> containerHostList) {
        this.containerHostList = containerHostList;
    }

    /**
     * Gets the host list.
     *
     * @return the host list
     */
    @SuppressWarnings("unchecked")
    public <T extends IntContainerHost> List<T> getContainerHostList() {
        return (List<T>) containerHostList;
    }

    public abstract void setDatacenter(IntContainerDataCenter datacenter);


}



