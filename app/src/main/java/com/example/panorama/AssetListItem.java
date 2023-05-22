package com.example.panorama;

public class AssetListItem {
    private String institutionName;
    private String accountName;
    private float accountBalance;
    private String accountId;
    private String itemId;

    public AssetListItem(String accountId, String institutionName, String accountName, float accountBalance, String itemId) {
        this.institutionName = institutionName;
        this.accountName = accountName;
        this.accountBalance = accountBalance;
        this.accountId = accountId;
        this.itemId = itemId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public float getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(float accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
