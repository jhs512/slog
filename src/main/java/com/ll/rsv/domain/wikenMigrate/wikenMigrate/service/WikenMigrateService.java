package com.ll.rsv.domain.wikenMigrate.wikenMigrate.service;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.member.member.service.MemberService;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.wikenMigrate.wikenMigrate.repository.WikenMigratePostRepository;
import com.ll.rsv.global.exceptions.GlobalException;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WikenMigrateService {
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final WikenMigratePostRepository wikenMigrateRepository;

    @Transactional
    public RsData<Empty> migrate(Member actor, String username, String password) {
        Member memberGarage = memberService.findByUsername("garage").orElseThrow(GlobalException.E404::new);
        List<Post> posts = wikenMigrateRepository.findByAuthorAndAddi1(memberGarage, username);

        if (!actor.isSocial()) {
            posts = posts
                    .stream()
                    .filter(post -> passwordEncoder.matches(password, post.getAddi2())).toList();
        }

        posts.forEach(post -> {
            post.setAuthor(actor);
        });

        return RsData.of("총 %d개의 글이 복구되었습니다.".formatted(posts.size()));
    }
}
