package com.jason.diarytodo.mapper.todo;

import com.jason.diarytodo.domain.todo.TodoReqDTO;
import com.jason.diarytodo.domain.todo.TodoRespDTO;
import com.jason.diarytodo.domain.todo.TodoSearchReqDTO;
import com.jason.diarytodo.domain.todo.TodoSearchRespDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TodoMapper {

  // TodoMapper.xml
  List<TodoRespDTO> selectTodos(TodoSearchReqDTO todoSearchReqDTO);

  // TodoMapper.xml
  int selectTotalTodos(TodoSearchReqDTO todoSearchReqDTO);

  // TodoMapper.xml
  int updateTodo(TodoReqDTO todoReqDTO);
}
