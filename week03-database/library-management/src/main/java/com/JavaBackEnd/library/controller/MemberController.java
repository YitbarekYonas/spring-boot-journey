package com.JavaBackEnd.library.controller;

import com.JavaBackEnd.library.entity.Member;
import com.JavaBackEnd.library.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Member>> getActiveMembers() {
        return ResponseEntity.ok(memberService.getActiveMembers());
    }

    @PostMapping
    public ResponseEntity<Member> createMember(
            @RequestBody Member member,
            UriComponentsBuilder uriBuilder) {

        Member created = memberService.createMember(member);
        URI location = uriBuilder
                .path("/api/members/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @RequestBody Member member) {
        return memberService.updateMember(id, member)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        boolean deactivated = memberService.deactivateMember(id);
        return deactivated ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}