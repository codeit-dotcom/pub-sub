package org.example.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    //private final TodoRepository repo;

    public TodoController() {
        //this.repo = repo;
    }

    @GetMapping
    public List<String> getAllTodos() {
        return List.of("hello", "hii");
    }

    @PostMapping
    public Boolean createTodo() {
        return true;
    }

}
