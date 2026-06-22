export class PreviewService {
  static generatePreview(htmlContent: string): string {
    return `
      <!DOCTYPE html>
      <html>
      <head>
        <style>
          * { margin: 0; padding: 0; }
          body { font-family: system-ui, -apple-system, sans-serif; }
        </style>
      </head>
      <body>
        ${htmlContent}
      </body>
      </html>
    `;
  }

  static async renderMarkdown(content: string): Promise<string> {
    // TODO: Implement markdown rendering
    return `<pre>${content}</pre>`;
  }
}
