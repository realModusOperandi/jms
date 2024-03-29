package jms.app

import jms.app.beans.LocalMessageProducer
import javax.ejb.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/mdb/local")
@Stateless
open class LocalMdbApi {

    @EJB
    open lateinit var producer: LocalMessageProducer

    @POST
    @Path("/do_message")
    @Produces(MediaType.APPLICATION_JSON)
    open fun doMessage(message: String): List<String> {
        val m = producer.session.createTextMessage(message)
        producer.send(m, producer.queue)
        return listOf("Sent $message")
    }
}