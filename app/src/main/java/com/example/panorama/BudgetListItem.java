package com.example.panorama;

public class BudgetListItem {
    private String budgetName;
    private String budgetType;
    private float budgetLimit;
    private float budgetBalance;

    public BudgetListItem(String budgetName, String budgetType, float budgetLimit, float budgetBalance) {
        this.budgetName = budgetName;
        this.budgetType = budgetType;
        this.budgetLimit = budgetLimit;
        this.budgetBalance = budgetBalance;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public float getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(float budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public float getBudgetBalance() {
        return budgetBalance;
    }

    public void setCurrentBalance(float budgetBalance) {
        this.budgetBalance = budgetBalance;
    }
}
