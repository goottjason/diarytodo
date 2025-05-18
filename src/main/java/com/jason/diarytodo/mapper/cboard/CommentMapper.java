package com.jason.diarytodo.mapper.cboard;

import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

  @Select("select count(*) from comments where board_no = #{boardNo}")
  int selectCommentCount(int boardNo);

  @Select("select * from comments where board_no = #{boardNo} order by comment_id desc")
  List<CommentRespDTO> selectAllComments(PageCBoardReqDTO pageCBoardReqDTO);


  @Select("select * from comments where board_no = #{boardNo} order by comment_id desc limit #{skip}, #{pageSize}")
  List<CommentRespDTO> selectAllCommentsLimitPage(PageCBoardReqDTO pageCBoardReqDTO);
}
