# final_project_wa2
Web Applications II final project 

### Microservizi

L'applicazione è composta da 4 microservizi: 
* **login_service**: gestisce la registrazione di nuovi utenti (da confermare con email) e il login. Per quest'ultimo ritorna un jwt da allegare nell'header Bearer per le richieste agli altri microservizi.
* **traveler_service**: permette di ottenere e modificare info sui traveler, di ottenere info sui ticket e di aggiungere ticket alla lista di quelli comprati da un certo traveler. Inoltre contiene i path per le cose dei qr reader
* **ticket_catalogue_service**: permette di ottenere e modificare la lista di tipi di ticket disponibili, la lista di ordini effettuati ed espone l'endpoint per l'acquisto di ticket
* **payment_service**: effettua le richieste di pagamento e fornisce la lista di transazioni effettuate

I microservizi espongono **api** a cui utenti (traveler, admin, qr reader) possono fare richieste.
I microservizi comunicano fra loro tramite **Kafka**. In particolare:
* **ticket_catalogue_service** comunica con **payment_service** tramite il topic PaymentRequestTopic per mandare richieste di pagamento
* **payment_service** comunica con **ticket_catalogue_service** tramite il topic PaymentResponseTopic per mandare l'esito del pagamento
* **ticket_catalogue_service** comunica con **traveler_service** tramite il topic TicketPurchasedTopic per dirgli - in seguito a un pagamento avvenuto con successo - di aggiungere ticket alla tabella dei ticket acquistati
* **ticket_catalogue_service** comunica con **traveler_service** tramite il topic TravelcardPurchasedTopic per dirgli - in seguito a un pagamento avvenuto con successo - di aggiungere travelcard alla tabella delle travelcard acquistate

### Servizi richiesti

* traveler deve registrarsi con email e password
  * login_service POST /user/register
  * login_service POST /user/login
* traveler loggato deve gestire il suo profilo
  * traveler_service GET /my/profile
  * traveler_service POST /my/profile
* traveler loggato deve comprare tickets
  * ticket_catalogue_service POST /shop/tickets/{ticket-id}
* traveler loggato deve comprare travel cards
  * ticket_catalogue_service POST /shop/travelcards/{travelcard-id}
* traveler loggato deve consultare la lista degli acquisti
  * ticket_catalogue_service GET /orders
* traveler loggato deve scaricare singoli documenti di viaggio sottoforma di QR che rappresenta un JWS
  * traveler_service GET /my/tickets/qr/{ticketId}
* QR readers devono autenticarsi come sistemi embedded
  * login_service POST /user/login
* QR readers loggati devono ottenere il segreto per validare il JWS
  * traveler_service GET /qr/validation
* QR readers loggati devono validare il JWS e dare info su transit count
  * traveler_service POST /qr/ticket-validated
* amministratore loggato deve registrare altri amministratori
  * login_service POST /admin/register
* amministratore loggato può creare tipi di tickets
  * ticket_catalogue_service POST /admin/tickets
* amministratore loggato può modificare proprietà dei tipi di tickets
  * ticket_catalogue_service PUT /admin/tickets/{ticket-id}
* amministratore loggato può eliminare tipi di tickets
  * ticket_catalogue_service DELETE /admin/tickets/{ticket-id}
* amministratore loggato può creare tipi di travel cards
  * ticket_catalogue_service POST /admin/travelcards
* amministratore loggato può modificare proprietà dei tipi di travel cards
  * ticket_catalogue_service PUT /admin/travelcards/{travelcard-id}
* amministratore loggato può eliminare tipi di travel cards
  * ticket_catalogue_service DELETE /admin/travelcards/{travelcard-id}
* amministratore loggato può accedere a report di acquisti relativi a singoli utenti relativi a periodi di tempo selezionabili
  * payment_service GET /admin/transactions/user/range/?from=xxx&to=yyy
* amministratore loggato può accedere a report di acquisti totali relativi a periodi di tempo selezionabili
  * payment_service GET /admin/transactions/range/?from=xxx&to=yyy
* amministratore loggato può accedere a report di transiti relativi a singoli utenti relativi a periodi di tempo selezionabili
  * traveler_service GET /admin/traveler/transits/{username}?from=xxx&to=yyy
* amministratore loggato può accedere a report di transiti totali relativi a periodi di tempo selezionabili
  * traveler_service GET /admin/transits/{username}?from=xxx&to=yyy

### Servizi aggiuntivi

* admin loggato può ottenere la lista di traveler
  * traveler_service GET /admin/travelers
* admin loggato può ottenere il profilo di un traveler dato il suo userID
  * traveler_service GET /admin/traveler/{userID}/profile
* admin loggato può ottenere la lista di ticket attualmente in possesso dell'utente
  * traveler_service GET /admin/traveler/{userID}/tickets
* traveler loggato può ottenere la lista di ticket attualmente in suo possesso
  * traveler_service GET /my/tickets 
* traveler loggato può ottenere il catalogo dei tipi di ticket disponibili
  * ticket_catalogue_service GET /tickets 
* traveler loggato può consultare un suo acquisto nello specifico
  * ticket_catalogue_service GET /orders/{order-id}
* amministratore loggato può consultare l'elenco degli ordini di tutti gli utenti
  * ticket_catalogue_service GET /admin/orders
* amministratore loggato può consultare l'elenco degli ordini di uno specifico utente
  * ticket_catalogue_service GET /admin/orders/{user-id}
