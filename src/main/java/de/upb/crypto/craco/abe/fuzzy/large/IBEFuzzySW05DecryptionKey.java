package de.upb.crypto.craco.abe.fuzzy.large;


import de.upb.crypto.craco.interfaces.DecryptionKey;
import de.upb.crypto.craco.interfaces.pe.KeyIndex;
import de.upb.crypto.craco.interfaces.pe.MasterSecret;
import de.upb.crypto.craco.kem.fuzzy.large.IBEFuzzySW05KEM;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.AnnotatedRepresentationUtil;
import de.upb.crypto.math.serialization.annotations.v2.ReprUtil;
import de.upb.crypto.math.serialization.annotations.v2.Represented;
import de.upb.crypto.math.serialization.annotations.RepresentedMap;

import java.math.BigInteger;
import java.util.Map;

/**
 * A {@link DecryptionKey} for the {@link IBEFuzzySW05} and {@link IBEFuzzySW05KEM}.
 * <p>
 * The key is generated by {@link AbstractIBEFuzzySW05#generateDecryptionKey(MasterSecret, KeyIndex)}.
 *
 * @author Mirko Jürgens, refactoring: Denis Diemert
 */
public class IBEFuzzySW05DecryptionKey implements DecryptionKey {

    /**
     * { D_i \in G_1 }_i for i \in {@link #identity}
     */
    @Represented(restorer = "int -> G1")
    private Map<BigInteger, GroupElement> dElementMap;

    /**
     * {R_i \in G_1 }_i for i \in {@link #identity}
     */
    @Represented(restorer = "int -> G1")
    private Map<BigInteger, GroupElement> rElementMap;

    @Represented
    private Identity identity;

    public IBEFuzzySW05DecryptionKey(Map<BigInteger, GroupElement> dElementMap,
                                     Map<BigInteger, GroupElement> rElementMap, Identity identity) {
        this.rElementMap = rElementMap;
        this.dElementMap = dElementMap;
        this.identity = identity;
    }

    public IBEFuzzySW05DecryptionKey(Representation repr, IBEFuzzySW05PublicParameters kpp) {
        new ReprUtil(this).register(kpp.getGroupG1(), "G1").deserialize(repr);
    }

    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    public Map<BigInteger, GroupElement> getDElementMap() {
        return dElementMap;
    }

    public Map<BigInteger, GroupElement> getRElementMap() {
        return rElementMap;
    }

    public Identity getIdentity() {
        return identity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dElementMap == null) ? 0 : dElementMap.hashCode());
        result = prime * result + ((rElementMap == null) ? 0 : rElementMap.hashCode());
        result = prime * result + ((identity == null) ? 0 : identity.hashCode());
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
        IBEFuzzySW05DecryptionKey other = (IBEFuzzySW05DecryptionKey) obj;
        if (dElementMap == null) {
            if (other.dElementMap != null)
                return false;
        } else if (!dElementMap.equals(other.dElementMap))
            return false;
        if (rElementMap == null) {
            if (other.rElementMap != null)
                return false;
        } else if (!rElementMap.equals(other.rElementMap))
            return false;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.equals(other.identity))
            return false;
        return true;
    }

}

