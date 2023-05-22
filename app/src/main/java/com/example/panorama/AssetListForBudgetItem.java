package com.example.panorama;

public class AssetListForBudgetItem extends AssetListItem{
    Boolean selected;
    public AssetListForBudgetItem(String accountId, String institutionName, String accountName, float accountBalance, String itemId, Boolean selected) {
        super(accountId, institutionName, accountName, accountBalance, itemId);
        this.selected = selected;
    }
}
