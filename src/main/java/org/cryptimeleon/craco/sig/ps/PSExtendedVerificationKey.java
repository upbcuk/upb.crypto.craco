package org.cryptimeleon.craco.sig.ps;

import org.cryptimeleon.craco.commitment.pedersen.PedersenCommitmentScheme;
import org.cryptimeleon.craco.protocols.CommonInput;
import org.cryptimeleon.craco.sig.VerificationKey;
import org.cryptimeleon.math.hash.ByteAccumulator;
import org.cryptimeleon.math.hash.UniqueByteRepresentable;
import org.cryptimeleon.math.hash.annotations.AnnotatedUbrUtil;
import org.cryptimeleon.math.hash.annotations.UniqueByteRepresented;
import org.cryptimeleon.math.hash.impl.ByteArrayAccumulator;
import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.serialization.annotations.ReprUtil;
import org.cryptimeleon.math.serialization.annotations.Represented;
import org.cryptimeleon.math.structures.groups.Group;
import org.cryptimeleon.math.structures.groups.GroupElement;
import org.cryptimeleon.math.structures.groups.cartesian.GroupElementVector;
import org.cryptimeleon.math.structures.groups.elliptic.BilinearMap;

import java.util.Objects;


/**
 * Extension of the verification key for a Pointcheval Sanders Signature Scheme to store the generator g and the
 * group elements Y_i from group 1.
 * These parameters are generated by
 * {@link PSExtendedSignatureScheme#generateKeyPair(int)}.
 * The reason for storing those further variables is the combined usage of the {@link PSExtendedSignatureScheme}
 * with the {@link PedersenCommitmentScheme} is for being able blind and unblind messages before and after signing them.
 * This is achieved by using the same g and Y_i in the {@code PedersenCommitmentScheme} as provided by the
 * {@link PSExtendedSignatureScheme}.
 * This allows a user to receive a signature on a commitment for a message and
 * to then calculate the signature for the uncommitted message, and thereby receive a signature of a signer for a
 * message without the signer knowing the content of the message.
 */
public class PSExtendedVerificationKey extends PSVerificationKey
        implements VerificationKey, UniqueByteRepresentable, CommonInput {

    // Added parameters to enable blindly signing messages in combination with the Pedersen commitment scheme
    // g for enabling optional blinding/unblinding
    @UniqueByteRepresented
    @Represented(restorer = "G1")
    private GroupElement group1ElementG;

    // Y_i for enabling optional blinding/unblinding
    @UniqueByteRepresented
    @Represented(restorer = "G1")
    private GroupElementVector group1ElementsYi;

    /**
     * Extended constructor for the extended verification key in the ACS allowing direct instantiation.
     *
     * @param group1Element         {@link GroupElement} g is a generator from {@link Group} 1
     * @param group1ElementsYi      Array of {@link GroupElement} containing Y_i from {@link Group} 1
     * @param group2ElementTildeG   {@link GroupElement} g_Tilde is a generator from {@link Group} 2
     * @param group2ElementTildeX   {@link GroupElement} x_Tilde from {@link Group} 1
     * @param group2ElementsTildeYi Array of {@link GroupElement} containing  Y_i_Tilde from {@link Group} 2
     */
    public PSExtendedVerificationKey(GroupElement group1Element, GroupElementVector group1ElementsYi,
                                     GroupElement group2ElementTildeG, GroupElement group2ElementTildeX,
                                     GroupElementVector group2ElementsTildeYi) {
        super(group2ElementTildeG, group2ElementTildeX, group2ElementsTildeYi);
        this.group1ElementG = group1Element;
        this.group1ElementsYi = group1ElementsYi;
    }

    private PSExtendedVerificationKey() {
        super();
    }

    /**
     * Extended constructor for the extended verification key in the ACS (from representation).
     *
     * @param groupG1 {@link Group} group 1 from {@link BilinearMap}
     * @param groupG2 {@link Group} group 2 from {@link BilinearMap}
     * @param repr    {@link Representation} of {@link PSExtendedVerificationKey}
     */
    public PSExtendedVerificationKey(Group groupG1, Group groupG2, Representation repr) {
        new ReprUtil(this).register(groupG1, "G1").register(groupG2, "G2").deserialize(repr);
    }

    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    public GroupElement getGroup1ElementG() {
        return group1ElementG;
    }

    public GroupElementVector getGroup1ElementsYi() {
        return group1ElementsYi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PSExtendedVerificationKey that = (PSExtendedVerificationKey) o;
        return Objects.equals(group1ElementG, that.group1ElementG) &&
                Objects.equals(group1ElementsYi, that.group1ElementsYi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), group1ElementG);
    }

    @Override
    public ByteAccumulator updateAccumulator(ByteAccumulator byteAccumulator) {
        return AnnotatedUbrUtil.autoAccumulate(byteAccumulator, this);
    }

    @Override
    public byte[] getUniqueByteRepresentation() {
        return this.updateAccumulator(new ByteArrayAccumulator()).extractBytes();
    }
}
