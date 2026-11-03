# Script de capture reelle : interroge le service SOAP en HTTP et enregistre
# chaque echange complet (requete + reponse) dans docs/logs.
# Aucune simulation : les fichiers produits sont les sorties brutes de curl.exe.

param(
    [string]$Base = "http://localhost:9091/services/"
)

$logs = Join-Path $PSScriptRoot "..\logs"
$tmp  = Join-Path $env:TEMP "tp12-soap"
if (-not (Test-Path $logs)) { New-Item -ItemType Directory -Path $logs | Out-Null }
if (-not (Test-Path $tmp))  { New-Item -ItemType Directory -Path $tmp  | Out-Null }

function Enveloppe([string]$corps) {
    return @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ban="http://soap.ahmedbenidar.ma/banque/">
  <soapenv:Header/>
  <soapenv:Body>
$corps
  </soapenv:Body>
</soapenv:Envelope>
"@
}

function Token([string]$user, [string]$pass) {
    return @"
<soapenv:Header>
  <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                 xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
    <wsse:UsernameToken wsu:Id="UT-1">
      <wsse:Username>$user</wsse:Username>
      <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">$pass</wsse:Password>
    </wsse:UsernameToken>
  </wsse:Security>
</soapenv:Header>
"@
}

# UsernameToken en PasswordDigest : Base64( SHA-1( nonce + date + motDePasse ) )
function TokenDigest([string]$user, [string]$pass, [string]$nonce, [string]$date) {
    $octets = [System.Text.Encoding]::UTF8.GetBytes($nonce + $date + $pass)
    $sha1 = [System.Security.Cryptography.SHA1]::Create()
    $digest = [Convert]::ToBase64String($sha1.ComputeHash($octets))
    $nonceBase64 = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($nonce))
    return @"
<soapenv:Header>
  <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                 xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
    <wsse:UsernameToken wsu:Id="UT-2">
      <wsse:Username>$user</wsse:Username>
      <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest">$digest</wsse:Password>
      <wsse:Nonce EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary">$nonceBase64</wsse:Nonce>
      <wsu:Created>$date</wsu:Created>
    </wsse:UsernameToken>
  </wsse:Security>
</soapenv:Header>
"@
}

function EnveloppeSecurisee([string]$user, [string]$pass, [string]$corps) {
    $xml = Enveloppe $corps
    return $xml.Replace("<soapenv:Header/>", (Token $user $pass))
}

function EnveloppeSecuriseeDigest([string]$user, [string]$pass, [string]$nonce, [string]$date, [string]$corps) {
    $xml = Enveloppe $corps
    return $xml.Replace("<soapenv:Header/>", (TokenDigest $user $pass $nonce $date))
}

function AppelSoap([string]$titre, [string]$url, [string]$xml, [string]$fichier) {
    $chemin = Join-Path $tmp "$fichier.xml"
    [System.IO.File]::WriteAllText($chemin, $xml, (New-Object System.Text.UTF8Encoding $false))
    $sortie = Join-Path $logs "$fichier.txt"

    "### $titre" | Out-File -FilePath $sortie -Encoding UTF8
    "POST $url" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    "Content-Type: text/xml; charset=UTF-8" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    "" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    "----- REQUETE SOAP ENVOYEE -----" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    $xml | Out-File -FilePath $sortie -Encoding UTF8 -Append
    "" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    "----- REPONSE HTTP + SOAP -----" | Out-File -FilePath $sortie -Encoding UTF8 -Append

    $reponse = & curl.exe -s -i -X POST $url -H "Content-Type: text/xml; charset=UTF-8" --data-binary "@$chemin" 2>&1
    $reponse | Out-File -FilePath $sortie -Encoding UTF8 -Append

    Write-Host ("  -> {0}" -f $fichier)
}

function AppelGet([string]$titre, [string]$url, [string]$fichier) {
    $sortie = Join-Path $logs "$fichier.txt"
    "### $titre" | Out-File -FilePath $sortie -Encoding UTF8
    "GET $url" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    "" | Out-File -FilePath $sortie -Encoding UTF8 -Append
    $reponse = & curl.exe -s -i $url 2>&1
    $reponse | Out-File -FilePath $sortie -Encoding UTF8 -Append
    Write-Host ("  -> {0}" -f $fichier)
}

Write-Host "Captures SOAP reelles vers $logs"

AppelGet "WSDL de l'endpoint ouvert" ($Base + "banque?wsdl") "wsdl-ouvert"
AppelGet "WSDL de l'endpoint securise" ($Base + "banque-secure?wsdl") "wsdl-securise"
AppelGet "WSDL de l'endpoint PasswordDigest" ($Base + "banque-digest?wsdl") "wsdl-digest"

AppelSoap "Operation Saluer" ($Base + "banque") (Enveloppe @"
    <ban:Saluer>
      <nomClient>Ahmed</nomClient>
    </ban:Saluer>
"@) "soap-saluer"

AppelSoap "Operation ConsulterCompte" ($Base + "banque") (Enveloppe @"
    <ban:ConsulterCompte>
      <numeroCompte>MA-1003</numeroCompte>
    </ban:ConsulterCompte>
"@) "soap-consulter-compte"

AppelSoap "Operation ListerComptesAgence" ($Base + "banque") (Enveloppe @"
    <ban:ListerComptesAgence>
      <agence>Rabat</agence>
    </ban:ListerComptesAgence>
"@) "soap-lister-agence"

AppelSoap "Operation EffectuerVirement" ($Base + "banque") (Enveloppe @"
    <ban:EffectuerVirement>
      <compteDebit>MA-1005</compteDebit>
      <compteCredit>MA-1004</compteCredit>
      <montant>2500.0</montant>
    </ban:EffectuerVirement>
"@) "soap-virement"

AppelSoap "Endpoint securise SANS UsernameToken" ($Base + "banque-secure") (Enveloppe @"
    <ban:Saluer>
      <nomClient>Intrus</nomClient>
    </ban:Saluer>
"@) "soap-securise-sans-token"

AppelSoap "Endpoint securise AVEC UsernameToken valide (ahmed)" ($Base + "banque-secure") (EnveloppeSecurisee "ahmed" "Banque@2026" @"
    <ban:Saluer>
      <nomClient>Salma</nomClient>
    </ban:Saluer>
"@) "soap-securise-token-valide"

AppelSoap "Endpoint securise AVEC mauvais mot de passe" ($Base + "banque-secure") (EnveloppeSecurisee "ahmed" "mot-de-passe-errone" @"
    <ban:Saluer>
      <nomClient>Salma</nomClient>
    </ban:Saluer>
"@) "soap-securise-token-invalide"

$dateXml = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ss.fffZ")
$nonce = [Guid]::NewGuid().ToString("N")
AppelSoap "Endpoint PasswordDigest AVEC token valide (salma)" ($Base + "banque-digest") (EnveloppeSecuriseeDigest "salma" "Salma#Cxf" $nonce $dateXml @"
    <ban:ConsulterCompte>
      <numeroCompte>MA-1005</numeroCompte>
    </ban:ConsulterCompte>
"@) "soap-digest-token-valide"

$dateXml2 = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ss.fffZ")
$nonce2 = [Guid]::NewGuid().ToString("N")
AppelSoap "Endpoint PasswordDigest AVEC digest errone" ($Base + "banque-digest") (EnveloppeSecuriseeDigest "salma" "mauvais-secret" $nonce2 $dateXml2 @"
    <ban:ConsulterCompte>
      <numeroCompte>MA-1005</numeroCompte>
    </ban:ConsulterCompte>
"@) "soap-digest-token-invalide"

Write-Host "Termine."
