<script lang="ts">
  import type { components } from '$lib/types/api/v1/schema';

  import rq from '$lib/rq/rq.svelte';

  import PostCommentWrite from '$lib/business/postComment/PostCommentWrite.svelte';
  import PostCommentList from '$lib/business/postComment/PostCommentList.svelte';

  const { post: _post } = $props<{
    post: components['schemas']['PostWithBodyDto'];
  }>();
  const post = $state(_post);
  let postComments = $state<components['schemas']['PostCommentDto'][]>([]);

  function addPostComment(postComment: components['schemas']['PostCommentDto']) {
    postComments.unshift(postComment);
    post.commentsCount++;
  }

  function deletePostComment(postComment: components['schemas']['PostCommentDto']) {
    postComments.splice(postComments.indexOf(postComment), 1);
    post.commentsCount--;
  }

  function modifyPostComment(postComment: components['schemas']['PostCommentDto']) {
    const idx = postComments.findIndex((v) => v.id == postComment.id);

    postComments[idx] = postComment;
  }

  function increasePostCommentChildrenCount(postCommentId: number) {
    const idx = postComments.findIndex((v) => v.id == postCommentId);

    postComments[idx].childrenCount++;
  }

  async function loadPostComments() {
    if (import.meta.env.SSR) throw new Error('CSR ONLY');

    const { data } = await rq.apiEndPoints().GET('/api/v1/postComments/{postId}', {
      params: {
        path: {
          postId: post.id
        }
      }
    });

    postComments = data!.data.items;
  }
</script>

<h1>댓글({post.commentsCount})</h1>

<div><span class="text-gray-400">{post.title}</span> 에 대한 댓글</div>

{#await loadPostComments()}
  loading...
{:then}
  <PostCommentWrite {post} {addPostComment} />
  <PostCommentList
    {post}
    {postComments}
    {deletePostComment}
    {modifyPostComment}
    {increasePostCommentChildrenCount}
  />
{:catch error}
  {error.msg}
{/await}
