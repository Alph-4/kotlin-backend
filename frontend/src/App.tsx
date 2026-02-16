import { useEffect, useMemo, useState } from 'react'
import './App.css'

type AuthResponse = {
  token: string
  type: string
  user: {
    id: number
    email: string
    displayName: string
    role: string
  }
}

type UserResponse = {
  id: number
  email: string
  displayName: string
  role: string
}

type TodoResponse = {
  id: number
  title: string
  description?: string | null
  completed: boolean
  createdAt: string
  updatedAt: string
}

type RequestLog = {
  id: number
  timestamp: string
  method: string
  path: string
  status: number
  durationMs: number
  user: string
  ip: string
}

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

function App() {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem('jwt')
  )
  const [authUser, setAuthUser] = useState<UserResponse | null>(null)
  const [authError, setAuthError] = useState('')
  const [dataError, setDataError] = useState('')
  const [reqError, setReqError] = useState('')

  const [loginEmail, setLoginEmail] = useState('')
  const [loginPassword, setLoginPassword] = useState('')
  const [registerEmail, setRegisterEmail] = useState('')
  const [registerDisplayName, setRegisterDisplayName] = useState('')
  const [registerPassword, setRegisterPassword] = useState('')

  const [users, setUsers] = useState<UserResponse[]>([])
  const [todos, setTodos] = useState<TodoResponse[]>([])
  const [requests, setRequests] = useState<RequestLog[]>([])

  const authHeader = useMemo(() => {
    if (!token) return {}
    return { Authorization: `Bearer ${token}` }
  }, [token])

  useEffect(() => {
    if (token) {
      void fetchMe()
      void fetchTodos()
    }
  }, [token])

  async function login() {
    setAuthError('')
    try {
      const response = await fetch(`${API_BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: loginEmail,
          password: loginPassword,
        }),
      })
      if (!response.ok) {
        throw new Error(`Login failed (${response.status})`)
      }
      const data = (await response.json()) as AuthResponse
      localStorage.setItem('jwt', data.token)
      setToken(data.token)
      setAuthUser(data.user)
    } catch (error) {
      setAuthError((error as Error).message)
    }
  }

  async function register() {
    setAuthError('')
    try {
      const response = await fetch(`${API_BASE}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: registerEmail,
          displayName: registerDisplayName,
          password: registerPassword,
        }),
      })
      if (!response.ok) {
        throw new Error(`Register failed (${response.status})`)
      }
      const data = (await response.json()) as AuthResponse
      localStorage.setItem('jwt', data.token)
      setToken(data.token)
      setAuthUser(data.user)
    } catch (error) {
      setAuthError((error as Error).message)
    }
  }

  function logout() {
    localStorage.removeItem('jwt')
    setToken(null)
    setAuthUser(null)
    setUsers([])
    setTodos([])
    setRequests([])
  }

  async function fetchMe() {
    setDataError('')
    try {
      const response = await fetch(`${API_BASE}/api/users/me`, {
        headers: { ...authHeader },
      })
      if (!response.ok) {
        throw new Error(`Load me failed (${response.status})`)
      }
      const data = (await response.json()) as UserResponse
      setAuthUser(data)
    } catch (error) {
      setDataError((error as Error).message)
    }
  }

  async function fetchUsers() {
    setDataError('')
    try {
      const response = await fetch(`${API_BASE}/api/users`, {
        headers: { ...authHeader },
      })
      if (!response.ok) {
        throw new Error(`Load users failed (${response.status})`)
      }
      const data = (await response.json()) as UserResponse[]
      setUsers(data)
    } catch (error) {
      setDataError((error as Error).message)
    }
  }

  async function fetchTodos() {
    setDataError('')
    try {
      const response = await fetch(`${API_BASE}/api/todos`, {
        headers: { ...authHeader },
      })
      if (!response.ok) {
        throw new Error(`Load todos failed (${response.status})`)
      }
      const data = (await response.json()) as TodoResponse[]
      setTodos(data)
    } catch (error) {
      setDataError((error as Error).message)
    }
  }

  async function fetchRequests() {
    setReqError('')
    try {
      const response = await fetch(`${API_BASE}/api/metrics/requests?limit=200`, {
        headers: { ...authHeader },
      })
      if (!response.ok) {
        throw new Error(`Load requests failed (${response.status})`)
      }
      const data = (await response.json()) as RequestLog[]
      setRequests(data)
    } catch (error) {
      setReqError((error as Error).message)
    }
  }

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">Realtime Backend View</p>
          <h1>Backend Pulseboard</h1>
          <p className="subtitle">
            Live view of users, todos, and request traffic from your Spring API.
          </p>
        </div>
        <div className="hero-actions">
          <button className="primary" onClick={() => void fetchRequests()} disabled={!token}>
            Refresh requests
          </button>
          <button className="ghost" onClick={() => void fetchTodos()} disabled={!token}>
            Refresh data
          </button>
        </div>
      </header>

      <section className="grid">
        <article className="card auth">
          <div className="card-header">
            <h2>Auth</h2>
            {token ? (
              <button className="ghost" onClick={logout}>
                Logout
              </button>
            ) : null}
          </div>
          <div className="auth-grid">
            <div>
              <h3>Login</h3>
              <label>
                Email
                <input
                  value={loginEmail}
                  onChange={(event) => setLoginEmail(event.target.value)}
                  placeholder="user@example.com"
                />
              </label>
              <label>
                Password
                <input
                  type="password"
                  value={loginPassword}
                  onChange={(event) => setLoginPassword(event.target.value)}
                  placeholder="password"
                />
              </label>
              <button className="primary" onClick={() => void login()}>
                Login
              </button>
            </div>
            <div>
              <h3>Register</h3>
              <label>
                Email
                <input
                  value={registerEmail}
                  onChange={(event) => setRegisterEmail(event.target.value)}
                  placeholder="new@example.com"
                />
              </label>
              <label>
                Display name
                <input
                  value={registerDisplayName}
                  onChange={(event) => setRegisterDisplayName(event.target.value)}
                  placeholder="Jane Doe"
                />
              </label>
              <label>
                Password
                <input
                  type="password"
                  value={registerPassword}
                  onChange={(event) => setRegisterPassword(event.target.value)}
                  placeholder="password"
                />
              </label>
              <button className="ghost" onClick={() => void register()}>
                Create account
              </button>
            </div>
          </div>
          {authUser ? (
            <div className="pill-row">
              <span className="pill">{authUser.displayName}</span>
              <span className="pill">{authUser.email}</span>
              <span className="pill">role: {authUser.role}</span>
            </div>
          ) : (
            <p className="muted">Login or register to load data.</p>
          )}
          {authError ? <p className="error">{authError}</p> : null}
        </article>

        <article className="card">
          <div className="card-header">
            <h2>Users</h2>
            <button className="ghost" onClick={() => void fetchUsers()} disabled={!token}>
              Refresh
            </button>
          </div>
          <p className="muted">Requires ADMIN role.</p>
          {dataError ? <p className="error">{dataError}</p> : null}
          <div className="table">
            <div className="row header">
              <span>ID</span>
              <span>Email</span>
              <span>Name</span>
              <span>Role</span>
            </div>
            {users.length === 0 ? (
              <div className="row empty">No users loaded.</div>
            ) : (
              users.map((user) => (
                <div className="row" key={user.id}>
                  <span>{user.id}</span>
                  <span>{user.email}</span>
                  <span>{user.displayName}</span>
                  <span>{user.role}</span>
                </div>
              ))
            )}
          </div>
        </article>

        <article className="card">
          <div className="card-header">
            <h2>Todos</h2>
            <button className="ghost" onClick={() => void fetchTodos()} disabled={!token}>
              Refresh
            </button>
          </div>
          {dataError ? <p className="error">{dataError}</p> : null}
          <div className="table">
            <div className="row header">
              <span>ID</span>
              <span>Title</span>
              <span>Status</span>
              <span>Updated</span>
            </div>
            {todos.length === 0 ? (
              <div className="row empty">No todos loaded.</div>
            ) : (
              todos.map((todo) => (
                <div className="row" key={todo.id}>
                  <span>{todo.id}</span>
                  <span>{todo.title}</span>
                  <span>{todo.completed ? 'Done' : 'Open'}</span>
                  <span>{todo.updatedAt}</span>
                </div>
              ))
            )}
          </div>
        </article>

        <article className="card wide">
          <div className="card-header">
            <h2>Request log</h2>
            <button className="ghost" onClick={() => void fetchRequests()} disabled={!token}>
              Refresh
            </button>
          </div>
          {reqError ? <p className="error">{reqError}</p> : null}
          <div className="table">
            <div className="row header">
              <span>Time</span>
              <span>Method</span>
              <span>Path</span>
              <span>Status</span>
              <span>Ms</span>
              <span>User</span>
              <span>IP</span>
            </div>
            {requests.length === 0 ? (
              <div className="row empty">No requests logged yet.</div>
            ) : (
              requests.map((entry) => (
                <div className="row" key={entry.id}>
                  <span>{entry.timestamp}</span>
                  <span>{entry.method}</span>
                  <span>{entry.path}</span>
                  <span>{entry.status}</span>
                  <span>{entry.durationMs}</span>
                  <span>{entry.user}</span>
                  <span>{entry.ip}</span>
                </div>
              ))
            )}
          </div>
        </article>
      </section>
    </div>
  )
}

export default App
