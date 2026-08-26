import React, { useEffect, useState } from 'react';
import { LogIn, UserPlus, X } from 'lucide-react';

const initialForm = { loginId: '', nickname: '', password: '' };

export default function AuthDialog({ mode, pending, error, onClose, onSubmit, onModeChange }) {
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    setForm(initialForm);
  }, [mode]);

  if (!mode) return null;

  const isSignup = mode === 'signup';

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit(form);
  }

  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
      <section
        className="auth-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="auth-title"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <button className="dialog-close" type="button" onClick={onClose} aria-label="닫기">
          <X size={20} />
        </button>
        <p className="eyebrow">Boardpick Account</p>
        <h2 id="auth-title">{isSignup ? '회원가입' : '로그인'}</h2>
        <p className="dialog-copy">
          {isSignup ? '내 보드게임 리스트를 만들고 관리해보세요.' : '내 게임 리스트를 이어서 관리하세요.'}
        </p>

        <form onSubmit={handleSubmit}>
          <label>
            로그인 ID
            <input
              autoFocus
              name="loginId"
              minLength="3"
              maxLength="20"
              required
              value={form.loginId}
              onChange={handleChange}
              autoComplete="username"
            />
          </label>
          {isSignup && (
            <label>
              닉네임
              <input
                name="nickname"
                maxLength="30"
                required
                value={form.nickname}
                onChange={handleChange}
                autoComplete="nickname"
              />
            </label>
          )}
          <label>
            비밀번호
            <input
              name="password"
              type="password"
              minLength={isSignup ? 8 : undefined}
              maxLength="100"
              required
              value={form.password}
              onChange={handleChange}
              autoComplete={isSignup ? 'new-password' : 'current-password'}
            />
          </label>
          {error && <p className="status error">{error}</p>}
          <button className="primary-button full" type="submit" disabled={pending}>
            {isSignup ? <UserPlus size={18} /> : <LogIn size={18} />}
            {pending ? '처리 중...' : isSignup ? '가입하기' : '로그인'}
          </button>
        </form>

        <button
          className="auth-switch"
          type="button"
          onClick={() => onModeChange(isSignup ? 'login' : 'signup')}
        >
          {isSignup ? '이미 계정이 있나요? 로그인' : '처음이신가요? 회원가입'}
        </button>
      </section>
    </div>
  );
}
