package com.ll.rsv.domain.post.postComment.repository;

import com.ll.rsv.domain.member.member.entity.Member;
import com.ll.rsv.domain.post.post.entity.Post;
import com.ll.rsv.domain.post.postComment.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Optional<PostComment> findTop1ByPostAndAuthorAndParentCommentAndPublishedAndBodyOrderByIdDesc(Post post, Member author, PostComment parentComment, boolean published, String body);

    List<PostComment> findByPostAndPublishedAndParentCommentOrderByIdDesc(Post post, boolean published, PostComment parentComment);
}
