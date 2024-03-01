<script lang="ts">
  import PostDetail from '$lib/business/post/PostDetail.svelte';
  import PostCommentWriteAndList from '$lib/business/postComment/PostCommentWriteAndList.svelte';
  import type { components } from '$lib/types/api/v1/schema';

  const { data } = $props<{ data: { post: components['schemas']['PostWithBodyDto'] } }>();
  const { post } = data;
</script>

<svelte:head>
  <title>{post.title}</title>
  <meta name="description" content={post.body} />
</svelte:head>

<div class="flex-grow flex justify-center items-center">
  <div class="w-full px-2 mt-4">
    <PostDetail {post} />
    <PostCommentWriteAndList {post} />
  </div>
</div>
