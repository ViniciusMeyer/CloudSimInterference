package cloudsim.interference;

import java.util.List;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerDatacenter;
import org.cloudbus.cloudsim.container.core.ContainerDatacenterCharacteristics;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEvent;

public class IntContainerDataCenter extends ContainerDatacenter {


	
	private double vmStartupDelay;
    private double containerStartupDelay;
	
	public IntContainerDataCenter(String name, ContainerDatacenterCharacteristics characteristics,
			ContainerVmAllocationPolicy vmAllocationPolicy, ContainerAllocationPolicy containerAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, String experimentName, String logAddress, double vmStartupDelay, double containerStartupDelay)
			throws Exception {
		super(name, characteristics, vmAllocationPolicy, containerAllocationPolicy, storageList, schedulingInterval,
				experimentName, logAddress);
		
        this.vmStartupDelay = vmStartupDelay;
        this.containerStartupDelay = containerStartupDelay;

		// TODO Auto-generated constructor stub
	}
	
		
	

}
