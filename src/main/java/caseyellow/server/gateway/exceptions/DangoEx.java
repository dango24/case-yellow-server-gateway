package caseyellow.server.gateway.exceptions;

import caseyellow.server.gateway.enums.ErrorStatus;

/**
 * Created by dango on 8/15/17.
 */
public class DangoEx extends InternalException {

    public DangoEx(String message) {
        super(ErrorStatus.ANALYZE_FAILED, message);
    }
}
