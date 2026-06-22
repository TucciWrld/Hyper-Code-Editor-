import React, { useState, useCallback } from 'react';
import Sidebar from './components/Sidebar';
import Editor from './components/Editor';
import Terminal from './components/Terminal';
import FileExplorer from './components/FileExplorer';
import './styles/App.css';

const App: React.FC = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [terminalOpen, setTerminalOpen] = useState(false);
  const [activeFile, setActiveFile] = useState<string | null>(null);
  const [files, setFiles] = useState<Map<string, string>>(new Map());

  const handleFileSelect = useCallback((filePath: string, content: string) => {
    setActiveFile(filePath);
    setFiles(prev => new Map(prev).set(filePath, content));
  }, []);

  const handleSaveFile = useCallback((filePath: string, content: string) => {
    setFiles(prev => new Map(prev).set(filePath, content));
  }, []);

  return (
    <div className="app-container">
      <div className="app-header">
        <button 
          className="sidebar-toggle"
          onClick={() => setSidebarOpen(!sidebarOpen)}
          title="Toggle Sidebar"
        >
          ☰
        </button>
        <h1>Hyper Code Editor V2</h1>
      </div>

      <div className="app-main">
        {sidebarOpen && (
          <Sidebar>
            <FileExplorer onFileSelect={handleFileSelect} />
          </Sidebar>
        )}

        <div className="editor-container">
          <Editor 
            filePath={activeFile}
            content={activeFile ? files.get(activeFile) || '' : ''}
            onSave={handleSaveFile}
          />
        </div>
      </div>

      <div className="app-footer">
        <button 
          className="terminal-toggle"
          onClick={() => setTerminalOpen(!terminalOpen)}
          title="Toggle Terminal"
        >
          Terminal {terminalOpen ? '▼' : '▶'}
        </button>
      </div>

      {terminalOpen && (
        <Terminal />
      )}
    </div>
  );
};

export default App;
