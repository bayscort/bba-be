package com.project.bbapalmchain.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetadataTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-foto.username}")
    private String botUsername;

    @Value("${telegram.bot-foto.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("📩 Update diterima dari Telegram: {}", update);

        try {
            if (update.hasMessage()) {
                Long chatId = update.getMessage().getChatId();
                String fileId = null;

                // KASUS 1: Foto dikirim sebagai gambar (metadata hilang)
                if (update.getMessage().hasPhoto()) {
                    log.info("➡️ Pesan tipe FOTO diterima dari chatId: {}", chatId);
                    PhotoSize photo = update.getMessage().getPhoto()
                            .stream()
                            .max(Comparator.comparing(PhotoSize::getFileSize))
                            .orElse(null);
                    if (photo != null) {
                        fileId = photo.getFileId();
                    }
                }
                // KASUS 2: Foto dikirim sebagai file (metadata ADA)
                else if (update.getMessage().hasDocument()) {
                    log.info("➡️ Pesan tipe DOKUMEN diterima dari chatId: {}", chatId);
                    // Pastikan dokumen adalah gambar
                    String mimeType = update.getMessage().getDocument().getMimeType();
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        fileId = update.getMessage().getDocument().getFileId();
                    } else {
                        log.warn("⚠️ Dokumen bukan gambar, diabaikan. MimeType: {}", mimeType);
                        // Opsional: kirim pesan balasan ke user
                        execute(SendMessage.builder()
                                .chatId(chatId.toString())
                                .text("Harap kirim file gambar (JPG, PNG, dll).")
                                .build());
                    }
                }

                // Jika ada fileId yang valid, proses file tersebut
                if (fileId != null) {
                    processImageFile(fileId, chatId);
                } else {
                    log.info("ℹ️ Update tidak berisi foto atau dokumen gambar yang valid, diabaikan.");
                }
            }
        } catch (Exception e) {
            log.error("❌ Error saat memproses update", e);
        }
    }

    /**
     * Method terpisah untuk download dan ekstrak metadata
     * @param fileId ID file di server Telegram
     * @param chatId ID chat untuk mengirim balasan
     */
    private void processImageFile(String fileId, Long chatId) {
        // Memberi nama file yang unik untuk menghindari konflik
        File localFile = new File("downloaded_" + fileId + ".jpg");
        try {
            log.info("📷 FileId yang akan diproses: {}", fileId);

            String filePath = execute(new GetFile(fileId)).getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
            log.info("🌐 Mendownload dari URL: {}", fileUrl);

            // --- INI BAGIAN YANG DIPERBAIKI ---
            // Mengunduh file dengan menyalin byte stream untuk menjaga keutuhan metadata
            try (InputStream in = new URL(fileUrl).openStream()) {
                Files.copy(in, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("✅ File berhasil diunduh (stream copy) ke: {}", localFile.getAbsolutePath());
            }
            // --- AKHIR BAGIAN YANG DIPERBAIKI ---

            // Baca metadata EXIF dari file yang sudah diunduh utuh
            Metadata metadata = ImageMetadataReader.readMetadata(localFile);
            log.info("🔍 Metadata berhasil dibaca dari file.");

            StringBuilder msg = new StringBuilder("📸 Metadata Foto:\n");

            // Timestamp
            ExifSubIFDDirectory exifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDir != null && exifDir.getDateOriginal() != null) {
                Date date = exifDir.getDateOriginal();
                msg.append("📅 Waktu Pengambilan: ").append(date).append("\n");
                log.info("📅 Timestamp ditemukan: {}", date);
            } else {
                log.info("⚠️ Tidak ada timestamp di metadata.");
            }

            // GPS
            GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDir != null && gpsDir.getGeoLocation() != null) {
                GeoLocation geo = gpsDir.getGeoLocation();
                msg.append("📍 Lokasi (GPS): ")
                        .append(geo.getLatitude()).append(", ")
                        .append(geo.getLongitude()).append("\n");
                log.info("📍 GPS ditemukan: {}, {}", geo.getLatitude(), geo.getLongitude());
            } else {
                log.info("⚠️ Tidak ada GPS di metadata.");
            }

            if (msg.toString().equals("📸 Metadata Foto:\n")) {
                msg.append("Tidak ada metadata GPS atau Waktu Pengambilan yang ditemukan.");
            }

            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(msg.toString())
                    .build());
            log.info("✅ Pesan balasan berhasil dikirim ke user.");

        } catch (Exception e) {
            log.error("❌ Error saat memproses file dengan ID: {}", fileId, e);
        } finally {
            if (localFile.exists()) {
                boolean deleted = localFile.delete();
                log.info("🗑️ File lokal '{}' dihapus: {}", localFile.getName(), deleted);
            }
        }
    }
}
