package com.baoxian.controller;

import com.baoxian.common.statemachine.entity.State;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @create: 2020-06-28 09:26
 */
@RestController
public class IndexController {

    @GetMapping("index")
    @ResponseBody
    public List<State> index() {
        return new State().query();
    }

}
