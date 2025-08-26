package tech.build.jbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class DeleteWalletException extends JbankException {

    private final String detail;

    public DeleteWalletException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pd.setTitle("You connot delete the wallet");
        pd.setDetail(detail);

        return pd;
    }
}
