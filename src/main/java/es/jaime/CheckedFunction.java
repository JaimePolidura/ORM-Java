package es.jaime;

@FunctionalInterface
public interface CheckedFunction<I, O> {
    O apply(I input) throws Exception;

    default O applyOrRethrow(I input) {
        try{
            return apply(input);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    default O applyOrRethrow(I input, String errorMessage) {
        try{
            return apply(input);
        }catch (Exception e) {
            throw new RuntimeException(errorMessage);
        }
    }
}
