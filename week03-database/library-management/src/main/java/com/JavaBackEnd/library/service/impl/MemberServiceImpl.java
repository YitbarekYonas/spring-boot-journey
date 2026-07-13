package com.JavaBackEnd.library.service.impl;

import com.JavaBackEnd.library.entity.Member;
import com.JavaBackEnd.library.repository.MemberRepository;
import com.JavaBackEnd.library.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public List<Member> getActiveMembers() {
        return memberRepository.findByActive(true);
    }

    @Override
    @Transactional
    public Member createMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException(
                "Member with email already exists: " + member.getEmail());
        }
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Optional<Member> updateMember(Long id, Member updatedMember) {
        return memberRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedMember.getName());
                    existing.setPhoneNumber(updatedMember.getPhoneNumber());
                    return memberRepository.save(existing);
                });
    }

    @Override
    @Transactional
    public boolean deactivateMember(Long id) {
        return memberRepository.findById(id)
                .map(member -> {
                    member.setActive(false);
                    memberRepository.save(member);
                    return true;
                })
                .orElse(false);
    }
}