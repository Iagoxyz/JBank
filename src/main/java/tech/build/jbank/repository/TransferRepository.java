package tech.build.jbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.build.jbank.entities.Transfer;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
