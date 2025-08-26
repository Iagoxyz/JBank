package tech.build.jbank.service;

import org.springframework.stereotype.Service;
import tech.build.jbank.controller.dto.CreateWalletDto;
import tech.build.jbank.entities.Wallet;
import tech.build.jbank.exception.DeleteWalletException;
import tech.build.jbank.exception.WalletDataAlreadyExistsException;
import tech.build.jbank.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
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
}
