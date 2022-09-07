package it.polito.ticket_catalogue_service.unitTest

import it.polito.ticket_catalogue_service.exceptions.NullOrderException
import it.polito.ticket_catalogue_service.repository.OrderRepository
import it.polito.ticket_catalogue_service.service.OrderService
import kotlinx.coroutines.flow.toList
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner

class OrderUnitTest {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)

    class OrderTest {

        lateinit var orderService : OrderService
        lateinit var orderRepository: OrderRepository

        init{
            orderService = Mockito.mock(OrderService::class.java)
            orderRepository = Mockito.mock(OrderRepository::class.java)
        }

        @Test
        suspend fun checkGetOrder(){
            //se ordine non c'è
            var ret = orderService.getOrder(0L)
            assert(ret==null)
        }

        @Test
        suspend fun checkGetAllOrders(){
            var ret = orderService.getAllOrders()
            assert(ret.toList().isNotEmpty())
        }

        @Test
        suspend fun checkGetUserOrder(){
            //se user non c'è
            var ret = orderService.getUserOrders("userIdExample")
            assert(ret==null)
        }

        @Test
        suspend fun checkCreateOrderBadUsername() {
            var ret = orderService.createOrder("", 2, 0L)
            assert(ret== 0L)
        }

        @Test
        suspend fun checkCreateOrderBadTicket() {
            var ret = orderService.createOrder("user", 2000, 0L)
            assert(ret== 0L)
        }

        @Test
        suspend fun checkCreateOrderBadTicketId() {
            var ret = orderService.createOrder("user", 2, 999L)
            assert(ret== 0L)
        }

        @Test
        suspend fun checkCreateOrderAllWrong() {
            var ret = orderService.createOrder("", 2000, 999L)
            assert(ret== 0L)
        }

        //TODO: checkCreateTravelcardOrder
        @Test
        suspend fun checkCreateTravelcardOrder(){
            var ret = orderService.createTravelcardOrder("user", 0L)
            assert(ret==0L)
        }

        //TODO: controllare le send??
    }
}