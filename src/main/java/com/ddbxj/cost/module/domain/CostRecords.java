package com.ddbxj.cost.module.domain;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author lee.li
 * @date 2018/06/25
 */
public class CostRecords implements Serializable {
    private static final long serialVersionUID = 3047368827628740529L;

    private List<CostRecord> costRecordList = new ArrayList<>();

    public List<CostRecord> getCostRecordList() {
        return costRecordList;
    }

    public void setCostRecordList(List<CostRecord> costRecordList) {
        this.costRecordList = costRecordList;
    }

    public static class CostRecord implements Serializable {

        private static final long serialVersionUID = 3047368827628740529L;

        private String identity = StringUtils.removeAll(UUID.randomUUID().toString(), "-");

        /**
         * 类目
         */
        private String category;

        /**
         * 开销
         */
        private BigDecimal newSpend;

        /**
         * 时间
         */
        private Date date = new Date();

        /**
         * 备注
         */
        private String note;

        /**
         * 消费者
         */
        private String consumer;

        public String getConsumer() {
            return consumer;
        }

        public void setConsumer(String consumer) {
            this.consumer = consumer;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getNewSpend() {
            return newSpend;
        }

        public void setNewSpend(BigDecimal newSpend) {
            this.newSpend = newSpend;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
