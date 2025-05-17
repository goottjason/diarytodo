package com.jason.diarytodo.controller.todo;

import com.jason.diarytodo.domain.todo.TodoReqDTO;
import com.jason.diarytodo.domain.todo.TodoRespDTO;
import com.jason.diarytodo.domain.todo.TodoSearchReqDTO;
import com.jason.diarytodo.domain.todo.TodoSearchRespDTO;
import com.jason.diarytodo.service.todo.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TodoController {
  private final TodoService todoService;

  @GetMapping("/todo")
  public String index(TodoSearchReqDTO todoSearchReqDTO, Model model) {
    /*TodoSearchRespDTO todoSearchRespDTO = todoService.getTodos(todoSearchReqDTO);
    model.addAttribute("todoSearchRespDTO", todoSearchRespDTO);*/

    return "todo/index";
  }
  @PostMapping("/todo/getTodos")
  public String getTodos(@RequestBody TodoSearchReqDTO todoSearchReqDTO, Model model) {

    TodoSearchRespDTO todoSearchRespDTO = todoService.getTodos(todoSearchReqDTO);
    List<TodoRespDTO> todos = todoSearchRespDTO.getTodos();
    model.addAttribute("todoSearchRespDTO", todoSearchRespDTO);
    model.addAttribute("todos", todos);
    model.addAttribute("now", LocalDate.now());
    return "todo/todo-table :: todoTable"; /*todo-table.html에 th:fragment="todoTable"*/
  }
  @PostMapping("/todo/modifyTodo")
  public String modifyTodo(@RequestBody TodoReqDTO todoReqDTO, Model model) {
    int result = todoService.modifyTodo(todoReqDTO);
    if (result == 1) {
      return "success";
    }
    return "failure";
  }

}
