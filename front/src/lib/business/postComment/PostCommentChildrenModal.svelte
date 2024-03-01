<script lang="ts">
  import rq from '$lib/rq/rq.svelte';
  import type { components } from '$lib/types/api/v1/schema';

  const { post, postComment, children, setPostCommentChildren, addPostCommentChild } = $props<{
    post: components['schemas']['PostWithBodyDto'];
    postComment: components['schemas']['PostCommentDto'];
    children: components['schemas']['PostCommentDto'][];
    setPostCommentChildren: (children: components['schemas']['PostCommentDto'][]) => void;
    addPostCommentChild: (child: components['schemas']['PostCommentDto']) => void;
  }>();

  const modalId = `post_comment_write_modal_${Math.random().toString(36).substring(2, 9)}`;

  export function showModal() {
    rq.showModal(modalId);
  }

  function hideModal() {
    rq.hideModal(modalId);
  }

  async function loadChildren() {
    const { data } = await rq
      .apiEndPoints()
      .GET('/api/v1/postComments/{postId}/{postCommentId}/children', {
        params: {
          path: {
            postId: post.id,
            postCommentId: postComment.id
          }
        }
      });

    setPostCommentChildren(data!.data.items);
  }

  rq.effect(() => {
    loadChildren();
  });
</script>

<dialog id={modalId} class="modal">
  <div class="modal-box max-w-5xl">
    <form method="dialog">
      <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
    </form>
    <h3 class="font-bold text-lg">
      {postComment.id}번 댓글에 대한 답글({postComment.childrenCount})
    </h3>

    <div class="my-2">
      댓글 내용 : {postComment.body}
    </div>

    <ul class="grid gap-4">
      {#each children as child (child.id)}
        <li class="card bg-base-100 shadow">
          <div class="card-body">
            <div>{child.body}</div>
            <div>{child.authorName}</div>
            <div>{child.createDate}</div>
          </div>
        </li>
      {/each}
    </ul>
  </div>
  <form method="dialog" class="modal-backdrop">
    <button>close</button>
  </form>
</dialog>
