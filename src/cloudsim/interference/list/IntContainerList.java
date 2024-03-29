package cloudsim.interference.list;


import java.util.List;

import cloudsim.interference.IntContainer;

/**
 * Created by sareh on 17/07/15.
 */
public class IntContainerList {
    public static <T extends IntContainer> T getById(List<T> containerList, int id) {
        for (T container : containerList) {
            if (container.getId() == id) {
                return container;
            }
        }
        return null;
    }

    /**
     * Return a reference to a Vm object from its ID and user ID.
     *
     * @param id            ID of required VM
     * @param userId        the user ID
     * @param containerList the vm list
     * @return Vm with the given ID, $null if not found
     * @pre $none
     * @post $none
     */
    public static <T extends IntContainer> T getByIdAndUserId(List<T> containerList, int id, int userId) {
        for (T container : containerList) {
            if (container.getId() == id && container.getUserId() == userId) {
                return container;
            }
        }
        return null;
    }


}
