import fs from 'fs/promises';
import path from 'path';

export interface FileContent {
  path: string;
  content: string;
  language: string;
}

export class FileService {
  static async readFile(filePath: string): Promise<string> {
    try {
      return await fs.readFile(filePath, 'utf-8');
    } catch (error) {
      console.error(`Error reading file ${filePath}:`, error);
      throw error;
    }
  }

  static async writeFile(filePath: string, content: string): Promise<void> {
    try {
      const dir = path.dirname(filePath);
      await fs.mkdir(dir, { recursive: true });
      await fs.writeFile(filePath, content, 'utf-8');
    } catch (error) {
      console.error(`Error writing file ${filePath}:`, error);
      throw error;
    }
  }

  static async deleteFile(filePath: string): Promise<void> {
    try {
      await fs.unlink(filePath);
    } catch (error) {
      console.error(`Error deleting file ${filePath}:`, error);
      throw error;
    }
  }

  static async listDirectory(dirPath: string): Promise<string[]> {
    try {
      return await fs.readdir(dirPath);
    } catch (error) {
      console.error(`Error listing directory ${dirPath}:`, error);
      throw error;
    }
  }

  static getLanguageFromExtension(filePath: string): string {
    const ext = path.extname(filePath).toLowerCase();
    const languageMap: { [key: string]: string } = {
      '.js': 'javascript',
      '.jsx': 'javascript',
      '.ts': 'typescript',
      '.tsx': 'typescript',
      '.html': 'html',
      '.css': 'css',
      '.scss': 'scss',
      '.less': 'less',
      '.json': 'json',
      '.md': 'markdown',
      '.py': 'python',
      '.java': 'java',
      '.cpp': 'cpp',
      '.c': 'c',
      '.go': 'go',
      '.rs': 'rust',
      '.rb': 'ruby',
      '.php': 'php',
      '.sql': 'sql',
      '.xml': 'xml',
      '.yaml': 'yaml',
      '.yml': 'yaml',
    };
    return languageMap[ext] || 'plaintext';
  }
}
