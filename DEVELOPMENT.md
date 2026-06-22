# Development Guidelines

## Architecture Overview

```
Hyper Code Editor V2
├── Main Process (Electron)
│   ├── Window Management
│   ├── File Operations
│   └── IPC Communication
├── Renderer (React)
│   ├── UI Components
│   ├── State Management
│   └── Styling
└── Services
    ├── File Service
    ├── Git Service
    ├── Lint Service
    └── Preview Service
```

## Development Workflow

### Starting the Development Server
```bash
npm start
```
This will start both Webpack dev server and Electron in watch mode.

### Building for Production
```bash
npm run build
npm run dist
```

## Key Components

### Editor
- Uses Monaco Editor (VS Code's editor)
- Supports multiple languages
- Real-time syntax highlighting
- Code formatting and linting

### File Explorer
- Tree view of project files
- Drag-and-drop support (planned)
- Context menu operations (planned)

### Terminal
- Integrated terminal using xterm.js
- Command execution
- Output display

## State Management

Currently using React Context API. Can migrate to Redux/Zustand if needed.

## Performance Tips

1. Use React.memo for expensive components
2. Implement code splitting for large features
3. Lazy load components when possible
4. Monitor memory usage for large files

## Testing

(To be implemented)
- Unit tests with Jest
- Component tests with React Testing Library
- E2E tests with Playwright

## Debugging

### Main Process
```bash
DEBUG=electron-main npm start
```

### Renderer Process
Use Chrome DevTools (F12)

## Common Issues

### Port Already in Use
```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9
```

### Monaco Editor Not Loading
- Ensure webpack is configured correctly
- Check browser console for errors
- Rebuild: `npm run build`

## Resources

- [Electron Docs](https://www.electronjs.org/docs)
- [React Docs](https://react.dev)
- [Monaco Editor](https://microsoft.github.io/monaco-editor/)
- [xterm.js](https://xtermjs.org/)
