package com.dipasquale.simulation.cart.pole;

import com.dipasquale.common.RandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CartPoleEnvironment { // code based on: https://github.com/CodeReclaimers/neat-python/blob/master/examples/single-pole-balancing/cart_pole.py and source was http://coneural.org/florian/papers/05_cart_pole.pdf
    private final double gravity;
    private final Cart cart;
    private final Pole pole;
    private final double stepTime;
    private final double positionLimit;
    private final double angleRadiansLimit;
    private double x;
    private double theta;
    private double dx;
    private double dtheta;
    private double accx;
    private double acctheta;
    @Getter
    private double timeSpent;

    @Builder
    private static CartPoleEnvironment create(final Double gravity, final Cart cart, final Pole pole, final Double stepTime, final Double positionLimit, final Double angleRadiansLimit, final RandomSupport randomSupport, final Double x, final Double theta, final Double dx, final Double dtheta) {
        double gravityFixed = Optional.ofNullable(gravity)
                .orElse(9.8D);

        Cart cartFixed = Optional.ofNullable(cart)
                .orElseGet(() -> Cart.builder()
                        .build());

        Pole poleFixed = Optional.ofNullable(pole)
                .orElseGet(() -> Pole.builder()
                        .build());

        double stepTimeFixed = Optional.ofNullable(stepTime)
                .orElse(0.01D);

        RandomSupport randomSupportFixed = Optional.ofNullable(randomSupport)
                .orElseGet(() -> RandomSupport.create(false));

        double positionLimitFixed = Optional.ofNullable(positionLimit)
                .orElse(2.4D);

        double angleRadiansLimitFixed = Optional.ofNullable(angleRadiansLimit)
                .orElse(45D * Math.PI / 180D);

        double xFixed = Optional.ofNullable(x)
                .orElseGet(() -> randomSupportFixed.next(-0.5D * positionLimitFixed, 0.5D * positionLimitFixed));

        double thetaFixed = Optional.ofNullable(theta)
                .orElseGet(() -> randomSupportFixed.next(-0.5D * angleRadiansLimitFixed, 0.5 * angleRadiansLimitFixed));

        double dxFixed = Optional.ofNullable(dx)
                .orElseGet(() -> randomSupportFixed.next(-1D, 1D));

        double dthetaFixed = Optional.ofNullable(dtheta)
                .orElseGet(() -> randomSupportFixed.next(-1D, 1D));

        double xacc = 0D;
        double tacc = 0D;
        double timeSpent = 0D;

        return new CartPoleEnvironment(gravityFixed, cartFixed, poleFixed, stepTimeFixed, positionLimitFixed, angleRadiansLimitFixed, xFixed, thetaFixed, dxFixed, dthetaFixed, xacc, tacc, timeSpent);
    }

    public double[] getState() {
        return new double[]{
                0.5D * (x + positionLimit) / positionLimit,
                (dx + 0.75D) / 1.5D,
                0.5D * (theta + angleRadiansLimit) / angleRadiansLimit,
                (dtheta + 1D) / 2D
        };
    }

    public boolean isLimitHit() {
        return Math.abs(x) >= positionLimit || Math.abs(theta) >= angleRadiansLimit;
    }

    public double step(final double force) {
        x += stepTime * dx + 0.5D * accx * Math.pow(stepTime, 2D);
        theta += stepTime * dtheta + 0.5D * acctheta * Math.pow(stepTime, 2D);

        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double poleHalfLength = pole.getLength() / 2D;
        double totalMass = cart.getMass() + pole.getMass();
        double accthetaNext = (gravity * sinTheta + cosTheta * (-force - pole.getMass() * poleHalfLength * Math.pow(dtheta, 2) * sinTheta) / totalMass) / (poleHalfLength * (4D / 3D - pole.getMass() * Math.pow(cosTheta, 2D) / totalMass));
        double accxNext = (force + pole.getMass() * poleHalfLength * (Math.pow(dtheta, 2D) * sinTheta - accthetaNext * cosTheta)) / totalMass;

        dx += 0.5D * (accx + accxNext) * stepTime;
        dtheta += 0.5D * (acctheta + accthetaNext) * stepTime;
        accx = accxNext;
        acctheta = accthetaNext;
        timeSpent += stepTime;

        return timeSpent;
    }

    public double stepInDiscrete(final double action) {
        if (Double.compare(action, 0.5D) > 0) {
            return step(10D);
        }

        return step(-10D);
    }

    public double stepInNoisyDiscrete(final double action, final RandomSupport randomSupport) {
        double temporary = action + randomSupport.next(0D, 0.2D);

        if (Double.compare(temporary, 0.5D) > 0) {
            return step(10D);
        }

        return step(-10D);
    }

    public double stepInContinuous(final double action) {
        double force = 2D * action - 10D;

        return step(force);
    }
}
