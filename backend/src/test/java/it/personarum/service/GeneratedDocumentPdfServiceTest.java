package it.personarum.service;

import it.personarum.domain.generation.GeneratedDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeneratedDocumentPdfServiceTest {

    private final GeneratedDocumentPdfService service = new GeneratedDocumentPdfService();

    @Test
    void shouldGeneratePdfFromDocumentContent() throws IOException {
        GeneratedDocument document = mock(GeneratedDocument.class);

        when(document.getContent()).thenReturn("""
            DICHIARAZIONE
            Il sottoscritto Mario Rossi
            dichiara quanto dichiarato :)
            """);

        byte[] pdf = service.generate(document);
        assertThat(pdf).isNotEmpty();

        try (PDDocument loadedDocument = Loader.loadPDF(pdf)) {
            String text = new PDFTextStripper().getText(loadedDocument);
            assertThat(text).contains("DICHIARAZIONE").contains("Mario Rossi").contains("dichiara quanto sopra.");
        }
    }
}
