package tech.build.jbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.build.jbank.entities.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByCpfOrEmail(String cpf, String email);
}
