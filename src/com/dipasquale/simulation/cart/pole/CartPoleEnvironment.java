package com.dipasquale.simulation.cart.pole;

import com.dipasquale.common.random.float2.RandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class CartPoleEnvironment { // code based on: https://github.com/CodeReclaimers/neat-python/blob/master/examples/single-pole-balancing/cart_pole.py and source was http://coneural.org/florian/papers/05_cart_pole.pdf
    private static final double POSITION_LIMIT = 2.4D;
    private static final double ANGLE_RADIANS_LIMIT = 45D * Math.PI / 180D;
    @Builder.Default
    private final double gravity = 9.8D;
    @Builder.Default
    private final Cart cart = Cart.builder().build();
    @Builder.Default
    private final Pole pole = Pole.builder().build();
    @Builder.Default
    private final double stepTime = 0.01D;
    @Builder.Default
    private final double positionLimit = POSITION_LIMIT;
    @Builder.Default
    private final double angleRadiansLimit = ANGLE_RADIANS_LIMIT;
    @Builder.Default
    private double x = 0D;
    @Builder.Default
    private double theta = 0D;
    @Builder.Default
    private double dx = 0D;
    @Builder.Default
    private double dtheta = 0D;
    @Builder.Default
    private double accx = 0D;
    @Builder.Default
    private double acctheta = 0D;
    @Builder.Default
    @Getter
    private double timeSpent = 0D;

    public static CartPoleEnvironment createRandom(final RandomSupport randomSupport, final double positionLimit, final double angleRadiansLimit) {
        double x = randomSupport.next(-0.5D * positionLimit, 0.5D * positionLimit);
        double theta = randomSupport.next(-0.5D * angleRadiansLimit, 0.5D * angleRadiansLimit);
        double dx = randomSupport.next(-1D, 1D);
        double dtheta = randomSupport.next(-1D, 1D);

        return CartPoleEnvironment.builder()
                .x(x)
                .theta(theta)
                .dx(dx)
                .dtheta(dtheta)
                .build();
    }

    public static CartPoleEnvironment createRandom(final RandomSupport randomSupport) {
        return createRandom(randomSupport, POSITION_LIMIT, ANGLE_RADIANS_LIMIT);
    }

    public double[] getState() {
        double x_state = 0.5D * (x + positionLimit) / positionLimit;
        double dx_state = (dx + 0.75D) / 1.5D;
        double theta_state = 0.5D * (theta + ANGLE_RADIANS_LIMIT) / ANGLE_RADIANS_LIMIT;
        double dtheta_state = (dtheta + 1D) / 2D;

        return new double[]{x_state, dx_state, theta_state, dtheta_state};
    }

    public boolean isLimitHit() {
        return Math.abs(x) >= positionLimit || Math.abs(theta) >= ANGLE_RADIANS_LIMIT;
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
