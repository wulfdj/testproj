import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * User: jpai
 */
public class AnonymousCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    	System.out.println("Called");
        for (Callback current : callbacks) {
        	System.out.println("" + current.toString());
            if (current instanceof NameCallback) {
                NameCallback ncb = (NameCallback) current;
                ncb.setName("admin");
            } else {
                throw new UnsupportedCallbackException(current);
            }
        }
    }
}