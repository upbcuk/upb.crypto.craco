package de.upb.crypto.craco.sig.sps.eq;

import de.upb.crypto.craco.sig.interfaces.Signature;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.v2.ReprUtil;
import de.upb.crypto.math.serialization.annotations.v2.Represented;

import java.util.Objects;

/**
 * Class for a signature of the SPS-EQ signature scheme.
 *
 * @author Fabian Eidens
 */

public class SPSEQSignature implements Signature {

    /**
     * First group element of the signature in G_1.
     */
    @Represented(restorer = "G1")
    protected GroupElement group1ElementSigma1Z;

    /**
     * Second group element of the signature in G_1.
     */
    @Represented(restorer = "G1")
    protected GroupElement group1ElementSigma2Y;

    /**
     * Third group element of the signature in G_2.
     */
    @Represented(restorer = "G2")
    protected GroupElement group1ElementSigma3HatY;

    public SPSEQSignature(Representation repr, Group groupG1, Group groupG2) {
        new ReprUtil(this).register(groupG1, "G1").register(groupG2, "G2"). deserialize(repr);
    }

    public SPSEQSignature(GroupElement group1ElementSigma1Z, GroupElement group1ElementSigma2Y, GroupElement group1ElementSigma3HatY) {
        super();
        this.group1ElementSigma1Z = group1ElementSigma1Z;
        this.group1ElementSigma2Y = group1ElementSigma2Y;
        this.group1ElementSigma3HatY = group1ElementSigma3HatY;
    }

    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    public GroupElement getGroup1ElementSigma1Z() {
        return group1ElementSigma1Z;
    }

    public void setGroup1ElementSigma1Z(GroupElement group1ElementSigma1Z) {
        this.group1ElementSigma1Z = group1ElementSigma1Z;
    }

    public GroupElement getGroup1ElementSigma2Y() {
        return group1ElementSigma2Y;
    }

    public void setGroup1ElementSigma2Y(GroupElement group1ElementSigma2Y) {
        this.group1ElementSigma2Y = group1ElementSigma2Y;
    }

    public GroupElement getGroup1ElementSigma3HatY() {
        return group1ElementSigma3HatY;
    }

    public void setGroup1ElementSigma3HatY(GroupElement group1ElementSigma3HatY) {
        this.group1ElementSigma3HatY = group1ElementSigma3HatY;
    }

    @Override
    public String toString() {
        return "SPSEQSignature [sigma_1_Z=" + group1ElementSigma1Z + ", sigma_2_Y=" + group1ElementSigma2Y +  ", sigma_3_Hat_Y" + group1ElementSigma3HatY + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SPSEQSignature that = (SPSEQSignature) o;
        return Objects.equals(group1ElementSigma1Z, that.group1ElementSigma1Z) &&
                Objects.equals(group1ElementSigma2Y, that.group1ElementSigma2Y) &&
                Objects.equals(group1ElementSigma3HatY, that.group1ElementSigma3HatY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group1ElementSigma1Z, group1ElementSigma2Y, group1ElementSigma3HatY);
    }
}
