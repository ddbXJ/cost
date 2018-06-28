package com.ddbxj.cost.module.request;

import com.ddbxj.cost.module.domain.CostRecords;

/**
 * @author lee.li
 * @date 2018/06/27
 */
public class AddCostRequest {
    private CostRecords.CostRecord record;
    private String monthStr;

    public CostRecords.CostRecord getRecord() {
        return record;
    }

    public void setRecord(CostRecords.CostRecord record) {
        this.record = record;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }
}
