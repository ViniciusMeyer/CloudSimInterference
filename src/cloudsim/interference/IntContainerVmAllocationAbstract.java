package cloudsim.interference;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.ContainerHost;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IntContainerVmAllocationAbstract extends IntContainerVmAllocationPolicy{

        /** The vm table. */
        private final Map<String, IntContainerHost> vmTable = new HashMap<String, IntContainerHost>();

        /**
         * Instantiates a new vm allocation policy abstract.
         *
         * @param list the list
         */
        public IntContainerVmAllocationAbstract(List<? extends IntContainerHost> list) {
            super(list);
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm)
         */
        @Override
        public boolean allocateHostForVm(IntContainerVm containerVm) {
            return allocateHostForVm(containerVm, findHostForVm(containerVm));
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
         * org.cloudbus.cloudsim.Host)
         */
        @Override
        public boolean allocateHostForVm(IntContainerVm containerVm, IntContainerHost host) {
            if (host == null) {
                Log.formatLine("%.2f: No suitable host found for VM #" + containerVm.getId() + "\n", CloudSim.clock());
                return false;
            }
            if (host.containerVmCreate(containerVm)) { // if vm has been succesfully created in the host
                getVmTable().put(containerVm.getUid(), host);
                Log.formatLine(
                        "%.2f: VM #" + containerVm.getId() + " has been allocated to the host #" + host.getId(),
                        CloudSim.clock());
                return true;
            }
            Log.formatLine(
                    "%.2f: Creation of VM #" + containerVm.getId() + " on the host #" + host.getId() + " failed\n",
                    CloudSim.clock());
            return false;
        }

        /**
         * Find host for vm.
         *
         * @param containerVm the vm
         * @return the host
         */
        public IntContainerHost findHostForVm(IntContainerVm containerVm) {
            for (IntContainerHost host : this.<IntContainerHost> getContainerHostList()) {
                if (host.isSuitableForContainerVm(containerVm)) {
                    return host;
                }
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#deallocateHostForVm(org.cloudbus.cloudsim.Vm)
         */
        @Override
        public void deallocateHostForVm(IntContainerVm containerVm) {
            IntContainerHost host = getVmTable().remove(containerVm.getUid());
            if (host != null) {
                host.containerVmDestroy(containerVm);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#getHost(org.cloudbus.cloudsim.Vm)
         */
        @Override
        public IntContainerHost getHost(IntContainerVm vm) {
            return getVmTable().get(vm.getUid());
        }

        /*
         * (non-Javadoc)
         * @see org.cloudbus.cloudsim.VmAllocationPolicy#getHost(int, int)
         */
        @Override
        public IntContainerHost getHost(int vmId, int userId) {
            return getVmTable().get(IntContainerVm.getUid(userId, vmId));
        }

        /**
         * Gets the vm table.
         *
         * @return the vm table
         */
        public Map<String, IntContainerHost> getVmTable() {
            return vmTable;
        }

    public List<IntContainerVm> getOverUtilizedVms() {
        List<IntContainerVm> vmList = new ArrayList<IntContainerVm>();
        for (IntContainerHost host : getContainerHostList()) {
            for (IntContainerVm vm : host.getVmList()) {
                if (vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) > vm.getTotalMips()) {
                    vmList.add(vm);

                }

            }

        }
        return vmList;
    }


}