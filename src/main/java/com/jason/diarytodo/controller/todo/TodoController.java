package com.jason.diarytodo.controller.todo;

import com.jason.diarytodo.domain.member.MemberRespDTO;
import com.jason.diarytodo.domain.todo.*;
import com.jason.diarytodo.service.todo.TodoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TodoController {
  private final TodoService todoService;

  /**
   *
   * @param model   : 메인페이지 접근시마다 각 status의 count를 조회하여 model에 전달하여 갱신
   * @param session : 로그인 멤버의 것으로 조회
   * @return        : 뷰를 반환
   */
  @GetMapping("/todo")
  public String index(Model model, HttpSession session) {
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    TodoStatusCountDTO todoCount = todoService.getTodoCount(loginMember.getLoginId());
    log.info("todoCount: {}", todoCount);
    model.addAttribute("todoCount", todoCount);
    return "/todo/index";
  }

  /**
   * @param todoSearchReqDTO : 뷰단에서 json 형태로 data 전달 => DTO에 해당 필드 값에 넣어줌(맵핑)
   * @param model            : todoSearchRespDTO, todos, now를 model에 전달
   * @param session          : 로그인 멤버의 것으로 조회
   * @return                 : 별도의 뷰에서 가공된 html을 ajax에 응답
   */
  @PostMapping("/todo/getTodos")
  public String getTodos(@RequestBody TodoSearchReqDTO todoSearchReqDTO, Model model, HttpSession session) {
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");
    todoSearchReqDTO.setWriter(loginMember.getLoginId());
    log.info("■■■Controller : req - {}", todoSearchReqDTO);
    TodoSearchRespDTO todoSearchRespDTO = todoService.getTodos(todoSearchReqDTO);

    log.info("■■■Controller : resp - {}", todoSearchRespDTO.toString());
    List<TodoRespDTO> todos = todoSearchRespDTO.getTodos();
    model.addAttribute("todoSearchRespDTO", todoSearchRespDTO);
    model.addAttribute("todos", todos);
    LocalDate now = LocalDate.now();
    model.addAttribute("now", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    return "todo/todo-table :: todoTable"; /*todo-table.html에 th:fragment="todoTable"*/
  }

  /**
   *
   * @param session: 로그인 멤버의 것으로 조회
   * @return : DTO를 json 값으로 반환 (뷰단에서는 받아온 값으로 바꿔줌)
   */
  @PostMapping(value = "/todo/todoCnt", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public TodoStatusCountDTO todoCnt(HttpSession session) {
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");

    return todoService.getTodoCount(loginMember.getLoginId());
  }


  /**
   *
   * @param todoReqDTO
   * @param model
   * @param session
   * @return
   */
  @PostMapping("/todo/addTodo")
  @ResponseBody
  public String addTodo(@RequestBody TodoReqDTO todoReqDTO, Model model, HttpSession session) {
    int result = todoService.addTodo(todoReqDTO);
    if (result == 1) {
      return "success";
    }
    return "failure";
  }

  @PostMapping("/todo/modifyTodo")
  @ResponseBody
  public String modifyTodo(@RequestBody TodoReqDTO todoReqDTO, Model model, HttpSession session) {
    int result = todoService.modifyTodo(todoReqDTO);
    if (result == 1) {
      return "success";
    }
    return "failure";
  }

  @PostMapping("/todo/getDetailInfos")
  public String getDetailInfos(@RequestBody TodoReqDTO todoReqDTO, Model model) {
    TodoRespDTO todoRespDTO = todoService.getDetailInfos(todoReqDTO);
    model.addAttribute("todoRespDTO", todoRespDTO);
    return "todo/todo-right :: todoRight";
  }

  @PostMapping("/todo/getListForCal")
  @ResponseBody
  public List<LocalDate> getListForCal(HttpSession session) {
    MemberRespDTO loginMember = (MemberRespDTO) session.getAttribute("loginMember");
    List<TodoRespDTO> todoRespDTOS = todoService.getListForCal(loginMember.getLoginId());
    List<LocalDate> listForCal = new ArrayList<>();
    for (TodoRespDTO todoRespDTO : todoRespDTOS) {
      listForCal.add(todoRespDTO.getDuedate());
    }
    return listForCal;
  }
}
