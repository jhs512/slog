package com.ll.rsv.domain.post.post.entity;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.postComment.entity.PostComment;
import com.ll.rsv.domain.post.postLike.entity.PostLike;
import com.ll.rsv.domain.post.postTag.entity.PostTag;
import com.ll.rsv.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
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
public class Post extends BaseTime {
    @OneToMany(mappedBy = "id.post", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<PostTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "id.post", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();
    @Column(columnDefinition = "BIGINT default 0")
    @Setter(PROTECTED)
    private long likesCount;

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @OrderBy("id DESC")
    private List<PostComment> comments = new ArrayList<>();
    @Column(columnDefinition = "BIGINT default 0")
    @Setter(PROTECTED)
    private long commentsCount;

    @ManyToOne(fetch = LAZY)
    private Member author;

    private String title;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @ToString.Exclude
    private PostDetail detailBody;

    @Column(columnDefinition = "BOOLEAN default false")
    private boolean published;
    @Column(columnDefinition = "BOOLEAN default false")
    private boolean listed;

    private String addi1; // 마이그레이션을 위한 임시 칼럼, 추후에 삭제될 예정
    private String addi2; // 마이그레이션을 위한 임시 칼럼, 추후에 삭제될 예정


    public void increaseLikesCount() {
        likesCount++;
    }

    private void decreaseLikesCount() {
        likesCount--;
    }

    public void addLike(Member member) {
        likes.add(PostLike.builder()
                .post(this)
                .member(member)
                .build());

        increaseLikesCount();
    }

    public void deleteLike(Member member) {
        likes.removeIf(
                it -> it.getMember().equals(member)
        );

        decreaseLikesCount();
    }

    public boolean hasLike(Member actor) {
        return likes
                .stream()
                .anyMatch(it -> it.getMember().equals(actor));
    }


    public void increaseCommentsCount() {
        commentsCount++;
    }

    private void decreaseCommentsCount() {
        commentsCount--;
    }

    public PostComment addComment(Member author, String body) {
        return addComment(author, body, true);
    }

    public PostComment addComment(Member author, String body, boolean published) {
        PostComment postComment = PostComment.builder()
                .post(this)
                .author(author)
                .body(body)
                .published(published)
                .build();

        comments.add(postComment);

        if (published) {
            increaseCommentsCount();
        }

        return postComment;
    }

    public void deleteComment(PostComment postComment) {
        comments.remove(postComment);

        decreaseCommentsCount();
    }

    public void addTag(String content) {
        tags.add(
                PostTag.builder()
                        .post(this)
                        .content(content)
                        .build()
        );
    }

    public void deleteTag(String content) {
        tags.removeIf(it -> it.getContent().equals(content));
    }

    public boolean hasTag(String content) {
        return tags
                .stream()
                .anyMatch(it -> it.getContent().equals(content));
    }

    public List<String> getTagContents() {
        return tags
                .stream()
                .map(PostTag::getContent)
                .toList();
    }

    public void setTagContents(List<String> tagContents) {
        // tags 에서 tagContents 에 없는 것은 삭제
        tags.removeIf(it -> !tagContents.contains(it.getContent()));

        // tagContents 에서 tags에 없는 것은 추가
        tagContents
                .stream()
                .filter(it -> !hasTag(it))
                .forEach(this::addTag);
    }
}