package com.example.ui.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.regex.Pattern

class CodeHighlighter(private val language: String) : VisualTransformation {

    // Themes
    private val keywordColor = Color(0xFF38BDF8)   // Cyber Cyan
    private val stringColor = Color(0xFF34D399)    // Matrix Emerald
    private val commentColor = Color(0xFF64748B)   // Slate Gray
    private val tagColor = Color(0xFFF472B6)       // Sunset Rose / Pink
    private val numberColor = Color(0xFFF59E0B)    // Amber Gold
    private val attributeColor = Color(0xFFFB7185) // Coral Pink

    // Regular Expressions
    private val keywordPattern = Pattern.compile(
        "\\b(const|let|var|function|class|interface|constructor|public|private|static|return|import|export|from|default|if|else|for|while|do|switch|case|break|try|catch|finally|throw|new|this|extends|super|implements|as|typeof|instanceof|void|any|string|number|boolean|unknown|never|readonly)\\b"
    )
    private val tagPattern = Pattern.compile("</?[a-zA-Z0-9:-]+>?")
    private val numberPattern = Pattern.compile("\\b\\d+\\b")
    private val stringPattern = Pattern.compile("\".*?\"|'.*?'|`.*?`")
    private val commentPattern = Pattern.compile("//.*|/\\*[\\s\\S]*?\\*/")

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            highlight(text.text),
            OffsetMapping.Identity
        )
    }

    private fun highlight(code: String): AnnotatedString {
        val builder = AnnotatedString.Builder(code)
        
        // 1. Highlight Keywords
        val keywordMatcher = keywordPattern.matcher(code)
        while (keywordMatcher.find()) {
            builder.addStyle(
                SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold),
                keywordMatcher.start(),
                keywordMatcher.end()
            )
        }

        // 2. Highlight Numbers
        val numberMatcher = numberPattern.matcher(code)
        while (numberMatcher.find()) {
            builder.addStyle(
                SpanStyle(color = numberColor),
                numberMatcher.start(),
                numberMatcher.end()
            )
        }

        // 3. Highlight HTML Tags if applicable
        if (language == "html" || language == "xml" || language == "jsx" || language == "tsx") {
            val tagMatcher = tagPattern.matcher(code)
            while (tagMatcher.find()) {
                builder.addStyle(
                    SpanStyle(color = tagColor, fontWeight = FontWeight.SemiBold),
                    tagMatcher.start(),
                    tagMatcher.end()
                )
            }
        }

        // 4. Highlight Strings (overrides keywords/numbers inside them)
        val stringMatcher = stringPattern.matcher(code)
        while (stringMatcher.find()) {
            builder.addStyle(
                SpanStyle(color = stringColor),
                stringMatcher.start(),
                stringMatcher.end()
            )
        }

        // 5. Highlight Comments (overrides everything)
        val commentMatcher = commentPattern.matcher(code)
        while (commentMatcher.find()) {
            builder.addStyle(
                SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                commentMatcher.start(),
                commentMatcher.end()
            )
        }

        return builder.toAnnotatedString()
    }
}
