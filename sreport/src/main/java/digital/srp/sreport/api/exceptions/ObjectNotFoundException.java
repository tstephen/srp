package digital.srp.sreport.api.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7402437013533929364L;

    public ObjectNotFoundException(Class<?> type, String id) {
        super(String.format("%1$s with identifier %2$s not found", type.getSimpleName(), id));
    }

}
