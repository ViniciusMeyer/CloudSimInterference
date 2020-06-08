package cloudsim.interference.vm;

import java.util.List;

import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.core.ContainerHost;


public class IntContainerVmSelectionPolicySimple extends IntContainerVmSelectionPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cloudbus.cloudsim.experiments.power.PowerVmSelectionPolicy#
	 * getVmsToMigrate(org.cloudbus .cloudsim.power.PowerHost)
	 */
	@Override
	public ContainerVm getVmToMigrate(ContainerHost host) {
		List<ContainerVm> migratableContainers = getMigratableVms(host);
		if (migratableContainers.isEmpty()) {
			return null;
		}
		ContainerVm VmsToMigrate = null;
		double maxMetric = Double.MIN_VALUE;
		for (ContainerVm vm : migratableContainers) {
			if (vm.isInMigration()) {
				continue;
			}
			double metric = vm.getCurrentRequestedTotalMips();
			if (maxMetric < metric) {
				maxMetric = metric;
				VmsToMigrate = vm;
			}
		}
//        Log.formatLine("The Container To migrate is #%d from VmID %d from host %d", containerToMigrate.getId(),containerToMigrate.getVm().getId(), host.getId());
		return VmsToMigrate;
	}

}
