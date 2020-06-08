package cloudsim.interference.vm;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.container.core.*;

public abstract class IntContainerVmSelectionPolicy {

	 /**
     * Gets the vms to migrate.
     *
     * @param host the host
     * @return the vms to migrate
     */
    public abstract ContainerVm getVmToMigrate(ContainerHost host);

    /**
     * Gets the migratable vms.
     *
     * @param host the host
     * @return the migratable vms
     */
    protected List<ContainerVm> getMigratableVms(ContainerHost host) {
        List<ContainerVm> migratableVms = new ArrayList<>();
        for (ContainerVm vm : host.<ContainerVm> getVmList()) {
            if (!vm.isInMigration()) {
                migratableVms.add(vm);
            }
        }
        return migratableVms;
    }

}

	
	