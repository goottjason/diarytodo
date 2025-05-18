package com.jason.diarytodo.mapper.todo;

import com.jason.diarytodo.domain.todo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoMapper {

  // TodoMapper.xml
  List<TodoRespDTO> selectTodos(TodoSearchReqDTO todoSearchReqDTO);

  // TodoMapper.xml
  int selectTotalTodos(TodoSearchReqDTO todoSearchReqDTO);

  // TodoMapper.xml
  int updateTodo(TodoReqDTO todoReqDTO);

  // TodoMapper.xml
  TodoStatusCountDTO selectTodoCount(LocalDate today);

  // TodoMapper.xml
  int insertTodo(TodoReqDTO todoReqDTO);
}
