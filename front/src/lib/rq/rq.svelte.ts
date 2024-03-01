import { goto } from '$app/navigation';

import type { components, paths } from '$lib/types/api/v1/schema';
import type { Page } from '@sveltejs/kit';
import createClient from 'openapi-fetch';

import toastr from 'toastr';
import 'toastr/build/toastr.css';

toastr.options = {
  showDuration: 300,
  hideDuration: 300,
  timeOut: 3000,
  extendedTimeOut: 1000,
  positionClass: 'toast-top-right'
};

class Rq {
  public dialogStack: string[] = [];

  public showModal(id: string) {
    const modal = document.getElementById(id) as HTMLDialogElement;

    this.dialogStack.push(id);

    toastr.options.target = '#' + id;
    toastr.options.positionClass = 'toast-center';

    if (!modal.hasAttribute('data-close-listener-added')) {
      modal.addEventListener('close', () => {
        // hideModal 함수에서 modalClosed 함수를 호출한다.
        // 그런데 여기서 이렇게 close 이벤트를 추가하는 이유는 무엇일까?
        // 그것은 hideModal 함수에 의해서가 아니라 다른 방법으로 modal 이 닫힐 수도 있기 때문이다.
        this.modalClosed(id);
      });

      modal.setAttribute('data-close-listener-added', 'true');
    }

    modal.showModal();

    return modal;
  }

  public hideModal(id: string) {
    const modal = document.getElementById(id) as HTMLDialogElement;

    modal.close();

    this.modalClosed(id);
  }

  // 모달이 닫힐 때마다 수행되어야 하는 작업을 여기에 추가한다.
  // 이렇게 함수로 따로 분리한 이유는
  // 모달 close 를 했을 때 그게 즉각적으로 닫히는게 아니라 천천히 닫힌다.
  // 그래서 아래 함수를 먼저 호출 할 수 있도록 이렇게 분리를 했다.
  // 중복 수행되어도 문제없도록 멱등성있게 구현했다.
  public modalClosed(id: string) {
    // 멱등성 처리
    if (this.dialogStack[this.dialogStack.length - 1] !== id) return;

    this.dialogStack.pop();

    if (this.dialogStack.length > 0) {
      toastr.options.target = '#' + this.dialogStack[this.dialogStack.length - 1];
    } else {
      toastr.options.target = 'body';
      toastr.options.positionClass = 'toast-top-right';
    }
  }

  public member: components['schemas']['MemberDto'];

  constructor() {
    this.member = this.makeReactivityMember();
  }

  public effect(fn: () => void) {
    $effect(fn);
  }

  public isAdmin() {
    if (this.isLogout()) return false;

    return this.member.authorities.includes('ROLE_ADMIN');
  }

  public isAdmPage($page: Page<Record<string, string>>) {
    return $page.url.pathname.startsWith('/adm');
  }

  public isUsrPage($page: Page<Record<string, string>>) {
    return !this.isAdmPage($page);
  }

  // URL
  public goTo(url: string) {
    goto(url);
  }

  public replace(url: string) {
    goto(url, { replaceState: true });
  }

  public reload() {
    this.replace('/redirect?url=' + window.location.href);
  }

  // API END POINTS
  public apiEndPoints() {
    return createClient<paths>({
      baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
      credentials: 'include'
    });
  }

  public apiEndPointsWithFetch(fetch: any) {
    return createClient<paths>({
      baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
      credentials: 'include',
      fetch
    });
  }

  // MSG, REDIRECT
  public msgAndRedirect(
    data: { msg: string } | undefined,
    error: { msg: string } | undefined,
    url: string,
    callback?: () => void
  ) {
    if (data) this.msgInfo(data.msg);
    if (error) this.msgError(error.msg);

    this.replace(url);

    if (callback) window.setTimeout(callback, 100);
  }

  public msgInfo(message: string) {
    toastr.info(message, '', toastr.options);
  }

  public msgError(message: string) {
    toastr.error(message, '', toastr.options);
  }

  // 인증
  // 이렇게 member 를 만들면 좋은 점이 있다.
  // member 의 값이 바뀌면, member 를 사용하는 모든 곳에서 자동으로 즉각 반영된다.
  public makeReactivityMember() {
    let id = $state(0);
    let name = $state('');
    let profileImgUrl = $state('');
    let createDate = $state('');
    let modifyDate = $state('');
    let authorities: string[] = $state([]);
    let social = $state(false);

    return {
      get id() {
        return id;
      },
      set id(value: number) {
        id = value;
      },
      get createDate() {
        return createDate;
      },
      set createDate(value: string) {
        createDate = value;
      },
      get modifyDate() {
        return modifyDate;
      },
      set modifyDate(value: string) {
        modifyDate = value;
      },
      get name() {
        return name;
      },
      set name(value: string) {
        name = value;
      },
      get profileImgUrl() {
        return profileImgUrl;
      },
      set profileImgUrl(value: string) {
        profileImgUrl = value;
      },
      get authorities() {
        return authorities;
      },
      set authorities(value: string[]) {
        authorities = value;
      },
      get social() {
        return social;
      },
      set social(value: boolean) {
        social = value;
      }
    };
  }

  public setLogined(member: components['schemas']['MemberDto']) {
    Object.assign(this.member, member);
  }

  public setLogout() {
    this.member.id = 0;
    this.member.createDate = '';
    this.member.modifyDate = '';
    this.member.name = '';
    this.member.profileImgUrl = '';
    this.member.authorities = [];
  }

  public isLogin() {
    return this.member.id !== 0;
  }

  public isLogout() {
    return !this.isLogin();
  }

  public async initAuth() {
    const { data } = await this.apiEndPoints().GET('/api/v1/members/me');

    if (data) {
      this.setLogined(data.data.item);
    }
  }

  public async logoutAndRedirect(url: string) {
    await this.apiEndPoints().POST('/api/v1/members/logout');

    this.setLogout();
    this.replace(url);
  }

  // 글
  public async confirmAndDeletePost(
    post: components['schemas']['PostDto'],
    callback: string | Function
  ) {
    if (!window.confirm('삭제하시겠습니까?')) return;

    await this.apiEndPoints().DELETE('/api/v1/posts/{id}', {
      params: {
        path: {
          id: post.id
        }
      }
    });

    if (typeof callback === 'function') callback();
    else this.replace(callback);
  }

  public async goToTempPostEditPage() {
    const { data } = await this.apiEndPoints().POST('/api/v1/posts/temp');

    if (data) {
      this.msgInfo(data.msg);
      this.goTo(`/p/${data.data.item.id}/edit`);
    }
  }

  public async like(
    post: components['schemas']['PostDto'],
    callback: string | ((data: components['schemas']['RsDataLikeResponseBody']) => void)
  ) {
    const { data } = await this.apiEndPoints().POST('/api/v1/posts/{id}/like', {
      params: {
        path: {
          id: post.id
        }
      }
    });

    if (typeof callback === 'function') callback(data!);
    else this.replace(callback);
  }

  public async cancelLike(
    post: components['schemas']['PostDto'],
    callback: string | ((data: components['schemas']['RsDataCancelLikeResponseBody']) => void)
  ) {
    const { data } = await this.apiEndPoints().DELETE('/api/v1/posts/{id}/cancelLike', {
      params: {
        path: {
          id: post.id
        }
      }
    });

    if (typeof callback === 'function') callback(data!);
    else this.replace(callback);
  }

  public getKakaoLoginUrl() {
    return `${
      import.meta.env.VITE_CORE_API_BASE_URL
    }/member/socialLogin/kakao?redirectUrl=${encodeURIComponent(
      import.meta.env.VITE_CORE_FRONT_BASE_URL
    )}/member/socialLoginCallback?provierTypeCode=kakao`;
  }

  public goToCurrentPageWithNewParam(name: string, value: string) {
    // 현재 URL 객체 생성
    const currentUrl = new URL(window.location.href);

    // 쿼리 매개변수를 수정하기 위한 URLSearchParams 객체 생성
    const searchParams = currentUrl.searchParams;

    // 'page' 매개변수를 새 페이지 번호로 설정
    searchParams.set(name, value);

    this.goToCurrentPageWithNewQueryStr(searchParams.toString());
  }

  public goToCurrentPageWithNewQueryStr(query: string) {
    this.goTo(window.location.pathname + '?' + query);
  }
}

const rq = new Rq();

export default rq;
