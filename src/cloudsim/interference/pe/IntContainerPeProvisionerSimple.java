package cloudsim.interference.pe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloudsim.interference.IntContainer;
import cloudsim.interference.pe.IntContainerPeProvisioner;

public class IntContainerPeProvisionerSimple extends IntContainerPeProvisioner {

    /**
     * The pe table.
     */
    private Map<String, List<Double>> peTable;
    /**
     * @param mips
     */
    /**
     * Creates the PeProvisionerSimple object.
     *
     * @param availableMips the available mips
     * @pre $none
     * @post $none
     */
    public IntContainerPeProvisionerSimple(double availableMips) {
        super(availableMips);
        setPeTable(new HashMap<String, ArrayList<Double>>());
    }

    /* (non-Javadoc)
     * @see ContainerVmPeProvisioner#allocateMipsForContainer
     */
    @Override
    public boolean allocateMipsForContainer(IntContainer container, double mips) {
        // TODO Auto-generated method stub

        return allocateMipsForContainer(container.getUid(), mips);
    }

    /* (non-Javadoc)
     * @see ContainerVmPeProvisioner#allocateMipsForContainer(java.lang.String, double)
     */
    @Override
    public boolean allocateMipsForContainer(String containerUid, double mips) {
        if (getAvailableMips() < mips) {
            return false;
        }

        List<Double> allocatedMips;

        if (getPeTable().containsKey(containerUid)) {
            allocatedMips = getPeTable().get(containerUid);
        } else {
            allocatedMips = new ArrayList<>();
        }

        allocatedMips.add(mips);

        setAvailableMips(getAvailableMips() - mips);
        getPeTable().put(containerUid, allocatedMips);

        return true;
    }

    /* (non-Javadoc)
     * @see ContainerVmPeProvisioner#allocateMipsForContainer(Container, java.util.List)
     */
    @Override
    public boolean allocateMipsForContainer(IntContainer container, List<Double> mips) {
        int totalMipsToAllocate = 0;
        for (double _mips : mips) {
            totalMipsToAllocate += _mips;
        }

        if (getAvailableMips() + getTotalAllocatedMipsForContainer(container) < totalMipsToAllocate) {
            return false;
        }

        setAvailableMips(getAvailableMips() + getTotalAllocatedMipsForContainer(container) - totalMipsToAllocate);

        getPeTable().put(container.getUid(), mips);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see containerProvisioners.PeProvisioner#deallocateMipsForAllContainers()
     */
    @Override
    public void deallocateMipsForAllContainers() {
        super.deallocateMipsForAllContainers();
        getPeTable().clear();
    }

    /* (non-Javadoc)
     * @see ContainerPeProvisioner#getAllocatedMipsForContainer
     */
    @Override
    public List<Double> getAllocatedMipsForContainer(IntContainer container) {
        if (getPeTable().containsKey(container.getUid())) {
            return getPeTable().get(container.getUid());
        }
        return null;
    }

    /* (non-Javadoc)
     * @see ContainerVmPeProvisioner#getTotalAllocatedMipsForContainer
     */
    @Override
    public double getTotalAllocatedMipsForContainer(IntContainer container) {
        if (getPeTable().containsKey(container.getUid())) {
            double totalAllocatedMips = 0.0;
            for (double mips : getPeTable().get(container.getUid())) {
                totalAllocatedMips += mips;
            }
            return totalAllocatedMips;
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see ContainerVmPeProvisioner#getAllocatedMipsForContainerByVirtualPeId(Container, int)
     */
    @Override
    public double getAllocatedMipsForContainerByVirtualPeId(IntContainer container,
                                                            int peId) {
        if (getPeTable().containsKey(container.getUid())) {
            try {
                return getPeTable().get(container.getUid()).get(peId);
            } catch (Exception e) {
            }
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see ContainerVmPeProvisioner#deallocateMipsForContainer(Container)
     */
    @Override
    public void deallocateMipsForContainer(IntContainer container) {
        if (getPeTable().containsKey(container.getUid())) {
            for (double mips : getPeTable().get(container.getUid())) {
                setAvailableMips(getAvailableMips() + mips);
            }
            getPeTable().remove(container.getUid());
        }
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

