package com.ddbxj.cost.module.request;

import java.math.BigDecimal;

/**
 * @author lee.li
 * @date 2018/06/27
 */
public class CreateOrUpdateCategoryRequest {
    private String category;
    private BigDecimal budget;
    private String monthStr;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }
}
