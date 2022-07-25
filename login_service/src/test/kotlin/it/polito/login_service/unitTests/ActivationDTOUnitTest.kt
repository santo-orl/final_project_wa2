package it.polito.login_service.unitTests

class ActivationDTOUnitTest {

    /*  TODO
        Invece di creare user, activation e activationDTO in ogni funzione si potrebbero
        avere come campi della classe (tanto sono sempre uguali)
    */
/*

    @Test
    //check fun toDTO: activation and activationDTO should have the same user
    fun userTest(){
        val user = User(0,"username","password","email","status")
        val activation = Activation(UUID.randomUUID(),user,"x","28/02/1999",5)
        val activationDTO = activation.toDTO()
        assert(activationDTO.user.equals(activation.user))
    }

    @Test
    //check fun toDTO: activation and activationDTO should have the same activationCode
    fun activationCodeTest(){
        val user = User(0,"username","password","email","status")
        val activation = Activation(UUID.randomUUID(),user,"x","28/02/1999",5)
        val activationDTO = activation.toDTO()
        assert(activationDTO.activationCode.equals(activation.activationCode))
    }

    @Test
    //check fun toDTO: activation and activationDTO should have the same activationDeadline
    fun activationDeadlineTest(){
        val user = User(0,"username","password","email","status")
        val activation = Activation(UUID.randomUUID(),user,"x","28/02/1999",5)
        val activationDTO = activation.toDTO()
        assert(activationDTO.activationDeadline.equals(activation.activationDeadline))
    }

    @Test
    //check fun toDTO: activation and activationDTO should have the same attemptCounter
    fun attemptCounterTest(){
        val user = User(0,"username","password","email","status")
        val activation = Activation(UUID.randomUUID(),user,"x","28/02/1999",5)
        val activationDTO = activation.toDTO()
        assert(activationDTO.attemptCounter==activation.attemptCounter)
    }

 */

}