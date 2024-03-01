<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';

  onMount(() => {
    const id = $page.params.id;
    const query = window.location.search; // 쿼리 문자열을 가져옵니다.
    const hash = window.location.hash; // 해시를 가져옵니다.

    goto(`/p/${id}${query}${hash}`);
  });
</script>
