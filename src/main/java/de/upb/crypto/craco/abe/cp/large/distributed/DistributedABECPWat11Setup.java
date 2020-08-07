package de.upb.crypto.craco.abe.cp.large.distributed;

import de.upb.crypto.craco.abe.cp.large.ABECPWat11MasterSecret;
import de.upb.crypto.craco.common.utils.PrimeFieldPolynom;
import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.factory.BilinearGroupFactory;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.random.interfaces.RandomGeneratorSupplier;
import de.upb.crypto.math.structures.zn.Zp;
import de.upb.crypto.math.structures.zn.Zp.ZpElement;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DistributedABECPWat11Setup {

    DistributedABECPWat11PublicParameters pp;

    Set<DistributedABECPWat11MasterKeyShare> masterKeyShares;

    ABECPWat11MasterSecret msk;

    /**
     * Sets up public parameters and the master key shares for a given security
     * parameter securityParameter. The parameter n specifies the maximum number
     * of attributes per key. The parameter l_max specifies the maximum number
     * of rows per MSPs.
     *
     * @param securityParameter the security parameter of the resulting encryption scheme
     * @param n                 the maximum amount of attributes in a decryption key
     * @param l_max             maximium number of rows per MSP
     * @param debug
     * @param t                 server threshold
     * @param L                 server count
     */
    public void doKeyGen(int securityParameter, int n, int l_max, int t, int L, boolean debug) {
        // Generate bilinear group
        BilinearGroupFactory fac = new BilinearGroupFactory(securityParameter);
        fac.setDebugMode(debug);
        fac.setRequirements(BilinearGroup.Type.TYPE_1, true, false, false);
        BilinearGroup group = fac.createBilinearGroup();

        doKeyGen(group, n, l_max, t, L);
    }

    public void doKeyGen(BilinearGroup group, int n, int l_max, int t, int L) {
        pp = new DistributedABECPWat11PublicParameters();
        pp.setN(n);
        pp.setL_max(l_max);

        pp.setGroupG1(group.getG1());
        pp.setGroupGT(group.getGT());
        pp.setHashToG1(group.getHashIntoG1());
        pp.setE(group.getBilinearMap());

        Zp zp = new Zp(pp.getGroupG1().size());

        // Do the scheme setup stuff

        ZpElement a = zp.getUniformlyRandomUnit();
        GroupElement g = pp.getGroupG1().getUniformlyRandomNonNeutral();
        pp.setG(g);
        GroupElement g_a = g.pow(a);
        pp.setG_a(g_a);
        
        Set<BigInteger> N = new HashSet<>();

        for (int i = 1; i <= n + 1; i++) {
            N.add(BigInteger.valueOf(i));
        }
        ZpElement y_0 = zp.getUniformlyRandomUnit();
        PrimeFieldPolynom q_0 = new PrimeFieldPolynom(zp, t - 1);
        q_0.createRandom(RandomGeneratorSupplier.instance().get());
        q_0.setCoefficient(y_0, 0);

        GroupElement Y = pp.getE().apply(pp.getG(), pp.getG()).pow(y_0);
        pp.setY(Y);

        Map<Integer, GroupElement> VK = new HashMap<>();
        masterKeyShares = new HashSet<>();

        for (int xi = 1; xi <= L; xi++) {
            int serverID = xi;
            BigInteger tmp = q_0.evaluate(BigInteger.valueOf(serverID));
            VK.put(xi, pp.getE().apply(pp.getG(), pp.getG()).pow(tmp));
            masterKeyShares.add(new DistributedABECPWat11MasterKeyShare(serverID, tmp));
        }
        pp.setVerificationKeys(VK);
        Map<BigInteger, GroupElement> T = new HashMap<>();
        for (BigInteger i : N) {
            ZpElement t_i = zp.getUniformlyRandomUnit();
            T.put(i, pp.getG().pow(t_i));
        }
        pp.setT(T);
        pp.setThreshold(t);

        msk = new ABECPWat11MasterSecret(g.pow(y_0));
    }

    public DistributedABECPWat11PublicParameters getPublicParameters() {
        return pp;
    }

    public Set<DistributedABECPWat11MasterKeyShare> getMasterKeyShares() {
        return masterKeyShares;
    }

    public ABECPWat11MasterSecret getMasterSecret() {
        return msk;
    }
}
