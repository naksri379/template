package com.mytest.nack.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MainController {
    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public String process() {
        return "Hello, World";
    }
}
