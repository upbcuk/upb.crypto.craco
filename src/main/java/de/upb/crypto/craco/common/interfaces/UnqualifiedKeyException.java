package de.upb.crypto.craco.common.interfaces;

/**
 * Exception to signal that a given secret key is not qualified to decrypt a given ciphertext.
 *
 *
 */
public class UnqualifiedKeyException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public UnqualifiedKeyException() {
        super();
    }

    public UnqualifiedKeyException(String e) {
        super(e);
    }
}
