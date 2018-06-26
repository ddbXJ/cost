package com.ddbxj.cost;

import com.ddbxj.cost.module.CostDomain;
import com.ddbxj.cost.module.CostDomainRequest;
import com.ddbxj.cost.module.CostIdentity;
import com.ddbxj.cost.module.CostRecords;
import com.ddbxj.cost.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lee.li
 * @date 2018/05/10
 */
@Controller
public class CostController {

    @Autowired
    private CostService costService;

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
        List<String> monthList = new ArrayList<>();
        monthList.add("2018-06");
        monthList.add("2018-07");
        monthList.add("2018-08");
        monthList.add("2018-09");
        monthList.add("2018-10");
        model.addAttribute("costIdentity", new CostIdentity());
        model.addAttribute("monthList", monthList);
        return "selectCost";
    }

    @PostMapping("/choose")
    public String choose(@ModelAttribute(value = "costIdentity") CostIdentity costIdentity) {
        if (null == costService.getCostDomain(costIdentity.getMonthStr())) {
            return "redirect:/newCost?monthStr=" + costIdentity.getMonthStr();
        }
        return "redirect:/cost?monthStr=" + costIdentity.getMonthStr();
    }

    /**
     * 创建预算页面
     */
    @GetMapping("/newCost")
    public String newCost(@RequestParam(value = "monthStr") String monthStr, Model model) {
        model.addAttribute("monthStr", monthStr);
        model.addAttribute("request", new CostDomainRequest());
        return "newCost";
    }

    @GetMapping("/cost")
    public String cost(@RequestParam(value = "monthStr") String monthStr, Model model) {
        CostDomain domain = costService.getCostDomain(monthStr);
        Set<String> set = domain.getCategoryList().stream().collect(Collectors.groupingBy(CostDomain.CostCategory::getCategory)).keySet();

        model.addAttribute("monthStr", monthStr);
        model.addAttribute("costDomain", domain);
        model.addAttribute("record", new CostRecords.CostRecord());
        model.addAttribute("categories", set);
        model.addAttribute("records", costService.getCostRecords(monthStr));

        return "cost";
    }

    @GetMapping("/records")
    public String records(@RequestParam(value = "monthStr") String monthStr, Model model) {
        model.addAttribute("records", costService.getCostRecords(monthStr));
        return "records";
    }

    /**
     * 添加一笔消费记录
     * @param record
     * @param monthStr
     * @return
     */
    @PostMapping("/addCost")
    public String addCost(@ModelAttribute("record") CostRecords.CostRecord record, @RequestParam(value = "monthStr") String monthStr) {
        costService.addCost(record, monthStr);
        return "redirect:/cost";
    }

    /**
     * 创建一个预算
     * @param request
     * @return
     */
    @PostMapping("/createCostDomain")
    public String createCostDomain(@ModelAttribute("request") CostDomainRequest request) {
        costService.createCostDomain(request);
        return "redirect:/cost?monthStr=" + request.getMonthStr();
    }

    /**
     * 修改总预算
     * @param totalBudget
     * @param monthStr
     * @return
     */
    @PostMapping("/updateTotalBudget")
    public String updateTotalBudget(@RequestParam(value = "totalBudget") BigDecimal totalBudget, @RequestParam(value = "monthStr") String monthStr) {
        costService.updateTotalBudget(totalBudget, monthStr);
        return "redirect:/cost?monthStr=" + monthStr;
    }

    /**
     * 修改分类预算或新增分类
     * @param category
     * @param budget
     * @param monthStr
     * @return
     */
    @PostMapping("/createOrUpdateCostCategory")
    public String createOrUpdateCostCategory(@RequestParam(value = "category") String category, @RequestParam(value = "budget") BigDecimal budget, @RequestParam(value = "monthStr") String monthStr) {
        costService.createOrUpdateCostCategory(category, budget, monthStr);
        return "redirect:/cost?monthStr=" + monthStr;
    }

    /**
     * 删除某笔记录
     * @param costIdentity
     * @param monthStr
     * @return
     */
    @PostMapping("/deleteCost")
    public String deleteCost(@RequestParam(value = "costIdentity") String costIdentity, @RequestParam(value = "monthStr") String monthStr) {
        costService.deleteCost(monthStr, costIdentity);
        return "redirect:/cost?monthStr=" + monthStr;
    }

}
