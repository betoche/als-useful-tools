package org.als.random.controller;

import org.als.random.RandomLogger;
import org.als.random.utils.MavenProjectParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/maven-project")
public class MavenProjectController {

    @GetMapping("")
    public ModelAndView getInitialMajenProjectView() {
        var genericFormView = new ModelAndView("maven-project");

        return genericFormView;
    }

    @GetMapping("/project-view")
    public ModelAndView getInitialMajenProjectView(@RequestParam("project-dir") String projectDir, @RequestParam("initial-project-dir") String initialProjectDir) {
        var genericFormView = new ModelAndView("maven-project");
        var mavenProject = new MavenProjectParser().parseProject(projectDir, initialProjectDir);
        genericFormView.addObject("project", mavenProject);
        RandomLogger log = new RandomLogger(MavenProjectController.class);
        log.log(mavenProject);

        return genericFormView;
    }
}
