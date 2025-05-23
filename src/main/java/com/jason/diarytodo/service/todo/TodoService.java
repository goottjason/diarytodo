package com.jason.diarytodo.service.todo;

import com.jason.diarytodo.domain.todo.*;

import java.util.List;

public interface TodoService {
  TodoSearchRespDTO getTodos(TodoSearchReqDTO todoSearchReqDTO);

  int modifyTodo(TodoReqDTO todoReqDTO);

  TodoStatusCountDTO getTodoCount(String writer);

  int addTodo(TodoReqDTO todoReqDTO);

  TodoRespDTO getDetailInfos(TodoReqDTO todoReqDTO);

  List<TodoRespDTO> getListForCal(String writer);

  int removeTodo(TodoReqDTO todoReqDTO);
}
