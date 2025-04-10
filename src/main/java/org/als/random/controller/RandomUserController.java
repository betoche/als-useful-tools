package org.als.random.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/random-user")
public class RandomUserController {

    @GetMapping({"", "/"})
    public ModelAndView showUserManagerForm(){
        ModelAndView mv = new ModelAndView("random-user-manager");

        return mv;
    }
}
