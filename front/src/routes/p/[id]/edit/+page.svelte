<script lang="ts">
  import { page } from '$app/stores';
  import rq from '$lib/rq/rq.svelte';
  import hotkeys from 'hotkeys-js';
  import ToastUiEditor from '$lib/components/ToastUiEditor.svelte';
  import { onMount } from 'svelte';
  import type { components } from '$lib/types/api/v1/schema';

  function Post__switchTab() {
    var inactiveTabs = document.querySelectorAll('.toastui-editor-tabs > .tab-item:not(.active)');
    inactiveTabs.forEach((value: Element) => {
      (value as HTMLElement).click();
    });
  }

  rq.effect(() => {
    rq.initAuth();

    hotkeys.filter = function (event) {
      return true;
    };

    hotkeys('ctrl+p,cmd+p', 'postEdit', function (event, handler) {
      Post__switchTab();
      event.preventDefault();
    });

    hotkeys.setScope('postEdit');

    return () => {
      hotkeys.deleteScope('postEdit');
    };
  });

  let toastUiEditor: any | undefined;
  let oldBody: string = '';

  function saveToLocalStorage(id: number, body: string) {
    const key = 'posts_' + id;
    // LocalStorage에서 기존 데이터를 가져옵니다.
    const existingData = localStorage.getItem(key);

    // 기존 데이터가 있으면 JSON으로 파싱하고, 없으면 빈 배열을 사용합니다.
    const posts = existingData ? JSON.parse(existingData) : [];

    // 새 데이터를 배열에 추가합니다.
    posts.push({
      id,
      body: body,
      date: new Date().toISOString()
    });

    // 배열의 크기가 50을 초과하면 가장 오래된 항목(첫 번째 항목)을 제거합니다.
    if (posts.length > 50) {
      posts.shift(); // 배열의 첫 번째 항목을 제거합니다.
    }

    // 변경된 배열을 JSON 문자열로 변환하여 LocalStorage에 저장합니다.
    localStorage.setItem(key, JSON.stringify(posts));
  }

  async function Post__saveBody() {
    const newBody = toastUiEditor.editor.getMarkdown().trim();

    if (oldBody === newBody) {
      return;
    }

    const { data, error } = await rq.apiEndPoints().PUT('/api/v1/posts/{id}/body', {
      params: { path: { id: parseInt($page.params.id) } },
      body: { body: newBody }
    });

    oldBody = newBody;

    saveToLocalStorage(parseInt($page.params.id), newBody);

    if (data) {
      rq.msgInfo('본문이 저장되었습니다.');
    }
  }

  async function load() {
    if (import.meta.env.SSR) throw new Error('CSR ONLY');

    const { data, error } = await rq
      .apiEndPoints()
      .GET('/api/v1/posts/{id}', { params: { path: { id: parseInt($page.params.id) } } });

    if (error) throw error;

    return data!;
  }

  let genFileMap = $state<Record<string, Record<number, components['schemas']['GenFileDto']>>>({});

  async function loadFiles() {
    if (import.meta.env.SSR) throw new Error('CSR ONLY');

    const { data, error } = await rq
      .apiEndPoints()
      .GET('/api/v1/posts/{id}/files', { params: { path: { id: parseInt($page.params.id) } } });

    if (error) throw error;

    genFileMap = data!.data.items.reduce((acc, cur) => {
      // '이름__나이' 형태의 키 생성
      const key = `${cur.typeCode}__${cur.type2Code}`;

      // 해당 키가 이미 존재하면 배열에 현재 요소 추가, 그렇지 않으면 새 배열로 시작
      if (!acc[key]) {
        acc[key] = {};
      }

      acc[key][cur.fileNo] = cur;

      return acc;
    }, {} as any);

    return data!;
  }

  async function submitEditForm(this: HTMLFormElement) {
    const form: HTMLFormElement = this;

    const titleInput = form.elements.namedItem('title') as HTMLInputElement;

    if (titleInput.value.length === 0) {
      rq.msgError('제목을 입력해주세요.');
      titleInput.focus();
      return;
    }

    toastUiEditor.editor.setMarkdown(toastUiEditor.editor.getMarkdown().trim());

    if (toastUiEditor.editor.getMarkdown().trim().length === 0) {
      rq.msgError('내용을 입력해주세요.');
      toastUiEditor.editor.focus();
      return;
    }

    for (let i = 1; true; i++) {
      if (!form[`video__${i}`]) break;

      if (form[`video__${i}`].files.length > 0) {
        const formData = new FormData();
        formData.append('file', form[`video__${i}`].files[0]);

        rq.msgInfo(`영상 ${i}(을)를 업로드 중입니다. 잠시만 기다려주세요.`);

        await rq.apiEndPoints().PUT('/api/v1/posts/{id}/mainVideo/{fileNo}', {
          params: { path: { id: parseInt($page.params.id), fileNo: i } },
          body: formData as any,
          bodySerializer: (body) => body
        });
      } else if (form[`video__${i}__delete`]?.checked) {
        rq.msgInfo(`영상 ${i}(을)를 삭제 중 입니다. 잠시만 기다려주세요.`);

        await rq.apiEndPoints().DELETE('/api/v1/posts/{id}/mainVideo/{fileNo}', {
          params: { path: { id: parseInt($page.params.id), fileNo: i } }
        });
      }
    }

    const { data, error } = await rq.apiEndPoints().PUT('/api/v1/posts/{id}', {
      params: { path: { id: parseInt($page.params.id) } },
      body: {
        title: titleInput.value,
        tagContents: form.tagContents.value
          .split('#')
          .map((tagContent: string) => tagContent.trim())
          .filter((tag: string) => tag.length > 0),
        body: toastUiEditor.editor.getMarkdown().trim(),
        published: form.published.checked,
        listed: form.listed.checked
      }
    });

    if (oldBody !== toastUiEditor.editor.getMarkdown().trim()) {
      saveToLocalStorage(parseInt($page.params.id), toastUiEditor.editor.getMarkdown().trim());
    }

    rq.msgAndRedirect(data, error, '/p/' + $page.params.id);
  }

  onMount(() => {
    loadFiles();
  });
</script>

<div class="flex-grow flex justify-center">
  <div class="w-full max-w-screen-2xl px-2 my-4">
    <div class="text-center">
      <div class="font-bold text-lg">글 편집</div>
      <div class="mt-3 text-gray-400">
        글의 제목, 내용, 공개 여부, 리스트 여부, 태그를 수정할 수 있습니다. 글 본문만 수정하고
        싶다면, 에디터의 저장 버튼을 눌러주세요.
      </div>
    </div>

    <div class="divider"></div>

    {#await load()}
      <div>loading...</div>
    {:then { data: { item: post } }}
      <form on:submit|preventDefault={submitEditForm} class="grid grid-cols-1 gap-4">
        <!-- svelte-ignore a11y-label-has-associated-control -->
        <label class="form-control">
          <div class="label">
            <span class="label-text">번호</span>
          </div>
          <div class="badge">{post.id}</div>
        </label>

        <!-- svelte-ignore a11y-label-has-associated-control -->
        <label class="form-control">
          <div class="label">
            <span class="label-text">추천</span>
          </div>
          <div class="badge">{post.likesCount.toLocaleString()}</div>
        </label>

        <label class="form-control">
          <div class="label">
            <span class="label-text">공개</span>
          </div>
          <input
            class="toggle"
            type="checkbox"
            name="published"
            value={true}
            checked={post.published}
          />
        </label>

        <label class="form-control">
          <div class="label">
            <span class="label-text">글 목록에서 공개</span>
          </div>
          <input class="toggle" type="checkbox" name="listed" value={true} checked={post.listed} />
        </label>

        <label class="form-control">
          <div class="label">
            <span class="label-text">제목</span>
          </div>
          <input
            class="input input-bordered"
            type="text"
            name="title"
            value={post.title}
            maxlength="150"
          />
        </label>

        <label class="form-control">
          <div class="label">
            <span class="label-text">태그</span>
          </div>
          <input
            class="input input-bordered"
            type="text"
            name="tagContents"
            value={post.tagContents.map((tag) => `#${tag}`).join(' ')}
            maxlength="50"
          />
          <div class="label">
            <span class="label-text-alt"> 구분자는 `#` 입니다. </span>
          </div>
        </label>

        <div class="form-control">
          <div class="label">
            <span class="label-text">내용</span>
          </div>
          <div>
            {#key post.id}
              <ToastUiEditor bind:this={toastUiEditor} body={post.body} saveBody={Post__saveBody} />
            {/key}
          </div>
          <div class="label">
            <span class="label-text-alt">
              에디터의 F 키를 누르면 에디터 풀스크린 모드가 토글 됩니다. 해당 단축키는 Ctrl(Cmd) + Q
              입니다. 기타 다른 단축키는 메인 화면의 공지사항을 참고해주세요.
            </span>
          </div>
        </div>

        {#each [1, 2] as videoIndex}
          {#if genFileMap['common__mainVideo'] && genFileMap['common__mainVideo'][videoIndex]}
            <div class="form-control">
              <div class="label">
                <span class="label-text">기존 영상 {videoIndex} 삭제</span>
              </div>
              <div>
                <input class="toggle" type="checkbox" name={`video__${videoIndex}__delete`} />
              </div>

              <div>
                <a
                  href={import.meta.env.VITE_CORE_API_BASE_URL +
                    genFileMap['common__mainVideo'][videoIndex].downloadUrl}
                  class="link"
                >
                  ({(genFileMap['common__mainVideo'][videoIndex].fileSize / 1024 / 1024).toFixed(
                    2
                  )}MB) {genFileMap['common__mainVideo'][videoIndex].originFileName}
                </a>
              </div>
            </div>
          {/if}

          <div class="form-control">
            <div class="label">
              <span class="label-text">영상 {videoIndex}</span>
            </div>
            <div>
              <input
                class="file-input file-input-bordered"
                type="file"
                name={`video__${videoIndex}`}
              />
            </div>
            <div class="label">
              <span class="label-text-alt">
                mp4 파일만 업로드 가능합니다. 파일 업로드는 5MB 이하만 가능합니다.
              </span>
            </div>
          </div>
        {/each}

        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <button
            class="btn btn-outline"
            type="button"
            onclick={() => window.confirm('편집을 마치시겠습니까?') && history.back()}>취소</button
          >
          <button class="btn btn-primary" type="submit">
            <i class="fa-solid fa-pen-to-square"></i> 저장
          </button>
        </div>
      </form>
    {:catch error}
      {error.msg}
    {/await}
  </div>
</div>
