package com.jason.diarytodo.mapper.cboard;

import com.jason.diarytodo.domain.cboard.CBoardReqDTO;
import com.jason.diarytodo.domain.cboard.CBoardRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.common.AttachmentReqDTO;
import com.jason.diarytodo.domain.member.MemberReqDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CBoardMapper {

  /*@Select("SELECT count(*) FROM cboard")*/
  int selectTotalPostsCount(PageCBoardReqDTO pageCBoardReqDTO);

/*  @Select("""
    SELECT * FROM cboard
    ORDER BY ref DESC, ref_order ASC LIMIT #{offset}, #{pageSize}""")*/
  List<CBoardRespDTO> selectPostsByPage(PageCBoardReqDTO pageCBoardReqDTO);



  @Insert("""
    INSERT INTO cboard (title, content, writer)
    VALUES (#{title}, #{content}, #{writer})""")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertNewPost(CBoardReqDTO cBoardReqDTO);

  // 파라미터 한 개라면, @Param("boardNo")을 붙이지 않아도 됨
  @Update("UPDATE cboard SET ref = #{boardNo} WHERE board_no = #{boardNo}")
  int updateSetRefToBoardNo(int boardNo);







  @Select("SELECT * FROM cboard WHERE board_no = #{boardNo}")
  CBoardRespDTO selectPostByboardNo(int boardNo);

  /*
   파라미터 두 개라면, 반드시 @Param("")을 붙여야 함
   안 붙이려면, param1, param2로 전달하여 #{param1}, #{param2}로 사용하면 되지만 (네이밍이 헷갈릴 수 있고, 비효율적)
   */
  @Update("UPDATE cboard SET ref_order = ref_order + 1 WHERE ref = #{ref} and ref_order > #{refOrder}")
  void updateSetRefOrderPlusOne(@Param("ref") int ref, @Param("refOrder") int refOrder);

  @Insert("""
    INSERT INTO cboard (title, content, writer, ref, step, ref_order)
    VALUES (#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder})""")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertNewReply(CBoardReqDTO cBoardReqDTO);

  @Select("""
    SELECT IFNULL((SELECT TIMESTAMPDIFF(HOUR, read_when, NOW()) FROM cboard_log
    WHERE read_who = #{ipAddr} and board_no = #{boardNo}), -1)""")
  int selectDateDiff(@Param("boardNo") int boardNo, @Param("ipAddr") String ipAddr);

  @Update("update cboard set view_count = view_count + 1 where board_no = #{boardNo}")
  int incrementReadCount(int boardNo);

  @Update("update cboard_log set read_when = now() where read_who = #{readWho} and board_no = #{boardNo}")
  void updateLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Insert("insert into cboard_log (read_who, board_no) values(#{readWho}, #{boardNo})")
  int insertLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Update("update cboard set title = #{title}, content = #{content} where board_no = #{boardNo}")
  void updatePost(CBoardReqDTO cBoardReqDTO);

  @Delete("update cboard set deleted_flag = 1, title = '', content = '' where board_no = #{boardNo}")
  void deletePost(int boardNo);


  @Update("INSERT INTO attachment (original_name, stored_name, stored_thumb_name, is_image, ext, size, ref_type, ref_id, base64, stored_path, stored_thumb_path) VALUES (#{originalName}, #{storedName}, #{storedThumbName}, #{isImage}, #{ext}, #{size}, #{refType}, #{refId}, #{base64}, #{storedPath}, #{storedThumbPath})")
  void insertAttachment(AttachmentReqDTO attachmentReqDTO);
}
