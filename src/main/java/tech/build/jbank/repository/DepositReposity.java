package tech.build.jbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.build.jbank.entities.Deposit;

import java.util.UUID;

public interface DepositReposity extends JpaRepository<Deposit, UUID> {
}
