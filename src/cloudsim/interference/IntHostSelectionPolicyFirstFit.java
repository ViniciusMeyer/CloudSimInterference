package cloudsim.interference;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.ContainerHost;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicy;

public class IntHostSelectionPolicyFirstFit extends IntHostSelectionPolicy {

	@Override
	public IntContainerHost getHost(List<IntContainerHost> hostList, Object obj,
			Set<? extends IntContainerHost> excludedHostList) {
		IntContainerHost host = null;
		for (IntContainerHost host1 : hostList) {
			if (excludedHostList.contains(host1)) {
				continue;
			}
			host = host1;
			break;
		}
		Log.printLine("HOST "+host.getId());
		return host;
	}

}
