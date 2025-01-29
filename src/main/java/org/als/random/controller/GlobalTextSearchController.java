package org.als.random.controller;

import org.als.random.service.GlobalTextSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/global-text-search")
public class GlobalTextSearchController {
    @Autowired
    private GlobalTextSearchService globalTextSearchService;

    @GetMapping({"", "/"})
    public ModelAndView getGlobalTextSearch() {
        ModelAndView mv = new ModelAndView("global-text-search");
        mv.addObject("searchList", globalTextSearchService.getSearchHistoryList());

        return mv;
    }
}
