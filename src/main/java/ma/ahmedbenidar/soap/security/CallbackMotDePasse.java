package ma.ahmedbenidar.soap.security;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Map;

/**
 * Callback appele par WSS4J quand il doit valider un UsernameToken.
 * La map des utilisateurs est injectee au demarrage du serveur.
 */
public class CallbackMotDePasse implements CallbackHandler {

    private final Map<String, String> utilisateurs;
    private int validations = 0;

    public CallbackMotDePasse(Map<String, String> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback motDePasse) {
                String attendu = utilisateurs.get(motDePasse.getIdentifier());
                if (attendu == null) {
                    return;
                }
                motDePasse.setPassword(attendu);
                validations++;
                System.out.println("[WS-SECURITY] Identifiant valide : " + motDePasse.getIdentifier()
                        + " (validation n " + validations + ")");
                return;
            }
        }
    }

    public Map<String, String> getUtilisateurs() {
        return utilisateurs;
    }

    public int getValidations() {
        return validations;
    }
}
