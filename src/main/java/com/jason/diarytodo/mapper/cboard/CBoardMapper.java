package com.jason.diarytodo.mapper.cboard;

import com.jason.diarytodo.domain.cboard.CBoardReqDTO;
import com.jason.diarytodo.domain.cboard.CBoardRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.hboard.domain.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CBoardMapper {

  @Insert("""
    INSERT INTO selfmember (memberId, memberPwd, memberName, memberMobile, memberEmail, memberImg, memberPoint, memberGender) 
    VALUES (#{memberId}, #{memberPwd}, #{memberName}, #{memberMobile}, #{memberEmail}, #{memberImg}, #{memberPoint}, #{memberGender})""")
  int insertNewMember(MemberReqDTO member);

  @Select("SELECT * FROM selfmember where memberId = #{memberId}")
  List<MemberRespDTO> selectMember(@Param("memberId") String memberId);

  // =========================================



  /*@Select("SELECT count(*) FROM selfhboard")*/
  int selectTotalPostsCount(PageCBoardReqDTO pageCBoardReqDTO);

/*  @Select("""
    SELECT * FROM selfhboard 
    ORDER BY ref DESC, refOrder ASC LIMIT #{offset}, #{pageSize}""")*/
  List<CBoardRespDTO> selectPostsByPage(PageCBoardReqDTO pageCBoardReqDTO);



  @Insert("""
    INSERT INTO selfhboard (title, content, writer)
    VALUES (#{title}, #{content}, #{writer})""")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertNewPost(CBoardReqDTO cBoardReqDTO);

  // 파라미터 한 개라면, @Param("boardNo")을 붙이지 않아도 됨
  @Update("UPDATE selfhboard SET ref = #{boardNo} WHERE boardNo = #{boardNo}")
  int updateSetRefToBoardNo(int boardNo);







  @Select("SELECT * FROM selfhboard WHERE boardNo = #{boardNo}")
  CBoardRespDTO selectPostByboardNo(int boardNo);

  /*
   파라미터 두 개라면, 반드시 @Param("")을 붙여야 함
   안 붙이려면, param1, param2로 전달하여 #{param1}, #{param2}로 사용하면 되지만 (네이밍이 헷갈릴 수 있고, 비효율적)
   */
  @Update("UPDATE selfhboard SET refOrder = refOrder + 1 WHERE ref = #{ref} and refOrder > #{refOrder}")
  void updateSetRefOrderPlusOne(@Param("ref") int ref, @Param("refOrder") int refOrder);

  @Insert("""
    INSERT INTO selfhboard (title, content, writer, ref, step, refOrder)
    VALUES (#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder})""")
  @Options(useGeneratedKeys = true, keyProperty = "boardNo")
  int insertNewReply(CBoardReqDTO cBoardReqDTO);

  @Select("""
    SELECT IFNULL((SELECT TIMESTAMPDIFF(HOUR, readWhen, NOW()) FROM selfhboardlog
    WHERE readWho = #{ipAddr} and boardNo = #{boardNo}), -1)""")
  int selectDateDiff(@Param("boardNo") int boardNo, @Param("ipAddr") String ipAddr);

  @Update("update selfhboard set readCount = readCount + 1 where boardNo = #{boardNo}")
  int incrementReadCount(int boardNo);

  @Update("update selfhboardlog set readWhen = now() where readWho = #{readWho} and boardNo = #{boardNo}")
  void updateLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Insert("insert into selfhboardlog (readWho, boardNo) values(#{readWho}, #{boardNo})")
  int insertLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

  @Update("update selfhboard set title = #{title}, content = #{content} where boardNo = #{boardNo}")
  void updatePost(CBoardReqDTO cBoardReqDTO);

  @Delete("update selfhboard set status = 'D', title = '', content = '' where boardNo = #{boardNo}")
  void deletePost(int boardNo);
}
