package com.jason.diarytodo.controller.todo;

import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.domain.todo.*;
import com.jason.diarytodo.service.todo.TodoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TodoController {
  private final TodoService todoService;

  @GetMapping("/todo")
  public String index(Model model, HttpSession session) {
    /*TodoSearchRespDTO todoSearchRespDTO = todoService.getTodos(todoSearchReqDTO);
    model.addAttribute("todoSearchRespDTO", todoSearchRespDTO);*/
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    TodoStatusCountDTO todoCount = todoService.getTodoCount(loginMember.getLoginId());
    log.info("todoCount: {}", todoCount);
    model.addAttribute("todoCount", todoCount);
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

  @PostMapping("/todo/addTodo")
  @ResponseBody
  public String addTodo(@RequestBody TodoReqDTO todoReqDTO, Model model) {
    int result = todoService.addTodo(todoReqDTO);
    if (result == 1) {
      return "success";
    }
    return "failure";
  }

  @PostMapping("/todo/modifyTodo")
  @ResponseBody
  public String modifyTodo(@RequestBody TodoReqDTO todoReqDTO, Model model) {
    int result = todoService.modifyTodo(todoReqDTO);
    if (result == 1) {
      return "success";
    }
    return "failure";
  }
  @PostMapping("/todo/getDetailInfos")
  public String getDetailInfos(@RequestBody int dno, Model model) {
    TodoRespDTO todoRespDTO = todoService.getDetailInfos(dno);
    model.addAttribute("todoRespDTO", todoRespDTO);
    return "todo/todo-right :: todoRight";
  }
  @PostMapping("/todo/getListForCal")
  public ResponseEntity<List> getListForCal() {
    List<TodoRespDTO> todoRespDTOS = todoService.getListForCal();
    log.info("todoRespDTOS: {}", todoRespDTOS);
    List<LocalDate> listForCal = new ArrayList<>();
    for (TodoRespDTO todoRespDTO : todoRespDTOS) {
      listForCal.add(todoRespDTO.getDuedate());
    }
    log.info("listForCal: {}", listForCal);
    return ResponseEntity.ok().body(listForCal);
  }
}
