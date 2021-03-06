package org.cryptimeleon.craco.enc.asym.elgamal;

import org.cryptimeleon.craco.enc.DecryptionKey;
import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.serialization.annotations.ReprUtil;
import org.cryptimeleon.math.serialization.annotations.Represented;
import org.cryptimeleon.math.structures.groups.Group;
import org.cryptimeleon.math.structures.groups.GroupElement;
import org.cryptimeleon.math.structures.rings.zn.Zn;
import org.cryptimeleon.math.structures.rings.zn.Zn.ZnElement;

import java.util.Objects;

/**
 * An elgamal private key.
 *
 *
 */
public class ElgamalPrivateKey implements DecryptionKey {

    /**
     * The private parameter a.
     */
    @Represented(restorer = "Zn")
    private ZnElement a;

    /**
     * The public key
     **/
    @Represented(restorer = "Scheme")
    private ElgamalPublicKey publicKey;

    public ElgamalPrivateKey(Representation repr, ElgamalEncryption scheme) {
        new ReprUtil(this).register(scheme.getGroup().getZn(), "Zn").register(scheme, "Scheme").deserialize(repr);
    }

    private void init(Group groupG, GroupElement g, ZnElement a, GroupElement h) {
        this.a = a;
        this.publicKey = new ElgamalPublicKey(g, h);
    }

    public ElgamalPrivateKey(ElgamalPublicKey pub, ZnElement a) {
        this.a = a;
        this.publicKey = pub;
    }

    private void init(Group groupG, GroupElement g, ZnElement a) {
        init(groupG, g, a, g.pow(a));
    }

    private void init(Group groupG, GroupElement g) {
        Zn z = new Zn(groupG.size());
        ZnElement a = z.getUniformlyRandomElement();
        init(groupG, g, a);

    }

    private void init(Group groupG) {
        GroupElement g = groupG.getGenerator();
        init(groupG, g);
    }

    /**
     * Creates a new Elgamal private key.
     *
     * @param groupG the group
     * @param g      a generator of the group {@code groupG}
     * @param a      the private exponent such that \(h = g^a\)
     * @param h      the public key \(h = g^a\)
     */
    public ElgamalPrivateKey(Group groupG, GroupElement g, ZnElement a, GroupElement h) {
        init(groupG, g, a, h);
    }

    /**
     * Creates a new ElGamal private key.
     * <p>
     * Sets the public parameter h=g^a.
     *
     * @param groupG the group of this key
     * @param g      a generator of this group
     * @param a      the private exponent
     */
    public ElgamalPrivateKey(Group groupG, GroupElement g, ZnElement a) {
        init(groupG, g, a);
    }


    /**
     * Creates a new ElGamal private key.
     * <p>
     * Chooses a uniformly at random in {0,...,n-1} where n is the size of the group.
     * Sets the public parameter h=g^a.
     *
     * @param groupG the group of this key
     * @param g      a generator of this group
     */
    public ElgamalPrivateKey(Group groupG, GroupElement g) {
        init(groupG, g);
    }

    /**
     * Create a new ElGamal private key.
     * <p>
     * Chooses a random generator g of the given cyclic group of order n.
     * Chooses a random exponent a in {0,....,n-1} and sets the private key to
     * g,a,h=g^a
     *
     * @param groupG the group over which ElGamal is done
     */
    public ElgamalPrivateKey(Group groupG) {
        init(groupG);
    }


    public ElgamalPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(ElgamalPublicKey pk) {
        this.publicKey = pk;
    }


    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    public ZnElement getA() {
        return a;
    }


    public Group getGroupG() {
        return publicKey.getG().getStructure();
    }

    public GroupElement getG() {
        return publicKey.getG();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ElgamalPrivateKey other = (ElgamalPrivateKey) obj;
        return Objects.equals(a, other.a)
                && Objects.equals(publicKey, other.publicKey);
    }


}
