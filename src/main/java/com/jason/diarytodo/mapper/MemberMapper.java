package com.jason.diarytodo.mapper;

import com.jason.diarytodo.domain.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper {
  @Insert("insert into member (memberId, memberPwd, memberName, mobile, email, memberImg, gender) values (#{memberId}, #{memberPwd}, #{memberName}, #{mobile}, #{email}, #{memberImg}, #{gender})")
  int insertMemberByMember(Member member);

  @Select("select * from member where memberId = #{memberId]}")
  Member findMemberById(String memberId);
}
