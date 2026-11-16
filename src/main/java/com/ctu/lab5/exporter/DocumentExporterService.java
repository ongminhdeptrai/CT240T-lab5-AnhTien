package com.ctu.lab5.exporter;
import java.util.ArrayList;
import java.util.List;



// 1. Factory Pattern Interface & Concrete Products
interface DocumentReader {
    String readContent(String filePath);
}

class PDFReader implements DocumentReader {
    @Override
    public String readContent(String filePath) {
        return "Content from PDF file: " + filePath;
    }
}

class DocxReader implements DocumentReader {
    @Override
    public String readContent(String filePath) {
        return "Content from DOCX file: " + filePath;
    }
}

class DocumentReaderFactory {
    public static DocumentReader getReader(String fileType) {
        if (fileType == null) throw new IllegalArgumentException("File type null");
        return switch (fileType.toUpperCase()) {
            case "PDF" -> new PDFReader();
            case "DOCX" -> new DocxReader();
            case "CSV"  -> new CsvReader();   // <-- thêm dòng này
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
        };
    }
}

// 2. Strategy Pattern Interface & Concrete Strategies
interface CompressionStrategy {
    byte[] compress(String rawData);
}

class ZipCompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(String rawData) {
        System.out.println("Nén dữ liệu theo chuẩn ZIP...");
        return ("ZIP[" + rawData + "]").getBytes();
    }
}

class RarCompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(String rawData) {
        System.out.println("Nén dữ liệu theo chuẩn RAR...");
        return ("RAR[" + rawData + "]").getBytes();
    }
}

// 3. Observer Pattern Interface & Concrete Observers
interface ExportObserver {
    void onExportSuccess(String fileName, int compressedSize);
}

class AuditLogObserver implements ExportObserver {
    @Override
    public void onExportSuccess(String fileName, int compressedSize) {
        System.out.println("[Audit Log] Đã ghi nhật ký xuất file: " + fileName + " (Kích thước: " + compressedSize + " bytes)");
    }
}

class ConsoleNotificationObserver implements ExportObserver {
    @Override
    public void onExportSuccess(String fileName, int compressedSize) {
        System.out.println("[Thông báo] Xuất báo cáo thành công file " + fileName);
    }
}

// 4. Main Service combining Factory, Strategy, Observer
public class DocumentExporterService {
    private CompressionStrategy compressionStrategy;
    private final List<ExportObserver> observers = new ArrayList<>();

    public void setCompressionStrategy(CompressionStrategy strategy) {
        this.compressionStrategy = strategy;
    }
    public void attachObserver(ExportObserver observer) {
        observers.add(observer);
    }

    public byte[] exportDocument(String filePath, String fileType) {
        if (compressionStrategy == null) {
            throw new IllegalStateException("Chưa thiết lập thuật toán nén!");
        }
        // 1. Dùng Factory Method đọc nội dung
        DocumentReader reader = DocumentReaderFactory.getReader(fileType);
        String rawContent = reader.readContent(filePath);
        // 2. Dùng Strategy Pattern nén dữ liệu
        byte[] compressedData = compressionStrategy.compress(rawContent);
        // 3. Dùng Observer Pattern phát sự kiện
        for (ExportObserver obs : observers) {
            obs.onExportSuccess(filePath, compressedData.length);
        }
        return compressedData;
    }
    // Runner kiểm thử hệ thống
    public static void main(String[] args) {
        DocumentExporterService exporter = new DocumentExporterService();
        exporter.attachObserver(new AuditLogObserver());
        exporter.attachObserver(new ConsoleNotificationObserver());
        exporter.setCompressionStrategy(new ZipCompressionStrategy());
        System.out.println("Đang tiến hành xuất file PDF...");
        exporter.exportDocument("BaoCaoTaiChinh2026.pdf", "PDF");
        // Thử định dạng CSV với thuật toán TAR
        exporter.setCompressionStrategy(new TarCompressionStrategy());
        System.out.println("Đang tiến hành xuất file CSV...");
        exporter.exportDocument("DanhSachSinhVien.csv", "CSV");

    }
}

