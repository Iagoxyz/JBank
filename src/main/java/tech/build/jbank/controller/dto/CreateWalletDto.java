package tech.build.jbank.controller.dto;

import java.math.BigDecimal;

public record CreateWalletDto(String cpf,
                              String email,
                              String name) {
}
