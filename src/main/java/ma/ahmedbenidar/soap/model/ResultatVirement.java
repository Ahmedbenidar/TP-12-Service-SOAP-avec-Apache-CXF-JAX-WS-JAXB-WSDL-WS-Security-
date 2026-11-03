package ma.ahmedbenidar.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Reponse de l'operation EffectuerVirement : un objet compose, ce qui
 * montre que JAXB serialise aussi des types imbriques dans le Body SOAP.
 */
@XmlRootElement(name = "ResultatVirement")
@XmlType(name = "ResultatVirementType", propOrder = {
        "reference", "statut", "message", "montantDebite", "nouveauSoldeDebit", "nouveauSoldeCredit"
})
@XmlAccessorType(XmlAccessType.NONE)
public class ResultatVirement {

    private String reference;
    private String statut;
    private String message;
    private Double montantDebite;
    private Double nouveauSoldeDebit;
    private Double nouveauSoldeCredit;

    public ResultatVirement() {
        // constructeur vide exige par JAXB
    }

    public ResultatVirement(String reference, String statut, String message,
                            Double montantDebite, Double nouveauSoldeDebit, Double nouveauSoldeCredit) {
        this.reference = reference;
        this.statut = statut;
        this.message = message;
        this.montantDebite = montantDebite;
        this.nouveauSoldeDebit = nouveauSoldeDebit;
        this.nouveauSoldeCredit = nouveauSoldeCredit;
    }

    @XmlElement(name = "reference")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlElement(name = "statut")
    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlElement(name = "montantDebite")
    public Double getMontantDebite() {
        return montantDebite;
    }

    public void setMontantDebite(Double montantDebite) {
        this.montantDebite = montantDebite;
    }

    @XmlElement(name = "nouveauSoldeDebit")
    public Double getNouveauSoldeDebit() {
        return nouveauSoldeDebit;
    }

    public void setNouveauSoldeDebit(Double nouveauSoldeDebit) {
        this.nouveauSoldeDebit = nouveauSoldeDebit;
    }

    @XmlElement(name = "nouveauSoldeCredit")
    public Double getNouveauSoldeCredit() {
        return nouveauSoldeCredit;
    }

    public void setNouveauSoldeCredit(Double nouveauSoldeCredit) {
        this.nouveauSoldeCredit = nouveauSoldeCredit;
    }

    @Override
    public String toString() {
        return "ResultatVirement{" + reference + ", " + statut + ", " + message
                + ", debite=" + montantDebite + ", soldeDebit=" + nouveauSoldeDebit
                + ", soldeCredit=" + nouveauSoldeCredit + "}";
    }
}
