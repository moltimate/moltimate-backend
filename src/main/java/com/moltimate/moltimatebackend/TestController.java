package com.moltimate.moltimatebackend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moltimatetest")
public class TestController {

    @RequestMapping(value = "/josh", method = RequestMethod.GET)
    public String contractorPaymentsQueryOne() {
        return "miller";
    }
}
