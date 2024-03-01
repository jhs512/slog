package com.ll.rsv.global.initData;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.member.member.service.MemberService;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.post.service.PostService;
import com.ll.rsv.domain.post.postComment.entity.PostComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Profile("!prod")
@Configuration
@Slf4j
@RequiredArgsConstructor
public class NotProd {
    private final MemberService memberService;
    private final PostService postService;

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd() {
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) {
                if (memberService.findByUsername("user1").isPresent()) return;

                Member memberUser1 = memberService.join("user1", "1234").getData();
                memberUser1.setRefreshToken("user1");

                Member memberUser2 = memberService.join("user2", "1234").getData();
                memberUser2.setRefreshToken("user2");

                Member memberUser3 = memberService.join("user3", "1234").getData();
                memberUser3.setRefreshToken("user3");

                Member memberUser4 = memberService.join("user4", "1234").getData();
                memberUser4.setRefreshToken("user4");

                Post post1 = postService.write(memberUser1, "제목 1", "내용 1", true, true);

                post1.addLike(memberUser1);
                post1.addLike(memberUser2);
                post1.addLike(memberUser3);
                post1.addLike(memberUser4);

                PostComment postComment1 = post1.addComment(memberUser1, "댓글 1");
                PostComment postComment2 = post1.addComment(memberUser1, "댓글 2");
                PostComment postComment3 = post1.addComment(memberUser1, "댓글 3");
                PostComment postComment4 = post1.addComment(memberUser1, "댓글 4");
                PostComment postComment5 = post1.addComment(memberUser2, "댓글 5");
                PostComment postComment6 = post1.addComment(memberUser2, "댓글 6");
                PostComment postComment7 = post1.addComment(memberUser2, "댓글 7");
                PostComment postComment8 = post1.addComment(memberUser3, "댓글 8");
                PostComment postComment9 = post1.addComment(memberUser3, "댓글 9");
                PostComment postComment10 = post1.addComment(memberUser4, "댓글 10");

                postComment10.addComment(memberUser1, "답글 1");
                postComment10.addComment(memberUser1, "답글 2");
                postComment10.addComment(memberUser2, "답글 3");
                postComment10.addComment(memberUser2, "답글 4");

                post1.addTag("자바");
                post1.addTag("스프링");
                post1.addTag("스프링부트");

                Post post2 = postService.write(memberUser1, "제목 2", "내용 2", true, true);

                post2.addLike(memberUser1);
                post2.addLike(memberUser2);
                post2.addLike(memberUser3);

                post2.addComment(memberUser1, "# 댓글 11");
                post2.addComment(memberUser1, "## 댓글 12");
                post2.addComment(memberUser1, "`댓글 13`");
                post2.addComment(memberUser2, """
                        ```js
                        const title = '댓글 14';
                        ```
                        """.stripIndent().trim());
                post2.addComment(memberUser2, "댓글 15");
                post2.addComment(memberUser3, "댓글 16");

                post2.addTag("DB");
                post2.addTag("JPA");

                Post post3 = postService.write(memberUser1, "제목 3", "내용 3", false, true);
                post3.addLike(memberUser1);
                post3.addLike(memberUser2);

                post3.addTag("코틀린");
                post3.addTag("코프링");

                Post post4 = postService.write(memberUser1, "제목 4", "내용 4", true, true);
                post4.addLike(memberUser1);

                post4.addTag("코틀린");
                post4.addTag("코프링");
                post4.addTag("코프링 JPA");

                Post post5 = postService.write(memberUser2, "제목 5", "내용 5", true, false);
                Post post6 = postService.write(memberUser2, "제목 6", "내용 6", false, true);

                IntStream.rangeClosed(7, 1200).forEach(i -> {
                    postService.write(memberUser3, "제목 " + i, "내용 " + i, true, true);
                });
            }
        };
    }
}
