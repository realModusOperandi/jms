package jms.app

import jms.app.beans.MqMessageProducer
import javax.ejb.*
import javax.jms.Session
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/mq/mdb")
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
open class MqMdbApi {

    @EJB
    open lateinit var producer: MqMessageProducer

    @POST
    @Path("/do_message")
    @Produces(MediaType.APPLICATION_JSON)
    open fun doMessage(message: String): List<String> {
        val conn = producer.cf.createQueueConnection()
        conn.start()

        val session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
        val m = session.createTextMessage(message)
        producer.send(m, producer.queue)
        return listOf("Sent $message")
    }
}