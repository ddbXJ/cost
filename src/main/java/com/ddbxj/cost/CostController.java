package com.ddbxj.cost;

import com.ddbxj.cost.module.domain.CostDomain;
import com.ddbxj.cost.module.domain.CostRecords;
import com.ddbxj.cost.module.request.*;
import com.ddbxj.cost.service.CostService;
import org.apache.logging.log4j.util.PropertySource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lee.li
 * @date 2018/05/10
 */
@Controller
public class CostController {

    @Autowired
    private CostService costService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping("/")
    public String index(Model model) {
        return "redirect:selectCost";
    }

    /**
     * 选择日期页面
     * @param model
     * @return
     */
    @GetMapping("/selectCost")
    public String selectCost(Model model) {

        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM");

        DateTime dateTime = DateTime.now();

        List<String> monthList = new ArrayList<>();

        for (int i = -6; i <= 6 ;i ++ ) {
            monthList.add(dateTimeFormat.print(dateTime.plusMonths(i)));
        }

        model.addAttribute("chooseRequest", new ChooseRequest());
        model.addAttribute("monthList", monthList);
        model.addAttribute("selectedMonth", dateTimeFormat.print(dateTime));
        return "selectCost";
    }

    @PostMapping("/choose")
    public String choose(@ModelAttribute(value = "chooseRequest") ChooseRequest chooseRequest) {
        if (null == costService.getCostDomain(chooseRequest.getMonthStr())) {
            return "redirect:/newCost?monthStr=" + chooseRequest.getMonthStr();
        }
        return "redirect:/cost?monthStr=" + chooseRequest.getMonthStr();
    }

    /**
     * 创建预算页面
     */
    @GetMapping("/newCost")
    public String newCost(@RequestParam(value = "monthStr") String monthStr, Model model) {
        model.addAttribute("monthStr", monthStr);
        model.addAttribute("request", new CreateCostDomainRequest());
        return "newCost";
    }

    @GetMapping("/cost")
    public String cost(@RequestParam(value = "monthStr") String monthStr, Model model) {
        CostDomain domain = costService.getCostDomain(monthStr);
        CostRecords records = costService.getCostRecords(monthStr);
        domain.fillDomainByRecords(records);
        Set<String> set = domain.getCategoryList().stream().collect(Collectors.groupingBy(CostDomain.CostCategory::getCategory)).keySet();


        records.getCostRecordList().sort(Comparator.comparing(CostRecords.CostRecord::getDate).reversed());

        model.addAttribute("monthStr", monthStr);
        model.addAttribute("updateTotalBudgetRequest", new UpdateTotalBudgetRequest());
        model.addAttribute("request", new CreateOrUpdateCategoryRequest());
        model.addAttribute("addCostRequest", new AddCostRequest());
        model.addAttribute("deleteCostRequest", new DeleteCostRequest());
        model.addAttribute("costDomain", domain);
        model.addAttribute("categories", set);
        model.addAttribute("records", records);
        model.addAttribute("categoriesSum", domain.getCategoryList().stream().map(CostDomain.CostCategory::getBudget).reduce(BigDecimal.ZERO, BigDecimal::add));

        return "cost";
    }

    @GetMapping("/records")
    public String records(@RequestParam(value = "monthStr") String monthStr, Model model) {
        model.addAttribute("records", costService.getCostRecords(monthStr));
        return "records";
    }

    /**
     * 添加一笔消费记录
     * @param request
     * @return
     */
    @PostMapping("/addCost")
    public String addCost(@ModelAttribute("addCostRequest") AddCostRequest request) {
        costService.addCost(request.getRecord(), request.getMonthStr());
        return "redirect:/cost?monthStr=" + request.getMonthStr();
    }

    /**
     * 创建一个预算
     * @param request
     * @return
     */
    @PostMapping("/createCostDomain")
    public String createCostDomain(@ModelAttribute("request") CreateCostDomainRequest request) {
        costService.createCostDomain(request);
        return "redirect:/cost?monthStr=" + request.getMonthStr();
    }

    /**
     * 修改总预算
     * @param updateTotalBudgetRequest
     * @return
     */
    @PostMapping("/updateTotalBudget")
    public String updateTotalBudget(@ModelAttribute(value = "updateTotalBudgetRequest") UpdateTotalBudgetRequest updateTotalBudgetRequest) {
        costService.updateTotalBudget(updateTotalBudgetRequest.getTotalBudget(), updateTotalBudgetRequest.getMonthStr());
        return "redirect:/cost?monthStr=" + updateTotalBudgetRequest.getMonthStr();
    }

    /**
     * 修改分类预算或新增分类
     * @param request
     * @return
     */
    @PostMapping("/createOrUpdateCostCategory")
    public String createOrUpdateCostCategory(@ModelAttribute("request") CreateOrUpdateCategoryRequest request) {
        costService.createOrUpdateCostCategory(request.getCategory(), request.getBudget(), request.getMonthStr());
        return "redirect:/cost?monthStr=" + request.getMonthStr();
    }

    /**
     * 删除某笔记录
     * @param deleteCostRequest
     * @return
     */
    @PostMapping("/deleteCost")
    public String deleteCost(@ModelAttribute(value = "deleteCostRequest") DeleteCostRequest deleteCostRequest) {
        costService.deleteCost(deleteCostRequest.getMonthStr(), deleteCostRequest.getCostIdentity());
        return "redirect:/cost?monthStr=" + deleteCostRequest.getMonthStr();
    }

}
