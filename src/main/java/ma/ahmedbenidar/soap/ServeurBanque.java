package ma.ahmedbenidar.soap;

import ma.ahmedbenidar.soap.impl.BanqueServiceImpl;
import ma.ahmedbenidar.soap.security.CallbackMotDePasse;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Publie trois endpoints CXF sur un serveur Jetty embarque :
 *   - /services/banque         : endpoint ouvert
 *   - /services/banque-secure  : endpoint protege par WS-Security UsernameToken (PasswordText)
 *   - /services/banque-digest  : endpoint protege par WS-Security UsernameToken (PasswordDigest)
 */
public class ServeurBanque {

    private static final int PORT = 9091;
    private static final String BASE = "http://localhost:" + PORT + "/services/";

    public static void main(String[] args) throws Exception {
        Map<String, String> utilisateurs = new HashMap<>();
        utilisateurs.put("ahmed", "Banque@2026");
        utilisateurs.put("salma", "Salma#Cxf");

        Server ouvert = publier(BASE + "banque", null, null);
        Server securise = publier(BASE + "banque-secure", utilisateurs, "PasswordText");
        Server digest = publier(BASE + "banque-digest", utilisateurs, "PasswordDigest");

        System.out.println("======================================================");
        System.out.println(" Service SOAP bancaire demarre sur le port " + PORT);
        System.out.println(" Endpoint ouvert          : " + BASE + "banque");
        System.out.println(" WSDL ouvert              : " + BASE + "banque?wsdl");
        System.out.println(" Endpoint PasswordText    : " + BASE + "banque-secure");
        System.out.println(" WSDL PasswordText        : " + BASE + "banque-secure?wsdl");
        System.out.println(" Endpoint PasswordDigest  : " + BASE + "banque-digest");
        System.out.println(" WSDL PasswordDigest      : " + BASE + "banque-digest?wsdl");
        System.out.println(" Utilisateurs WS-Security : " + utilisateurs.keySet());
        System.out.println("======================================================");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ouvert.stop();
            securise.stop();
            digest.stop();
            System.out.println("Endpoints CXF arretes.");
        }));

        new CountDownLatch(1).await();
    }

    private static Server publier(String adresse, Map<String, String> utilisateurs, String typeMotDePasse) {
        JaxWsServerFactoryBean fabrique = new JaxWsServerFactoryBean();
        fabrique.setServiceClass(BanqueServiceImpl.class);
        fabrique.setAddress(adresse);
        fabrique.setServiceName(new javax.xml.namespace.QName(
                "http://soap.ahmedbenidar.ma/banque/", "BanqueService"));
        fabrique.setEndpointName(new javax.xml.namespace.QName(
                "http://soap.ahmedbenidar.ma/banque/", "BanqueServicePort"));

        Server serveur = fabrique.create();

        if (utilisateurs != null) {
            Map<String, Object> proprietes = new HashMap<>();
            proprietes.put("action", "UsernameToken");
            proprietes.put("passwordType", typeMotDePasse);
            proprietes.put("passwordCallbackRef", new CallbackMotDePasse(utilisateurs));
            // PasswordDigest : WSS4J recoit le mot de passe en clair via le callback
            // puis recalcule SHA1(nonce + created + motDePasse) et compare au digest recu.
            serveur.getEndpoint().getInInterceptors().add(new WSS4JInInterceptor(proprietes));
            System.out.println("WS-Security UsernameToken (" + typeMotDePasse + ") active sur " + adresse);
        }

        return serveur;
    }
}
