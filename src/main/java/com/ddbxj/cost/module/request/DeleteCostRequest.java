package com.ddbxj.cost.module.request;

/**
 * @author lee.li
 * @date 2018/06/27
 */
public class DeleteCostRequest {
    private String costIdentity;
    private String monthStr;

    public String getCostIdentity() {
        return costIdentity;
    }

    public void setCostIdentity(String costIdentity) {
        this.costIdentity = costIdentity;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }
}
