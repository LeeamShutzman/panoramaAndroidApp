package com.example.panorama;

public class AddAssetItem {

    String assetType;
    int assetIcon;

    public AddAssetItem(String assetType, int assetIcon) {
        this.assetType = assetType;
        this.assetIcon = assetIcon;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getAssetIcon() {
        return assetIcon;
    }

    public void setAssetIcon(int assetIcon) {
        this.assetIcon = assetIcon;
    }
}
