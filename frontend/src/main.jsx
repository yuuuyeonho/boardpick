import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Dices,
  Edit3,
  Gamepad2,
  Plus,
  RefreshCw,
  Save,
  Search,
  Trash2,
  Users,
  X
} from 'lucide-react';
import { createGame, deleteGame, getGames, pickGame, updateGame } from './api/games';
import './styles.css';

const emptyForm = {
  name: '',
  minPlayer: 2,
  maxPlayer: 4,
  category: ''
};

function App() {
  const [games, setGames] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [pickedGame, setPickedGame] = useState(null);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState({ type: 'idle', message: '' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadGames();
  }, []);

  const filteredGames = useMemo(() => {
    const keyword = query.trim().toLowerCase();
    if (!keyword) {
      return games;
    }

    return games.filter((game) =>
      [game.name, game.category].some((value) => value?.toLowerCase().includes(keyword))
    );
  }, [games, query]);

  const categories = useMemo(() => {
    return Array.from(new Set(games.map((game) => game.category).filter(Boolean))).slice(0, 6);
  }, [games]);

  async function loadGames() {
    try {
      setLoading(true);
      const data = await getGames();
      setGames(data);
      setStatus({ type: 'idle', message: '' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    } finally {
      setLoading(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]: name.includes('Player') ? Number(value) : value
    }));
  }

  function startEdit(game) {
    setEditingId(game.id);
    setForm({
      name: game.name,
      minPlayer: game.minPlayer,
      maxPlayer: game.maxPlayer,
      category: game.category
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
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
      setStatus({
        type: 'success',
        message: editingId ? '게임 정보를 수정했습니다.' : '새 게임을 추가했습니다.'
      });
      resetForm();
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function handleDelete(game) {
    const confirmed = window.confirm(`${game.name}을(를) 삭제할까요?`);
    if (!confirmed) {
      return;
    }

    try {
      await deleteGame(game.id);
      setGames((current) => current.filter((item) => item.id !== game.id));
      if (pickedGame?.id === game.id) {
        setPickedGame(null);
      }
      setStatus({ type: 'success', message: '게임을 삭제했습니다.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function handlePick() {
    try {
      const game = await pickGame();
      setPickedGame(game);
      setStatus({ type: 'success', message: '오늘의 추천 게임을 골랐습니다.' });
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
            <a href="http://localhost:8080/users/login">로그인</a>
            <a href="http://localhost:8080/signup">회원가입</a>
          </div>
        </nav>

        <div className="hero-grid">
          <div className="hero-copy">
            <p className="eyebrow">Board Game Picker</p>
            <h1>모임에 맞는 보드게임을 빠르게 고르세요.</h1>
            <p>
              인원수와 카테고리를 한눈에 비교하고, 결정이 필요할 때는 랜덤 추천으로 바로
              선택할 수 있습니다.
            </p>
            <div className="hero-actions">
              <button className="primary-button" type="button" onClick={handlePick}>
                <Dices size={18} />
                게임 추천
              </button>
              <button className="ghost-button" type="button" onClick={loadGames}>
                <RefreshCw size={18} />
                새로고침
              </button>
            </div>
          </div>

          <aside className="pick-panel" aria-label="추천 게임">
            <div className="panel-label">오늘의 추천</div>
            {pickedGame ? (
              <>
                <h2>{pickedGame.name}</h2>
                <p>{pickedGame.category}</p>
                <div className="player-chip">
                  <Users size={16} />
                  {pickedGame.minPlayer}~{pickedGame.maxPlayer}명
                </div>
              </>
            ) : (
              <>
                <h2>아직 추천 전입니다</h2>
                <p>버튼을 누르면 등록된 게임 중 하나를 골라드립니다.</p>
              </>
            )}
          </aside>
        </div>
      </section>

      <section className="workspace">
        <form className="editor" onSubmit={handleSubmit}>
          <div className="section-heading">
            <div>
              <p className="eyebrow">Game Editor</p>
              <h2>{editingId ? '게임 수정' : '게임 추가'}</h2>
            </div>
            {editingId && (
              <button className="icon-button" type="button" onClick={resetForm} aria-label="수정 취소">
                <X size={18} />
              </button>
            )}
          </div>

          <label>
            게임 이름
            <input name="name" value={form.name} onChange={handleChange} placeholder="스플렌더" />
          </label>

          <div className="field-row">
            <label>
              최소 인원
              <input
                name="minPlayer"
                type="number"
                min="1"
                value={form.minPlayer}
                onChange={handleChange}
              />
            </label>
            <label>
              최대 인원
              <input
                name="maxPlayer"
                type="number"
                min="1"
                value={form.maxPlayer}
                onChange={handleChange}
              />
            </label>
          </div>

          <label>
            카테고리
            <input name="category" value={form.category} onChange={handleChange} placeholder="전략" />
          </label>

          {status.message && <p className={`status ${status.type}`}>{status.message}</p>}

          <button className="primary-button full" type="submit">
            <Save size={18} />
            {editingId ? '수정 저장' : '게임 추가'}
          </button>
        </form>

        <section className="library">
          <div className="library-header">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Library</p>
                <h2>게임 목록</h2>
              </div>
              <span className="count-badge">{games.length}개</span>
            </div>

            <label className="search-box">
              <Search size={18} />
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="이름 또는 카테고리 검색"
              />
            </label>

            {categories.length > 0 && (
              <div className="category-strip">
                {categories.map((category) => (
                  <button type="button" key={category} onClick={() => setQuery(category)}>
                    {category}
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="game-grid">
            {loading ? (
              <div className="empty-state">게임 목록을 불러오는 중입니다.</div>
            ) : filteredGames.length === 0 ? (
              <div className="empty-state">표시할 게임이 없습니다.</div>
            ) : (
              filteredGames.map((game) => (
                <article className="game-card" key={game.id}>
                  <div>
                    <p>{game.category}</p>
                    <h3>{game.name}</h3>
                  </div>
                  <div className="card-footer">
                    <span>
                      <Users size={15} />
                      {game.minPlayer}~{game.maxPlayer}명
                    </span>
                    <div className="card-actions">
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
                        onClick={() => handleDelete(game)}
                        aria-label={`${game.name} 삭제`}
                      >
                        <Trash2 size={17} />
                      </button>
                    </div>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </section>
    </main>
  );
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
