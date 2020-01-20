package com.example.neighborly;

public class BuildingModelDataHolder {
    private BuildingModel currentBuilding;

    public BuildingModel getCurrentBuilding() {
        return currentBuilding;
    }

    public void setCurrentBuilding(BuildingModel currentBuilding) {
        this.currentBuilding = currentBuilding;
    }

    private static final BuildingModelDataHolder holder = new BuildingModelDataHolder();

    public static BuildingModelDataHolder getInstance() {
        return holder;
    }

}
