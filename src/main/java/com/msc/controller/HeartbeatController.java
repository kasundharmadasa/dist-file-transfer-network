package com.msc.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HeartbeatController {

    @GetMapping("/ping")
    public ResponseEntity<Resource> ping(HttpServletRequest request) {

        return ResponseEntity.ok().build();
    }
}
