package cloudsim.interference.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPeProvisioner;
import org.cloudbus.cloudsim.container.core.ContainerVm;

public class IntContainerVmPeProvisionerSimple extends IntContainerVmPeProvisioner{


	    /** The pe table. */
	    private Map<String, List<Double>> peTable;

	    /**
	     * Creates the PeProvisionerSimple object.
	     *
	     * @param availableMips the available mips
	     *
	     * @pre $none
	     * @post $none
	     */
	    public IntContainerVmPeProvisionerSimple(double availableMips) {
	        super(availableMips);
	        setPeTable(new HashMap<String, ArrayList<Double>>());
	    }




	    @Override
	    public boolean allocateMipsForContainerVm(IntContainerVm containerVm, double mips) {

	        return allocateMipsForContainerVm(containerVm.getUid(), mips);
	    }

	    @Override
	    public boolean allocateMipsForContainerVm(String containerVmUid, double mips) {
	        if (getAvailableMips() < mips) {
	            return false;
	        }

	        List<Double> allocatedMips;

	        if (getPeTable().containsKey(containerVmUid)) {
	            allocatedMips = getPeTable().get(containerVmUid);
	        } else {
	            allocatedMips = new ArrayList<>();
	        }

	        allocatedMips.add(mips);

	        setAvailableMips(getAvailableMips() - mips);
	        getPeTable().put(containerVmUid, allocatedMips);

	        return true;
	    }

	    @Override
	    public boolean allocateMipsForContainerVm(IntContainerVm containerVm, List<Double> mips) {
	        int totalMipsToAllocate = 0;
	        for (double _mips : mips) {
	            totalMipsToAllocate += _mips;
	        }

	        if (getAvailableMips() + getTotalAllocatedMipsForContainerVm(containerVm)< totalMipsToAllocate) {
	            return false;
	        }

	        setAvailableMips(getAvailableMips() + getTotalAllocatedMipsForContainerVm(containerVm)- totalMipsToAllocate);

	        getPeTable().put(containerVm.getUid(), mips);

	        return true;
	    }

	    @Override
	    public List<Double> getAllocatedMipsForContainerVm(IntContainerVm containerVm) {
	        if (getPeTable().containsKey(containerVm.getUid())) {
	            return getPeTable().get(containerVm.getUid());
	        }
	        return null;
	    }

	    @Override
	    public double getTotalAllocatedMipsForContainerVm(IntContainerVm containerVm) {
	        if (getPeTable().containsKey( containerVm.getUid())) {
	            double totalAllocatedMips = 0.0;
	            for (double mips : getPeTable().get(containerVm.getUid())) {
	                totalAllocatedMips += mips;
	            }
	            return totalAllocatedMips;
	        }
	        return 0;
	    }

	    @Override
	    public double getAllocatedMipsForContainerVmByVirtualPeId(IntContainerVm containerVm, int peId) {
	        if (getPeTable().containsKey(containerVm.getUid())) {
	            try {
	                return getPeTable().get(containerVm.getUid()).get(peId);
	            } catch (Exception e) {
	            }
	        }
	        return 0;
	    }

	    @Override
	    public void deallocateMipsForContainerVm(IntContainerVm containerVm) {
	        if (getPeTable().containsKey(containerVm.getUid())) {
	            for (double mips : getPeTable().get(containerVm.getUid())) {
	                setAvailableMips(getAvailableMips() + mips);
	            }
	            getPeTable().remove(containerVm.getUid());
	        }
	    }

	    @Override
	    public void deallocateMipsForAllContainerVms() {
	        super.deallocateMipsForAllContainerVms();
	        getPeTable().clear();
	    }
	    /**
	     * Gets the pe table.
	     *
	     * @return the peTable
	     */
	    protected Map<String, List<Double>> getPeTable() {
	        return peTable;
	    }

	    /**
	     * Sets the pe table.
	     *
	     * @param peTable the peTable to set
	     */
	    @SuppressWarnings("unchecked")
	    protected void setPeTable(Map<String, ? extends List<Double>> peTable) {
	        this.peTable = (Map<String, List<Double>>) peTable;
	    }
	}

	
	
	
