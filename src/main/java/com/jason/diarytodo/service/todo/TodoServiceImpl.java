package com.jason.diarytodo.service.todo;

import com.jason.diarytodo.domain.todo.*;
import com.jason.diarytodo.mapper.todo.TodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoServiceImpl implements TodoService {
  private final TodoMapper todoMapper;
  @Override
  public TodoSearchRespDTO getTodos(TodoSearchReqDTO todoSearchReqDTO) {
    log.info(todoSearchReqDTO.toString());
    // SQL injection 예방
    List<String> allowSortBy = List.of("dno", "title", "duedate", "created_at", "updated_at");
    if (!allowSortBy.contains(todoSearchReqDTO.getSortBy())) {
      throw new IllegalArgumentException("허용되지 않은 정렬 컬럼입니다.");
    }
    String sortDirection = todoSearchReqDTO.getSortDirection();
    if (!("ASC".equalsIgnoreCase(sortDirection) || "DESC".equalsIgnoreCase(sortDirection))) {
      throw new IllegalArgumentException("정렬 방향은 ASC 또는 DESC만 가능합니다.");
    }

    // 필터링
    switch (todoSearchReqDTO.getStatus()) {
      case "todayList":
        todoSearchReqDTO.setDuedate(LocalDate.now());
        break;
      case "unfinishedList":
        todoSearchReqDTO.setIsFinished(false);
        break;
      case "importantList":
        todoSearchReqDTO.setIsImportant(true);
        break;
      case "hasDuedateList":
        todoSearchReqDTO.setHasDuedate(true);
        break;
      case "noDuedateList":
        todoSearchReqDTO.setHasDuedate(false);
        break;
      case "calPickDuedateList":
        // 이미 값을 받아오므로 생략 가능
        break;
      case "allList":
      default:
        // 아무것도 세팅하지 않음
        break;
    }

    int totalTodos = todoMapper.selectTotalTodos(todoSearchReqDTO);

    List<TodoRespDTO> todos = todoMapper.selectTodos(todoSearchReqDTO);

    return TodoSearchRespDTO.withPageInfo()
      .totalTodos(totalTodos)
      .todoSearchReqDTO(todoSearchReqDTO)
      .todos(todos)
      .build();
  }

  @Override
  public int modifyTodo(TodoReqDTO todoReqDTO) {
    return todoMapper.updateTodo(todoReqDTO);
  }

  @Override
  public TodoStatusCountDTO getTodoCount(String writer) {
    LocalDate today = LocalDate.now();
    return todoMapper.selectTodoCount(today, writer);
  }

  @Override
  public int addTodo(TodoReqDTO todoReqDTO) {
    return todoMapper.insertTodo(todoReqDTO);
  }

  @Override
  public TodoRespDTO getDetailInfos(int dno) {
    return todoMapper.selectTodoByDno(dno);
  }
}
