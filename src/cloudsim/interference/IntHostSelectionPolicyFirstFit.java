package cloudsim.interference;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.container.core.ContainerHost;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicy;

public class IntHostSelectionPolicyFirstFit extends HostSelectionPolicy {

	@Override
	public ContainerHost getHost(List<ContainerHost> hostList, Object obj,
			Set<? extends ContainerHost> excludedHostList) {
		ContainerHost host = null;
		for (ContainerHost host1 : hostList) {
			if (excludedHostList.contains(host1)) {
				continue;
			}
			host = host1;
			break;
		}
		return host;
	}

}
