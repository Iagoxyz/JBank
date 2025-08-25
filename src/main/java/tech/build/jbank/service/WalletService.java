package tech.build.jbank.service;

import org.springframework.stereotype.Service;
import tech.build.jbank.controller.dto.CreateWalletDto;
import tech.build.jbank.entities.Wallet;
import tech.build.jbank.exception.WalletDataAlreadyExistsException;
import tech.build.jbank.repository.WalletRepository;

import java.math.BigDecimal;

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
}
