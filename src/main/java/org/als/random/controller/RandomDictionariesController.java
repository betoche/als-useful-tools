package org.als.random.controller;

import org.als.random.domain.HtmlFormDefinition;
import org.als.random.utils.DictionaryEntityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/dictionaries")
public class RandomDictionariesController {

    @GetMapping("/{entityClass}")
    public ModelAndView getCreateDictionaryForm(@RequestParam String entityClass) {
        ModelAndView mv = new ModelAndView("random-dictionary-form");
        HtmlFormDefinition htmlFormDefinition = DictionaryEntityHelper.createHtmlFormDefinitionFromEntityName(entityClass);
        mv.addObject("htmlFormDefinition", htmlFormDefinition);

        return mv;
    }
}
