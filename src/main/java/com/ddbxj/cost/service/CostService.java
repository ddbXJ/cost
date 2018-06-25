package com.ddbxj.cost.service;

import com.ddbxj.cost.module.CostDomain;
import com.ddbxj.cost.module.CostRecords;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;

/**
 * @author lee.li
 * @date 2018/05/10
 */
@Service
public class CostService {

    private static final String COST_DOMAIN_FILE_NAME = "cost_domain_";
    private static final String COST_RECORDS_FILE_NAME = "cost_records_";
    public static final String DEFAULT_FILE_SUFFIX = "default";

    public CostDomain getCostDomain(String name) {
        CostDomain domain = get(COST_DOMAIN_FILE_NAME + name, CostDomain.class);
        if (domain == null) {
            return initCostDomain();
        }
        return domain;
    }

    public CostRecords getCostRecords(String name) {
        CostRecords records = get(COST_RECORDS_FILE_NAME + name, CostRecords.class);
        if (records == null) {
            return new CostRecords();
        }
        return records;
    }

    public CostDomain initCostDomain() {
        CostDomain domain = new CostDomain(BigDecimal.valueOf(6000), BigDecimal.valueOf(0), DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate());

        domain.getCategoryList().add(new CostDomain.CostCategory("点点", BigDecimal.valueOf(620), BigDecimal.ZERO));
        domain.getCategoryList().add(new CostDomain.CostCategory("工作日伙食", BigDecimal.valueOf(840), BigDecimal.ZERO));
        domain.getCategoryList().add(new CostDomain.CostCategory("周末伙食", BigDecimal.valueOf(1840), BigDecimal.ZERO));
        domain.getCategoryList().add(new CostDomain.CostCategory("车/行", BigDecimal.valueOf(1000), BigDecimal.ZERO));
        domain.getCategoryList().add(new CostDomain.CostCategory("其他", BigDecimal.valueOf(1700), BigDecimal.ZERO));

        set(domain, COST_DOMAIN_FILE_NAME + DEFAULT_FILE_SUFFIX);
        return domain;
    }

    public CostDomain addCost(CostRecords.CostRecord costRecord, String name) {
        CostRecords records = getCostRecords(name);
        records.getCostRecordList().add(costRecord);
        set(records, COST_RECORDS_FILE_NAME + name);

        CostDomain domain = getCostDomain(name);
        domain.addCostRecord(costRecord);
        set(domain, COST_DOMAIN_FILE_NAME + name);

        return domain;
    }

    private static <T> void set(T t, String fileName) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
            oos.writeObject(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> T get(String fileName, Class<T> clazz) {
        try {
            File f = new File(fileName);
            if(f.exists() && !f.isDirectory()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                return clazz.cast(ois.readObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
