package com.ddbxj.cost.module;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.ddbxj.cost.module.CostDomain.CostCategory.newCostCategoryFromCostRecord;

/**
 * @author lee.li
 * @date 2018/06/25
 */
public class CostDomain implements Serializable {

    private static final long serialVersionUID = 3047368827628740529L;

    public CostDomain() {
    }

    public CostDomain(BigDecimal totalBudget, BigDecimal totalSpend, Date start, Date end) {
        this.totalBudget = totalBudget;
        this.totalSpend = totalSpend;
        this.totalRemaining = totalBudget.subtract(totalSpend);
        this.start = start;
        this.end = end;
    }

    /**
     * 总
     */
    private BigDecimal totalBudget;

    /**
     * 已用
     */
    private BigDecimal totalSpend;

    /**
     * 剩余可用
     */
    private BigDecimal totalRemaining;

    /**
     * 开始时间
     */
    private Date start;

    /**
     * 截止时间
     */
    private Date end;

    /**
     * 分类
     */
    private List<CostCategory> categoryList = new ArrayList<CostCategory>();

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(BigDecimal totalSpend) {
        this.totalSpend = totalSpend;
    }

    public BigDecimal getTotalRemaining() {
        return totalRemaining;
    }

    public void setTotalRemaining(BigDecimal totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public List<CostCategory> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CostCategory> categoryList) {
        this.categoryList = categoryList;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public static class CostCategory implements Serializable {

        private static final long serialVersionUID = 3047368827628740529L;

        private String category;
        private BigDecimal budget;
        private BigDecimal spend;
        private BigDecimal remaining;

        public CostCategory() {
        }

        public CostCategory(String category, BigDecimal budget, BigDecimal spend) {
            this.category = category;
            this.budget = budget;
            this.spend = spend;
            this.remaining = budget.subtract(spend);
        }

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

        public BigDecimal getSpend() {
            return spend;
        }

        public void setSpend(BigDecimal spend) {
            this.spend = spend;
        }

        public BigDecimal getRemaining() {
            return remaining;
        }

        public void setRemaining(BigDecimal remaining) {
            this.remaining = remaining;
        }

        public void addNewCost(CostRecords.CostRecord record) {
            spend = spend.add(record.getNewSpend());
            remaining = remaining.subtract(record.getNewSpend());
        }

        public static CostCategory newCostCategoryFromCostRecord(CostRecords.CostRecord record) {
            return new CostDomain.CostCategory(record.getCategory(), BigDecimal.ZERO, record.getNewSpend());
        }
    }

    public void addCostRecord(CostRecords.CostRecord record) {
        Optional<CostDomain.CostCategory> optional = getCategoryList().stream().filter(x->x.category.equals(record.getCategory())).findAny();
        if (optional.isPresent()) {
            CostDomain.CostCategory category = optional.get();
            category.addNewCost(record);
        } else {
            categoryList.add(newCostCategoryFromCostRecord(record));
        }

        totalSpend = totalSpend.add(record.getNewSpend());
        totalRemaining = totalRemaining.subtract(record.getNewSpend());
    }
}
