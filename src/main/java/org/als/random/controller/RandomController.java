package org.als.random.controller;

import org.als.random.config.RandomOptionsConfiguration;
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
@RequestMapping("/")
public class RandomController {
    @Autowired
    private RandomOptionsConfiguration myRandomOptions;
    private final static Logger LOGGER = LoggerFactory.getLogger(RandomController.class);

    @GetMapping({"/", "/home", "/error"})
    public ModelAndView getHome() {
        LOGGER.info(String.format("Name: %s", myRandomOptions.getName()));
        LOGGER.info(String.format("StringList: %s", myRandomOptions.getStringList()));
        LOGGER.info(String.format("GenericOptions: %s", myRandomOptions.getGenericOptions()));
        LOGGER.info(String.format("SpecificOptions: %s", myRandomOptions.getSpecificOptions()));

        String dateStr = "12/30/2025";
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdfYYYY = new SimpleDateFormat("MM/dd/YYYY");
        SimpleDateFormat sdfyyyy = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date YYYY = sdfYYYY.parse(dateStr);
            Date yyyy = sdfyyyy.parse(dateStr);

            LOGGER.info(String.format("YYYY: %s, yyyy: %s", YYYY, yyyy));
            LOGGER.info(String.format("today: { YYYY: %s, yyyy: %s }", sdfYYYY.format(today), sdfyyyy.format(today)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        return new ModelAndView("home");
    }
}
