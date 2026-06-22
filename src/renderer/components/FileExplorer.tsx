import React, { useState, useEffect } from 'react';
import '../styles/FileExplorer.css';

interface FileExplorerProps {
  onFileSelect: (filePath: string, content: string) => void;
}

interface FileItem {
  name: string;
  path: string;
  isDirectory: boolean;
  children?: FileItem[];
}

const FileExplorer: React.FC<FileExplorerProps> = ({ onFileSelect }) => {
  const [files, setFiles] = useState<FileItem[]>([]);
  const [expandedDirs, setExpandedDirs] = useState<Set<string>>(new Set());

  useEffect(() => {
    // Mock file structure
    const mockFiles: FileItem[] = [
      {
        name: 'index.html',
        path: 'index.html',
        isDirectory: false,
      },
      {
        name: 'styles',
        path: 'styles',
        isDirectory: true,
        children: [
          {
            name: 'style.css',
            path: 'styles/style.css',
            isDirectory: false,
          },
        ],
      },
      {
        name: 'scripts',
        path: 'scripts',
        isDirectory: true,
        children: [
          {
            name: 'main.js',
            path: 'scripts/main.js',
            isDirectory: false,
          },
        ],
      },
    ];
    setFiles(mockFiles);
  }, []);

  const toggleDir = (path: string) => {
    const newExpanded = new Set(expandedDirs);
    if (newExpanded.has(path)) {
      newExpanded.delete(path);
    } else {
      newExpanded.add(path);
    }
    setExpandedDirs(newExpanded);
  };

  const handleFileClick = async (item: FileItem) => {
    if (!item.isDirectory) {
      // In real implementation, read file content from IPC
      const mockContent = `// ${item.name}\nFile content here`;
      onFileSelect(item.path, mockContent);
    } else {
      toggleDir(item.path);
    }
  };

  const renderFileTree = (items: FileItem[], depth: number = 0) => {
    return items.map((item) => (
      <div key={item.path} className="file-item" style={{ marginLeft: `${depth * 16}px` }}>
        <button
          className={`file-button ${item.isDirectory ? 'directory' : 'file'}`}
          onClick={() => handleFileClick(item)}
        >
          <span className="file-icon">
            {item.isDirectory ? (
              expandedDirs.has(item.path) ? '📂' : '📁'
            ) : (
              getFileIcon(item.name)
            )}
          </span>
          <span className="file-name">{item.name}</span>
        </button>
        {item.isDirectory && expandedDirs.has(item.path) && item.children && (
          renderFileTree(item.children, depth + 1)
        )}
      </div>
    ));
  };

  return (
    <div className="file-explorer">
      <div className="file-explorer-header">
        <h3>Explorer</h3>
      </div>
      <div className="file-tree">
        {renderFileTree(files)}
      </div>
    </div>
  );
};

function getFileIcon(fileName: string): string {
  const ext = fileName.split('.').pop()?.toLowerCase();
  const iconMap: { [key: string]: string } = {
    html: '🌐',
    css: '🎨',
    js: '⚙️',
    jsx: '⚙️',
    ts: '📘',
    tsx: '📘',
    json: '📄',
    md: '📝',
  };
  return iconMap[ext || ''] || '📄';
}

export default FileExplorer;
