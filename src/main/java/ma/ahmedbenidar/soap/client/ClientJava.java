package ma.ahmedbenidar.soap.client;

import ma.ahmedbenidar.soap.api.BanqueService;
import ma.ahmedbenidar.soap.model.Compte;
import ma.ahmedbenidar.soap.model.ResultatVirement;

import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import java.net.URL;
import java.util.List;

/**
 * Client JAX-WS "bas niveau" : on part du WSDL publie, on cree un proxy
 * dynamique avec Service.getPort et chaque appel Java devient un message SOAP.
 */
public class ClientJava {

    private static final String NAMESPACE = "http://soap.ahmedbenidar.ma/banque/";

    public static void main(String[] args) throws Exception {
        String base = args.length > 0 ? args[0] : "http://localhost:9091/services/";
        BanqueService port = creerPort(base + "banque?wsdl");

        System.out.println("=== Client Java JAX-WS sur l'endpoint ouvert ===");
        System.out.println("Saluer            : " + port.saluer("Ahmed"));
        System.out.println("ConsulterCompte   : " + port.consulterCompte("MA-1001"));

        List<Compte> comptes = port.listerComptesAgence("Rabat");
        System.out.println("ListerComptesAgence (Rabat) : " + comptes.size() + " compte(s)");
        for (Compte c : comptes) {
            System.out.println("   -> " + c);
        }

        ResultatVirement virement = port.effectuerVirement("MA-1003", "MA-1002", 1500.0);
        System.out.println("EffectuerVirement : " + virement);
    }

    static BanqueService creerPort(String wsdl) throws Exception {
        URL url = new URL(wsdl);
        QName qname = new QName(NAMESPACE, "BanqueService");
        Service service = Service.create(url, qname);
        return service.getPort(BanqueService.class);
    }
}
