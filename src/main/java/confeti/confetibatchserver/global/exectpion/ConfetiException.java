package confeti.confetibatchserver.global.exectpion;

import confeti.confetibatchserver.global.message.ErrorMessage;

public class ConfetiException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public ConfetiException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}

