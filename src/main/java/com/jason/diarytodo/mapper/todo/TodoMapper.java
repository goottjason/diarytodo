package com.jason.diarytodo.mapper.todo;

import com.jason.diarytodo.domain.todo.*;
import org.apache.ibatis.annotations.*;

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
  TodoStatusCountDTO selectTodoCount(@Param("today") LocalDate today, @Param("writer") String writer);

  // TodoMapper.xml
  int insertTodo(TodoReqDTO todoReqDTO);

  @Select("select * from todo where dno = #{dno}")
  TodoRespDTO selectTodoByDno(TodoReqDTO todoReqDTO);

  @Select("select * from todo where duedate is not null and writer = #{writer}")
  List<TodoRespDTO> selectListForCal(String writer);

  @Delete("delete from todo where dno = #{dno} and writer = #{writer}")
  int deleteTodo(TodoReqDTO todoReqDTO);
}
