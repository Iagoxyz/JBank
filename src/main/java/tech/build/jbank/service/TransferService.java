package tech.build.jbank.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import tech.build.jbank.controller.dto.TransferMoneyDto;
import tech.build.jbank.entities.Transfer;
import tech.build.jbank.exception.TransferException;
import tech.build.jbank.exception.WalletNotFoundException;
import tech.build.jbank.repository.TransferRepository;
import tech.build.jbank.repository.WalletRepository;

import java.time.LocalDateTime;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    public TransferService(TransferRepository transferRepository, WalletRepository walletRepository) {
        this.transferRepository = transferRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void transferMoney(TransferMoneyDto dto) {

        var sender = walletRepository.findById(dto.sender())
                .orElseThrow(() -> new WalletNotFoundException("sender does not exist"));

        var receiver = walletRepository.findById(dto.receiver())
                .orElseThrow(() -> new WalletNotFoundException("receiver does not exist"));

        if (sender.getBalance().compareTo(dto.value()) == -1) {
            throw new TransferException(
                    "insufficient balance. you current balance is $" + sender.getBalance());
        }

        var transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setReceiver(receiver);
        transfer.setTransferValue(dto.value());
        transfer.setTransferDateTime(LocalDateTime.now());
        transferRepository.save(transfer);

        sender.setBalance(sender.getBalance().subtract(dto.value()));
        receiver.setBalance(receiver.getBalance().add(dto.value()));

        walletRepository.save(sender);
        walletRepository.save(receiver);

    }
}
