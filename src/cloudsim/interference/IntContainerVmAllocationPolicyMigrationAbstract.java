package cloudsim.interference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerHost;
import org.cloudbus.cloudsim.container.core.ContainerHostDynamicWorkload;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.core.PowerContainerHost;
import org.cloudbus.cloudsim.container.core.PowerContainerHostUtilizationHistory;
import org.cloudbus.cloudsim.container.core.PowerContainerVm;
import org.cloudbus.cloudsim.container.lists.PowerContainerVmList;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerVmAllocationAbstract;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;

public abstract class IntContainerVmAllocationPolicyMigrationAbstract extends IntContainerVmAllocationAbstract {

    /**
     * The vm selection policy.
     */
    private IntContainerVmSelectionPolicy vmSelectionPolicy;

    /**
     * The saved allocation.
     */
    private final List<Map<String, Object>> savedAllocation = new ArrayList<Map<String, Object>>();

    /**
     * The utilization history.
     */
    private final Map<Integer, List<Double>> utilizationHistory = new HashMap<Integer, List<Double>>();

    /**
     * The metric history.
     */
    private final Map<Integer, List<Double>> metricHistory = new HashMap<Integer, List<Double>>();

    /**
     * The time history.
     */
    private final Map<Integer, List<Double>> timeHistory = new HashMap<Integer, List<Double>>();

    /**
     * The execution time history vm selection.
     */
    private final List<Double> executionTimeHistoryVmSelection = new LinkedList<Double>();

    /**
     * The execution time history host selection.
     */
    private final List<Double> executionTimeHistoryHostSelection = new LinkedList<Double>();

    /**
     * The execution time history vm reallocation.
     */
    private final List<Double> executionTimeHistoryVmReallocation = new LinkedList<Double>();

    /**
     * The execution time history total.
     */
    private final List<Double> executionTimeHistoryTotal = new LinkedList<Double>();

    /**
     * Instantiates a new vm allocation policy migration abstract.
     *
     * @param hostList          the host list
     * @param vmSelectionPolicy the vm selection policy
     */
    public IntContainerVmAllocationPolicyMigrationAbstract(List<? extends IntContainerHost> hostList, IntContainerVmSelectionPolicy vmSelectionPolicy) {
        super(hostList);
        setVmSelectionPolicy(vmSelectionPolicy);

    }

    /**
     * Optimize allocation of the VMs according to current utilization.
     *
     * @param vmList the vm list
     * @return the array list< hash map< string, object>>
     * 
     * OTIMIZAÇÃO DE VMS ... nao sei se VMS serão alteradas(otimizadas)
     * 
     */
    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends IntContainerVm> vmList) {
    	Log.printLine("ERRO ====== getPowerAfterAllocation VM ALLOCATION POLICY 1");
    	/*
    	ExecutionTimeMeasurer.start("optimizeAllocationTotal");

        ExecutionTimeMeasurer.start("optimizeAllocationHostSelection");
        List<PowerContainerHostUtilizationHistory> overUtilizedHosts = getOverUtilizedHosts();
        getExecutionTimeHistoryHostSelection().add(ExecutionTimeMeasurer.end("optimizeAllocationHostSelection"));

        printOverUtilizedHosts(overUtilizedHosts);

        saveAllocation();

        ExecutionTimeMeasurer.start("optimizeAllocationVmSelection");
        List<? extends ContainerVm> vmsToMigrate = getVmsToMigrateFromHosts(overUtilizedHosts);
        getExecutionTimeHistoryVmSelection().add(ExecutionTimeMeasurer.end("optimizeAllocationVmSelection"));

        Log.printLine("Reallocation of VMs from the over-utilized hosts:");
        ExecutionTimeMeasurer.start("optimizeAllocationVmReallocation");
        List<Map<String, Object>> migrationMap = getNewVmPlacement(vmsToMigrate, new HashSet<ContainerHost>(overUtilizedHosts));
        getExecutionTimeHistoryVmReallocation().add(ExecutionTimeMeasurer.end("optimizeAllocationVmReallocation"));
        Log.printLine();

        migrationMap.addAll(getMigrationMapFromUnderUtilizedHosts(overUtilizedHosts, migrationMap));

        restoreAllocation();

        getExecutionTimeHistoryTotal().add(ExecutionTimeMeasurer.end("optimizeAllocationTotal"));

        return migrationMap;*/
    	List<Map<String, Object>> s = null;
    	
    	return s;
    }

    /**
     * Gets the migration map from under utilized hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     * @return the migration map from under utilized hosts
     * 
     * MAPA DE MIGRAÇÃO ... ... nao sei se VMS serão alteradas(otimizadas)
     */
    protected List<Map<String, Object>> getMigrationMapFromUnderUtilizedHosts(
        List<PowerContainerHostUtilizationHistory> overUtilizedHosts, List<Map<String, Object>> previouseMap) {
        List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
        List<PowerContainerHost> switchedOffHosts = getSwitchedOffHosts();
        
    	Log.printLine("ERRO ====== getPowerAfterAllocation VM ALLOCATION POLICY 2");
/*
        // over-utilized hosts + hosts that are selected to migrate VMs to from over-utilized hosts
        Set<PowerContainerHost> excludedHostsForFindingUnderUtilizedHost = new HashSet<>();
        excludedHostsForFindingUnderUtilizedHost.addAll(overUtilizedHosts);
        excludedHostsForFindingUnderUtilizedHost.addAll(switchedOffHosts);
        excludedHostsForFindingUnderUtilizedHost.addAll(extractHostListFromMigrationMap(previouseMap));

        // over-utilized + under-utilized hosts
        Set<PowerContainerHost> excludedHostsForFindingNewVmPlacement = new HashSet<>();
        excludedHostsForFindingNewVmPlacement.addAll(overUtilizedHosts);
        excludedHostsForFindingNewVmPlacement.addAll(switchedOffHosts);

        int numberOfHosts = getContainerHostList().size();

        while (true) {
            if (numberOfHosts == excludedHostsForFindingUnderUtilizedHost.size()) {
                break;
            }

            PowerContainerHost underUtilizedHost = getUnderUtilizedHost(excludedHostsForFindingUnderUtilizedHost);
            if (underUtilizedHost == null) {
                break;
            }

            Log.printConcatLine("Under-utilized host: host #", underUtilizedHost.getId(), "\n");

            excludedHostsForFindingUnderUtilizedHost.add(underUtilizedHost);
            excludedHostsForFindingNewVmPlacement.add(underUtilizedHost);

            List<? extends IntContainerVm> vmsToMigrateFromUnderUtilizedHost = getVmsToMigrateFromUnderUtilizedHost(underUtilizedHost);
            if (vmsToMigrateFromUnderUtilizedHost.isEmpty()) {
                continue;
            }

            Log.print("Reallocation of VMs from the under-utilized host: ");
            if (!Log.isDisabled()) {
                for (IntContainerVm vm : vmsToMigrateFromUnderUtilizedHost) {
                    Log.print(vm.getId() + " ");
                }
            }
            Log.printLine();

            List<Map<String, Object>> newVmPlacement = getNewVmPlacementFromUnderUtilizedHost(
                    vmsToMigrateFromUnderUtilizedHost,
                    excludedHostsForFindingNewVmPlacement);

            excludedHostsForFindingUnderUtilizedHost.addAll(extractHostListFromMigrationMap(newVmPlacement));

            migrationMap.addAll(newVmPlacement);
            Log.printLine();
        }

        excludedHostsForFindingUnderUtilizedHost.clear();
        excludedHostsForFindingNewVmPlacement.clear();
        */
        return migrationMap;
    }

    /**
     * Prints the over utilized hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     */
    protected void printOverUtilizedHosts(List<PowerContainerHostUtilizationHistory> overUtilizedHosts) {
        if (!Log.isDisabled()) {
            Log.printLine("Over-utilized hosts:");
            for (PowerContainerHostUtilizationHistory host : overUtilizedHosts) {
                Log.printConcatLine("Host #", host.getId());
            }
            Log.printLine();
        }
    }

    /**
     * Find host for vm.
     *
     * @param vm            the vm
     * @param excludedHosts the excluded hosts
     * @return the power host
     */
    public IntContainerHost findHostForVm(IntContainerVm vm, Set<? extends IntContainerHost> excludedHosts) {
        double minPower = Double.MAX_VALUE;
        IntContainerHost allocatedHost = null;

        for (IntContainerHost host : this.<IntContainerHost>getContainerHostList()) {

        	//se o host que recebe aqui é o mesmo da vm, pula ele
        	if (excludedHosts.contains(host)) {
                continue;
            }
            if (host.isSuitableForContainerVm(vm)) {
                if (getUtilizationOfCpuMips(host) != 0 && isHostOverUtilizedAfterAllocation(host, vm)) {
                    continue;
                }

                try {
                    double powerAfterAllocation = getPowerAfterAllocation(host, vm);
                    if (powerAfterAllocation != -1) {
                        Log.printLine("ERRRO - - - -");
                    	//double powerDiff = powerAfterAllocation - host.getPower();
                        double powerDiff = powerAfterAllocation - 0;
                        if (powerDiff < minPower) {
                            minPower = powerDiff;
                            allocatedHost = host;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return allocatedHost;
    }

    /**
     * Checks if is host over utilized after allocation.
     *
     * @param host the host
     * @param vm   the vm
     * @return true, if is host over utilized after allocation
     */
    protected boolean isHostOverUtilizedAfterAllocation(IntContainerHost host, IntContainerVm vm) {
        boolean isHostOverUtilizedAfterAllocation = true;
        if (host.containerVmCreate(vm)) {
            isHostOverUtilizedAfterAllocation = isHostOverUtilized(host);
            host.containerVmDestroy(vm);
        }
        return isHostOverUtilizedAfterAllocation;
    }

    /**
     * Find host for vm.
     *
     * @param vm the vm
     * @return the power host
     */
    @Override
    public IntContainerHost findHostForVm(IntContainerVm vm) {
    	   	
        Set<IntContainerHost> excludedHosts = new HashSet<>();
        if (vm.getHost() != null) {
            excludedHosts.add(vm.getHost());
        }
        IntContainerHost hostForVm = findHostForVm(vm, excludedHosts);
        excludedHosts.clear();

        return hostForVm;
    	
    }

    /**
     * Extract host list from migration map.
     *
     * @param migrationMap the migration map
     * @return the list
     */
    protected List<PowerContainerHost> extractHostListFromMigrationMap(List<Map<String, Object>> migrationMap) {
        List<PowerContainerHost> hosts = new LinkedList<PowerContainerHost>();
        for (Map<String, Object> map : migrationMap) {
            hosts.add((PowerContainerHost) map.get("host"));
        }

        return hosts;
    }

    /**
     * Gets the new vm placement.
     *
     * @param vmsToMigrate  the vms to migrate
     * @param excludedHosts the excluded hosts
     * @return the new vm placement
     */
    protected List<Map<String, Object>> getNewVmPlacement(
            List<? extends IntContainerVm> vmsToMigrate,
            Set<? extends IntContainerHost> excludedHosts) {
        List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
        
        /*
        IntContainerVmList.sortByCpuUtilization(vmsToMigrate);
        for (ContainerVm vm : vmsToMigrate) {
            IntContainerHost allocatedHost = findHostForVm(vm, excludedHosts);
            if (allocatedHost != null) {
                allocatedHost.containerVmCreate(vm);
                Log.printConcatLine("VM #", vm.getId(), " allocated to host #", allocatedHost.getId());

                Map<String, Object> migrate = new HashMap<String, Object>();
                migrate.put("vm", vm);
                migrate.put("host", allocatedHost);
                migrationMap.add(migrate);
            }
        }*/
        return migrationMap;
    }

    /**
     * Gets the new vm placement from under utilized host.
     *
     * @param vmsToMigrate  the vms to migrate
     * @param excludedHosts the excluded hosts
     * @return the new vm placement from under utilized host
     */
    protected List<Map<String, Object>> getNewVmPlacementFromUnderUtilizedHost(
            List<? extends IntContainerVm> vmsToMigrate,
            Set<? extends IntContainerHost> excludedHosts) {
        List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
        /*
        PowerContainerVmList.sortByCpuUtilization(vmsToMigrate);
        for (ContainerVm vm : vmsToMigrate) {
            PowerContainerHost allocatedHost = findHostForVm(vm, excludedHosts);
            if (allocatedHost != null) {
                allocatedHost.containerVmCreate(vm);
                Log.printConcatLine("VM #", vm.getId(), " allocated to host #", allocatedHost.getId());

                Map<String, Object> migrate = new HashMap<String, Object>();
                migrate.put("vm", vm);
                migrate.put("host", allocatedHost);
                migrationMap.add(migrate);
            } else {
                Log.printLine("Not all VMs can be reallocated from the host, reallocation cancelled");
                for (Map<String, Object> map : migrationMap) {
                    ((ContainerHost) map.get("host")).containerVmDestroy((ContainerVm) map.get("vm"));
                }
                migrationMap.clear();
                break;
            }
        }
        */
        return migrationMap;
    }

    /**
     * Gets the vms to migrate from hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     * @return the vms to migrate from hosts
     */
    protected List<? extends IntContainerVm> getVmsToMigrateFromHosts(List<PowerContainerHostUtilizationHistory> overUtilizedHosts) {
Log.printLine("ERRRO ++++++");
    	
    	List<IntContainerVm> vmsToMigrate = new LinkedList<IntContainerVm>();
        //for (PowerContainerHostUtilizationHistory host : overUtilizedHosts) {
        //    while (true) {
        //        IntContainerVm vm = getVmSelectionPolicy().getVmToMigrate(host);
         //       if (vm == null) {
         //           break;
         //       }
         //       vmsToMigrate.add(vm);
          //      host.containerVmDestroy(vm);
           //     if (!isHostOverUtilized(host)) {
            //        break;
             //   }
           // }
        //}
        return vmsToMigrate;
    }


    /**
     * Gets the vms to migrate from under utilized host.
     *
     * @param host the host
     * @return the vms to migrate from under utilized host
     */
    protected List<? extends ContainerVm> getVmsToMigrateFromUnderUtilizedHost(PowerContainerHost host) {
        List<ContainerVm> vmsToMigrate = new LinkedList<ContainerVm>();
        for (ContainerVm vm : host.getVmList()) {
            if (!vm.isInMigration()) {
                vmsToMigrate.add(vm);
            }
        }
        return vmsToMigrate;
    }

    /**
     * Gets the over utilized hosts.
     *
     * @return the over utilized hosts
     */
    protected List<PowerContainerHostUtilizationHistory> getOverUtilizedHosts() {
    	Log.printLine("ERRO ====== getPowerAfterAllocation VM ALLOCATION POLICY 4");/*
        List<PowerContainerHostUtilizationHistory> overUtilizedHosts = new LinkedList<PowerContainerHostUtilizationHistory>();
        for (PowerContainerHostUtilizationHistory host : this.<PowerContainerHostUtilizationHistory>getContainerHostList()) {
            if (isHostOverUtilized(host)) {
                overUtilizedHosts.add(host);
            }
            }
        return overUtilizedHosts;*/
    	return null;
    }

    /**
     * Gets the switched off host.
     *
     * @return the switched off host
     */
    protected List<PowerContainerHost> getSwitchedOffHosts() {
    	
    	Log.printLine("ERRO ====== getPowerAfterAllocation VM ALLOCATION POLICY 5");
    	/*
        List<PowerContainerHost> switchedOffHosts = new LinkedList<PowerContainerHost>();
        for (PowerContainerHost host : this.<PowerContainerHost>getContainerHostList()) {
            if (host.getUtilizationOfCpu() == 0) {
                switchedOffHosts.add(host);
            }
        }
        
        return switchedOffHosts;
        */
    	
    	return null;
    }

    /**
     * Gets the under utilized host.
     *
     * @param excludedHosts the excluded hosts
     * @return the under utilized host
     */
    protected IntContainerHost getUnderUtilizedHost(Set<? extends IntContainerHost> excludedHosts) {
        
     	Log.printLine("ERRO ====== getPowerAfterAllocation VM ALLOCATION POLICY");
    	/*double minUtilization = 1;
        IntContainerHost underUtilizedHost = null;
        for (IntContainerHost host : this.<IntContainerHost>getContainerHostList()) {
            if (excludedHosts.contains(host)) {
                continue;
            }

            double utilization = host.getUtilizationOfCpu();
            if (utilization > 0 && utilization < minUtilization
                    && !areAllVmsMigratingOutOrAnyVmMigratingIn(host)&& !areAllContainersMigratingOutOrAnyContainersMigratingIn(host)) {
                minUtilization = utilization;
                underUtilizedHost = host;
            }
        }
     	
        return underUtilizedHost;*/
     	return null;
    }






    /**
     * Checks whether all vms are in migration.
     *
     * @param host the host
     * @return true, if successful
     */
    protected boolean areAllVmsMigratingOutOrAnyVmMigratingIn(PowerContainerHost host) {
        for (PowerContainerVm vm : host.<PowerContainerVm>getVmList()) {
            if (!vm.isInMigration()) {
                return false;
            }
            if (host.getVmsMigratingIn().contains(vm)) {
                return true;
            }
        }
        return true;
    }

    /**
     * Checks whether all vms are in migration.
     *
     * @param host the host
     * @return true, if successful
     */
    protected boolean areAllContainersMigratingOutOrAnyContainersMigratingIn(PowerContainerHost host) {
        for (PowerContainerVm vm : host.<PowerContainerVm>getVmList()) {
           if(vm.getContainersMigratingIn().size() != 0){
               return true;
           }
            for(Container container:vm.getContainerList()){
                if(!container.isInMigration()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if is host over utilized.
     *
     * @param host the host
     * @return true, if is host over utilized
     */
    protected abstract boolean isHostOverUtilized(IntContainerHost host);


    /**
     * Checks if is host over utilized.
     *
     * @param host the host
     * @return true, if is host over utilized
     */
    protected abstract boolean isHostUnderUtilized(IntContainerHost host);


    /**
     * Adds the history value.
     *
     * @param host   the host
     * @param metric the metric
     */
    protected void addHistoryEntry(ContainerHostDynamicWorkload host, double metric) {
        int hostId = host.getId();
        if (!getTimeHistory().containsKey(hostId)) {
            getTimeHistory().put(hostId, new LinkedList<Double>());
        }
        if (!getUtilizationHistory().containsKey(hostId)) {
            getUtilizationHistory().put(hostId, new LinkedList<Double>());
        }
        if (!getMetricHistory().containsKey(hostId)) {
            getMetricHistory().put(hostId, new LinkedList<Double>());
        }
        if (!getTimeHistory().get(hostId).contains(CloudSim.clock())) {
            getTimeHistory().get(hostId).add(CloudSim.clock());
            getUtilizationHistory().get(hostId).add(host.getUtilizationOfCpu());
            getMetricHistory().get(hostId).add(metric);
        }
    }

    /**
     * Save allocation.
     */
    protected void saveAllocation() {
        getSavedAllocation().clear();
        for (IntContainerHost host : getContainerHostList()) {
            for (IntContainerVm vm : host.getVmList()) {
                if (host.getVmsMigratingIn().contains(vm)) {
                    continue;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("host", host);
                map.put("vm", vm);
                getSavedAllocation().add(map);
            }
        }
    }

    /**
     * Restore allocation.
     */
    protected void restoreAllocation() {
        for (IntContainerHost host : getContainerHostList()) {
            host.containerVmDestroyAll();
            host.reallocateMigratingInContainerVms();
        }
        for (Map<String, Object> map : getSavedAllocation()) {
            IntContainerVm vm = (IntContainerVm) map.get("vm");
            IntContainerHost host = (IntContainerHost) map.get("host");
            if (!host.containerVmCreate(vm)) {
                Log.printConcatLine("Couldn't restore VM #", vm.getId(), " on host #", host.getId());
                System.exit(0);
            }
            getVmTable().put(vm.getUid(), host);
        }
    }

    /**
     * Gets the power after allocation.
     *
     * @param host the host
     * @param vm   the vm
     * @return the power after allocation
     */
    protected double getPowerAfterAllocation(IntContainerHost host, IntContainerVm vm) {
        double power = 0;
        try {
        	Log.printLine("ERRO ====== getPowerAfterAllocation VM ALLOCATION POLICY");
            //power = host.getPowerModel().getPower(getMaxUtilizationAfterAllocation(host, vm));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return power;
    }

    /**
     * Gets the power after allocation. We assume that load is balanced between PEs. The only
     * restriction is: VM's max MIPS < PE's MIPS
     *
     * @param host the host
     * @param vm   the vm
     * @return the power after allocation
     */
    protected double getMaxUtilizationAfterAllocation(IntContainerHost host, IntContainerVm vm) {
        double requestedTotalMips = vm.getCurrentRequestedTotalMips();
        double hostUtilizationMips = getUtilizationOfCpuMips(host);
        double hostPotentialUtilizationMips = hostUtilizationMips + requestedTotalMips;
        double pePotentialUtilization = hostPotentialUtilizationMips / host.getTotalMips();
        return pePotentialUtilization;
    }

    /**
     * Gets the utilization of the CPU in MIPS for the current potentially allocated VMs.
     *
     * @param host the host
     * @return the utilization of the CPU in MIPS
     */
    protected double getUtilizationOfCpuMips(IntContainerHost host) {
        double hostUtilizationMips = 0;
        for (IntContainerVm vm2 : host.getVmList()) {
            if (host.getVmsMigratingIn().contains(vm2)) {
                // calculate additional potential CPU usage of a migrating in VM
                hostUtilizationMips += host.getTotalAllocatedMipsForContainerVm(vm2) * 0.9 / 0.1;
            }
            hostUtilizationMips += host.getTotalAllocatedMipsForContainerVm(vm2);
        }
        
        Log.printLine(" ========== " + hostUtilizationMips);
        return hostUtilizationMips;
    }

    /**
     * Gets the saved allocation.
     *
     * @return the saved allocation
     */
    protected List<Map<String, Object>> getSavedAllocation() {
        return savedAllocation;
    }

    /**
     * Sets the vm selection policy.
     *
     * @param vmSelectionPolicy the new vm selection policy
     */
    protected void setVmSelectionPolicy(IntContainerVmSelectionPolicy vmSelectionPolicy) {
        this.vmSelectionPolicy = vmSelectionPolicy;
    }

    /**
     * Gets the vm selection policy.
     *
     * @return the vm selection policy
     */
    protected IntContainerVmSelectionPolicy getVmSelectionPolicy() {
        return vmSelectionPolicy;
    }

    /**
     * Gets the utilization history.
     *
     * @return the utilization history
     */
    public Map<Integer, List<Double>> getUtilizationHistory() {
        return utilizationHistory;
    }

    /**
     * Gets the metric history.
     *
     * @return the metric history
     */
    public Map<Integer, List<Double>> getMetricHistory() {
        return metricHistory;
    }

    /**
     * Gets the time history.
     *
     * @return the time history
     */
    public Map<Integer, List<Double>> getTimeHistory() {
        return timeHistory;
    }

    /**
     * Gets the execution time history vm selection.
     *
     * @return the execution time history vm selection
     */
    public List<Double> getExecutionTimeHistoryVmSelection() {
        return executionTimeHistoryVmSelection;
    }

    /**
     * Gets the execution time history host selection.
     *
     * @return the execution time history host selection
     */
    public List<Double> getExecutionTimeHistoryHostSelection() {
        return executionTimeHistoryHostSelection;
    }

    /**
     * Gets the execution time history vm reallocation.
     *
     * @return the execution time history vm reallocation
     */
    public List<Double> getExecutionTimeHistoryVmReallocation() {
        return executionTimeHistoryVmReallocation;
    }

    /**
     * Gets the execution time history total.
     *
     * @return the execution time history total
     */
    public List<Double> getExecutionTimeHistoryTotal() {
        return executionTimeHistoryTotal;
    }


//    public abstract List<? extends Container> getContainersToMigrateFromHosts(List<PowerContainerHostUtilizationHistory> overUtilizedHosts);
}