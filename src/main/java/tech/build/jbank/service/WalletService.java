package tech.build.jbank.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.build.jbank.controller.dto.*;
import tech.build.jbank.entities.Deposit;
import tech.build.jbank.entities.Wallet;
import tech.build.jbank.exception.DeleteWalletException;
import tech.build.jbank.exception.StatementException;
import tech.build.jbank.exception.WalletDataAlreadyExistsException;
import tech.build.jbank.exception.WalletNotFoundException;
import tech.build.jbank.repository.DepositReposity;
import tech.build.jbank.repository.WalletRepository;
import tech.build.jbank.repository.dto.StatementView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final DepositReposity depositReposity;

    public WalletService(WalletRepository walletRepository, DepositReposity depositReposity) {
        this.walletRepository = walletRepository;
        this.depositReposity = depositReposity;
    }

    public Wallet createWallet(CreateWalletDto dto) {

        var wallet =walletRepository.findByCpfOrEmail(dto.cpf(), dto.email());
        if (wallet.isPresent()) {
            throw new WalletDataAlreadyExistsException("cpf or email already exists");
        }

        var newWallet = new Wallet();
        newWallet.setCpf(dto.cpf());
        newWallet.setName(dto.name());
        newWallet.setEmail(dto.email());
        newWallet.setBalance(BigDecimal.ZERO);

        return walletRepository.save(newWallet);
    }

    public boolean deleteWallet(UUID walletId) {

        var wallet = walletRepository.findById(walletId);

        if (wallet.isPresent()) {

            if (wallet.get().getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new DeleteWalletException("the balance is not zero. The current amount is $" + wallet.get().getBalance());
            }

            walletRepository.deleteById(walletId);
        }

        return wallet.isPresent();
    }

    @Transactional
    public void depositMoney(UUID walletId,
                             DepositMoneyDto dto,
                             String ipAddress) {

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("there is no wallet with this id"));

        var deposit = new Deposit();
        deposit.setWallet(wallet);
        deposit.setDepositValue(dto.value());
        deposit.setDepositDateTime(LocalDateTime.now());
        deposit.setIpAddress(ipAddress);

        depositReposity.save(deposit);

        wallet.setBalance(wallet.getBalance().add(dto.value()));
        walletRepository.save(wallet);
    }

    public StatementDto getStatements(UUID walletId, Integer page, Integer pageSize) {

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("there is no wallet with this id"));

        var pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "statement_date_time");

        var statements = walletRepository.findStatements(walletId.toString(), pageRequest)
                .map(view -> mapToDto(walletId, view));

        return new StatementDto(
                new WalletDto(wallet.getWalletId(), wallet.getCpf(), wallet.getName(), wallet.getEmail(), wallet.getBalance()),
                statements.getContent(),
                new PaginationDto(statements.getNumber(), statements.getSize(), statements.getTotalElements(), statements.getTotalPages())
        );
    }

    private StatementItemDto mapToDto(UUID walletId, StatementView view) {

        if (view.getType().equalsIgnoreCase("deposit")) {
            return mapToDeposit(view);
        }

        if (view.getType().equalsIgnoreCase("transfer")
             && view.getWalletSender().equalsIgnoreCase(walletId.toString())) {

             return mapWhenTransferReceivedSent(walletId, view);
        }

        if (view.getType().equalsIgnoreCase("transfer")
            && view.getWalletReceiver().equalsIgnoreCase(walletId.toString())) {

            return mapWhenTransferReceived(walletId, view);
        }

        throw new StatementException("invalid type " + view.getType());
    }

    private StatementItemDto mapWhenTransferReceived(UUID walletId, StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money received from" + view.getWalletSender(),
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.CREDIT
        );
    }

    private StatementItemDto mapWhenTransferReceivedSent(UUID walletId, StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money sent to" + view.getWalletReceiver(),
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.DEBIT
        );
    }

    private static StatementItemDto mapToDeposit(StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money deposit",
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.CREDIT

        );
    }
}
