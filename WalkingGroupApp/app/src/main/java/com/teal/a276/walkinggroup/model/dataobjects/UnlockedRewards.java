package com.teal.a276.walkinggroup.model.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to track rewards/Serialize Json objects
 *
 */

public class UnlockedRewards {
    private List<Integer> unlockedItems = new ArrayList<>();
    public UnlockedRewards(){

    }

    @Override
    public String toString() {
        return "UnlockedRewards{" +
                '}';
    }

    public List<Integer> getUnlockedItems() {
        return unlockedItems;
    }

    public void setUnlockedItems(List<Integer> unlockedItems) {
        this.unlockedItems = unlockedItems;
    }
}
