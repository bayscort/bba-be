package com.project.bbapalmchain.service;

import com.project.bbapalmchain.dto.FundRequestApprovalLogReqDTO;
import com.project.bbapalmchain.enums.ApprovalStage;
import com.project.bbapalmchain.model.*;
import com.project.bbapalmchain.repository.FundRequestApprovalLogRepository;
import com.project.bbapalmchain.repository.FundRequestRepository;
import com.project.bbapalmchain.repository.UserRepository;
import com.project.bbapalmchain.util.ForbiddenException;
import com.project.bbapalmchain.util.NotFoundException;
import com.project.bbapalmchain.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FundRequestApprovalLogService {

    private final FundRequestApprovalLogRepository fundRequestApprovalLogRepository;
    private final FundRequestRepository fundRequestRepository;
    private final UserRepository userRepository;

    private final UserContext userContext;
    private final TelegramService telegramService;

    public void create(FundRequestApprovalLogReqDTO dto) {

        Optional<FundRequest> fundRequestOpt = fundRequestRepository.findById(dto.getFundRequestId());
        if (fundRequestOpt.isEmpty()) {
            throw new NotFoundException("Fund Request not found");
        }

        String message = buildFundRequestMessage(fundRequestOpt.get());

        if (dto.getApprovalStage().equals(ApprovalStage.SUBMITTED_BY_ADMIN_OPS)) {

            List<User> usersNextApproval = userRepository.findByRoleId(fundRequestOpt.get().getNextApprovalRoleId());
            // send notification telegram to next approval user
            // send to staff ops role
            usersNextApproval.forEach(user -> {
                if (!Objects.isNull(user.getChatId())) {
                    telegramService.sendMessage(message, user.getChatId());
                }
            });
        }

        if (dto.getApprovalStage().equals(ApprovalStage.ACKNOWLEDGED_BY_STAFF_OPS)) {
            if (!userContext.getCurrentUser().getRole().getId().equals(fundRequestOpt.get().getNextApprovalRoleId())) {
                throw new ForbiddenException("You are not authorized to perform this action at the ACKNOWLEDGED_BY_STAFF_OPS stage.");
            } else {
                fundRequestOpt.get().setNextApprovalRoleId(9L);
                fundRequestRepository.save(fundRequestOpt.get());

                // send to manager ops role
                List<User> usersNextApproval = userRepository.findByRoleId(fundRequestOpt.get().getNextApprovalRoleId());
                usersNextApproval.forEach(user -> {
                    if (!Objects.isNull(user.getChatId())) {
                        telegramService.sendMessage(message, user.getChatId());
                    }
                });
            }
        }

        if (dto.getApprovalStage().equals(ApprovalStage.APPROVED_BY_MANAGER_OPS)) {
            if (!userContext.getCurrentUser().getRole().getId().equals(fundRequestOpt.get().getNextApprovalRoleId())) {
                throw new ForbiddenException("You are not authorized to perform this action at the APPROVED_BY_MANAGER_OPS stage.");
            } else {
                fundRequestOpt.get().setNextApprovalRoleId(8L);
                fundRequestRepository.save(fundRequestOpt.get());

                // send to finance role
                List<User> usersNextApproval = userRepository.findByRoleId(fundRequestOpt.get().getNextApprovalRoleId());
                usersNextApproval.forEach(user -> {
                    if (!Objects.isNull(user.getChatId())) {
                        telegramService.sendMessage(message, user.getChatId());
                    }
                });
            }
        }

        if (dto.getApprovalStage().equals(ApprovalStage.REVIEWED_BY_FINANCE)) {
            if (!userContext.getCurrentUser().getRole().getId().equals(fundRequestOpt.get().getNextApprovalRoleId())) {
                throw new ForbiddenException("You are not authorized to perform this action at the REVIEWED_BY_FINANCE stage.");
            } else {
                fundRequestOpt.get().setNextApprovalRoleId(3L);
                fundRequestRepository.save(fundRequestOpt.get());

                // send to manager role
                List<User> usersNextApproval = userRepository.findByRoleId(fundRequestOpt.get().getNextApprovalRoleId());
                usersNextApproval.forEach(user -> {
                    if (!Objects.isNull(user.getChatId())) {
                        telegramService.sendMessage(message, user.getChatId());
                    }
                });
            }
        }

        if (dto.getApprovalStage().equals(ApprovalStage.APPROVED_BY_MANAGER_FIN)) {
            if (!userContext.getCurrentUser().getRole().getId().equals(fundRequestOpt.get().getNextApprovalRoleId())) {
                throw new ForbiddenException("You are not authorized to perform this action at the APPROVED_BY_MANAGER stage.");
            } else {
                fundRequestOpt.get().setNextApprovalRoleId(10L);
                fundRequestRepository.save(fundRequestOpt.get());

                // send to director role
                List<User> usersNextApproval = userRepository.findByRoleId(fundRequestOpt.get().getNextApprovalRoleId());
                usersNextApproval.forEach(user -> {
                    if (!Objects.isNull(user.getChatId())) {
                        telegramService.sendMessage(message, user.getChatId());
                    }
                });
            }
        }

        if (dto.getApprovalStage().equals(ApprovalStage.APPROVED_BY_DIRECTOR)) {
            if (!userContext.getCurrentUser().getRole().getId().equals(fundRequestOpt.get().getNextApprovalRoleId())) {
                throw new ForbiddenException("You are not authorized to perform this action at the APPROVED_BY_DIRECTOR stage.");
            } else {
                fundRequestOpt.get().setNextApprovalRoleId(null);
                fundRequestRepository.save(fundRequestOpt.get());

                // send to all STAFF, FINANCE, MANAGER OPS, MANAGER FIN
                String messageApprovedByDirector = buildFundRequestApproveDirectorMessage(fundRequestOpt.get(), userContext.getUsername());

                List<Long> roleIds = Arrays.asList(2L, 8L, 3L, 9L);
                List<User> users = userRepository.findByRoleIdIn(roleIds);
                users.forEach(user -> {
                    if (!Objects.isNull(user.getChatId())) {
                        telegramService.sendMessage(messageApprovedByDirector, user.getChatId());
                    }
                });

            }
        }

        FundRequestApprovalLog log = new FundRequestApprovalLog();
        log.setFundRequest(fundRequestOpt.get());
        log.setApprovalStage(dto.getApprovalStage());
        log.setStageTimestamp(LocalDateTime.now());
        log.setNotes(dto.getNotes());

        fundRequestOpt.get().getFundRequestApprovalLogList().add(log);
        fundRequestApprovalLogRepository.save(log);
    }

    public String buildFundRequestMessage(FundRequest fundRequest) {
        StringBuilder message = new StringBuilder();

        // Format tanggal
        LocalDate tanggal = fundRequest.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

        message.append("Permintaan Dana ").append(fundRequest.getFundRequestCode()).append("\n")
                .append("Tanggal: ").append(tanggal.format(formatter)).append("\n")
                .append("Total: ").append(formatRupiah(fundRequest.getTotalAmount())).append("\n\n")
                .append("Rincian Item:\n");

        for (FundRequestItem item : fundRequest.getFundRequestItemList()) {
            String financeItemName = item.getFinanceItem().getName();
            String amount = formatRupiah(item.getAmount());
            message.append("- ").append(financeItemName).append(": ").append(amount).append("\n");
        }

        return message.toString();
    }

    public String buildFundRequestApproveDirectorMessage(FundRequest fundRequest, String directorName) {
        StringBuilder message = new StringBuilder();

        // Format tanggal
        LocalDate tanggal = fundRequest.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

        message.append("✅ *Permintaan Dana telah DISETUJUI oleh Director*\n\n")
                .append("*Kode:* ").append(fundRequest.getFundRequestCode()).append("\n")
                .append("*Tanggal:* ").append(tanggal.format(formatter)).append("\n")
                .append("*Total:* ").append(formatRupiah(fundRequest.getTotalAmount())).append("\n")
                .append("*Disetujui oleh:* ").append(directorName).append("\n\n")
                .append("*Rincian Item:*\n");

        for (FundRequestItem item : fundRequest.getFundRequestItemList()) {
            String financeItemName = item.getFinanceItem().getName();
            String amount = formatRupiah(item.getAmount());
            message.append("• ").append(financeItemName).append(": ").append(amount).append("\n");
        }

        return message.toString();
    }


    private String formatRupiah(BigDecimal amount) {
        if (amount == null) return "Rp0";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

}
