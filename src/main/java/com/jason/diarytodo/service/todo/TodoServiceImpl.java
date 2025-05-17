package com.jason.diarytodo.service.todo;

import com.jason.diarytodo.domain.todo.TodoRespDTO;
import com.jason.diarytodo.domain.todo.TodoSearchReqDTO;
import com.jason.diarytodo.domain.todo.TodoSearchRespDTO;
import com.jason.diarytodo.mapper.todo.TodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoServiceImpl implements TodoService {
  private final TodoMapper todoMapper;
  @Override
  public TodoSearchRespDTO getTodos(TodoSearchReqDTO todoSearchReqDTO) {

    List<String> allowSortBy = List.of("title", "duedate", "created_at", "updated_at");
    if (!allowSortBy.contains(todoSearchReqDTO.getSortBy())) {
      throw new IllegalArgumentException("허용되지 않은 정렬 컬럼입니다.");
    }
    String sortDirection = todoSearchReqDTO.getSortDirection();
    if (!("ASC".equalsIgnoreCase(sortDirection) || "DESC".equalsIgnoreCase(sortDirection))) {
      throw new IllegalArgumentException("정렬 방향은 ASC 또는 DESC만 가능합니다.");
    }

    int totalTodos = todoMapper.selectTotalTodos(todoSearchReqDTO);

    List<TodoRespDTO> todos = todoMapper.selectTodos(todoSearchReqDTO);

    return TodoSearchRespDTO.withPageInfo()
      .totalTodos(totalTodos)
      .todoSearchReqDTO(todoSearchReqDTO)
      .todos(todos)
      .build();
  }
}
