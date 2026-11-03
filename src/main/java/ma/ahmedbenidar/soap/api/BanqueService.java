package ma.ahmedbenidar.soap.api;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import ma.ahmedbenidar.soap.model.Compte;
import ma.ahmedbenidar.soap.model.ResultatVirement;

import java.util.List;

/**
 * Contrat JAX-WS du service. CXF derive le WSDL de cette interface :
 * chaque methode devient une operation, chaque type de retour devient
 * un type XSD grace a JAXB.
 */
@WebService(targetNamespace = "http://soap.ahmedbenidar.ma/banque/")
public interface BanqueService {

    @WebMethod(operationName = "Saluer")
    @WebResult(name = "messageAccueil")
    String saluer(@WebParam(name = "nomClient") String nomClient);

    @WebMethod(operationName = "ConsulterCompte")
    @WebResult(name = "compte")
    Compte consulterCompte(@WebParam(name = "numeroCompte") String numeroCompte);

    @WebMethod(operationName = "ListerComptesAgence")
    @WebResult(name = "comptes")
    List<Compte> listerComptesAgence(@WebParam(name = "agence") String agence);

    @WebMethod(operationName = "EffectuerVirement")
    @WebResult(name = "resultat")
    ResultatVirement effectuerVirement(@WebParam(name = "compteDebit") String compteDebit,
                                       @WebParam(name = "compteCredit") String compteCredit,
                                       @WebParam(name = "montant") Double montant);
}
