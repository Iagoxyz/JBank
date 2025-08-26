package tech.build.jbank.exception.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import tech.build.jbank.exception.JbankException;

public class WalletNotFoundException extends JbankException {

    private final String detail;

    public WalletNotFoundException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        pd.setTitle("Wallet not found");
        pd.setDetail(detail);

        return pd;
    }
}
