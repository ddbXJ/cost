package com.ddbxj.cost.service;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.ddbxj.cost.module.domain.CostDomain;
import com.ddbxj.cost.module.domain.CostRecords;
import com.ddbxj.cost.module.request.CreateCostDomainRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.Optional;

import static com.ddbxj.cost.module.domain.CostDomain.CostCategory.newCostCategory;

/**
 * @author lee.li
 * @date 2018/05/10
 */
@Service
public class CostService {

    private static final String COST_DOMAIN_FILE_NAME = "cost_domain_";
    private static final String COST_RECORDS_FILE_NAME = "cost_records_";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    public CostDomain getCostDomain(String monthStr) {
        CostDomain domain = get(getCostDomainFileName(monthStr), CostDomain.class);
        if (domain == null) {
            return null;
        }
        return domain;
    }

    public CostRecords getCostRecords(String monthStr) {
        CostRecords records = get(getCostRecordsFileName(monthStr), CostRecords.class);
        if (records == null) {
            return new CostRecords();
        }
        return records;
    }

    public CostDomain createCostDomain(CreateCostDomainRequest request) {
        DateTime dateTime = DATE_TIME_FORMATTER.parseDateTime(request.getMonthStr());
        CostDomain costDomain = new CostDomain(request.getTotalBudget(), BigDecimal.ZERO, dateTime.toDate(), dateTime.plusMonths(1).toDate());

        for (CreateCostDomainRequest.CostCategoryRequest categoryRequest : request.getCategoryRequestList()) {
            costDomain.getCategoryList().add(new CostDomain.CostCategory(categoryRequest.getCategory(), categoryRequest.getBudget(), BigDecimal.ZERO));
        }

        set(costDomain, getCostDomainFileName(request.getMonthStr()));
        return costDomain;
    }

    /**
     * 修改总额度
     * @param totalBudget
     * @param monthStr
     */
    public boolean updateTotalBudget(BigDecimal totalBudget, String monthStr) {
        CostDomain domain = getCostDomain(monthStr);
        if (domain == null) {
            return false;
        }
        if (domain.updateTotalBudget(totalBudget)) {
            set(domain, getCostDomainFileName(monthStr));
            return true;
        }
        return false;
    }

    /**
     * 修改分类额度或新增分类
     * @param budget
     * @param monthStr
     * @param category
     */
    public boolean createOrUpdateCostCategory(String category, BigDecimal budget, String monthStr) {
        CostDomain domain = getCostDomain(monthStr);
        if (domain == null) {
            return false;
        }

        boolean result;

        CostDomain.CostCategory costCategory = domain.getCategory(category);
        if (costCategory == null) {
            result = domain.addCostCategory(newCostCategory(budget, category));
        } else {
            result = domain.updateCostCategoryBudget(costCategory, budget);
        }

        if (result) {
            set(domain, getCostDomainFileName(monthStr));
        }
        return result;

    }

    /**
     * 删除某笔记录
     * @param monthStr
     * @param costIdentity
     */
    public void deleteCost(String monthStr, String costIdentity) {
        CostDomain domain = getCostDomain(monthStr);
        if (domain == null) {
            return;
        }
        CostRecords records = getCostRecords(monthStr);
        Optional<CostRecords.CostRecord> optional = records.getCostRecordList().stream().filter(x -> x.getIdentity().equals(costIdentity)).findAny();
        if (!optional.isPresent()) {
            return;
        }

        //删记录
        records.getCostRecordList().remove(optional.get());
        set(records, getCostRecordsFileName(monthStr));

        ////减金额
        //domain.deleteCostRecord(optional.get());
        //set(domain, getCostDomainFileName(monthStr));
    }

    public void addCost(CostRecords.CostRecord costRecord, String monthStr) {
        CostRecords records = getCostRecords(monthStr);
        records.getCostRecordList().add(costRecord);
        set(records, getCostRecordsFileName(monthStr));

        //CostDomain domain = getCostDomain(monthStr);
        //domain.addCostRecord(costRecord);
        //set(domain, getCostDomainFileName(monthStr));

        //return domain;
    }

    private static String getCostDomainFileName(String monthStr) {
        return COST_DOMAIN_FILE_NAME + monthStr;
    }

    private static String getCostRecordsFileName(String monthStr) {
        return COST_RECORDS_FILE_NAME + monthStr;
    }

    private static <T> void set(T t, String fileName) {
        try {
            JSONWriter jsonWriter = new JSONWriter(new FileWriter(new File(fileName)));
            jsonWriter.writeObject(t);
            jsonWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> T get(String fileName, Class<T> clazz) {
        try {
            File f = new File(fileName);
            if(f.exists() && !f.isDirectory()) {
                JSONReader jsonReader = new JSONReader(new FileReader(f));
                T t = jsonReader.readObject(clazz);
                jsonReader.close();
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
