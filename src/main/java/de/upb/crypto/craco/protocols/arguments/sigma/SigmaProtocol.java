package de.upb.crypto.craco.protocols.arguments.sigma;

import de.upb.crypto.craco.protocols.CommonInput;
import de.upb.crypto.craco.protocols.SecretInput;
import de.upb.crypto.craco.protocols.arguments.InteractiveArgument;
import de.upb.crypto.craco.protocols.arguments.InteractiveArgumentInstance;
import de.upb.crypto.craco.protocols.arguments.sigma.instance.SigmaProtocolInstance;
import de.upb.crypto.craco.protocols.arguments.sigma.instance.SigmaProtocolProverInstance;
import de.upb.crypto.craco.protocols.arguments.sigma.instance.SigmaProtocolVerifierInstance;
import de.upb.crypto.math.serialization.Representation;

import java.math.BigInteger;

/**
 * A three-message public coin interactive argument with the following properties.
 *
 * <ul>
 *     <li>Completeness: honestly generated transcripts are accepting.</li>
 *     <li>Honest-verifier zero-knowledge: trascripts generated by {@link SigmaProtocol#generateSimulatedTranscript(CommonInput, Challenge)} are distributed the same as honestly generated transcripts that contain the same {@link Challenge}.</li>
 *     <li>(Computational) soundness: Given {@link CommonInput} and two {@link SigmaProtocolTranscript}s with the same announcement but two different {@link Challenge}s, one can efficiently compute a witness (or at least it's computationally hard to generate two transcripts for which this is not possible).</li>
 *     <li>Public-coin: the verifier actively participates only by sending random values.</li>
 * </ul>
 *
 * A normal protocol run works as follows (see {@link SigmaProtocolInstance}):
 * <ol>
 *     <li>Before the protocol starts, prover and verifier have the same {@link CommonInput} (through whatever prior process). The prover has some {@link SecretInput} (the witness for the protocol)</li>
 *     <li>The prover generates an {@link AnnouncementSecret} (which will be their private input for the remainder of this process).</li>
 *     <li>The prover generates an {@link Announcement} and sends it to the verifier.</li>
 *     <li>The verifier generates a {@link Challenge} and sends it to the prover.</li>
 *     <li>The prover generates a {@link Response} and sends it to the verifier.</li>
 *     <li>The verifier checks whether they accept the transcript consisting of {@link Announcement}, {@link Challenge}, and {@link Response}.</li>
 * </ol>
 */
public interface SigmaProtocol extends InteractiveArgument {
    /**
     * Used by the prover to generate a secret value that will be input for future method calls.
     * For example, for an implementation of the original Schnorr protocol, the announcement secret would be a random exponent r.
     *
     * @param commonInput input to the overall protocol that both prover and verifier use.
     * @param secretInput input to the overall protocol that only the prover gets.
     * @return a secret (internal) value used for generating announcements and responses.
     */
    AnnouncementSecret generateAnnouncementSecret(CommonInput commonInput, SecretInput secretInput);

    /**
     * Used by the prover to generate an announcement (the first message sent in the protocol)
     */
    Announcement generateAnnouncement(CommonInput commonInput, SecretInput secretInput, AnnouncementSecret announcementSecret);

    /**
     * Used by the verifier to generate a challenge (the second message in the protocol)
     */
    Challenge generateChallenge(CommonInput commonInput);

    /**
     * Used by the prover to generate a response (the third and last message sent in the protocol)
     */
    Response generateResponse(CommonInput commonInput, SecretInput secretInput, Announcement announcement, AnnouncementSecret announcementSecret, Challenge challenge);

    /**
     * Used by the verifier to checks whether the given transcript is accepting.
     */
    boolean checkTranscript(CommonInput commonInput, Announcement announcement, Challenge challenge, Response response);

    /**
     * Used by the verifier to checks whether the given transcript is accepting.
     * @see SigmaProtocol#checkTranscript(CommonInput, Announcement, Challenge, Response)
     */
    default boolean checkTranscript(CommonInput commonInput, SigmaProtocolTranscript transcript) {
        return checkTranscript(commonInput, transcript.getAnnouncement(), transcript.getChallenge(), transcript.getResponse());
    }

    /**
     * Returns a compressed (shorter) version of the given transcript.
     * Useful for {@link de.upb.crypto.craco.protocols.arguments.fiatshamir.FiatShamirProofSystem}.
     */
    default Representation compressTranscript(CommonInput commonInput, SigmaProtocolTranscript transcript) {
        return transcript.getRepresentation();
    }

    /**
     * Decompressed a transcript compressed with {@link SigmaProtocol#compressTranscript(CommonInput, SigmaProtocolTranscript)}
     * 
     * The guarantee is that if a transcript is valid, then compressing and decompressing yields the same transcript.
     * Additionally, any transcript output by this method is valid (i.e. {@link SigmaProtocol#checkTranscript(CommonInput, SigmaProtocolTranscript)} returns true).
     *
     * @throws IllegalArgumentException is the given compressedTranscript cannot be decompressed into a valid transcript.
     */
    default SigmaProtocolTranscript decompressTranscript(CommonInput commonInput, Representation compressedTranscript) throws IllegalArgumentException {
        return recreateTranscript(compressedTranscript, commonInput);
    }

    /**
     * Generates a random transcript with the same distribution as an honestly generated one that contains the given {@link Challenge}.
     */
    SigmaProtocolTranscript generateSimulatedTranscript(CommonInput commonInput, Challenge challenge);

    Announcement recreateAnnouncement(CommonInput commonInput, Representation repr);
    Challenge recreateChallenge(CommonInput commonInput, Representation repr);
    Response recreateResponse(CommonInput commonInput, Announcement announcement, Challenge challenge, Representation repr);
    default SigmaProtocolTranscript recreateTranscript(Representation repr, CommonInput commonInput) {
        return new SigmaProtocolTranscript(this, commonInput, repr);
    }

    /**
     * Creates a challenge from the given {@code byte[]}.
     * For byte arrays of length {@code floor(log8(getChallengeSpaceSize(commonInput)))},
     * this mapping should be injective almost everywhere (i.e. almost all images have only one primage).
     */
    Challenge createChallengeFromBytes(CommonInput commonInput, byte[] bytes);

    /**
     * Returns the number of possible values returned by generateChallenge(commonInput).
     */
    BigInteger getChallengeSpaceSize();

    @Override
    default String getFirstMessageRole() {
        return InteractiveArgument.PROVER_ROLE;
    }

    @Override
    default InteractiveArgumentInstance instantiateProtocol(String role, CommonInput commonInput, SecretInput secretInput) {
        return PROVER_ROLE.equals(role) ? new SigmaProtocolProverInstance(this, commonInput, secretInput) :
                VERIFIER_ROLE.equals(role) ? new SigmaProtocolVerifierInstance(this, commonInput) : null;
    }

    default SigmaProtocolProverInstance getProverInstance(CommonInput commonInput, SecretInput secretInput) {
        return new SigmaProtocolProverInstance(this, commonInput, secretInput);
    }

    default SigmaProtocolVerifierInstance getVerifierInstance(CommonInput commonInput) {
        return new SigmaProtocolVerifierInstance(this, commonInput);
    }
}
