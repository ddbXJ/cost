package com.ddbxj.cost;

import com.ddbxj.cost.module.CostDomain;
import com.ddbxj.cost.module.CostRecords;
import com.ddbxj.cost.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lee.li
 * @date 2018/05/10
 */
@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private CostService costService;

    @GetMapping("/cost")
    public String cost(@RequestParam(value = "name", required = false, defaultValue = "default") String name, Model model) {
        CostDomain domain = costService.getCostDomain(name);
        Set<String> set = domain.getCategoryList().stream().collect(Collectors.groupingBy(CostDomain.CostCategory::getCategory)).keySet();

        model.addAttribute("costDomain", domain);
        model.addAttribute("record", new CostRecords.CostRecord());
        model.addAttribute("categories", set);
        model.addAttribute("records", costService.getCostRecords(name));

        return "cost";
    }

    @GetMapping("/records")
    public String records(@RequestParam(value = "name", required = false, defaultValue = "default") String name, Model model) {
        model.addAttribute("records", costService.getCostRecords(name));
        return "records";
    }

    @PostMapping("/addCost")
    public String addCost(@ModelAttribute("record") CostRecords.CostRecord record, @RequestParam(value = "name", required = false, defaultValue = "default") String name) {
        costService.addCost(record, name);
        return "redirect:/cost";
    }

}
