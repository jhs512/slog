<script lang="ts">
  import { page } from '$app/stores';
  import rq from '$lib/rq/rq.svelte';
  import hotkeys from 'hotkeys-js';
  import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';
  import { prettyDateTime } from '$lib/utils';
  import type { components } from '$lib/types/api/v1/schema';

  const { post } = $props<{
    post: components['schemas']['PostWithBodyDto'];
  }>();
  // 본문을 표시하기 위한 에디터(뷰어) 객체
  let toastUiEditor = $state() as any;

  let Post__lastModified = post.modifyDate;

  async function Post__loadLatestBody() {
    if (import.meta.env.SSR) throw new Error('CSR ONLY');

    // 에디터 자체가 아직 로드가 안되었다면 1초 후에 재시도
    if (toastUiEditor === undefined) {
      setTimeout(Post__loadLatestBody, 1000);
      return;
    }

    // 브라우저 자체가 숨겨져있거나, 뷰어가 풀스크린이 아니라면 5초 후에 재시도
    if (document.hidden || !toastUiEditor.isFullScreen) {
      setTimeout(Post__loadLatestBody, 5000);
      return;
    }

    const postId = $page.params.id;

    const { data, error } = await rq.apiEndPoints().GET('/api/v1/posts/{id}/body', {
      params: { path: { id: parseInt(postId) }, query: { lastModifyDate: Post__lastModified } }
    });

    if (data) {
      rq.msgInfo(data.msg);

      toastUiEditor.editor.setMarkdown(data.data.body);

      Post__lastModified = data.data.modifyDate;
    }

    setTimeout(Post__loadLatestBody, 5000);
  }

  function Post__switchTab() {
    var inactiveTabs = document.querySelectorAll('.toastui-editor-tabs > .tab-item:not(.active)');
    inactiveTabs.forEach((value: Element) => {
      (value as HTMLElement).click();
    });
  }

  rq.effect(() => {
    Post__loadLatestBody();

    hotkeys.filter = function (event) {
      return true;
    };

    hotkeys('ctrl+p,cmd+p', 'postDetail', function (event, handler) {
      Post__switchTab();
      event.preventDefault();
    });

    hotkeys('ctrl+d,cmd+d', 'postDetail', function (event, handler) {
      toastUiEditor.toggleFullScreen();
      event.preventDefault();
    });

    hotkeys.setScope('postDetail');

    return () => {
      hotkeys.deleteScope('postDetail');
    };
  });
</script>

<div class="flex-grow flex justify-center items-center">
  <div class="w-full px-2 mt-4">
    <div class="card bg-base-100 shadow">
      <div class="card-body">
        <div class="detail grid grid-cols-[repeat(auto-fit,minmax(100px,1fr))] gap-3">
          <div class="form-control">
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="label">
              <span class="label-text">번호</span>
            </label>
            <div>{post.id}</div>
          </div>

          <div class="form-control">
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="label">
              <span class="label-text">작성일</span>
            </label>
            <div>{prettyDateTime(post.createDate)}</div>
          </div>

          <div class="form-control">
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="label">
              <span class="label-text">작성자</span>
            </label>
            <div>{post.authorName}</div>
          </div>

          <div class="form-control">
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="label">
              <span class="label-text">공개여부</span>
            </label>
            {#if post.published}
              <i class="fa-regular fa-eye"></i>
            {:else}
              <i class="fa-regular fa-eye-slash"></i>
            {/if}
          </div>

          <div class="form-control col-span-full">
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="label">
              <span class="label-text">제목</span>
            </label>
            <div>
              {post.title}
              <div class="tooltip tooltip-right" data-tip="Ctrl + d, Cmd + d">
                <button class="btn btn-sm" on:click={() => toastUiEditor.toggleFullScreen()}
                  >전체화면</button
                >
              </div>
            </div>
          </div>

          <div class="flex gap-2">
            {#if post.actorCanDelete}
              <button
                class="btn btn-outline"
                onclick={() => rq.confirmAndDeletePost(post, '/p/list')}>삭제</button
              >
            {/if}

            {#if post.actorCanEdit}
              <a class="btn btn-outline" href="/p/{post.id}/edit">수정</a>
            {/if}
          </div>

          <div class="form-control col-span-full">
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="label">
              <span class="label-text">내용</span>
            </label>

            {#key post.id}
              <ToastUiEditor bind:this={toastUiEditor} body={post.body} viewer={true}>
                <div slot="beforeContent" class="visible-on-fullscreen">
                  <!-- svelte-ignore a11y-click-events-have-key-events -->
                  <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                  <h1>
                    {post.title},
                    <div class="tooltip tooltip-right" data-tip="Ctrl + d, Cmd + d">
                      <button class="btn btn-sm" on:click={() => toastUiEditor.toggleFullScreen()}
                        >전체화면</button
                      >
                    </div>
                  </h1>
                </div>
              </ToastUiEditor>
            {/key}
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
