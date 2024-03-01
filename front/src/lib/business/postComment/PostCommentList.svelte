<script lang="ts">
  import { page } from '$app/stores';
  import rq from '$lib/rq/rq.svelte';
  import type { components } from '$lib/types/api/v1/schema';
  import { prettyDateTime } from '$lib/utils';
  import PostCommentEditModal from './PostCommentEditModal.svelte';
  import PostCommentChildrenModal from './PostCommentChildrenModal.svelte';

  const {
    post,
    postComments,
    deletePostComment,
    modifyPostComment,
    increasePostCommentChildrenCount
  } = $props<{
    post: components['schemas']['PostWithBodyDto'];
    postComments: components['schemas']['PostCommentDto'][];
    deletePostComment: (postComment: components['schemas']['PostCommentDto']) => void;
    modifyPostComment: (postComment: components['schemas']['PostCommentDto']) => void;
    increasePostCommentChildrenCount(postCommentId: number): void;
  }>();

  let forModifyPostComment = $state<components['schemas']['PostCommentDto'] | undefined>();
  let forModifyPostCommentEditModal = $state() as any;

  let forReplyParentPostComment = $state<components['schemas']['PostCommentDto'] | undefined>();
  let forReplyPostComment = $state<components['schemas']['PostCommentDto'] | undefined>();
  let forReplyPostCommentEditModal = $state() as any;

  async function confirmAndDeletePostComment(postComment: components['schemas']['PostCommentDto']) {
    if (!confirm('삭제하시겠습니까?')) return;

    const { data, error } = await rq
      .apiEndPoints()
      .DELETE('/api/v1/postComments/{postId}/{postCommentId}', {
        params: {
          path: { postId: parseInt($page.params.id), postCommentId: postComment.id }
        }
      });

    if (error) throw error;

    deletePostComment(postComment);

    rq.msgInfo(data.msg);
  }

  function showEditModal(postComment: components['schemas']['PostCommentDto']) {
    forModifyPostComment = postComment;

    forModifyPostCommentEditModal.showModal();
  }

  async function saveReplyPostComment(
    post: components['schemas']['PostWithBodyDto'],
    postComment: components['schemas']['PostCommentDto'],
    body: string
  ) {
    const { data } = await rq.apiEndPoints().PUT('/api/v1/postComments/{postId}/{postCommentId}', {
      params: {
        path: {
          postId: post.id,
          postCommentId: postComment.id
        }
      },
      body: {
        body
      }
    });

    increasePostCommentChildrenCount(data!.data.item.parentCommentId);
    addPostCommentChild(data!.data.item);

    return data!.msg;
  }

  async function savePostComment(
    post: components['schemas']['PostWithBodyDto'],
    postComment: components['schemas']['PostCommentDto'],
    body: string
  ) {
    const { data } = await rq.apiEndPoints().PUT('/api/v1/postComments/{postId}/{postCommentId}', {
      params: {
        path: {
          postId: post.id,
          postCommentId: postComment.id
        }
      },
      body: {
        body
      }
    });

    modifyPostComment(data!.data.item);

    return data!.msg;
  }

  async function loadTempPostComment(parentComment: components['schemas']['PostCommentDto']) {
    const { data: tempRsData } = await rq
      .apiEndPoints()
      .POST('/api/v1/postComments/{postId}/{postCommentId}/temp', {
        params: {
          path: {
            postId: post.id,
            postCommentId: parentComment!.id
          }
        }
      });

    forReplyParentPostComment = parentComment;
    forReplyPostComment = tempRsData!.data.item;
    forReplyPostComment.body = '';
  }

  async function startReply(parentComment: components['schemas']['PostCommentDto']) {
    await loadTempPostComment(parentComment);

    forReplyPostCommentEditModal.showModal();
  }

  let postCommentChildrenModal = $state() as any;
  let forChildrenPostComment = $state<components['schemas']['PostCommentDto'] | undefined>();
  let postCommentChildren = $state<components['schemas']['PostCommentDto'][]>([]);

  function addPostCommentChild(child: components['schemas']['PostCommentDto']) {
    postCommentChildren.unshift(child);
  }

  function setPostCommentChildren(children: components['schemas']['PostCommentDto'][]) {
    postCommentChildren = children;
  }

  async function showChildren(parentComment: components['schemas']['PostCommentDto']) {
    forChildrenPostComment = parentComment;

    setTimeout(() => {
      postCommentChildrenModal.showModal();
    });
  }
</script>

<h2>댓글 목록</h2>

<ul class="grid gap-4">
  {#each postComments as postComment (postComment.id)}
    <li class="card bg-base-100 shadow">
      <div class="card-body">
        <div>번호 : {postComment.id}</div>
        <div>작성 : {prettyDateTime(postComment.createDate)}</div>
        <div>수정 : {prettyDateTime(postComment.modifyDate)}</div>
        <div>작성자 : {postComment.authorName}</div>
        <div>
          <img src={postComment.authorProfileImgUrl} width="30" class="rounded-full" alt="" />
        </div>
        <div>
          {postComment.body}
        </div>
        <div>
          <div>
            {#if postComment.actorCanDelete}
              <button
                class="btn btn-outline"
                onclick={() => confirmAndDeletePostComment(postComment)}>삭제</button
              >
            {/if}

            {#if postComment.actorCanEdit}
              <button class="btn btn-outline" onclick={() => showEditModal(postComment)}
                >수정</button
              >
            {/if}

            {#if postComment.actorCanReply}
              <button class="btn btn-outline" onclick={() => startReply(postComment)}>답글</button>
            {/if}

            {#if postComment.childrenCount > 0}
              <div>
                <button class="btn" onclick={() => showChildren(postComment)}>
                  총 {postComment.childrenCount}개의 답글
                </button>
              </div>
            {/if}
          </div>
        </div>
      </div>
    </li>
  {/each}
</ul>

{#if forModifyPostComment}
  <PostCommentEditModal
    bind:this={forModifyPostCommentEditModal}
    {post}
    postComment={forModifyPostComment}
    title={`글 "${post.title}" 에 대한 댓글 수정`}
    save={savePostComment}
    submitBtnText="댓글 수정"
  />
{/if}

{#if forReplyPostComment && forReplyParentPostComment}
  <PostCommentEditModal
    bind:this={forReplyPostCommentEditModal}
    {post}
    postComment={forReplyPostComment}
    title={`댓글 "${forReplyParentPostComment.body}" 에 대한 답글`}
    save={saveReplyPostComment}
    submitBtnText="답글 작성"
  />
{/if}

{#if forChildrenPostComment}
  <PostCommentChildrenModal
    bind:this={postCommentChildrenModal}
    {post}
    postComment={forChildrenPostComment}
    children={postCommentChildren}
    {setPostCommentChildren}
    {addPostCommentChild}
  />
{/if}
