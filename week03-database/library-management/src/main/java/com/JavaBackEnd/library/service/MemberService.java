package com.JavaBackEnd.library.service;

import com.JavaBackEnd.library.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    List<Member> getAllMembers();

    Optional<Member> getMemberById(Long id);

    Optional<Member> getMemberByEmail(String email);

    List<Member> getActiveMembers();

    Member createMember(Member member);

    Optional<Member> updateMember(Long id, Member updatedMember);

    boolean deactivateMember(Long id);
}