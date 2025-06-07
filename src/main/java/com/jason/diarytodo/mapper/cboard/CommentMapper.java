package com.jason.diarytodo.mapper.cboard;

import com.jason.diarytodo.domain.cboard.CommentReqDTO;
import com.jason.diarytodo.domain.cboard.CommentRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import org.apache.ibatis.annotations.*;

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

  @Select("select * from comments where comment_id = #{commentId}")
  CommentRespDTO selectCommentByCommentId(Integer commentId);

  @Update("update comments set content = #{content} where comment_id = #{commentId}")
  int updateComment(CommentReqDTO commentReqDTO);

  @Delete("delete from comments where comment_id = ${commentId}")
  int deleteComment(Integer commentId);

  @Update("update cboard set comment_count = comment_count + 1 where board_no = #{boardNo}")
  void updateCommentCountPlusFromCboard(CommentReqDTO commentReqDTO);

  @Update("update cboard set comment_count = comment_count - 1 where board_no = #{boardNo}")
  void updateCommentCountMinusFromCboard(Integer boardNo);

  @Select("select board_no from comments where comment_id = #{commentId}")
  int selectBoardNoByCommendId(Integer commentId);
}
