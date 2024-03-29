package cloudsim.interference.cloudlet;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.container.core.ContainerCloudlet;

import cloudsim.interference.Interference;

public class IntContainerCloudlet extends ContainerCloudlet{

public Interference interfMetrics;
	
	public IntContainerCloudlet(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize,
			long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam,
			UtilizationModel utilizationModelBw, Interference interfMetrics) {
		super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);
		// TODO Auto-generated constructor stub
		
		setInterferenceMetrics(interfMetrics);

	}
	
	
	public void setInterferenceMetrics(Interference interfMetrics){
		this.interfMetrics = interfMetrics;	
	}
	
	public Interference getInterferenceMetrics(){
		return interfMetrics;	
	}
	
	
	


}
