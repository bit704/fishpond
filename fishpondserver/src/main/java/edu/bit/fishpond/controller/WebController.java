package edu.bit.fishpond.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class WebController {

    @GetMapping("/data")
    public String mockData() {
        return  "你好！";
    }

}
