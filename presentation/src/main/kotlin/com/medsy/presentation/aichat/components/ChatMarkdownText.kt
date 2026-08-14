package com.medsy.presentation.aichat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dependency-free renderer for the small Markdown subset the assistant
 * actually produces: `**bold**`, `- `/`* `/`• ` bullets, `1.` numbered lists
 * and `#`-style headings. Anything it doesn't recognize renders as literal
 * text — it must never crash or drop content.
 */
@Composable
fun ChatMarkdownText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> Text(
                    text = block.text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )

                is MarkdownBlock.Paragraph -> Text(
                    text = block.text,
                    style = style,
                    color = color,
                )

                is MarkdownBlock.ListItem -> Row {
                    Text(
                        text = block.marker,
                        style = style,
                        fontWeight = FontWeight.Bold,
                        color = color,
                    )
                    Text(
                        text = block.text,
                        style = style,
                        color = color,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private sealed interface MarkdownBlock {
    data class Heading(val text: AnnotatedString) : MarkdownBlock
    data class Paragraph(val text: AnnotatedString) : MarkdownBlock
    data class ListItem(val marker: String, val text: AnnotatedString) : MarkdownBlock
}

private val NUMBERED_REGEX = Regex("""^(\d{1,3})[.)]\s+(.*)$""")
private val BULLET_PREFIXES = listOf("- ", "* ", "• ")

private fun parseMarkdownBlocks(text: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    text.lines().forEach { rawLine ->
        val line = rawLine.trim()
        if (line.isEmpty()) return@forEach

        val bulletPrefix = BULLET_PREFIXES.firstOrNull(line::startsWith)
        val numberedMatch = NUMBERED_REGEX.find(line)
        when {
            line.startsWith("#") -> blocks += MarkdownBlock.Heading(
                boldAware(line.trimStart('#').trim()),
            )

            bulletPrefix != null -> blocks += MarkdownBlock.ListItem(
                marker = "•  ",
                text = boldAware(line.removePrefix(bulletPrefix).trim()),
            )

            numberedMatch != null -> blocks += MarkdownBlock.ListItem(
                marker = "${numberedMatch.groupValues[1]}.  ",
                text = boldAware(numberedMatch.groupValues[2]),
            )

            else -> blocks += MarkdownBlock.Paragraph(boldAware(line))
        }
    }
    return blocks
}

/** Applies `**bold**` spans; an unmatched `**` renders literally. */
private fun boldAware(text: String): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    while (cursor < text.length) {
        val open = text.indexOf("**", cursor)
        if (open < 0) {
            append(text.substring(cursor))
            break
        }
        val close = text.indexOf("**", open + 2)
        if (close < 0) {
            append(text.substring(cursor))
            break
        }
        append(text.substring(cursor, open))
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(text.substring(open + 2, close))
        pop()
        cursor = close + 2
    }
}
