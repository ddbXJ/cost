package com.ddbxj.cost.module.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.ddbxj.cost.module.domain.CostDomain.CostCategory.newCostCategoryFromCostRecord;

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

    //region getter and setter

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

    //endregion

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

        public void updateBudget(BigDecimal budget) {
            this.budget = budget;
            this.remaining = budget.subtract(spend);
        }

        private void addNewCost(CostRecords.CostRecord record) {
            spend = spend.add(record.getNewSpend());
            remaining = remaining.subtract(record.getNewSpend());
        }

        private void deleteCost(CostRecords.CostRecord record) {
            spend = spend.subtract(record.getNewSpend());
            remaining = remaining.add(record.getNewSpend());
        }

        public static CostCategory newCostCategoryFromCostRecord(CostRecords.CostRecord record) {
            return new CostDomain.CostCategory(record.getCategory(), BigDecimal.ZERO, record.getNewSpend());
        }

        public static CostCategory newCostCategory(BigDecimal budget, String category) {
            return new CostDomain.CostCategory(category, budget, BigDecimal.ZERO);
        }
    }

    /**
     * 新增一笔消费
     * @param record
     */
    private void addCostRecord(CostRecords.CostRecord record) {
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

    public void fillDomainByRecords(CostRecords records) {
        totalSpend = BigDecimal.ZERO;
        totalRemaining = totalBudget;

        getCategoryList().forEach(x -> {
            x.spend = BigDecimal.ZERO;
            x.remaining = x.budget;
        });

        records.getCostRecordList().forEach(this::addCostRecord);
    }

    /**
     * 根据分类名获取分类
     * @param category
     * @return
     */
    public CostCategory getCategory(String category) {
        Optional<CostDomain.CostCategory> optional = getCategoryList().stream().filter(x->x.category.equals(category)).findAny();
        return optional.orElse(null);
    }

    /**
     * 计算分类预算总和
     * @return
     */
    private BigDecimal getCategoryBudgetSum() {
        return categoryList.stream().map(CostCategory::getBudget).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 添加分类预算
     * 分类预算总额度不能超过总额度
     * @param category
     */
    public boolean addCostCategory(CostCategory category) {
        if (totalBudget.subtract(getCategoryBudgetSum()).compareTo(category.budget) < 0) {
            return false;
        }
        this.categoryList.add(category);
        return true;
    }

    /**
     * 修改分类预算
     * 分类预算总额度不能超过总额度
     * @param costCategory
     */
    public boolean updateCostCategoryBudget(CostDomain.CostCategory costCategory, BigDecimal newbudget) {
        if (getCategoryBudgetSum().subtract(costCategory.budget).add(newbudget).compareTo(totalBudget) > 0) {
            return false;
        }
        costCategory.updateBudget(newbudget);
        return true;
    }

    /**
     * 修改总额度
     * @param totalBudget
     */
    public boolean updateTotalBudget(BigDecimal totalBudget) {
        if (totalBudget.equals(this.totalBudget)) {
            return true;
        }
        if (totalBudget.compareTo(getCategoryBudgetSum()) < 0) {
            return false;
        }

        this.totalBudget = totalBudget;
        this.totalRemaining = totalBudget.subtract(totalSpend);
        return true;
    }
}
