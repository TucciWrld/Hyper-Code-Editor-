import React, { useEffect, useRef } from 'react';
import * as Monaco from 'monaco-editor';
import '../styles/Editor.css';

interface EditorProps {
  filePath: string | null;
  content: string;
  onSave: (filePath: string, content: string) => void;
}

const Editor: React.FC<EditorProps> = ({ filePath, content, onSave }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const editorRef = useRef<Monaco.editor.IStandaloneCodeEditor | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    if (!editorRef.current) {
      editorRef.current = Monaco.editor.create(containerRef.current, {
        value: content,
        language: getLanguageFromFile(filePath),
        theme: 'vs-dark',
        automaticLayout: true,
        fontSize: 14,
        fontFamily: "'Fira Code', 'Courier New', monospace",
        minimap: { enabled: true },
        scrollBeyondLastLine: false,
        formatOnPaste: true,
        formatOnType: true,
      });

      // Save on Ctrl+S
      editorRef.current.addCommand(Monaco.KeyMod.CtrlCmd | Monaco.KeyCode.KeyS, () => {
        if (filePath) {
          const editorContent = editorRef.current?.getValue() || '';
          onSave(filePath, editorContent);
        }
      });
    }

    if (editorRef.current) {
      editorRef.current.setValue(content);
      editorRef.current.setModel(
        Monaco.editor.createModel(
          content,
          getLanguageFromFile(filePath),
          filePath ? Monaco.Uri.file(filePath) : undefined
        )
      );
    }
  }, [filePath, content, onSave]);

  return (
    <div className="editor">
      {!filePath ? (
        <div className="editor-welcome">
          <h2>Welcome to Hyper Code Editor V2</h2>
          <p>Select a file to start editing</p>
        </div>
      ) : (
        <div ref={containerRef} className="editor-container" />
      )}
    </div>
  );
};

function getLanguageFromFile(filePath: string | null): string {
  if (!filePath) return 'plaintext';
  const ext = filePath.split('.').pop()?.toLowerCase();
  const languageMap: { [key: string]: string } = {
    js: 'javascript',
    jsx: 'javascript',
    ts: 'typescript',
    tsx: 'typescript',
    html: 'html',
    css: 'css',
    scss: 'scss',
    less: 'less',
    json: 'json',
    md: 'markdown',
    py: 'python',
  };
  return languageMap[ext || 'plaintext'] || 'plaintext';
}

export default Editor;
