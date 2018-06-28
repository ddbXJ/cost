package com.ddbxj.cost.module.request;

import java.math.BigDecimal;

/**
 * @author lee.li
 * @date 2018/06/27
 */
public class UpdateTotalBudgetRequest {
    private BigDecimal totalBudget;
    private String monthStr;

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }
}
