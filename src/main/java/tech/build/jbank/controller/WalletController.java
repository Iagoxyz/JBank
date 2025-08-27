package tech.build.jbank.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.build.jbank.controller.dto.CreateWalletDto;
import tech.build.jbank.controller.dto.DepositMoneyDto;
import tech.build.jbank.controller.dto.StatementDto;
import tech.build.jbank.exception.WalletDataAlreadyExistsException;
import tech.build.jbank.service.WalletService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Void> createWallet(@RequestBody @Valid CreateWalletDto dto) {

        var wallet = walletService.createWallet(dto);
        return ResponseEntity.created(URI.create("/wallets/" + wallet.getWalletId().toString())).build();
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable("walletId") UUID walletId) {

        var deleted = walletService.deleteWallet(walletId);

        return deleted ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @PostMapping("/{walletId}/deposits")
    public ResponseEntity<Void> depositMoney(@PathVariable("walletId") UUID walletId,
                                             @RequestBody @Valid DepositMoneyDto dto,
                                             HttpServletRequest servletRequest) {

       walletService.depositMoney(walletId, dto, servletRequest.getAttribute("x-user-ip").toString());
       return ResponseEntity.ok().build();
    }

    @GetMapping("/{walletId}/statements")
    public ResponseEntity<StatementDto> getStatements(@PathVariable UUID walletId,
                                                      @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        var statement = walletService.getStatements(walletId, page, pageSize);

        return ResponseEntity.ok(statement);
    }

}
