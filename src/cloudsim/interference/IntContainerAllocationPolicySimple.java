package cloudsim.interference;

import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import cloudsim.interference.vm.IntContainerVm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sareh
 *
 */
public class IntContainerAllocationPolicySimple extends IntContainerAllocationPolicy {

	/** The vm table. */
	private Map<String, IntContainerVm> containerVmTable;

	/** The used pes. */
	private Map<String, Integer> usedPes;

	/** The free pes. */
	private List<Integer> freePes;

	private boolean control = false;

	/**
	 * Creates the new VmAllocationPolicySimple object.
	 *
	 * @pre $none
	 * @post $none
	 */
	public IntContainerAllocationPolicySimple() {
		super();
		setFreePes(new ArrayList<Integer>());
		setContainerVmTable(new HashMap<String, IntContainerVm>());
		setUsedPes(new HashMap<String, Integer>());
	}

	@Override
	public boolean allocateVmForContainer(IntContainer container, List<IntContainerVm> containerVmList) {
//		the available container list is updated. It gets is from the data center.
		setContainerVmList(containerVmList);

		// ajuste para armazenar no getFreePes() apenas a primera vez
		// Caso sejam criadas mais Vms, deve-se alterar isso no ELSE
		if (!this.control) {
			for (IntContainerVm containerVm : getContainerVmList()) {
				// armazena na lista FreePes a quantidade de Cores na VM
				getFreePes().add(containerVm.getNumberOfPes());
				// Log.printLine("z"+containerVm.getNumberOfPes());
				// Log.printLine("AAA Container quer " + container.getNumberOfPes() + " e tem "+
				// containerVm.getNumberOfFreePes());

			}
			control = true;
		} else {
			// Atualizar getFreePes() com os Pe das nova(s) Vm(s)
		}

		// recebe a quantidade de Cores que o Container quer
		int requiredPes = container.getNumberOfPes();
		boolean result = false;
		int tries = 0;
		List<Integer> freePesTmp = new ArrayList<>();
		for (Integer freePes : getFreePes()) {
			// Log.printLine("aaaa"+freePes);
			// copia a lista de cores disponiveis para uma lista temporaria
			freePesTmp.add(freePes);
			// Log.printLine("ddd"+getFreePes().size());
		}

		if (!getContainerVmTable().containsKey(container.getUid())) { // if this vm was not created
			// Log.printLine("UID " + container.getUid());

			do {// we still trying until we find a host or until we try all of them
				int moreFree = Integer.MIN_VALUE;
				int idx = -1;

				// we want the host with less pes in use
				for (int i = 0; i < freePesTmp.size(); i++) {
					// Log.printLine("ddd"+i + " cont"+ freePesTmp.get(i));
					if (freePesTmp.get(i) > moreFree) {
						moreFree = freePesTmp.get(i);
						// id da vm com mais cores disponiveis
						idx = i;
					}
				}

				//Log.printLine("Container quer " + container.getNumberOfPes() + " e tem " + getFreePes().get(idx));
				IntContainerVm containerVm = getContainerVmList().get(idx);
				result = containerVm.containerCreate(container);

				if (result) { // if vm were succesfully created in the host

					getContainerVmTable().put(container.getUid(), containerVm);
					getUsedPes().put(container.getUid(), requiredPes);
					//Log.printLine("id: " + idx + " quant " + (getFreePes().get(idx) + " - " + requiredPes));
					//getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

					result = true;
					break;
				} else {
					freePesTmp.set(idx, Integer.MIN_VALUE);
				}
				tries++;
			} while (!result && tries < getFreePes().size());

		}
		freePesTmp.clear();

		return result;
	}

	@Override
	public boolean allocateVmForContainer(IntContainer container, IntContainerVm containerVm) {
		if (containerVm.containerCreate(container)) { // if vm has been succesfully created in the host
			getContainerVmTable().put(container.getUid(), containerVm);

			int requiredPes = container.getNumberOfPes();
			int idx = getContainerVmList().indexOf(container);
			getUsedPes().put(container.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			Log.formatLine(
					"%.2f: Container #" + container.getId() + " has been allocated to the Vm #" + containerVm.getId(),
					CloudSim.clock());
			return true;
		}

		return false;
	}

	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends IntContainer> containerList) {
		return null;
	}

	@Override
	public void deallocateVmForContainer(IntContainer container) {

		IntContainerVm containerVm = getContainerVmTable().remove(container.getUid());
		int idx = getContainerVmList().indexOf(containerVm);
		int pes = getUsedPes().remove(container.getUid());
		if (containerVm != null) {
			containerVm.containerDestroy(container);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
		}

	}

	@Override
	public IntContainerVm getContainerVm(IntContainer container) {
		return getContainerVmTable().get(container.getUid());
	}

	@Override
	public IntContainerVm getContainerVm(int containerId, int userId) {
		return getContainerVmTable().get(Container.getUid(userId, containerId));
	}

	protected Map<String, IntContainerVm> getContainerVmTable() {
		return containerVmTable;
	}

	protected void setContainerVmTable(Map<String, IntContainerVm> containerVmTable) {
		this.containerVmTable = containerVmTable;
	}

	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	protected List<Integer> getFreePes() {
		return freePes;
	}

	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
	}
}
