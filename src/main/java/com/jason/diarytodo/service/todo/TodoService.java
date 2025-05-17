package com.jason.diarytodo.service.todo;

import com.jason.diarytodo.domain.todo.TodoRespDTO;
import com.jason.diarytodo.domain.todo.TodoSearchReqDTO;
import com.jason.diarytodo.domain.todo.TodoSearchRespDTO;

public interface TodoService {
  TodoSearchRespDTO getTodos(TodoSearchReqDTO todoSearchReqDTO);
}
