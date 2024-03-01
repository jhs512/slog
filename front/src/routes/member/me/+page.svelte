<script lang="ts">
  import rq from '$lib/rq/rq.svelte';
  import { prettyDate } from '$lib/utils';
</script>

<div class="flex-grow flex justify-center items-center">
  <div class="w-full max-w-sm px-2">
    <div class="text-center">
      <div class="font-bold text-lg">내 정보</div>
      <div class="mt-3 text-gray-400">현재 로그인한 회원의 정보입니다.</div>
    </div>

    <div class="divider"></div>

    <div class="grid grid-colos-1 gap-4">
      <div class="flex justify-center">
        <div>
          <img src={rq.member.profileImgUrl} width="100" class="rounded-full" alt="" />
        </div>
      </div>

      <div>
        <div class="font-bold">번호</div>
        <div class="text-gray-500 mt-1">{rq.member.id}</div>
      </div>

      <div>
        <div class="font-bold">가입</div>
        <div class="text-gray-500 mt-1">{prettyDate(rq.member.createDate)}</div>
      </div>

      <div>
        <div class="font-bold">이름</div>
        <div class="text-gray-500 mt-1">{rq.member.name}</div>
      </div>
    </div>
  </div>
</div>
