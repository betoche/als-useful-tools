package org.als.random.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("database-search")
public class DatabaseSearchController {
    public static final String DATABASE_SEARCH_VIEW = "database-search";

    @GetMapping({"", "/"})
    public ModelAndView getDatabaseSearchView() {
        return new ModelAndView(DATABASE_SEARCH_VIEW);
    }

}
