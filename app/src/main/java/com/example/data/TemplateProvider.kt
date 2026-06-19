package com.example.data

object TemplateProvider {

    fun getDefaultFiles(projectId: Long, templateType: String): List<ProjectFile> {
        return when (templateType) {
            "HTML" -> getHtmlWebsiteTemplate(projectId)
            "REACT" -> getReactAppTemplate(projectId)
            "TS" -> getTypeScriptTemplate(projectId)
            "REACT_TS" -> getReactTsTemplate(projectId)
            "LANDING" -> getLandingPageTemplate(projectId)
            "PORTFOLIO" -> getPortfolioTemplate(projectId)
            "BLOG" -> getBlogTemplate(projectId)
            "DASHBOARD" -> getDashboardTemplate(projectId)
            "TODO_APP" -> getTodoTemplate(projectId)
            "CALCULATOR" -> getCalculatorTemplate(projectId)
            else -> getHtmlWebsiteTemplate(projectId)
        }
    }

    private fun getHtmlWebsiteTemplate(projectId: Long): List<ProjectFile> {
        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hyper Code HTML5 Template</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="card">
        <h1>🚀 Hyper Code Modern IDE</h1>
        <p>Welcome to your newly scaffolded HTML website project, hosted by <strong>Tucci Cyber Nation</strong> & <strong>Kawooya Raymond</strong>.</p>
        
        <div class="interactive-panel">
            <h3>Live Interaction</h3>
            <p>Click the button below to test JavaScript bindings:</p>
            <button id="counterButton">Click Count: 0</button>
        </div>
    </div>
    
    <script src="script.js"></script>
</body>
</html>"""

        val styleCss = """body {
    background-color: #0f111a;
    color: #e2e8f0;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
}
.card {
    background: linear-gradient(135deg, #1e293b, #0f172a);
    padding: 2.5rem;
    border-radius: 1rem;
    box-shadow: 0 10px 25px rgba(0,0,0,0.5);
    max-width: 500px;
    width: 90%;
    text-align: center;
    border: 1px solid #334155;
    transition: transform 0.2s;
}
.card:hover {
    transform: translateY(-5px);
}
h1 {
    color: #38bdf8;
    margin-bottom: 1rem;
}
p {
    line-height: 1.6;
    color: #94a3b8;
}
strong {
    color: #38bdf8;
}
.interactive-panel {
    margin-top: 2rem;
    padding-top: 1.5rem;
    border-top: 1px dashed #334155;
}
button {
    background-color: #38bdf8;
    color: #0f111a;
    border: none;
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
    font-weight: bold;
    border-radius: 0.5rem;
    cursor: pointer;
    transition: background 0.2s;
}
button:hover {
    background-color: #0ea5e9;
}"""

        val scriptJs = """// JavaScript Code Execution Engine
let count = 0;
const button = document.getElementById('counterButton');

if (button) {
    button.addEventListener('click', () => {
        count++;
        button.textContent = `Click Count: ` + count;
        console.log(`[IDE Console] Counter updated to: ` + count);
    });
}

console.log("[IDE Console] HTML5 Website Initialized successfully.");"""

        return listOf(
            ProjectFile("$projectId:index.html", projectId, "index.html", indexHtml, false, "html"),
            ProjectFile("$projectId:style.css", projectId, "style.css", styleCss, false, "css"),
            ProjectFile("$projectId:script.js", projectId, "script.js", scriptJs, false, "javascript")
        )
    }

    private fun getReactAppTemplate(projectId: Long): List<ProjectFile> {
        val appJsx = """import React, { useState } from 'react';

export default function App() {
    const [count, setCount] = useState(0);
    const [themeColor, setThemeColor] = useState('#2563eb');

    return (
        <div style={{
            fontFamily: 'system-ui, sans-serif',
            padding: '24px',
            backgroundColor: '#0b0f19',
            color: '#f8fafc',
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center'
        }}>
            <div style={{
                maxWidth: '480px',
                width: '100%',
                padding: '32px',
                borderRadius: '16px',
                background: '#111827',
                border: `2px solid ` + themeColor,
                boxShadow: '0 4px 20px rgba(0,0,0,0.3)',
                textAlign: 'center'
            }}>
                <span style={{ fontSize: '48px' }}>⚛️</span>
                <h1 style={{ color: themeColor, marginTop: '8px' }}>React Web App</h1>
                <p style={{ color: '#9ca3af', lineHeight: '1.5' }}>
                    Running live in Hyper Code Live-compiler with Babel Transpiler support!
                </p>

                <div style={{ margin: '24px 0' }}>
                    <div style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '12px' }}>
                        Count: {count}
                    </div>
                    <button 
                        onClick={() => {
                            setCount(count + 1);
                            console.log("[React] Count state incremented: " + (count + 1));
                        }}
                        style={{
                            backgroundColor: themeColor,
                            color: '#ffffff',
                            border: 'none',
                            padding: '10px 20px',
                            borderRadius: '8px',
                            cursor: 'pointer',
                            fontSize: '16px',
                            fontWeight: 'bold',
                            marginRight: '8px'
                        }}
                    >
                        Increment
                    </button>
                    <button 
                        onClick={() => setCount(0)}
                        style={{
                            backgroundColor: '#374151',
                            color: '#ffffff',
                            border: 'none',
                            padding: '10px 20px',
                            borderRadius: '8px',
                            cursor: 'pointer',
                            fontSize: '16px'
                        }}
                    >
                        Reset
                    </button>
                </div>

                <div style={{ borderTop: '1px solid #1f2937', paddingTop: '16px' }}>
                    <div style={{ fontSize: '14px', color: '#9ca3af', marginBottom: '8px' }}>Change Theme Action Color:</div>
                    <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
                        {['#3b82f6', '#10b981', '#ec4899', '#f59e0b', '#8b5cf6'].map(color => (
                            <button
                                key={color}
                                onClick={() => setThemeColor(color)}
                                style={{
                                    width: '28px',
                                    height: '28px',
                                    borderRadius: '50%',
                                    backgroundColor: color,
                                    border: themeColor === color ? '3px solid white' : 'none',
                                    cursor: 'pointer'
                                }}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}"""

        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>React Live Environment</title>
    <!-- React & Babel Standalone Libraries -->
    <script src="https://unpkg.com/react@18/umd/react.development.js" crossorigin></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js" crossorigin></script>
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
</head>
<body style="margin:0; padding:0;">
    <div id="root"></div>

    <script type="text/babel">
        // Import App from virtual files injected by the app
        /*FILE_INJECTION_PLACEHOLDER*/
        
        import App from './App.jsx';
        const rootElement = document.getElementById('root');
        const root = ReactDOM.createRoot(rootElement);
        root.render(<App />);
    </script>
</body>
</html>"""

        return listOf(
            ProjectFile("$projectId:App.jsx", projectId, "App.jsx", appJsx, false, "javascript"),
            ProjectFile("$projectId:index.html", projectId, "index.html", indexHtml, false, "html")
        )
    }

    private fun getTypeScriptTemplate(projectId: Long): List<ProjectFile> {
        val mainTs = """// TypeScript Demonstration
interface DevProfile {
    author: string;
    studio: string;
    framework: string;
    year: number;
}

class HyperCodeIDE {
    private config: DevProfile;

    constructor(config: DevProfile) {
        this.config = config;
    }

    public launch(): void {
        const titleEl = document.getElementById('ide-title');
        const authorEl = document.getElementById('ide-desc');
        
        if (titleEl) {
            titleEl.textContent = `🚀 Hyper Code Web IDE`;
        }
        
        if (authorEl) {
            authorEl.innerHTML = `This project was built offline using TypeScript compiling. <br><br>` + 
                `<strong>Author:</strong> ` + this.config.author + `<br>` +
                `<strong>Studio:</strong> ` + this.config.studio + `<br>` +
                `<strong>Framework:</strong> ` + this.config.framework;
        }
        
        console.log(`[TypeScript Console] Compilation complete. Launched: `, this.config);
    }
}

// Instantiate and launch the compiler
window.addEventListener('DOMContentLoaded', () => {
    const ide = new HyperCodeIDE({
        author: "Kawooya Raymond",
        studio: "Tucci Cyber Nation",
        framework: "TypeScript 5.0 Transpiler",
        year: 2026
    });
    ide.launch();
});"""

        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TypeScript Live Sandbox</title>
    <!-- Use Babel to transpile TypeScript on the fly in the WebView -->
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
    <style>
        body {
            background-color: #090a0f;
            color: #eceff4;
            font-family: system-ui, sans-serif;
            text-align: center;
            padding: 32px;
        }
        .container {
            background: #141622;
            padding: 24px;
            border-radius: 12px;
            border: 1px solid #2e3440;
            max-width: 450px;
            margin: auto;
            text-align: left;
            box-shadow: 0 4px 20px rgba(0,0,0,0.5);
        }
        h2 { color: #88c0d0; }
        strong { color: #d8dee9; }
    </style>
</head>
<body>
    <div class="container">
        <h2 id="ide-title">Compiling TypeScript...</h2>
        <p id="ide-desc">Transpiling in-progress...</p>
    </div>

    <!-- Inject source code and run with TypeScript preset -->
    <script type="text/babel" data-presets="typescript">
        /*FILE_INJECTION_PLACEHOLDER*/
    </script>
</body>
</html>"""

        return listOf(
            ProjectFile("$projectId:main.ts", projectId, "main.ts", mainTs, false, "typescript"),
            ProjectFile("$projectId:index.html", projectId, "index.html", indexHtml, false, "html")
        )
    }

    private fun getReactTsTemplate(projectId: Long): List<ProjectFile> {
        val appTsx = """import React, { useState } from 'react';

interface Task {
    id: number;
    text: string;
    completed: boolean;
}

export default function App(): JSX.Element {
    const [tasks, setTasks] = useState<Task[]>([
        { id: 1, text: "Explore typescript bindings in Hyper Code", completed: true },
        { id: 2, text: "Build a responsive grid using Tailwind", completed: false }
    ]);
    const [input, setInput] = useState<string>('');

    const toggle = (id: number): void => {
        setTasks(tasks.map(t => t.id === id ? { ...t, completed: !t.completed } : t));
    };

    const add = (): void => {
        if (!input.trim()) return;
        setTasks([...tasks, { id: Date.now(), text: input, completed: false }]);
        setInput('');
        console.log("[React+TSX] Task added: " + input);
    };

    return (
        <div style={{
            background: '#0d1117',
            color: '#c9d1d9',
            fontFamily: 'sans-serif',
            minHeight: '100vh',
            padding: '24px'
        }}>
            <div style={{ maxWidth: '400px', margin: 'auto', background: '#161b22', padding: '20px', borderRadius: '8px' }}>
                <h3 style={{ color: '#58a6ff', margin: '0 0 16px 0' }}>📂 React TS Todo list</h3>
                
                <div style={{ margin: '0 0 16px 0', display: 'flex' }}>
                    <input 
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder="Add tasks..."
                        style={{
                            flex: 1,
                            padding: '8px',
                            background: '#0d1117',
                            border: '1px solid #30363d',
                            color: '#c9d1d9',
                            borderRadius: '4px'
                        }}
                    />
                    <button 
                        onClick={add}
                        style={{
                            marginLeft: '8px',
                            background: '#238636',
                            color: 'white',
                            border: 'none',
                            padding: '8px 16px',
                            borderRadius: '4px',
                            cursor: 'pointer'
                        }}
                    >
                        Add
                    </button>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {tasks.map(t => (
                        <div 
                            key={t.id}
                            style={{ 
                                display: 'flex', 
                                alignItems: 'center', 
                                gap: '8px', 
                                padding: '8px', 
                                background: '#21262d', 
                                borderRadius: '4px' 
                            }}
                        >
                            <input 
                                type="checkbox"
                                checked={t.completed}
                                onChange={() => toggle(t.id)}
                                style={{ transform: 'scale(1.2)' }}
                            />
                            <span style={{ textDecoration: t.completed ? 'line-through' : 'none' }}>
                                {t.text}
                            </span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}"""

        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>React TypeScript Application</title>
    <script src="https://unpkg.com/react@18/umd/react.development.js"></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
</head>
<body style="margin:0; background: #0d1117">
    <div id="root"></div>

    <script type="text/babel" data-presets="typescript,react">
        /*FILE_INJECTION_PLACEHOLDER*/
        
        import App from './App.tsx';
        const root = ReactDOM.createRoot(document.getElementById('root'));
        root.render(<App />);
    </script>
</body>
</html>"""

        return listOf(
            ProjectFile("$projectId:App.tsx", projectId, "App.tsx", appTsx, false, "typescript"),
            ProjectFile("$projectId:index.html", projectId, "index.html", indexHtml, false, "html")
        )
    }

    private fun getLandingPageTemplate(projectId: Long): List<ProjectFile> {
        val code = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tucci Cyber Defenses</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen">
    <!-- Navbar -->
    <nav class="flex items-center justify-between p-6 border-b border-slate-800">
        <span class="text-2xl font-black bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent">
            🛡️ TUCCI CYBER
        </span>
        <div class="space-x-4">
            <a href="#" class="hover:text-cyan-400">Services</a>
            <a href="#" class="hover:text-cyan-400">Security</a>
            <button class="bg-cyan-500 text-slate-950 font-bold px-4 py-2 rounded-lg hover:bg-cyan-400 transition">
                Consultation
            </button>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="max-w-4xl mx-auto text-center px-6 py-20 mt-10">
        <h1 class="text-5xl font-extrabold tracking-tight md:text-6xl mb-6">
            Cyber Defense Systems built by <span class="text-cyan-400">Tucci Cyber Nation</span>
        </h1>
        <p class="text-lg text-slate-400 max-w-xl mx-auto mb-10">
            Engineered protection against zero-day exploits, penetration vector routing, and cloud ledger security led by chief architect Kawooya Raymond.
        </p>
        <div class="flex justify-center gap-4">
            <button class="bg-cyan-500 text-slate-950 font-bold px-6 py-3 rounded-xl hover:bg-cyan-400 transition" onclick="alert('Defenses activated!')">
                Launch Security Scan
            </button>
            <button class="border border-slate-800 hover:border-slate-700 px-6 py-3 rounded-xl transition">
                Documentation
            </button>
        </div>
    </section>

    <!-- Feature Grid -->
    <section class="max-w-5xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8 px-6 py-12">
        <div class="bg-slate-900 border border-slate-800 p-6 rounded-2xl hover:border-cyan-500/50 transition">
            <h3 class="text-xl font-bold text-cyan-400 mb-2">⚡ Prototyping IDE</h3>
            <p class="text-slate-400">Write high-performance TypeScript components directly into your secure workspace sandbox.</p>
        </div>
        <div class="bg-slate-900 border border-slate-800 p-6 rounded-2xl hover:border-cyan-500/50 transition">
            <h3 class="text-xl font-bold text-cyan-400 mb-2">🐳 Babel Runtime</h3>
            <p class="text-slate-400">Compile TS and React modules inside a responsive secure local container client-side.</p>
        </div>
        <div class="bg-slate-900 border border-slate-800 p-6 rounded-2xl hover:border-cyan-500/50 transition">
            <h3 class="text-xl font-bold text-cyan-400 mb-2">🔐 Supabase Client</h3>
            <p class="text-slate-400">Store project metadata, collaborate on code bases, and push versions to cloud repositories.</p>
        </div>
    </section>
</body>
</html>"""
        return listOf(ProjectFile("$projectId:index.html", projectId, "index.html", code, false, "html"))
    }

    private fun getPortfolioTemplate(projectId: Long): List<ProjectFile> {
        val code = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Portfolio - Kawooya Raymond</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-zinc-950 text-zinc-100 font-sans min-h-screen">
    <div class="max-w-3xl mx-auto px-6 py-16">
        <header class="mb-12">
            <h1 class="text-4xl font-extrabold mb-1">Kawooya Raymond</h1>
            <p class="text-emerald-400 font-semibold mb-4">Cyber Defense Architect & Software Designer</p>
            <p class="text-zinc-400 max-w-lg">
                I build mobile compilers, local sandbox rendering engines, and military-grade threat defenses for global enterprise cloud databases.
            </p>
        </header>

        <section class="mb-12">
            <h2 class="text-xl font-bold text-zinc-300 uppercase tracking-widest border-b border-zinc-800 pb-2 mb-6">Expertise</h2>
            <div class="grid grid-cols-2 gap-4">
                <span class="bg-zinc-900 p-3 rounded-lg border border-zinc-800">🛠️ Reactive Compiler Dev</span>
                <span class="bg-zinc-900 p-3 rounded-lg border border-zinc-800">🛡️ Network Penetration Security</span>
                <span class="bg-zinc-900 p-3 rounded-lg border border-zinc-800">⚛️ React, JSX & TypeScript</span>
                <span class="bg-zinc-900 p-3 rounded-lg border border-zinc-800">⚡ SQLite Architecture</span>
            </div>
        </section>

        <section>
            <h2 class="text-xl font-bold text-zinc-300 uppercase tracking-widest border-b border-zinc-800 pb-2 mb-4">Featured Work</h2>
            <div class="space-y-6">
                <div>
                    <h3 class="text-lg font-bold">Hyper Code IDE</h3>
                    <p class="text-sm text-zinc-500 mb-1">A real-time reactive mobile web engineering canvas.</p>
                    <p class="text-zinc-400">Capable of running Babel scripts, checking types, logging outputs, and optimizing code snippets offline.</p>
                </div>
            </div>
        </section>
    </div>
</body>
</html>"""
        return listOf(ProjectFile("$projectId:index.html", projectId, "index.html", code, false, "html"))
    }

    private fun getBlogTemplate(projectId: Long): List<ProjectFile> {
        val code = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cyber Ledger - Tucci Nation</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-neutral-950 text-neutral-200 min-h-screen p-6">
    <div class="max-w-xl mx-auto">
        <h1 class="text-3xl font-black text-amber-500 border-b border-amber-500/20 pb-4 mb-8">📟 THE CYBER LEDGER</h1>

        <article class="mb-12">
            <span class="text-xs text-neutral-500">June 19, 2026 • Kawooya Raymond</span>
            <h2 class="text-2xl font-bold text-neutral-100 hover:text-amber-400 cursor-pointer mt-1 mb-2">
                Compiling React Inside Mobile Android Sandboxes
            </h2>
            <p class="text-neutral-400 leading-relaxed mb-4">
                How we achieved hot reloading, dynamic Babel compilations, and full typescript inspection in native client frameworks using sandboxed webview bridges.
            </p>
            <a href="#" class="text-amber-500 font-semibold hover:underline">Read full journal &rarr;</a>
        </article>

        <article class="mb-12">
            <span class="text-xs text-neutral-500">June 12, 2026 • Tucci Cyber Team</span>
            <h2 class="text-2xl font-bold text-neutral-100 hover:text-amber-400 cursor-pointer mt-1 mb-2">
                Securing Local SQLite DBs from Memory Dumps
            </h2>
            <p class="text-neutral-400 leading-relaxed mb-4">
                Best practices for Room schema design, Cascade deletes, and encryption configurations on modern devices.
            </p>
            <a href="#" class="text-amber-500 font-semibold hover:underline">Read full journal &rarr;</a>
        </article>
    </div>
</body>
</html>"""
        return listOf(ProjectFile("$projectId:index.html", projectId, "index.html", code, false, "html"))
    }

    private fun getDashboardTemplate(projectId: Long): List<ProjectFile> {
        val code = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tucci Command System</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-[#0b0e14] text-slate-200 min-h-screen p-6">
    <div class="max-w-5xl mx-auto">
        <header class="flex justify-between items-center mb-8">
            <div>
                <h1 class="text-2xl font-bold">Admin Telemetry Console</h1>
                <p class="text-slate-400 text-sm">Real-time compilation nodes & workspace indicators</p>
            </div>
            <button class="bg-[#1b2535] border border-cyan-500/30 text-cyan-400 px-4 py-2 rounded-lg font-bold">
                Nodes Online: 4
            </button>
        </header>

        <!-- Grid Cards -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <div class="bg-[#121824] border border-slate-800 p-6 rounded-xl">
                <span class="text-[#8e9bb3] text-sm font-semibold uppercase">Total Files</span>
                <h2 class="text-3xl font-black text-cyan-400 mt-2">124</h2>
            </div>
            <div class="bg-[#121824] border border-slate-800 p-6 rounded-xl">
                <span class="text-[#8e9bb3] text-sm font-semibold uppercase">Babel Builds</span>
                <h2 class="text-3xl font-black text-emerald-400 mt-2">4,812ms</h2>
            </div>
            <div class="bg-[#121824] border border-slate-800 p-6 rounded-xl">
                <span class="text-[#8e9bb3] text-sm font-semibold uppercase">Cloud Sync</span>
                <h2 class="text-3xl font-black text-amber-500 mt-2">Active</h2>
            </div>
            <div class="bg-[#121824] border border-slate-800 p-6 rounded-xl">
                <span class="text-[#8e9bb3] text-sm font-semibold uppercase">IDE Core API</span>
                <h2 class="text-3xl font-black text-pink-500 mt-2">v5.1</h2>
            </div>
        </div>

        <!-- Custom Canvas Drawing for Analytics -->
        <div class="bg-[#121824] border border-slate-800 p-6 rounded-xl">
            <h3 class="text-lg font-bold mb-4">Transpilation Performance Scale</h3>
            <canvas id="performanceChart" width="800" height="250" class="w-full bg-slate-950/80 rounded-lg p-2"></canvas>
        </div>
    </div>

    <script>
        const canvas = document.getElementById('performanceChart');
        if (canvas) {
            const ctx = canvas.getContext('2d');
            ctx.strokeStyle = '#22d3ee';
            ctx.lineWidth = 3;
            ctx.beginPath();
            ctx.moveTo(50, 200);
            ctx.lineTo(150, 150);
            ctx.lineTo(250, 180);
            ctx.lineTo(350, 80);
            ctx.lineTo(450, 120);
            ctx.lineTo(550, 50);
            ctx.lineTo(650, 90);
            ctx.lineTo(750, 30);
            ctx.stroke();

            // Gradient fill under the curve
            const gradient = ctx.createLinearGradient(0,0,0,250);
            gradient.addColorStop(0, 'rgba(34, 211, 238, 0.3)');
            gradient.addColorStop(1, 'rgba(34, 211, 238, 0)');
            ctx.fillStyle = gradient;
            ctx.lineTo(750, 220);
            ctx.lineTo(50, 220);
            ctx.closePath();
            ctx.fill();
        }
    </script>
</body>
</html>"""
        return listOf(ProjectFile("$projectId:index.html", projectId, "index.html", code, false, "html"))
    }

    private fun getTodoTemplate(projectId: Long): List<ProjectFile> {
        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Responsive Todo Application</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div id="todo-app">
        <h2>📂 Workspace Todos</h2>
        <div class="input-row">
            <input type="text" id="todoInput" placeholder="Add custom action...">
            <button id="addTodo">Add</button>
        </div>
        <ul id="todoList"></ul>
    </div>
    <script src="script.js"></script>
</body>
</html>"""

        val styleCss = """body {
    background-color: #0b0f19;
    color: #f1f5f9;
    font-family: system-ui, sans-serif;
    display: flex;
    justify-content: center;
    padding-top: 40px;
    margin: 0;
}
#todo-app {
    width: 90%;
    max-width: 400px;
    background: #111827;
    padding: 24px;
    border-radius: 12px;
    border: 1px solid #1f2937;
    box-shadow: 0 4px 15px rgba(0,0,0,0.4);
}
h2 {
    color: #3b82f6;
    margin-bottom: 20px;
}
.input-row {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
}
input {
    flex: 1;
    background: #1f2937;
    border: 1px solid #374151;
    color: #f1f5f9;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 14px;
}
button {
    background-color: #3b82f6;
    color: white;
    cursor: pointer;
    border: none;
    border-radius: 6px;
    padding: 8px 16px;
    font-weight: bold;
}
button:hover { background-color: #2563eb; }
ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
}
li {
    background: #1f2937;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.task-completed {
    text-decoration: line-through;
    color: #4b5563;
}"""

        val scriptJs = """const input = document.getElementById('todoInput');
const btn = document.getElementById('addTodo');
const list = document.getElementById('todoList');

let listItems = [
    { text: "Test typescript compiler", completed: true },
    { text: "Integrate Supabase triggers", completed: false }
];

function draw() {
    list.innerHTML = "";
    listItems.forEach((item, index) => {
        const li = document.createElement('li');
        
        const label = document.createElement('span');
        label.textContent = item.text;
        if (item.completed) label.className = "task-completed";
        label.onclick = () => {
            item.completed = !item.completed;
            draw();
        };

        const del = document.createElement('button');
        del.textContent = "🗑️";
        del.style.background = "none";
        del.style.color = "#ef4444";
        del.onclick = () => {
            listItems.splice(index, 1);
            draw();
            console.log("[Todo Console] Index deleted: " + index);
        };

        li.appendChild(label);
        li.appendChild(del);
        list.appendChild(li);
    });
}

btn.onclick = () => {
    if (!input.value.trim()) return;
    listItems.push({ text: input.value, completed: false });
    input.value = "";
    draw();
};

draw();"""

        return listOf(
            ProjectFile("$projectId:index.html", projectId, "index.html", indexHtml, false, "html"),
            ProjectFile("$projectId:style.css", projectId, "style.css", styleCss, false, "css"),
            ProjectFile("$projectId:script.js", projectId, "script.js", scriptJs, false, "javascript")
        )
    }

    private fun getCalculatorTemplate(projectId: Long): List<ProjectFile> {
        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Responsive Calculator</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="calc-card">
        <input type="text" id="display" readonly>
        <div class="grid">
            <button onclick="clearVal()" style="color:#ef4444;">C</button>
            <button onclick="backspace()">⌫</button>
            <button onclick="append('/')">/</button>
            <button onclick="append('*')">*</button>
            
            <button onclick="append('7')">7</button>
            <button onclick="append('8')">8</button>
            <button onclick="append('9')">9</button>
            <button onclick="append('-')">-</button>
            
            <button onclick="append('4')">4</button>
            <button onclick="append('5')">5</button>
            <button onclick="append('6')">6</button>
            <button onclick="append('+')">+</button>
            
            <button onclick="append('1')">1</button>
            <button onclick="append('2')">2</button>
            <button onclick="append('3')">3</button>
            <button onclick="evaluateExpr()" class="equal-btn" style="grid-row: span 2;">=</button>
            
            <button onclick="append('0')" style="grid-column: span 2;">0</button>
            <button onclick="append('.')">.</button>
        </div>
    </div>
    <script src="script.js"></script>
</body>
</html>"""

        val styleCss = """body {
    background-color: #0d1117;
    margin: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    font-family: system-ui, sans-serif;
}
.calc-card {
    background: #161b22;
    padding: 20px;
    border-radius: 12px;
    border: 1px solid #30363d;
    box-shadow: 0 4px 15px rgba(0,0,0,0.4);
    width: 280px;
}
#display {
    width: 95%;
    background-color: #0d1117;
    border: 1px solid #30363d;
    color: #58a6ff;
    padding: 12px;
    font-size: 24px;
    text-align: right;
    border-radius: 6px;
    margin-bottom: 16px;
}
.grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;
}
button {
    background-color: #21262d;
    color: #c9d1d9;
    border: 1px solid #30363d;
    font-size: 18px;
    padding: 16px 0;
    border-radius: 6px;
    cursor: pointer;
    transition: background 0.2s;
}
button:hover { background-color: #30363d; }
.equal-btn {
    background-color: #238636;
    color: white;
}
.equal-btn:hover { background-color: #2ea043; }"""

        val scriptJs = """const display = document.getElementById('display');

function append(val) {
    display.value += val;
}

function clearVal() {
    display.value = "";
}

function backspace() {
    display.value = display.value.slice(0, -1);
}

function evaluateExpr() {
    try {
        const expression = display.value;
        const result = eval(expression);
        display.value = result;
        console.log("[Calc Console] Calculated: " + expression + " = " + result);
    } catch(e) {
        display.value = "Error";
        console.log("[Calc Console] Syntax Error in evaluation");
    }
}"""

        return listOf(
            ProjectFile("$projectId:index.html", projectId, "index.html", indexHtml, false, "html"),
            ProjectFile("$projectId:style.css", projectId, "style.css", styleCss, false, "css"),
            ProjectFile("$projectId:script.js", projectId, "script.js", scriptJs, false, "javascript")
        )
    }
}
