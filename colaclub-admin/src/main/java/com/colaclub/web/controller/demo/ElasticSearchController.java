package com.colaclub.web.controller.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ElasticSearchController {
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public boolean createUser(@RequestBody User user) {
        return userService.insert(user);
    }

    @GetMapping("/user/searchContent")
    public List<User> search(@RequestParam(value = "searchContent") String searchContent) {
        return userService.search(searchContent);
    }

    @GetMapping("/user")
    public List<User> searchUser(@RequestParam(value = "pageNumber") Integer pageNumber,
                                 @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                 @RequestParam(value = "searchContent") String searchContent) {
        return userService.searchUser(pageNumber, pageSize, searchContent);
    }

    @GetMapping("/searchUserByWeight")
    public List<User> searchUserByWeight(@RequestParam(value = "searchContent") String searchContent) {
        return userService.searchUserByWeight(searchContent);
    }
}
