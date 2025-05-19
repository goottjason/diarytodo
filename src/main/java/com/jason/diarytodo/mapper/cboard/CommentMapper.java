package com.jason.diarytodo.mapper.cboard;

import com.jason.diarytodo.domain.cboard.CommentReqDTO;
import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper {

  @Select("select count(*) from comments where board_no = #{boardNo}")
  int selectCommentCount(int boardNo);

  @Select("select * from comments where board_no = #{boardNo} order by comment_id desc")
  List<CommentRespDTO> selectAllComments(PageCBoardReqDTO pageCBoardReqDTO);


  @Select("select * from comments where board_no = #{boardNo} order by comment_id desc limit #{offset}, #{pageSize}")
  List<CommentRespDTO> selectAllCommentsLimitPage(PageCBoardReqDTO pageCBoardReqDTO);

  @Insert("insert into comments (commenter, content, board_no) values (#{commenter}, #{content}, #{boardNo})")
  int insertComment(CommentReqDTO commentReqDTO);

  @Select("select * from comments where comment_id = #{commentNo}")
  CommentRespDTO selectCommentByCommentNo(Integer commentNo);

  @Update("update comments set content = #{content} where comment_id = #{commentId}")
  int updateComment(CommentReqDTO commentReqDTO);
}
