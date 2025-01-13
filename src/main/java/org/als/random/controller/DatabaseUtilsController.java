package org.als.random.controller;

import org.als.random.domain.RandomConfiguration;
import org.als.random.service.DBSnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
@RequestMapping("/db-utils")
public class DatabaseUtilsController {
    private Logger LOGGER = LoggerFactory.getLogger(DatabaseUtilsController.class);
    @Autowired
    private RandomConfiguration randomConfiguration;

    @GetMapping({"", "/"})
    public ModelAndView getDbUtilsHome(@RequestParam String optionKey) {
        if(Objects.isNull(optionKey)) {
            RandomController homeController = new RandomController();
            return homeController.getHome();
        }

        ModelAndView mv = new ModelAndView("database-utils");
        mv.addObject("option", randomConfiguration.findOptionByKey(optionKey));

        return mv;
    }

}
