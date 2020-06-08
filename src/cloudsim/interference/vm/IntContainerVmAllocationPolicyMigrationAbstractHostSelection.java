package cloudsim.interference.vm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Log;

import cloudsim.interference.IntContainerHost;
import cloudsim.interference.IntHostSelectionPolicy;
import cloudsim.interference.datacenter.IntContainerDataCenter;
import cloudsim.interference.list.IntContainerHostList;

public class IntContainerVmAllocationPolicyMigrationAbstractHostSelection extends IntContainerVmAllocationPolicyMigrationAbstract {

    private IntHostSelectionPolicy hostSelectionPolicy;
    private double utilizationThreshold = 0.9;
    private double underUtilizationThreshold = 0.7;

    /**
     * Instantiates a new power vm allocation policy migration abstract.
     *
     * @param hostSelectionPolicy
     * @param hostList            the host list
     * @param vmSelectionPolicy   the vm selection policy
     */
    public IntContainerVmAllocationPolicyMigrationAbstractHostSelection(List<? extends IntContainerHost> hostList, IntContainerVmSelectionPolicy vmSelectionPolicy, IntHostSelectionPolicy hostSelectionPolicy, double OlThreshold, double UlThreshold) {
        super(hostList, vmSelectionPolicy);
        setHostSelectionPolicy(hostSelectionPolicy);
        setUtilizationThreshold(OlThreshold);
        setUnderUtilizationThreshold(UlThreshold);
    }

    @Override
    /**
     * Find host for vm.
     *
     * @param vm            the vm
     * @param excludedHosts the excluded hosts
     * @return the power host
     */
    public IntContainerHost findHostForVm(IntContainerVm vm, Set<? extends IntContainerHost> excludedHosts) {
        IntContainerHost allocatedHost = null;
        Boolean find = false;
        Set<IntContainerHost> excludedHost1 = new HashSet<>();
        excludedHost1.addAll(excludedHosts);
        while (!find) {
            IntContainerHost host = getHostSelectionPolicy().getHost(getContainerHostList(), vm, excludedHost1);
            if (host == null) {
                return allocatedHost;
            }
            if (host.isSuitableForContainerVm(vm)) {
                find = true;
                allocatedHost = (IntContainerHost) host;
            } else {
                excludedHost1.add(host);
                if (getContainerHostList().size() == excludedHost1.size()) {

                    return null;

                }
            }

        }
        return allocatedHost;
    }
    
    


    public IntHostSelectionPolicy getHostSelectionPolicy() {
        return hostSelectionPolicy;
    }

    public void setHostSelectionPolicy(IntHostSelectionPolicy hostSelectionPolicy) {
        this.hostSelectionPolicy = hostSelectionPolicy;
    }


    /**
     * Checks if is host over utilized.
     *
     * @param host the _host
     * @return true, if is host over utilized
     */
    @Override
    protected boolean isHostOverUtilized(IntContainerHost host) {
        
    	Log.printLine("ERRRRO");
    	/*addHistoryEntry(host, getUtilizationThreshold());
        double totalRequestedMips = 0;
        for (IntContainerVm vm : host.getVmList()) {
            totalRequestedMips += vm.getCurrentRequestedTotalMips();
        }
        double utilization = totalRequestedMips / host.getTotalMips();
        return utilization > getUtilizationThreshold();*/
    	return true;
    }

    @Override
    protected boolean isHostUnderUtilized(IntContainerHost host) {
        return false;
    }

    /**
     * Sets the utilization threshold.
     *
     * @param utilizationThreshold the new utilization threshold
     */
    protected void setUtilizationThreshold(double utilizationThreshold) {
        this.utilizationThreshold = utilizationThreshold;
    }

    /**
     * Gets the utilization threshold.
     *
     * @return the utilization threshold
     */
    protected double getUtilizationThreshold() {
        return utilizationThreshold;
    }

    public double getUnderUtilizationThreshold() {
        return underUtilizationThreshold;
    }

    public void setUnderUtilizationThreshold(double underUtilizationThreshold) {
        this.underUtilizationThreshold = underUtilizationThreshold;
    }


    @Override
    /**
     * Gets the under utilized host.
     *Checks if the utilization is under the threshold then counts it as underUtilized :)
     * @param excludedHosts the excluded hosts
     * @return the under utilized host
     */
    protected IntContainerHost getUnderUtilizedHost(Set<? extends IntContainerHost> excludedHosts) {

        List<IntContainerHost> underUtilizedHostList = getUnderUtilizedHostList(excludedHosts);
        if (underUtilizedHostList.size() == 0) {

            return null;
        }
        IntContainerHostList.sortByCpuUtilizationDescending(underUtilizedHostList);
//        Log.print(String.format("The under Utilized Hosts are %d", underUtilizedHostList.size()));
        IntContainerHost underUtilizedHost = (IntContainerHost) underUtilizedHostList.get(0);

        return underUtilizedHost;
    }


    /**
     * Gets the under utilized host.
     *
     * @param excludedHosts the excluded hosts
     * @return the under utilized host
     */
    protected List<IntContainerHost> getUnderUtilizedHostList(Set<? extends IntContainerHost> excludedHosts) {
        Log.printLine("ERRRRO");
    	List<IntContainerHost> underUtilizedHostList = new ArrayList<>();
        for (IntContainerHost host : this.<IntContainerHost>getContainerHostList()) {
            if (excludedHosts.contains(host)) {
                continue;
            }
            //double utilization = host.getUtilizationOfCpu();
            
            //if (!areAllVmsMigratingOutOrAnyVmMigratingIn(host) && utilization < getUnderUtilizationThreshold() && !areAllContainersMigratingOutOrAnyContainersMigratingIn(host)) {
            //    underUtilizedHostList.add(host);
            //}
        }
        return underUtilizedHostList;
    }


    @Override
    public void setDatacenter(IntContainerDataCenter datacenter) {
    }
}
