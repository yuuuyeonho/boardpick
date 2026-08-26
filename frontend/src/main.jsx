import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Building2,
  Clock3,
  Dices,
  Edit3,
  Gamepad2,
  Plus,
  Save,
  Search,
  SlidersHorizontal,
  Trash2,
  Users,
  X
} from 'lucide-react';
import {
  addMyListGame,
  createGame,
  deleteGame,
  getListGames,
  getLists,
  getGames,
  pickListGame,
  pickGame,
  removeMyListGame,
  updateGame
} from './api/games';
import { getMe, login, logout, signup } from './api/members';
import AuthDialog from './components/AuthDialog';
import './styles.css';

const emptyForm = {
  name: '',
  minPlayer: 2,
  maxPlayer: 4,
  category: '',
  playTimeMinutes: 30,
  difficulty: 2
};

const emptyFilters = {
  players: '',
  category: '',
  maxPlayTime: '',
  keyword: ''
};

function App() {
  const [lists, setLists] = useState([]);
  const [selectedList, setSelectedList] = useState('');
  const [games, setGames] = useState([]);
  const [pickedGame, setPickedGame] = useState(null);
  const [filters, setFilters] = useState(emptyFilters);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [status, setStatus] = useState({ type: 'idle', message: '' });
  const [loading, setLoading] = useState(true);
  const [member, setMember] = useState(null);
  const [authMode, setAuthMode] = useState(null);
  const [authPending, setAuthPending] = useState(false);
  const [authError, setAuthError] = useState('');

  useEffect(() => {
    loadLists();
    loadGames();
    getMe().then(setMember).catch(() => setMember(null));

    const url = new URL(window.location.href);
    if (url.searchParams.get('oauthError') === 'true') {
      setStatus({ type: 'error', message: 'Google 로그인에 실패했습니다. 다시 시도해주세요.' });
      url.searchParams.delete('oauthError');
      window.history.replaceState({}, '', `${url.pathname}${url.search}${url.hash}`);
    }
  }, []);

  useEffect(() => {
    loadGames();
  }, [selectedList]);

  const list = useMemo(() => {
    return lists.find((item) => String(item.id) === selectedList);
  }, [lists, selectedList]);

  const visibleLists = useMemo(() => {
    return lists.filter((item) => item.isPublic || item.memberId === member?.id);
  }, [lists, member]);

  const ownList = useMemo(() => {
    return lists.find((item) => item.memberId === member?.id);
  }, [lists, member]);

  const isAdmin = member?.loginId === 'admin';
  const isOwnListSelected = Boolean(list && list.memberId === member?.id);

  const categories = useMemo(() => {
    return Array.from(new Set(games.map((game) => game.category).filter(Boolean))).sort();
  }, [games]);

  async function loadLists() {
    try {
      const data = await getLists();
      setLists(data);
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function loadGames(nextFilters = filters) {
    try {
      setLoading(true);
      const data = selectedList
        ? await getListGames(selectedList, nextFilters)
        : await getGames(nextFilters);
      setGames(data);
      setStatus({ type: 'idle', message: '' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    } finally {
      setLoading(false);
    }
  }

  function handleFilterChange(event) {
    const { name, value } = event.target;
    setFilters((current) => ({
      ...current,
      [name]: value
    }));
  }

  function handleFormChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]:
        name.includes('Player') || name === 'playTimeMinutes' || name === 'difficulty'
          ? Number(value)
          : value
    }));
  }

  async function handleSearch(event) {
    event.preventDefault();
    setPickedGame(null);
    await loadGames(filters);
  }

  async function handlePick() {
    try {
      const game = selectedList
        ? await pickListGame(selectedList, filters)
        : await pickGame(filters);
      setPickedGame(game);
      setStatus({ type: 'success', message: '조건에 맞는 게임을 골랐습니다.' });
    } catch (error) {
      setPickedGame(null);
      setStatus({ type: 'error', message: error.message });
    }
  }

  function resetFilters() {
    setFilters(emptyFilters);
    setPickedGame(null);
    loadGames(emptyFilters);
  }

  function startEdit(game) {
    setEditingId(game.id);
    setForm({
      name: game.name,
      minPlayer: game.minPlayer,
      maxPlayer: game.maxPlayer,
      category: game.category,
      playTimeMinutes: game.playTimeMinutes || 30,
      difficulty: game.difficulty || 2
    });
  }

  function resetForm() {
    setEditingId(null);
    setForm(emptyForm);
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!form.name.trim() || !form.category.trim()) {
      setStatus({ type: 'error', message: '게임 이름과 카테고리를 입력해주세요.' });
      return;
    }

    if (form.minPlayer > form.maxPlayer) {
      setStatus({ type: 'error', message: '최소 인원은 최대 인원보다 클 수 없습니다.' });
      return;
    }

    try {
      const saved = editingId ? await updateGame(editingId, form) : await createGame(form);
      setGames((current) => {
        if (editingId) {
          return current.map((game) => (game.id === editingId ? saved : game));
        }
        return [saved, ...current];
      });
      resetForm();
      setStatus({
        type: 'success',
        message: editingId ? 'DB 게임 정보를 수정했습니다.' : 'DB에 게임을 추가했습니다.'
      });
    } catch (error) {
      setStatus({ type: 'error', message: `${error.message} 관리자 계정으로 로그인해야 합니다.` });
    }
  }

  async function handleDeleteFromCatalog(game) {
    if (!window.confirm(`${game.name}을(를) DB에서 삭제할까요?`)) {
      return;
    }

    try {
      await deleteGame(game.id);
      setGames((current) => current.filter((item) => item.id !== game.id));
      setStatus({ type: 'success', message: 'DB에서 게임을 삭제했습니다.' });
    } catch (error) {
      setStatus({ type: 'error', message: `${error.message} 관리자 계정으로 로그인해야 합니다.` });
    }
  }

  async function handleAddToList(game) {
    if (!member) {
      openAuth('login');
      return;
    }
    try {
      await addMyListGame(game.id);
      setStatus({ type: 'success', message: `${ownList?.name || '내 게임 리스트'}에 추가했습니다.` });
    } catch (error) {
      setStatus({ type: 'error', message: `${error.message} 로그인해야 합니다.` });
    }
  }

  async function handleRemoveFromList(game) {
    if (!isOwnListSelected) {
      setStatus({ type: 'error', message: '본인의 게임 리스트만 수정할 수 있습니다.' });
      return;
    }
    if (!window.confirm(`${game.name}을(를) 내 게임 목록에서 제거할까요?`)) {
      return;
    }

    try {
      await removeMyListGame(game.id);
      setGames((current) => current.filter((item) => item.id !== game.id));
      setStatus({ type: 'success', message: '내 게임 목록에서 제거했습니다.' });
    } catch (error) {
      setStatus({ type: 'error', message: `${error.message} 로그인해야 합니다.` });
    }
  }

  function openAuth(mode) {
    setAuthError('');
    setAuthMode(mode);
  }

  async function handleAuthSubmit(credentials) {
    setAuthPending(true);
    setAuthError('');
    try {
      if (authMode === 'signup') {
        await signup(credentials);
      }
      const signedInMember = await login({
        loginId: credentials.loginId,
        password: credentials.password
      });
      setMember(signedInMember);
      await loadLists();
      setAuthMode(null);
      setStatus({
        type: 'success',
        message: authMode === 'signup' ? '회원가입하고 로그인했습니다.' : '로그인했습니다.'
      });
    } catch (error) {
      setAuthError(error.message);
    } finally {
      setAuthPending(false);
    }
  }

  async function handleLogout() {
    try {
      await logout();
      setMember(null);
      if (list && !list.isPublic) {
        setSelectedList('');
      }
      setStatus({ type: 'success', message: '로그아웃했습니다.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  return (
    <main className="app-shell">
      <section className="hero">
        <nav className="topbar" aria-label="메인 메뉴">
          <a className="brand" href="/">
            <Gamepad2 size={22} />
            <span>Boardpick</span>
          </a>
          <div className="nav-actions">
            {member ? (
              <>
                <span>{member.nickname}</span>
                <button type="button" onClick={handleLogout}>로그아웃</button>
              </>
            ) : (
              <>
                <button type="button" onClick={() => openAuth('login')}>로그인</button>
                <button type="button" onClick={() => openAuth('signup')}>회원가입</button>
              </>
            )}
          </div>
        </nav>

        <div className="hero-grid">
          <div className="hero-copy">
            <p className="eyebrow">Board Game Picker</p>
            <h1>선택이 어려울 때, 조건에 맞는 한 판을 골라드립니다.</h1>
            <p>
              게임 목록을 고르기 전에는 전체 보드게임 DB에서 찾고, 개인이나 카페의 보유 목록을
              고르면 그 안에서 조건에 맞는 게임을 추천받을 수 있습니다.
            </p>
          </div>

          <aside className="pick-panel" aria-label="추천 게임">
            <div className="panel-label">추천 결과</div>
            {pickedGame ? (
              <>
                <h2>{pickedGame.name}</h2>
                <p>{pickedGame.category}</p>
                <div className="chip-row">
                  <span className="player-chip">
                    <Users size={16} />
                    {pickedGame.minPlayer}~{pickedGame.maxPlayer}명
                  </span>
                  <span className="player-chip">
                    <Clock3 size={16} />
                    {pickedGame.playTimeMinutes || '-'}분
                  </span>
                </div>
              </>
            ) : (
              <>
                <h2>{list ? list.name : '전체 보드게임'}</h2>
                <p>조건을 입력한 뒤 추천 버튼을 누르면 한 게임을 골라드립니다.</p>
              </>
            )}
          </aside>
        </div>
      </section>

      <section className={`workspace ${isAdmin ? '' : 'without-admin'}`}>
        <aside className="editor filter-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Find & Pick</p>
              <h2>추천 조건</h2>
            </div>
            <SlidersHorizontal size={22} />
          </div>

          <label>
            게임 목록
            <select
              value={selectedList}
              onChange={(event) => setSelectedList(event.target.value)}
            >
              <option value="">전체 보드게임</option>
              {visibleLists.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.name}{item.memberId === member?.id ? ' · 내 리스트' : ''}
                </option>
              ))}
            </select>
          </label>

          <form onSubmit={handleSearch}>
            <label>
              검색어
              <input
                name="keyword"
                value={filters.keyword}
                onChange={handleFilterChange}
                placeholder="게임 이름 또는 카테고리"
              />
            </label>

            <div className="field-row">
              <label>
                인원수
                <input
                  name="players"
                  type="number"
                  min="1"
                  value={filters.players}
                  onChange={handleFilterChange}
                  placeholder="4"
                />
              </label>
              <label>
                최대 시간
                <input
                  name="maxPlayTime"
                  type="number"
                  min="1"
                  value={filters.maxPlayTime}
                  onChange={handleFilterChange}
                  placeholder="60"
                />
              </label>
            </div>

            <label>
              카테고리
              <select name="category" value={filters.category} onChange={handleFilterChange}>
                <option value="">전체</option>
                {categories.map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </label>

            <div className="button-row">
              <button className="primary-button" type="button" onClick={handlePick}>
                <Dices size={18} />
                하나 추천
              </button>
              <button className="ghost-button muted" type="submit">
                <Search size={18} />
                목록 검색
              </button>
            </div>
            <button className="text-button" type="button" onClick={resetFilters}>
              조건 초기화
            </button>
          </form>

          {status.message && <p className={`status ${status.type}`}>{status.message}</p>}
        </aside>

        <section className="library">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Game Library</p>
              <h2>{list ? `${list.name} 보유 게임` : '전체 보드게임'}</h2>
            </div>
            <span className="count-badge">{games.length}개</span>
          </div>

          <div className="game-grid">
            {loading ? (
              <div className="empty-state">게임 목록을 불러오는 중입니다.</div>
            ) : games.length === 0 ? (
              <div className="empty-state">조건에 맞는 게임이 없습니다.</div>
            ) : (
              games.map((game) => (
                <article className="game-card" key={game.id}>
                  <div>
                    <p>{game.category}</p>
                    <h3>{game.name}</h3>
                  </div>
                  <div className="meta-grid">
                    <span>
                      <Users size={15} />
                      {game.minPlayer}~{game.maxPlayer}명
                    </span>
                    <span>
                      <Clock3 size={15} />
                      {game.playTimeMinutes || '-'}분
                    </span>
                    <span>난이도 {game.difficulty || '-'}</span>
                  </div>
                  <div className="card-actions">
                    {!selectedList && (
                      <button
                        className="icon-button"
                        type="button"
                        onClick={() => handleAddToList(game)}
                        aria-label={`${game.name} 내 목록에 추가`}
                      >
                        <Building2 size={17} />
                      </button>
                    )}
                    {isOwnListSelected && (
                      <button
                        className="icon-button danger"
                        type="button"
                        onClick={() => handleRemoveFromList(game)}
                        aria-label={`${game.name} 내 목록에서 제거`}
                      >
                        <X size={17} />
                      </button>
                    )}
                    {isAdmin && !selectedList && (
                      <>
                        <button
                          className="icon-button"
                          type="button"
                          onClick={() => startEdit(game)}
                          aria-label={`${game.name} 수정`}
                        >
                          <Edit3 size={17} />
                        </button>
                        <button
                          className="icon-button danger"
                          type="button"
                          onClick={() => handleDeleteFromCatalog(game)}
                          aria-label={`${game.name} 삭제`}
                        >
                          <Trash2 size={17} />
                        </button>
                      </>
                    )}
                  </div>
                </article>
              ))
            )}
          </div>
        </section>

        {isAdmin && <form className="editor manage-panel" onSubmit={handleSubmit}>
          <div className="section-heading">
            <div>
              <p className="eyebrow">Admin</p>
              <h2>{editingId ? 'DB 게임 수정' : 'DB 게임 등록'}</h2>
            </div>
            {editingId ? (
              <button className="icon-button" type="button" onClick={resetForm} aria-label="수정 취소">
                <X size={18} />
              </button>
            ) : (
              <Plus size={22} />
            )}
          </div>

          <label>
            게임 이름
            <input name="name" value={form.name} onChange={handleFormChange} placeholder="스플렌더" />
          </label>

          <div className="field-row">
            <label>
              최소 인원
              <input
                name="minPlayer"
                type="number"
                min="1"
                value={form.minPlayer}
                onChange={handleFormChange}
              />
            </label>
            <label>
              최대 인원
              <input
                name="maxPlayer"
                type="number"
                min="1"
                value={form.maxPlayer}
                onChange={handleFormChange}
              />
            </label>
          </div>

          <div className="field-row">
            <label>
              플레이 시간
              <input
                name="playTimeMinutes"
                type="number"
                min="1"
                value={form.playTimeMinutes}
                onChange={handleFormChange}
              />
            </label>
            <label>
              난이도
              <input
                name="difficulty"
                type="number"
                min="1"
                max="5"
                value={form.difficulty}
                onChange={handleFormChange}
              />
            </label>
          </div>

          <label>
            카테고리
            <input name="category" value={form.category} onChange={handleFormChange} placeholder="전략" />
          </label>

          <button className="primary-button full" type="submit">
            <Save size={18} />
            {editingId ? '수정 저장' : '게임 등록'}
          </button>
        </form>}
      </section>
      <AuthDialog
        mode={authMode}
        pending={authPending}
        error={authError}
        onClose={() => setAuthMode(null)}
        onSubmit={handleAuthSubmit}
        onModeChange={openAuth}
      />
    </main>
  );
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
