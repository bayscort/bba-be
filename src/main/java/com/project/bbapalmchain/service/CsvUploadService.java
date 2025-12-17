package com.project.bbapalmchain.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.project.bbapalmchain.model.Account;
import com.project.bbapalmchain.model.BankStatement;
import com.project.bbapalmchain.repository.AccountRepository;
import com.project.bbapalmchain.repository.BankStatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CsvUploadService {

    private final BankStatementRepository bankStatementRepository;
    private final AccountRepository accountRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss", Locale.ENGLISH);

    public void processCsvFile(MultipartFile file) throws IOException, CsvValidationException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            // Lewati 1 baris header pertama
            csvReader.skip(1);

            String[] line;
            List<BankStatement> statements = new ArrayList<>();

            while ((line = csvReader.readNext()) != null) {
                // CSV menggunakan semicolon (;) sebagai pemisah
                String[] values = line[0].split(";");

                // Pastikan jumlah kolom sesuai
                if (values.length < 8) continue;

                // 1. Cari Akun berdasarkan nomor rekening dari CSV
                String accountNumber = values[0];
                Account account = accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new RuntimeException("Account not found for number: " + accountNumber));

                // 2. Buat objek BankStatement
                BankStatement statement = new BankStatement();
                statement.setAccount(account);
                statement.setCurrency(values[1]);
                statement.setPostDate(LocalDateTime.parse(values[2], DATE_TIME_FORMATTER));
                statement.setRemarks(values[3].trim().replaceAll("\\s+", " "));
                statement.setAdditionalDesc(values[4].trim().replaceAll("\\s+", " "));
                statement.setCreditAmount(new BigDecimal(values[5]));
                statement.setDebitAmount(new BigDecimal(values[6]));
                statement.setClosingBalance(new BigDecimal(values[7]));

                statements.add(statement);
            }

            // 3. Simpan semua data ke database dalam satu transaksi
            bankStatementRepository.saveAll(statements);
        }
    }
}
