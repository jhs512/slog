package com.ll.rsv.domain.post.postLike.entity;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.Delegate;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostLike {
    @EmbeddedId
    @Delegate
    @EqualsAndHashCode.Include
    private PostLikeId id;

    @Builder
    private static PostLike of(Post post, Member member) {
        return new PostLike(
                PostLikeId.builder()
                        .post(post)
                        .member(member)
                        .build()
        );
    }
}
