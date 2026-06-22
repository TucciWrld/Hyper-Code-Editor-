import React, { useEffect, useRef } from 'react';
import { Terminal as XTerminal } from 'xterm';
import { FitAddon } from 'xterm-addon-fit';
import 'xterm/css/xterm.css';
import '../styles/Terminal.css';

const Terminal: React.FC = () => {
  const terminalRef = useRef<HTMLDivElement>(null);
  const terminalInstanceRef = useRef<XTerminal | null>(null);

  useEffect(() => {
    if (!terminalRef.current) return;

    const term = new XTerminal({
      theme: {
        background: '#1e1e1e',
        foreground: '#d4d4d4',
      },
      fontSize: 12,
      fontFamily: "'Fira Code', monospace",
    });

    const fitAddon = new FitAddon();
    term.loadAddon(fitAddon);
    term.open(terminalRef.current);
    fitAddon.fit();

    term.writeln('\x1B[32m$ Hyper Terminal v2.0\x1B[0m');
    term.write('\x1B[32m$ \x1B[0m');

    let inputBuffer = '';

    term.onData((data) => {
      if (data === '\r') {
        term.writeln('');
        term.write(`\x1B[32m$ \x1B[0m`);
        inputBuffer = '';
      } else if (data === '\u007F') {
        if (inputBuffer.length > 0) {
          inputBuffer = inputBuffer.slice(0, -1);
          term.write('\b \b');
        }
      } else {
        inputBuffer += data;
        term.write(data);
      }
    });

    terminalInstanceRef.current = term;

    const handleResize = () => {
      fitAddon.fit();
    };

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      term.dispose();
    };
  }, []);

  return (
    <div className="terminal-panel">
      <div ref={terminalRef} className="terminal-instance" />
    </div>
  );
};

export default Terminal;
