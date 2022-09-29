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
* logged admin can access global purchase report related to selectable time period
  * payment_service GET /admin/transactions/range/?from=xxx&to=yyy
* logged admin can access transit report related to single users with respect to selectable time period
  * traveler_service GET /admin/traveler/transits/{username}?from=xxx&to=yyy
* logged admin can access total transit report related to selectable time period
  * traveler_service GET /admin/transits/{username}?from=xxx&to=yyy
  
### Ticket e travelcard

 **Travelcards** are subscriptions with the following properties (apart from the id) (in ticket_catalogue_service):
* type: {WEEK, MONTH, YEAR}
* price: Float
* minAge: Int
* maxAge: Int
* zid: String
When a travelcard is bought, TravelcardPurchased in traveler_service is created and it has the following fields (apart from the id):
* type: {WEEK, MONTH, YEAR}
* zid: String
* validFrom: LocalDateTime
* validTo: LocalDateTime
* userDetails: UserDetailsImpl

Activity perios is made of validFrom and validTo: these fields are fulfilled at acquiring time (insert in traveler_service) and according to the type. A user can use a travelcard as much as they wants inside the period validity. The travelcard will be removed automatically from TravelcardPurchased at expiration validity time.
### Funzionamento dei QR

* Manually it is required to inser a user with role QE_READER in login_service.
* qr reader authenticates from login_service.
* qr reader receives the key to use for validating jws through GET /qr/validation to traveler_service.
* traveler has their ticket/travelcard purchased and they is able to download it singularly under qr code through GET to /my/tickets/qr/{ticketId} or to /my/travelcards/qr/{travelcardId} in traveler_service.* Il traveler avrà quindi il suo qr sul telefono, lo passa fisicamente dal qr reader. Quest'ultimo ha il jws per validarlo, quindi eseguirà internamente la validazione. 
* once validated, qr reader will contact the traveler_service for removing the ticket/travelcard purchased from the user list and update their transit list. The user is able to perform this operation with a post to /qr/ticket-validated or to /qr/travelcard-validated from traveler_service.
