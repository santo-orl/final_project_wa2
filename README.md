# final_project_wa2
Web Applications II final project 

### Microservizi

The application is made of four microservices:
* **login_service**: it manages the registration of new users(to be confirmed through email) and the login. For the login, it returns a JWT to be attached in the Bearer header for requests to the other microservices.
* **traveler_service**: it allows to obtain and modify info on travelers, to obtain infor on ticket/travelcard and to add ticket/travelcard to the list  of the ones bought by a certain traveler. Furthermore it contains paths for stuffs related to QR reader.
* **ticket_catalogue_service**: it allows to obtain and modify the list of types of available ticket/travelcard, the list of orders done and exposes the endpoint for the shop of tickets
* **payment_service**: it makes payment requests and it gives the list of completed transactions 

Microservices expose **api** to which users (traveler, admin, qr reader) can make requests.
Microservices communicate each other through **Kafka**. Specifically:
* **ticket_catalogue_service** communicates with **payment_service** through topic PaymentRequestTopic for sending payment requests
* **payment_service** communicates with **ticket_catalogue_service** through topic PaymentResponseTopic for sending the result of the payment
* **ticket_catalogue_service** communicates with **traveler_service** through topic TicketPurchasedTopic for saying - after a successful payment - to add tickets to the table of ticket purchased
* **ticket_catalogue_service** communicates with **traveler_service** through topic TravelcardPurchasedTopic for saying - after a successful payment - to add travelcards to the table of travelcard purchased

### Servizi richiesti

* traveler must register with username and password
  * login_service POST /user/register
  * login_service POST /user/login
* logged traveler must handle his profile
  * traveler_service GET /my/profile
  * traveler_service POST /my/profile
* logged traveler must buy tickets
  * ticket_catalogue_service POST /shop/tickets/{ticket-id}
* logged traveler myst buy travelcards
  * ticket_catalogue_service POST /shop/travelcards/{travelcard-id}
* logged traveler must check shop list 
  * ticket_catalogue_service GET /orders
* logged traveler must download single travel documents through the QR which represents a JWS
  * traveler_service GET /my/tickets/qr/{ticketId}
* QR readers must authenticate themselves as embedded systems
  * login_service POST /user/login
* QR logged readers must obtain the secret to validate the JWS
  * traveler_service GET /qr/validation
* QR logged readers must validate the JWS and give info on transit count
  * traveler_service POST /qr/ticket-validated
* logged admin must register other admins
  * login_service POST /admin/register
* logged admin can create types of tickets
  * ticket_catalogue_service POST /admin/tickets
* logged admin can modify the property of types of tickets
  * ticket_catalogue_service PUT /admin/tickets/{ticket-id}
* logged admin can delete the types of tickets
  * ticket_catalogue_service DELETE /admin/tickets/{ticket-id}
* logged admin can create types of travelcards 
  * ticket_catalogue_service POST /admin/travelcards
* logged admin can modify properties of types of travelcards
  * ticket_catalogue_service PUT /admin/travelcards/{travelcard-id}
* logged admin can delete types of travelcards
  * ticket_catalogue_service DELETE /admin/travelcards/{travelcard-id}
* logged admin can see the report of acquisti relativi a singoli utenti relativi a periodi di tempo selezionabili
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
  
### Ticket e travelcard

Le **travelcard** sono abbonamenti con le seguenti proprietà (oltre all'id) (in ticket_catalogue_service):
* type: {WEEK, MONTH, YEAR}
* price: Float
* minAge: Int
* maxAge: Int
* zid: String
Quando la travelcard viene acquistata si crea la TravelcardPurchased in traveler_service e ha i seguenti campi (oltre all'id):
* type: {WEEK, MONTH, YEAR}
* zid: String
* validFrom: LocalDateTime
* validTo: LocalDateTime
* userDetails: UserDetailsImpl
Il periodo di validità è dato da validFrom e validTo: questi campi vengono riempiti al momento dell'acquisto (inserimento in traveler_service) e in base al type. Un utente può usare una travelcard quante volte vuole all'interno del periodo di validità. La travelcard verrà rimossa in automatico dalle TravelcardPurchased alla scadenza del periodo di validità.

### Funzionamento dei QR

* Manualmente bisogna inserire uno user con role QR_READER in login_service.
* Il qr reader si autentica da login_service
* Il qr reader riceve la chiave da usare per validare i jws con la chiamata GET /qr/validation a traveler_service
* Il traveler ha i suoi ticket/travelcard purchased e può scaricarli singolarmente sotto forma di qr con una get a /my/tickets/qr/{ticketId} o a /my/travelcards/qr/{travelcardId} di traveler_service
* Il traveler avrà quindi il suo qr sul telefono, lo passa fisicamente dal qr reader. Quest'ultimo ha il jws per validarlo, quindi eseguirà internamente la validazione. 
* Una volta validato, il qr reader dovrà contattare traveler_service per rimuovere il ticket/travelcard purchased dalla lista dell'utente e aggiornare il rispettivo elenco dei transiti. Lo fa con una post a /qr/ticket-validated o a /qr/travelcard-validated di traveler_service
