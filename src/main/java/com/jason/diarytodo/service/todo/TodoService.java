package com.jason.diarytodo.service.todo;

import com.jason.diarytodo.domain.todo.*;

public interface TodoService {
  TodoSearchRespDTO getTodos(TodoSearchReqDTO todoSearchReqDTO);

  int modifyTodo(TodoReqDTO todoReqDTO);

  TodoStatusCountDTO getTodoCount();

  int addTodo(TodoReqDTO todoReqDTO);
}
