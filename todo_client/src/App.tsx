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

type TodoResponse = {
    id: number
    title: string
    description?: string | null
    completed: boolean
    createdAt: string
    updatedAt: string
}

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

function App() {
    const [token, setToken] = useState<string | null>(
        localStorage.getItem('todo_jwt')
    )
    const [authError, setAuthError] = useState('')
    const [dataError, setDataError] = useState('')
    const [isGuest, setIsGuest] = useState(false)
    const [loginEmail, setLoginEmail] = useState('')
    const [loginPassword, setLoginPassword] = useState('')
    const [registerEmail, setRegisterEmail] = useState('')
    const [registerDisplayName, setRegisterDisplayName] = useState('')
    const [registerPassword, setRegisterPassword] = useState('')
    const [todos, setTodos] = useState<TodoResponse[]>([])
    const [newTitle, setNewTitle] = useState('')
    const [newDescription, setNewDescription] = useState('')
    const [editingId, setEditingId] = useState<number | null>(null)
    const [editTitle, setEditTitle] = useState('')
    const [editDescription, setEditDescription] = useState('')
    const [editCompleted, setEditCompleted] = useState(false)

    const authHeader = useMemo(() => {
        if (!token) return {}
        return { Authorization: `Bearer ${token}` }
    }, [token])

    useEffect(() => {
        if (token) {
            void fetchTodos()
        }
    }, [token])

    async function login() {
        setAuthError('')
        try {
            const response = await fetch(`${API_BASE}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: loginEmail, password: loginPassword }),
            })
            if (!response.ok) {
                throw new Error(`Login failed (${response.status})`)
            }
            const data = (await response.json()) as AuthResponse
            localStorage.setItem('todo_jwt', data.token)
            setToken(data.token)
            setIsGuest(false)
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
            localStorage.setItem('todo_jwt', data.token)
            setToken(data.token)
            setIsGuest(false)
        } catch (error) {
            setAuthError((error as Error).message)
        }
    }

    async function loginGuest() {
        setAuthError('')
        try {
            const response = await fetch(`${API_BASE}/api/auth/guest`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
            })
            if (!response.ok) {
                throw new Error(`Guest login failed (${response.status})`)
            }
            const data = (await response.json()) as AuthResponse
            localStorage.setItem('todo_jwt', data.token)
            setToken(data.token)
            setIsGuest(true)
        } catch (error) {
            setAuthError((error as Error).message)
        }
    }

    function logout() {
        localStorage.removeItem('todo_jwt')
        setToken(null)
        setTodos([])
        setIsGuest(false)
        setEditingId(null)
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

    async function createTodo() {
        setDataError('')
        try {
            const response = await fetch(`${API_BASE}/api/todos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', ...authHeader },
                body: JSON.stringify({
                    title: newTitle,
                    description: newDescription || null,
                    completed: false,
                }),
            })
            if (!response.ok) {
                throw new Error(`Create failed (${response.status})`)
            }
            setNewTitle('')
            setNewDescription('')
            await fetchTodos()
        } catch (error) {
            setDataError((error as Error).message)
        }
    }

    async function toggleTodo(todo: TodoResponse) {
        setDataError('')
        try {
            const response = await fetch(`${API_BASE}/api/todos/${todo.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', ...authHeader },
                body: JSON.stringify({
                    title: todo.title,
                    description: todo.description ?? null,
                    completed: !todo.completed,
                }),
            })
            if (!response.ok) {
                throw new Error(`Update failed (${response.status})`)
            }
            await fetchTodos()
        } catch (error) {
            setDataError((error as Error).message)
        }
    }

    function startEdit(todo: TodoResponse) {
        setEditingId(todo.id)
        setEditTitle(todo.title)
        setEditDescription(todo.description ?? '')
        setEditCompleted(todo.completed)
    }

    function cancelEdit() {
        setEditingId(null)
        setEditTitle('')
        setEditDescription('')
        setEditCompleted(false)
    }

    async function saveEdit(todo: TodoResponse) {
        setDataError('')
        try {
            const response = await fetch(`${API_BASE}/api/todos/${todo.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', ...authHeader },
                body: JSON.stringify({
                    title: editTitle,
                    description: editDescription || null,
                    completed: editCompleted,
                }),
            })
            if (!response.ok) {
                throw new Error(`Update failed (${response.status})`)
            }
            cancelEdit()
            await fetchTodos()
        } catch (error) {
            setDataError((error as Error).message)
        }
    }

    async function deleteTodo(id: number) {
        setDataError('')
        try {
            const response = await fetch(`${API_BASE}/api/todos/${id}`, {
                method: 'DELETE',
                headers: { ...authHeader },
            })
            if (!response.ok) {
                throw new Error(`Delete failed (${response.status})`)
            }
            await fetchTodos()
        } catch (error) {
            setDataError((error as Error).message)
        }
    }

    return (
        <div className="page">
            <header className="hero">
                <div>
                    <p className="eyebrow">Todo Client</p>
                    <h1>Playful Tasks</h1>
                    <p className="subtitle">
                        A lightweight web client connected to the Kotlin Spring backend.
                    </p>
                </div>
                {token ? (
                    <div className="hero-actions">
                        {isGuest ? <span className="pill">Guest mode</span> : null}
                        <button className="ghost" onClick={logout}>
                            Logout
                        </button>
                    </div>
                ) : null}
            </header>

            <section className="grid">
                <article className="card">
                    <h2>Sign in</h2>
                    <label>
                        Email
                        <input
                            value={loginEmail}
                            onChange={(event) => setLoginEmail(event.target.value)}
                            placeholder="you@example.com"
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
                    <button className="ghost" onClick={() => void loginGuest()}>
                        Demo (guest)
                    </button>
                </article>

                <article className="card">
                    <h2>Create account</h2>
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
                            placeholder="Alex"
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
                        Register
                    </button>
                </article>

                <article className="card wide">
                    <div className="card-header">
                        <h2>Your todos</h2>
                        <button className="ghost" onClick={() => void fetchTodos()} disabled={!token}>
                            Refresh
                        </button>
                    </div>
                    {authError ? <p className="error">{authError}</p> : null}
                    {dataError ? <p className="error">{dataError}</p> : null}

                    <div className="composer">
                        <input
                            value={newTitle}
                            onChange={(event) => setNewTitle(event.target.value)}
                            placeholder="New task title"
                            disabled={!token}
                        />
                        <input
                            value={newDescription}
                            onChange={(event) => setNewDescription(event.target.value)}
                            placeholder="Optional details"
                            disabled={!token}
                        />
                        <button className="primary" onClick={() => void createTodo()} disabled={!token || !newTitle}>
                            Add todo
                        </button>
                    </div>

                    <div className="list">
                        {todos.length === 0 ? (
                            <p className="muted">No todos yet. Create your first one.</p>
                        ) : (
                            todos.map((todo) => (
                                <div className="todo" key={todo.id}>
                                    <div>
                                        {editingId === todo.id ? (
                                            <div className="todo-edit">
                                                <input
                                                    value={editTitle}
                                                    onChange={(event) => setEditTitle(event.target.value)}
                                                    placeholder="Title"
                                                />
                                                <input
                                                    value={editDescription}
                                                    onChange={(event) => setEditDescription(event.target.value)}
                                                    placeholder="Description"
                                                />
                                                <label className="checkbox">
                                                    <input
                                                        type="checkbox"
                                                        checked={editCompleted}
                                                        onChange={(event) => setEditCompleted(event.target.checked)}
                                                    />
                                                    Completed
                                                </label>
                                            </div>
                                        ) : (
                                            <>
                                                <h3>{todo.title}</h3>
                                                <p>{todo.description ?? 'No description'}</p>
                                            </>
                                        )}
                                    </div>
                                    <div className="todo-actions">
                                        {editingId === todo.id ? (
                                            <>
                                                <button
                                                    className="primary"
                                                    onClick={() => void saveEdit(todo)}
                                                    disabled={!editTitle}
                                                >
                                                    Save
                                                </button>
                                                <button className="ghost" onClick={cancelEdit}>
                                                    Cancel
                                                </button>
                                            </>
                                        ) : (
                                            <>
                                                <button className="ghost" onClick={() => void toggleTodo(todo)}>
                                                    {todo.completed ? 'Reopen' : 'Done'}
                                                </button>
                                                <button className="ghost" onClick={() => startEdit(todo)}>
                                                    Edit
                                                </button>
                                                <button className="danger" onClick={() => void deleteTodo(todo.id)}>
                                                    Delete
                                                </button>
                                            </>
                                        )}
                                    </div>
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
