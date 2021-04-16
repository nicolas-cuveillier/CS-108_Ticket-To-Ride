package ch.epfl.tchu;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * use to easily check conditions
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * check if the boolean parameter is true and throw exception if not
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
