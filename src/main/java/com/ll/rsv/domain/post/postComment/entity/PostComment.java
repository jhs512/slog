package com.ll.rsv.domain.post.postComment.entity;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.global.jpa.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Getter
@Setter
public class PostComment extends BaseTime {
    @ManyToOne(fetch = LAZY)
    private PostComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostComment> children = new ArrayList<>();
    @Setter(PROTECTED)
    private long childrenCount;

    @ManyToOne(fetch = LAZY)
    private Post post;
    @ManyToOne(fetch = LAZY)
    private Member author;
    private boolean published;
    private String body;

    public void addComment(Member author, String body) {
        addComment(author, body, true);
    }

    public PostComment addComment(Member author, String body, boolean published) {
        PostComment comment = PostComment.builder()
                .parentComment(this)
                .post(post)
                .author(author)
                .body(body)
                .published(published)
                .build();
        this.children.add(comment);

        if (published) childrenCount++;

        return comment;
    }

    public boolean isReply() {
        return parentComment != null;
    }

    public void increaseChildrenCount() {
        childrenCount++;
    }
}
