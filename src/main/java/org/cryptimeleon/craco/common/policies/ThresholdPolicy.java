package org.cryptimeleon.craco.common.policies;

import org.cryptimeleon.math.hash.ByteAccumulator;
import org.cryptimeleon.math.hash.annotations.AnnotatedUbrUtil;
import org.cryptimeleon.math.hash.annotations.UniqueByteRepresented;
import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.serialization.annotations.ReprUtil;
import org.cryptimeleon.math.serialization.annotations.Represented;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A threshold policy consists of a threshold and a set of policies, and it is fulfilled if threshold many of the
 * children policies are fulfilled.
 */
public class ThresholdPolicy implements Policy {
    @UniqueByteRepresented
    @Represented
    private Integer threshold;

    @UniqueByteRepresented
    @Represented
    private ArrayList<Policy> children;

    public ThresholdPolicy(Representation repr) {
        new ReprUtil(this).deserialize(repr);
    }

    public ThresholdPolicy(int threshold, Collection<? extends Policy> children) {
        this.threshold = threshold;
        this.children = new ArrayList<>();
        this.children.addAll(children);
    }

    public ThresholdPolicy(int threshold, Policy... children) {
        this(threshold, Arrays.asList(children));
    }

    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    @Override
    public boolean isFulfilled(Collection<? extends PolicyFact> facts) {
        int fulfilled = 0;
        for (Policy policy : children) {
            fulfilled += policy.isFulfilled(facts) ? 1 : 0;
        }
        return fulfilled >= threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public ArrayList<Policy> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + threshold;
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
        ThresholdPolicy other = (ThresholdPolicy) obj;
        if (children == null) {
            if (other.children != null)
                return false;
        } else {
            if (!children.containsAll(other.children))
                return false;
            if (!other.children.containsAll(children))
                return false;
        }
        return threshold.equals(other.threshold);
    }

    @Override
    public ByteAccumulator updateAccumulator(ByteAccumulator accumulator) {
        return AnnotatedUbrUtil.autoAccumulate(accumulator, this);
    }
}
