package ma.ahmedbenidar.soap.client;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Callback cote client : WSS4J demande le mot de passe de l'identifiant
 * pour construire l'en-tete UsernameToken du message sortant.
 */
public class CallbackClient implements CallbackHandler {

    private final String identifiant;
    private final String motDePasse;

    public CallbackClient(String identifiant, String motDePasse) {
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback motDePasseDemande) {
                motDePasseDemande.setIdentifier(identifiant);
                motDePasseDemande.setPassword(motDePasse);
                return;
            }
        }
    }
}
