package org.als.random.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @GetMapping({"", "/"})
    public ModelAndView showExpenseForm(){
        ModelAndView mv = new ModelAndView("expense-form");

        return mv;
    }
}
