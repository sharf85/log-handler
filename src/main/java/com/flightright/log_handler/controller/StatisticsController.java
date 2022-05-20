package com.flightright.log_handler.controller;

import com.flightright.log_handler.service.UserEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ThreadPoolExecutor;


@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final ThreadPoolExecutor fileHandlerExecutor;
    private final UserEntryService userEntryService;

    public StatisticsController(ThreadPoolExecutor fileHandlerExecutor, UserEntryService userEntryService) {
        this.fileHandlerExecutor = fileHandlerExecutor;
        this.userEntryService = userEntryService;
    }

    @GetMapping("/unique-users-by-source")
    public String uniqueUsersBySource(Model model) {
        if (fileHandlerExecutor.isTerminated()) {
            model.addAttribute("map", userEntryService.getUniqueUsersNumberGroupedBySource());
            return "unique-users";
        }else{
            return "statistics-not-ready";
        }
    }
}
