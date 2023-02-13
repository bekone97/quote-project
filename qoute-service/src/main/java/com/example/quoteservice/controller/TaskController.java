package com.example.quoteservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/task")
public class TaskController {

    @GetMapping
    @ResponseStatus(OK)
    public void makeTask(){
//        Do nothing
    }
}
