package com.jason.diarytodo.mapper.member;

import com.jason.diarytodo.domain.member.MemberReqDTO;
import com.jason.diarytodo.domain.member.MemberRespDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface MemberMapper {
  @Insert("INSERT INTO member (login_id, password, nickname, email, profile_image, gender) VALUES (#{loginId}, #{password}, #{nickname}, #{email}, #{profileImage}, #{gender})")
  int insertMember(MemberReqDTO memberReqDTO);

  @Select("select * from member where login_id = #{loginId}")
  MemberRespDTO selectMemberByLoginId(String loginId);

  @Select("select * from member where email = #{email}")
  MemberRespDTO selectMemberByEmail(String email);

  // 아래 두개 하나로 합칠 수 있음 (나중에)
  @Update("update member set session_id = null, auto_login_limit = null where login_id = #{loginId}")
  void updateAuthLoginInfoToNull(String loginId);
  @Update("update member set session_id = #{autoLoginSessionId}, auto_login_limit = #{autoLoginLimit} where login_id = #{loginId}")
  void updateAuthLoginInfo(String loginId, String autoLoginSessionId, LocalDateTime autoLoginLimit);

  @Select("select * from member where session_id = #{autoLoginSessionId}")
  MemberRespDTO selectMemberByAutoLoginSessionId(String autoLoginSessionId);
}
