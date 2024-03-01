<script lang="ts">
  import rq from '$lib/rq/rq.svelte';
  import type { components } from '$lib/types/api/v1/schema';

  const modalId = `post_comment_write_modal_${Math.random().toString(36).substring(2, 9)}`;

  const { post, postComment, title, save, submitBtnText } = $props<{
    save: (
      post: components['schemas']['PostWithBodyDto'],
      postComment: components['schemas']['PostCommentDto'],
      body: string
    ) => Promise<string>;
    title: string;
    post: components['schemas']['PostWithBodyDto'];
    postComment: components['schemas']['PostCommentDto'];
    submitBtnText: string;
  }>();

  export function showModal() {
    const modal = rq.showModal(modalId);

    const inputBody = modal.querySelector('form input[type=text]') as HTMLInputElement;

    inputBody.focus();
  }

  function hideModal() {
    rq.hideModal(modalId);
  }

  async function submitWriteForm(this: HTMLFormElement) {
    const form = this;

    form.body.value = form.body.value.trim();

    if (form.body.value.length == 0) {
      rq.msgError('댓글 내용을 입력해주세요.');
      form.body.focus();

      return;
    }

    const msg = await save(post, postComment, form.body.value);

    form.body.value = '';

    hideModal();

    rq.msgInfo(msg);
  }
</script>

<dialog id={modalId} class="modal">
  <div class="modal-box">
    <form method="dialog">
      <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
    </form>
    <h3 class="font-bold text-lg">{title}</h3>

    <form class="grid gap-4" on:submit|preventDefault={submitWriteForm}>
      <label class="form-control">
        <div class="label">
          <span class="label-text">댓글 내용</span>
        </div>
        <input
          type="text"
          name="body"
          placeholder="댓글을 입력해주세요."
          class="input input-bordered w-full"
          value={postComment.body}
        />
      </label>

      <div class="grid grid-cols-2 gap-4">
        <button class="btn btn-outline" type="button" onclick={hideModal}>취소</button>
        <button class="btn btn-primary" type="submit">{submitBtnText}</button>
      </div>
    </form>
  </div>
  <form method="dialog" class="modal-backdrop">
    <button>close</button>
  </form>
</dialog>
