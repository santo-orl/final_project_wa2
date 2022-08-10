# final_project_wa2
Web Applications II final project 

L'applicazione è composta da 4 microservizi: 
* **login_service**: gestisce la registrazione di nuovi utenti (da confermare con email) e il login. Per quest'ultimo ritorna un jwt da allegare nell'header Bearer per le richieste agli altri microservizi.
* **traveler_service**: permette di ottenere e modificare info sui traveler, di ottenere info sui ticket e di aggiungere ticket alla lista di quelli comprati da un certo traveler
* **ticket_catalogue_service**: permette di ottenere e modificare la lista di tipi di ticket disponibili, la lista di ordini effettuati ed espone l'endpoint per l'acquisto di ticket
* **payment_service**: effettua le richieste di pagamento e fornisce la lista di transazioni effettuate

Elenco dei servizi offerti:

* traveler deve registrarsi con email e password
  * login_service POST /user/register
  * login_service POST /user/login
* traveler loggato deve gestire il suo profilo
  * traveler_service GET /my/profile
  * traveler_service POST /my/profile
* traveler loggato deve comprare tickets
* traveler loggato deve comprare travel cards
* traveler loggato deve consultare la lista degli acquisti
* traveler loggato deve scaricare singoli documenti di viaggio sottoforma di QR che rappresenta un JWS
* QR readers devono autenticarsi come sistemi embedded e quindi ottenere il segreto per validare il JWS
* QR readers devono validare il JWS e dare info su transit count
* amministratore loggato deve registrare altri amministratori
  * login_service POST /admin/register
* amministratore loggato può creare tipi di tickets
* amministratore loggato può modificare proprietà dei tipi di tickets (scritte su traccia)
* amministratore loggato può eliminare tipi di tickets
* amministratore loggato può creare tipi di travel cards
* amministratore loggato può modificare proprietà dei tipi di travel cards (scritte su traccia)
* amministratore loggato può eliminare tipi di travel cards
* amministratore loggato può accedere a report di acquisti relativi a singoli utenti o totali relativi a periodi di tempo selezionabili
* amministratore loggato può accedere a report di transiti relativi a singoli utenti o totali relativi a periodi di tempo selezionabili
