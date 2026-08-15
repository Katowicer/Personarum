package it.personarum.service;

import it.personarum.domain.generation.GeneratedDocument;
import it.personarum.service.exception.GeneratedDocumentPdfException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converte un documento generato nella corrispondente rappresentazione PDF.
 */
@Service
public class GeneratedDocumentPdfService {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 11;
    private static final float LINE_HEIGHT = 16;

    /**
     * Converte il contenuto del documento generato in un PDF A4, andando a capo quando necessario.
     *
     * @param document documento generato da esportare
     * @return contenuto binario del PDF
     * @throws GeneratedDocumentPdfException se PDFBox non riesce a produrre il documento
     */
    public byte[] generate(GeneratedDocument document) {
        try (PDDocument pdf = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            List<String> lines = wrapContent(document.getContent(), font);
            writePages(pdf, font, lines);
            pdf.save(output);

            return output.toByteArray();
        } catch (IOException | IllegalArgumentException exception) {
            throw new GeneratedDocumentPdfException(exception);
        }
    }

    private void writePages(PDDocument pdf, PDFont font, List<String> lines) throws IOException {
        int linesPerPage = (int) ((PDRectangle.A4.getHeight() - 2 * MARGIN) / LINE_HEIGHT);

        for (int start = 0; start < lines.size(); start += linesPerPage) {
            int end = Math.min(start + linesPerPage, lines.size());
            writePage(pdf, font, lines.subList(start, end));
        }

        if (lines.isEmpty()) {
            writePage(pdf, font, List.of(""));
        }
    }

    private void writePage(PDDocument pdf, PDFont font, List<String> lines) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(pdf, page)) {
            contentStream.beginText();

            contentStream.setFont(font, FONT_SIZE);

            contentStream.setLeading(LINE_HEIGHT);

            contentStream.newLineAtOffset(MARGIN, PDRectangle.A4.getHeight() - MARGIN);

            for (String line : lines) {
                if (!line.isEmpty()) {
                    contentStream.showText(line);
                }
                contentStream.newLine();
            }

            contentStream.endText();
        }
    }

    private List<String> wrapContent(String content, PDFont font) throws IOException {
        List<String> lines = new ArrayList<>();

        float maxWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
        String normalizedContent = content.replace("\t", "    ");

        String[] paragraphs = normalizedContent.split("\\R", -1);

        for (String paragraph : paragraphs) {
            wrapParagraph(paragraph, font, maxWidth, lines);
        }

        return lines;
    }

    private void wrapParagraph(String paragraph, PDFont font, float maxWidth, List<String> lines) throws IOException {
        if (paragraph.isBlank()) {
            lines.add("");
            return;
        }

        StringBuilder currentLine = new StringBuilder();

        for (String word : paragraph.split("\\s+")) {
            String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;

            if (fits(candidate, font, maxWidth)) {
                currentLine.setLength(0);
                currentLine.append(candidate);
                continue;
            }

            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString());
            }

            currentLine.setLength(0);
            currentLine.append(word);
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
    }

    private boolean fits(String text, PDFont font, float maxWidth) throws IOException {
        float width = font.getStringWidth(text) / 1000 * FONT_SIZE;
        return width <= maxWidth;
    }
}
