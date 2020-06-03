package cloudsim.interference;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.container.core.ContainerHost;

public abstract class IntHostSelectionPolicy {

    /**
     * Gets the host
     *
     * @param hostList the host
     * @return the destination host to migrate
     */
    public abstract IntContainerHost getHost(List<IntContainerHost> hostList, Object obj, Set<? extends IntContainerHost> excludedHostList);

}
