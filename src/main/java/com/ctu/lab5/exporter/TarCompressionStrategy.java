package com.ctu.lab5.exporter;

/** Concrete Strategy mới: nén theo chuẩn TAR. */
class TarCompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(String rawData) {
        System.out.println("Nén dữ liệu theo chuẩn TAR...");
        return ("TAR[" + rawData + "]").getBytes();
    }
}

