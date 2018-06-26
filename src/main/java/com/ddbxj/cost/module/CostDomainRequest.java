package com.ddbxj.cost.module;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lee.li
 * @date 2018/06/26
 */
public class CostDomainRequest implements Serializable {

    private static final long serialVersionUID = 3047368827628740529L;

    private String monthStr;

    /**
     * 总
     */
    private BigDecimal totalBudget;

    private List<CostCategoryRequest> categoryRequestList = new ArrayList<>();

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public List<CostCategoryRequest> getCategoryRequestList() {
        return categoryRequestList;
    }

    public void setCategoryRequestList(
        List<CostCategoryRequest> categoryRequestList) {
        this.categoryRequestList = categoryRequestList;
    }

    public static class CostCategoryRequest implements Serializable {

        private static final long serialVersionUID = 3047368827628740529L;

        private String category;
        private BigDecimal budget;

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
    }
}
