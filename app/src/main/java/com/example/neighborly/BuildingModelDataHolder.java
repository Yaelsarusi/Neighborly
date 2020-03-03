package com.example.neighborly;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BuildingModelDataHolder {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private BuildingModel currentBuilding;
    private static final BuildingModelDataHolder holder = new BuildingModelDataHolder();

    public BuildingModel getCurrentBuilding() {
        return currentBuilding;
    }

    /**
     * This function changes the current hold building and updates it in the DB.
     * @param currentBuilding - the current building to update
     */
    public void setCurrentBuilding(BuildingModel currentBuilding) {
        this.currentBuilding = currentBuilding;

        DatabaseReference buildingsRef = database.getReference().child(Constants.DB_BUILDINGS);
        Map<String, Object> buildings = new HashMap<>();
        buildings.put(currentBuilding.getAddress(), currentBuilding);
        buildingsRef.updateChildren(buildings);
    }

    public static BuildingModelDataHolder getInstance() {
        return holder;
    }

}
