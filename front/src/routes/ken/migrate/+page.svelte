<script lang="ts">
  import rq from '$lib/rq/rq.svelte';

  async function submitLoginForm(this: HTMLFormElement) {
    const form: HTMLFormElement = this;

    form.username.value = form.username.value.trim();
    form.password.value = form.password.value.trim();

    const { data, error } = await rq.apiEndPoints().POST('/api/v1/wikenMigrate/migrate', {
      body: {
        username: form.username.value,
        password: form.password.value
      }
    });

    if (error) rq.msgError(error.msg);
    else {
      rq.msgAndRedirect(data, undefined, '/');
    }
  }
</script>

<div class="flex-grow flex justify-center items-center">
  <div class="w-full max-w-sm px-2 py-6">
    <div class="text-center">
      <div class="font-bold text-lg">기존 글 복구</div>
      <div class="mt-3 text-gray-400">
        만약에 기존에 카카오 로그인을 사용하셨다면, 기존 아이디와 기존 비밀번호를 입력하지 않아도
        됩니다.
      </div>
    </div>

    <div class="divider"></div>

    <form on:submit|preventDefault={submitLoginForm} class="grid grid-cols-1 gap-4">
      <label class="form-control">
        <div class="label">
          <span class="label-text">기존 아이디</span>
        </div>
        <input
          type="text"
          name="username"
          class="input input-bordered w-full"
          placeholder="(선택입력)기존 아이디"
          autocomplete="off"
        />

        <div class="label">
          <span class="label-text-alt">
            이전 사이트(scode, wiken)에서 사용하시던 계정이 있다면, 그것의 아이디를 입력해주세요.
          </span>
        </div>
      </label>

      <label class="form-control">
        <div class="label">
          <span class="label-text">기존 비밀번호</span>
        </div>
        <input
          type="password"
          name="password"
          class="input input-bordered w-full"
          placeholder="(선택입력)기존 비밀번호"
        />

        <div class="label">
          <span class="label-text-alt">
            이전 사이트(scode, wiken)에서 사용하시던 계정이 있다면, 그것의 비밀번호를 입력해주세요.
          </span>
        </div>
      </label>

      <div>
        <button type="submit" class="btn btn-primary w-full"
          ><i class="fa-solid fa-cloud-arrow-down"></i> 기존 글 복구
        </button>
      </div>
    </form>
  </div>
</div>
