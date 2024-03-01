<script lang="ts">
  import { page } from '$app/stores';

  import rq from '$lib/rq/rq.svelte';
  import { prettyDateTime } from '$lib/utils';
  import type { components } from '$lib/types/api/v1/schema';
  import Pagination from '$lib/components/Pagination.svelte';
  import type { KwTypeV1 } from '$lib/types';

  let posts: components['schemas']['PostDto'][] = $state([]);

  async function load() {
    if (import.meta.env.SSR) throw new Error('CSR ONLY');

    const kw = $page.url.searchParams.get('kw') ?? '';
    const kwType = ($page.url.searchParams.get('kwType') ?? 'ALL') as KwTypeV1;
    const page_ = parseInt($page.url.searchParams.get('page') ?? '1');

    const { data } = await rq.apiEndPoints().GET('/api/v1/posts/mine', {
      params: {
        query: {
          kw,
          kwType,
          page: page_
        }
      }
    });

    posts = data!.data.itemPage.content;

    return data!;
  }
</script>

<div class="flex-grow flex justify-center">
  <div class="w-full max-w-screen-2xl px-2 my-4">
    <div class="text-center">
      <div class="font-bold text-lg">내 글</div>
      <div class="mt-3 text-gray-400">본인이 작성한 글을 보여줍니다.</div>
    </div>

    <div class="divider"></div>

    {#await load()}
      <p>loading...</p>
    {:then { data: { itemPage } }}
      <div class="flex flex-wrap gap-2 items-center mt-4">
        <div class="badge badge-outline">
          검색결과 : {itemPage.totalElementsCount}건
        </div>

        <div class="flex-grow"></div>

        <div class="flex gap-2">
          <button
            class="btn btn-primary"
            onclick={() => {
        const modal = document.querySelector('#search_modal_1') as HTMLDialogElement;
        modal.showModal();

        const inputKw = modal.querySelector('input[name="kw"]') as HTMLInputElement;
        inputKw.focus();
      }}
          >
            <i class="fa-solid fa-magnifying-glass"></i> 검색
            {#if $page.url.searchParams.get('kw')}
              <span class="text-sm text-gray-400"
                >(검색어 : {$page.url.searchParams.get('kw')})</span
              >
            {/if}
          </button>

          {#if $page.url.searchParams.get('kw')}
            <a class="btn" href={$page.url.pathname}>
              <i class="fa-solid fa-xmark"></i> 검색어 지우기
            </a>
          {/if}
        </div>
      </div>

      <dialog id="search_modal_1" class="modal">
        <div class="modal-box">
          <h3 class="font-bold text-lg">검색</h3>
          <form
            action={$page.url.pathname}
            class="bg-base rounded grid grid-cols-1 gap-4"
            onsubmit={() => {
          const modal = document.querySelector('#search_modal_1') as HTMLDialogElement;
          modal.close();
        }}
          >
            <div class="form-control">
              <!-- svelte-ignore a11y-label-has-associated-control -->
              <label class="label">
                <span class="label-text">검색필터</span>
              </label>

              <select
                name="kwType"
                class="select select-bordered"
                value={$page.url.searchParams.get('kwType') ?? 'ALL'}
              >
                <option value="ALL">전체</option>
                <option value="TITLE">제목</option>
                <option value="BODY">내용</option>
                <option value="NAME">작성자</option>
              </select>
            </div>

            <div class="form-control">
              <!-- svelte-ignore a11y-label-has-associated-control -->
              <label class="label">
                <span class="label-text">검색어</span>
              </label>

              <input
                placeholder="검색어"
                class="input input-bordered"
                name="kw"
                type="search"
                value={$page.url.searchParams.get('kw') ?? ''}
                autocomplete="off"
              />
            </div>

            <div>
              <button class="btn btn-block btn-primary gap-1">
                <i class="fa-solid fa-magnifying-glass"></i>
                <span>검색</span>
              </button>
            </div>
          </form>
        </div>
        <form method="dialog" class="modal-backdrop">
          <button>close</button>
        </form>
      </dialog>

      <div class="mt-4">
        <Pagination page={itemPage} />
      </div>

      <ul class="mt-4 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {#each posts as post}
          <li>
            <div class="card bg-base-100 shadow">
              <div class="card-body">
                <div class="detail grid grid-cols-[repeat(auto-fit,minmax(100px,1fr))] gap-5">
                  <a
                    href="/p/{post.id}"
                    class="col-span-full bg-gray-700 rounded p-3 text-gray-100 flex items-center justify-center flex-wrap aspect-video"
                  >
                    <div class="flex flex-wrap gap-1">
                      <div class="flex gap-1">
                        <span>🗒️</span>
                        <span>{post.title}</span>
                      </div>
                      <div class="flex-grow text-right text-gray-300 italic">
                        <span>by</span>
                        <span>{post.authorName}</span>
                      </div>
                    </div>
                  </a>

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
                      <span class="label-text">공개</span>
                    </label>
                    <div>
                      {#if post.published}
                        <i class="fa-regular fa-eye"></i>
                      {:else}
                        <i class="fa-regular fa-eye-slash"></i>
                      {/if}
                    </div>
                  </div>
                </div>

                <div class="card-actions justify-end mt-5">
                  {#if post.actorCanDelete}
                    <button
                      class="btn btn-outline"
                      onclick={() =>
                        rq.confirmAndDeletePost(post, () => {
                          posts.splice(posts.indexOf(post), 1);
                        })}><i class="fa-solid fa-trash"></i> 삭제</button
                    >
                  {/if}

                  {#if post.actorCanEdit}
                    <a class="btn btn-outline" href="/p/{post.id}/edit"
                      ><i class="fa-solid fa-pen-to-square"></i> 수정</a
                    >
                  {/if}

                  {#if post.actorCanLike}
                    <button
                      class="btn btn-outline"
                      onclick={() =>
                        rq.like(post, (data) => {
                          Object.assign(post, data.data.item);
                          rq.msgInfo(data.msg);
                        })}><i class="fa-regular fa-heart"></i></button
                    >
                  {/if}

                  {#if post.actorCanCancelLike}
                    <button
                      class="btn btn-outline"
                      onclick={() =>
                        rq.cancelLike(post, (data) => {
                          Object.assign(post, data.data.item);
                          rq.msgInfo(data.msg);
                        })}><i class="fa-solid fa-heart text-red-400"></i></button
                    >
                  {/if}

                  <a href="/p/{post.id}" class="btn btn-primary"
                    ><i class="fa-solid fa-book-open-reader"></i> 글 보기</a
                  >
                </div>
              </div>
            </div>
          </li>
        {/each}
      </ul>

      <div class="mt-4 mb-4">
        <Pagination page={itemPage} />
      </div>
    {/await}
  </div>
</div>
