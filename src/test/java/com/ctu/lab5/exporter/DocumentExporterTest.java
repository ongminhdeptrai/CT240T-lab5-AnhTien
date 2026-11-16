package com.ctu.lab5.exporter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentExporterTest {

    private DocumentExporterService exporter;

    @BeforeEach
    void setUp() {
        exporter = new DocumentExporterService();
    }

    @Test
    @DisplayName("Chưa set Strategy nén -> ném IllegalStateException")
    void exportWithoutStrategy_throwsIllegalStateException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> exporter.exportDocument("report.pdf", "PDF"));
        assertEquals("Chưa thiết lập thuật toán nén!", ex.getMessage());
    }

    @Test
    @DisplayName("CSV + TAR hoạt động mà không sửa DocumentExporterService")
    void exportCsvWithTar_returnsTarWrappedContent() {
        exporter.setCompressionStrategy(new TarCompressionStrategy());
        byte[] result = exporter.exportDocument("data.csv", "CSV");
        assertEquals("TAR[Content from CSV file: data.csv]",
                new String(result, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Định dạng không hỗ trợ -> IllegalArgumentException")
    void factory_unsupportedType_throws() {
        exporter.setCompressionStrategy(new ZipCompressionStrategy());
        assertThrows(IllegalArgumentException.class,
                () -> exporter.exportDocument("img.png", "PNG"));

    }
    /** Observer giả: ghi lại các sự kiện nhận được để kiểm tra. */
    static class RecordingObserver implements ExportObserver {
        final List<String> files = new ArrayList<>();
        final List<Integer> sizes = new ArrayList<>();
        @Override
        public void onExportSuccess(String fileName, int compressedSize) {
            files.add(fileName);
            sizes.add(compressedSize);
        }
    }

    @Test
    @DisplayName("Ngoại lệ xảy ra thì không observer nào được thông báo")
    void exportWithoutStrategy_doesNotNotifyObservers() {
        RecordingObserver spy = new RecordingObserver();
        exporter.attachObserver(spy);
        assertThrows(IllegalStateException.class,
                () -> exporter.exportDocument("report.pdf", "PDF"));
        assertTrue(spy.files.isEmpty());
    }

    @Test
    @DisplayName("Observer nhận đúng 1 sự kiện với kích thước chính xác")
    void export_notifiesObserverOnce() {
        RecordingObserver spy = new RecordingObserver();
        exporter.attachObserver(spy);
        exporter.setCompressionStrategy(new ZipCompressionStrategy());

        byte[] result = exporter.exportDocument("BaoCao.pdf", "PDF");

        assertEquals(List.of("BaoCao.pdf"), spy.files);
        assertEquals(List.of(result.length), spy.sizes);
    }
}
