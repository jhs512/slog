package com.ll.rsv.domain.post.postTag.entity;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.Delegate;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostTag {
    @EmbeddedId
    @Delegate
    @EqualsAndHashCode.Include
    private PostTagId id;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @Builder
    private static PostTag of(Post post, String content) {
        return new PostTag(
                PostTagId.builder()
                        .post(post)
                        .content(content)
                        .build(),
                post.getAuthor()
        );
    }
}
