package com.ll.rsv.domain.post.postTag.entity;

import com.ll.rsv.domain.post.post.entity.Post;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Getter
public class PostTagId implements Serializable {
    @ManyToOne(fetch = LAZY)
    private Post post;
    private String content;
}
