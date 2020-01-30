package cloudsim.interference;

import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.container.core.ContainerCloudlet;

public class IntContainerCloudlet extends ContainerCloudlet{

protected Interference interfMetrics = new Interference();
	
	public IntContainerCloudlet(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize,
			long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam,
			UtilizationModel utilizationModelBw, Interference interfMetrics) {
		super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);
		// TODO Auto-generated constructor stub
		
		this.interfMetrics = interfMetrics;
	}
	


}
