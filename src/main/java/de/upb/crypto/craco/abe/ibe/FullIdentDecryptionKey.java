package de.upb.crypto.craco.abe.ibe;

import de.upb.crypto.craco.interfaces.DecryptionKey;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.AnnotatedRepresentationUtil;
import de.upb.crypto.math.serialization.annotations.v2.ReprUtil;
import de.upb.crypto.math.serialization.annotations.v2.Represented;

/**
 * A {@link DecryptionKey} for the {@link FullIdent}.
 * <p>
 * This key is generated by
 * {@link FullIdent#generateDecryptionKey(de.upb.crypto.craco.interfaces.pe.MasterSecret,
 *                                        de.upb.crypto.craco.interfaces.pe.KeyIndex)}.
 *
 * @author Mirko Jürgens
 */
public class FullIdentDecryptionKey implements DecryptionKey {

    @Represented(restorer = "G1")
    private GroupElement d_id; //s * Q_id

    public FullIdentDecryptionKey(GroupElement d_id) {
        this.d_id = d_id;
    }

    public FullIdentDecryptionKey(Representation repr, FullIdentPublicParameters pp) {
        new ReprUtil(this).register(pp.getGroupG1(), "G1").deserialize(repr);
    }

    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    public GroupElement getD_id() {
        return d_id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((d_id == null) ? 0 : d_id.hashCode());
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
        FullIdentDecryptionKey other = (FullIdentDecryptionKey) obj;
        if (d_id == null) {
            if (other.d_id != null)
                return false;
        } else if (!d_id.equals(other.d_id))
            return false;
        return true;
    }
}
