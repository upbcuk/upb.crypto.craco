package org.cryptimeleon.craco.sig;

import org.cryptimeleon.craco.common.plaintexts.MessageBlock;
import org.cryptimeleon.craco.common.plaintexts.PlainText;
import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.serialization.StandaloneRepresentable;
import org.cryptimeleon.math.serialization.annotations.RepresentationRestorer;

import java.lang.reflect.Type;

/**
 * A {@code SignatureScheme} has the ability to sign plaintexts
 * and verify the resulting signature (using {@link SigningKey}s and {@link VerificationKey}s).
 * <p>
 * The functional contract is that {@code verify(sign(m, sk), pk) == true} for sk, pk that
 * "fit" together (depending on the concrete type of signature scheme).
 * <p>
 * Sub-Interfaces define how you obtain signing and verification keys.
 * <p>
 * {@code SignatureScheme} instances implement {@link StandaloneRepresentable}.
 * So once set up, you can restore the same scheme with the same public parameters
 * using the {@code Representation} mechanism (i.e. call the class's constructor
 * with a {@code Representation} argument).
 * <p>
 * We note that we see all signature schemes as single-message schemes.
 * However, some signature schemes may be able to sign {@link MessageBlock}s
 * (cf., for example, {@link StandardMultiMessageSignatureScheme}).
 *
 *
 */
public interface SignatureScheme extends StandaloneRepresentable, RepresentationRestorer {
    /**
     * Signs the giving plaintext using the given signing key. The signing key should contain all information
     * necessary to sign, therefore public key is not needed.
     *
     * @param plainText the message to sign
     * @param secretKey the secret signing key
     * @return signature on {@code plainText} computed using {@code secretKey}
     */
    Signature sign(PlainText plainText, SigningKey secretKey);

    /**
     * Verifies the given signature for the given plaintext using the given verification key.
     *
     * @param plainText the plaintext the signature should validate against
     * @param signature the signature to verify
     * @param publicKey the verification key to verify with
     * @return true if verification succeeds, false else
     */
    Boolean verify(PlainText plainText, Signature signature, VerificationKey publicKey);


    PlainText restorePlainText(Representation repr);

    Signature restoreSignature(Representation repr);

    SigningKey restoreSigningKey(Representation repr);

    VerificationKey restoreVerificationKey(Representation repr);

    default Signature sign(Representation plainText, Representation secretKey) {
        return sign(restorePlainText(plainText), restoreSigningKey(secretKey));
    }

    default Boolean verify(Representation plainText, Representation signature, Representation publicKey) {
        return verify(restorePlainText(plainText), restoreSignature(signature), restoreVerificationKey(publicKey));
    }

    /**
     * Provides an injective mapping of the given bytes to a {@link PlainText} usable with this scheme (which may be a
     * {@link MessageBlock}).
     * <p>
     * It only guarantees injectivity for arrays of the same length.
     * Applications that would like to use {@code mapToPlaintext} with multiple different array lengths
     * may want to devise a padding method and then only call {@code mapToPlaintext} with
     * byte arrays of the same (padded) length.
     * <p>
     * The contract is that {@link VerificationKey} pk and {@link SigningKey} sk are compatible
     * (in the sense that {@code verify(m,sign(m, sk),pk) == true}),
     *  then {@code mapToPlaintext(bytes, pk))} equals {@code mapToPlaintext(bytes, sk)} for all bytes.
     *
     * @param bytes bytes to be mapped to a {@link PlainText}
     * @param pk    the verification key for which the resulting {@code PlainText} should be valid
     *              (note that the plaintext space may differ for different verification keys).
     * @return the corresponding plaintext
     * @throws IllegalArgumentException if there is no injective {@link PlainText} element of
     *         these bytes (e.g., the byte array is too long)
     */
    PlainText mapToPlaintext(byte[] bytes, VerificationKey pk);

    /**
     * Provides an injective mapping of the given bytes to a {@link PlainText} usable with this scheme (which may be a
     * {@link MessageBlock}).
     * <p>
     * It only guarantees injectivity for arrays of the same length.
     * Applications that would like to use {@code mapToPlaintext} with multiple different array lengths
     * may want to devise a padding method and then only call {@code mapToPlaintext} with
     * byte arrays of the same (padded) length.
     * <p>
     * The contract is that {@link VerificationKey} pk and {@link SigningKey} sk are compatible
     * (in the sense that {@code verify(m,sign(m, sk),pk) == true}),
     *  then {@code mapToPlaintext(bytes, pk))} equals {@code mapToPlaintext(bytes, sk)} for all bytes.
     *
     * @param bytes bytes to be mapped to a {@link PlainText}
     * @param sk    the signing key for which the resulting {@code PlainText} should be valid
     *              (note that the plaintext space may differ for different signing keys).
     * @return the corresponding plaintext
     * @throws IllegalArgumentException if there is no injective {@link PlainText} element of
     *         these bytes (e.g., the byte array is too long)
     */
    PlainText mapToPlaintext(byte[] bytes, SigningKey sk);

    /**
     * Returns the maximal number of bytes that can be mapped injectively to a {@link PlainText} by
     * {@link #mapToPlaintext(byte[], SigningKey)} and {@link #mapToPlaintext(byte[], VerificationKey)}.
     * <p>
     * As described in {@link #mapToPlaintext} there might be no injective {@link PlainText} for some byte arrays, e.g.
     * if the byte array is too long. Therefore, this method provides the maximal number of bytes that can be mapped
     * injectively to a {@link PlainText}.
     *
     * @return maximal number of bytes that can be given to {@link #mapToPlaintext}.
     */
    int getMaxNumberOfBytesForMapToPlaintext();


    default Object restoreFromRepresentation(Type type, Representation repr) {
        if (type instanceof Class) {
            if (SigningKey.class.isAssignableFrom((Class) type)) {
                return this.restoreSigningKey(repr);
            } else if (VerificationKey.class.isAssignableFrom((Class) type)) {
                return this.restoreVerificationKey(repr);
            } else if (Signature.class.isAssignableFrom((Class) type)) {
                return this.restoreSignature(repr);
            } else if (PlainText.class.isAssignableFrom((Class) type)) {
                return this.restorePlainText(repr);
            }
        }
        throw new IllegalArgumentException("Cannot restore object of type: " + type.getTypeName());
    }
}
