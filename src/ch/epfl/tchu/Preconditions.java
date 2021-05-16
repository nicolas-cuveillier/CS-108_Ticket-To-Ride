package ch.epfl.tchu;

/**
 * Use to easily check conditions.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Check if the boolean parameter is true and throw exception if not.
     *
     * @param shouldBeTrue boolean condition that should be true
     * @throws IllegalArgumentException
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
