package org.als.random.controller;

import org.als.random.domain.RandomConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Controller
@RequestMapping("")
public class RandomController {
    @Autowired
    private RandomConfiguration randomConfiguration;
    private final static Logger LOGGER = LoggerFactory.getLogger(RandomController.class);

    @GetMapping({"", "/", "/home", "/error"})
    public ModelAndView getHome() {
        ModelAndView mv = new ModelAndView("home");
        mv.addObject("config", randomConfiguration);
        return mv;
    }
}
