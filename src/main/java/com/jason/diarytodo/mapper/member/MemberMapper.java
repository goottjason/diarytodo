package com.jason.diarytodo.mapper.member;

import com.jason.diarytodo.domain.member.MemberReqDTO;
import com.jason.diarytodo.domain.member.MemberRespDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper {
  @Insert("INSERT INTO member (login_id, password, nickname, email, profile_image, gender) VALUES (#{loginId}, #{password}, #{nickname}, #{email}, #{profileImage}, #{gender})")
  int insertMember(MemberReqDTO memberReqDTO);

  @Select("select * from member where login_id = #{loginId}")
  MemberRespDTO selectMemberByLoginId(String loginId);

  @Select("select * from member where email = #{email}")
  MemberRespDTO selectMemberByEmail(String email);
}
