package tech.build.jbank.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import tech.build.jbank.controller.dto.CreateWalletDto;
import tech.build.jbank.controller.dto.DepositMoneyDto;
import tech.build.jbank.entities.Deposit;
import tech.build.jbank.entities.Wallet;
import tech.build.jbank.exception.DeleteWalletException;
import tech.build.jbank.exception.WalletDataAlreadyExistsException;
import tech.build.jbank.exception.dto.WalletNotFoundException;
import tech.build.jbank.repository.DepositReposity;
import tech.build.jbank.repository.WalletRepository;

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
}
