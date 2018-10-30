package com.moltimate.moltimatebackend.query;

import com.moltimate.moltimatebackend.alignment.AlignmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/query")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AlignmentResponse query(@RequestBody QueryRequest queryRequest) {
        return queryService.query(queryRequest);
    }
}
