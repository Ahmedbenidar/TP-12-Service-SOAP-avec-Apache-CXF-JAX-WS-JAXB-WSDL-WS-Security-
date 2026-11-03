package ma.ahmedbenidar.soap.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Compte bancaire transporte dans le Body SOAP. Annote JAXB pour etre
 * serialise en XML par CXF lors de l'ecriture de la reponse.
 */
@XmlRootElement(name = "Compte")
@XmlType(name = "CompteType", propOrder = {
        "numeroCompte", "titulaire", "agence", "ville", "devise", "solde", "actif"
})
@XmlAccessorType(XmlAccessType.NONE)
public class Compte {

    private String numeroCompte;
    private String titulaire;
    private String agence;
    private String ville;
    private String devise;
    private Double solde;
    private Boolean actif;

    public Compte() {
        // constructeur vide exige par JAXB
    }

    public Compte(String numeroCompte, String titulaire, String agence,
                  String ville, String devise, Double solde, Boolean actif) {
        this.numeroCompte = numeroCompte;
        this.titulaire = titulaire;
        this.agence = agence;
        this.ville = ville;
        this.devise = devise;
        this.solde = solde;
        this.actif = actif;
    }

    @XmlElement(name = "numeroCompte", nillable = false)
    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    @XmlElement(name = "titulaire")
    public String getTitulaire() {
        return titulaire;
    }

    public void setTitulaire(String titulaire) {
        this.titulaire = titulaire;
    }

    @XmlElement(name = "agence")
    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }

    @XmlElement(name = "ville")
    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    @XmlElement(name = "devise")
    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    @XmlElement(name = "solde")
    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    @XmlElement(name = "actif")
    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "Compte{" + numeroCompte + ", " + titulaire + ", " + agence
                + " (" + ville + "), " + solde + " " + devise + ", actif=" + actif + "}";
    }
}
