package ma.ahmedbenidar.soap.client;

import ma.ahmedbenidar.soap.api.BanqueService;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Client qui ajoute un UsernameToken au message SOAP pour appeler les
 * endpoints proteges : PasswordText sur /banque-secure et PasswordDigest
 * sur /banque-digest. Le meme code sert aussi a montrer les refus.
 */
public class ClientSecurise {

    public static void main(String[] args) throws Exception {
        String base = args.length > 0 ? args[0] : "http://localhost:9091/services/";

        System.out.println("=== 1. Endpoint PasswordText, bons identifiants ===");
        appeler(base, "banque-secure", "PasswordText", "ahmed", "Banque@2026");

        System.out.println();
        System.out.println("=== 2. Endpoint PasswordText, mauvais mot de passe ===");
        try {
            appeler(base, "banque-secure", "PasswordText", "ahmed", "mauvais-mot-de-passe");
            System.out.println("PROBLEME : l'appel aurait du etre refuse");
        } catch (Exception e) {
            System.out.println("Appel refuse comme prevu : " + e.getClass().getSimpleName());
            System.out.println("Detail : " + premiereLigne(e.getMessage()));
        }

        System.out.println();
        System.out.println("=== 3. Endpoint PasswordDigest, bons identifiants ===");
        appeler(base, "banque-digest", "PasswordDigest", "salma", "Salma#Cxf");

        System.out.println();
        System.out.println("=== 4. Endpoint PasswordDigest, utilisateur inconnu ===");
        try {
            appeler(base, "banque-digest", "PasswordDigest", "inconnu", "peu-importe");
            System.out.println("PROBLEME : l'appel aurait du etre refuse");
        } catch (Exception e) {
            System.out.println("Appel refuse comme prevu : " + e.getClass().getSimpleName());
            System.out.println("Detail : " + premiereLigne(e.getMessage()));
        }
    }

    private static void appeler(String base, String chemin, String typeMotDePasse,
                                String utilisateur, String motDePasse) throws Exception {
        BanqueService port = ClientJava.creerPort(base + chemin + "?wsdl");

        Map<String, Object> proprietes = new HashMap<>();
        proprietes.put("action", "UsernameToken");
        proprietes.put("user", utilisateur);
        proprietes.put("passwordType", typeMotDePasse);
        proprietes.put("passwordCallbackRef", new CallbackClient(utilisateur, motDePasse));
        ClientProxy.getClient(port).getOutInterceptors().add(new WSS4JOutInterceptor(proprietes));

        System.out.println("Saluer          : " + port.saluer("Ahmed"));
        System.out.println("ConsulterCompte : " + port.consulterCompte("MA-1002"));
    }

    private static String premiereLigne(String texte) {
        if (texte == null) {
            return "(pas de detail)";
        }
        int fin = texte.indexOf('\n');
        return fin < 0 ? texte : texte.substring(0, fin);
    }
}
