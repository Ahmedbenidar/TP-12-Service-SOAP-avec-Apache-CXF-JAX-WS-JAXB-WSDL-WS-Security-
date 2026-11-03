package ma.ahmedbenidar.soap.impl;

import jakarta.jws.WebService;
import ma.ahmedbenidar.soap.api.BanqueService;
import ma.ahmedbenidar.soap.model.Compte;
import ma.ahmedbenidar.soap.model.ResultatVirement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation metier. Elle reste volontairement simple : la couche SOAP
 * (CXF, JAX-WS, WSS4J) est totalement separee de cette logique.
 */
@WebService(
        serviceName = "BanqueService",
        portName = "BanqueServicePort",
        endpointInterface = "ma.ahmedbenidar.soap.api.BanqueService",
        targetNamespace = "http://soap.ahmedbenidar.ma/banque/"
)
public class BanqueServiceImpl implements BanqueService {

    private static final Map<String, Compte> COMPTES = new LinkedHashMap<>();

    static {
        COMPTES.put("MA-1001", new Compte("MA-1001", "Yassine El Amrani", "Agence Casablanca Anfa",
                "Casablanca", "MAD", 18540.75, true));
        COMPTES.put("MA-1002", new Compte("MA-1002", "Salma Benjelloun", "Agence Casablanca Anfa",
                "Casablanca", "MAD", 7420.00, true));
        COMPTES.put("MA-1003", new Compte("MA-1003", "Hicham Ouazzani", "Agence Rabat Agdal",
                "Rabat", "MAD", 221150.40, true));
        COMPTES.put("MA-1004", new Compte("MA-1004", "Nadia Chraibi", "Agence Rabat Agdal",
                "Rabat", "MAD", 980.25, false));
        COMPTES.put("MA-1005", new Compte("MA-1005", "Mehdi Berrada", "Agence Marrakech Gueliz",
                "Marrakech", "MAD", 52300.00, true));
    }

    @Override
    public String saluer(String nomClient) {
        String nom = (nomClient == null || nomClient.isBlank()) ? "cher client" : nomClient.trim();
        return "Bonjour " + nom + ", bienvenue au service SOAP de la banque Ahmed Ben Idar.";
    }

    @Override
    public Compte consulterCompte(String numeroCompte) {
        Compte compte = COMPTES.get(numeroCompte);
        if (compte == null) {
            throw new IllegalArgumentException("Compte inconnu : " + numeroCompte);
        }
        return compte;
    }

    @Override
    public List<Compte> listerComptesAgence(String agence) {
        List<Compte> resultat = new ArrayList<>();
        for (Compte compte : COMPTES.values()) {
            if (agence == null || agence.isBlank()
                    || compte.getAgence().toLowerCase().contains(agence.toLowerCase())) {
                resultat.add(compte);
            }
        }
        return resultat;
    }

    @Override
    public ResultatVirement effectuerVirement(String compteDebit, String compteCredit, Double montant) {
        if (montant == null || montant <= 0) {
            return new ResultatVirement("-", "REJETE", "Le montant doit etre strictement positif",
                    montant, null, null);
        }

        Compte debit = COMPTES.get(compteDebit);
        Compte credit = COMPTES.get(compteCredit);

        if (debit == null) {
            throw new IllegalArgumentException("Compte debite inconnu : " + compteDebit);
        }
        if (credit == null) {
            throw new IllegalArgumentException("Compte credite inconnu : " + compteCredit);
        }
        if (!Boolean.TRUE.equals(debit.getActif())) {
            return new ResultatVirement("-", "REJETE",
                    "Le compte " + compteDebit + " est bloque, virement impossible",
                    montant, debit.getSolde(), credit.getSolde());
        }
        if (debit.getSolde() < montant) {
            return new ResultatVirement("-", "REJETE",
                    "Provision insuffisante sur " + compteDebit,
                    montant, debit.getSolde(), credit.getSolde());
        }

        debit.setSolde(debit.getSolde() - montant);
        credit.setSolde(credit.getSolde() + montant);

        return new ResultatVirement("VIR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "EXECUTE", "Virement execute avec succes",
                montant, debit.getSolde(), credit.getSolde());
    }
}
