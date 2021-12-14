package tech.zeroed.ConveyorTest.utilities;

public class FuncMagic {
    /**
     * NFI how this works but it lets you use a constructor matching an interface as a function parameter
     * @param <A1>
     * @param <A2>
     * @param <A3>
     * @param <R>
     */
    @FunctionalInterface
    public interface Function3<A1, A2, A3, R> {
        public R apply(A1 a1, A2 a2, A3 a3);
    }
}
