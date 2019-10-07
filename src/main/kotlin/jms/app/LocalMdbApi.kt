package jms.app

import jms.app.beans.LocalMessageProducer
import javax.ejb.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/mdb/local")
@Stateless
open class LocalMdbApi {

    @EJB
    open lateinit var producer: LocalMessageProducer

    @GET
    @Path("/do_message/{message}")
    @Produces(MediaType.APPLICATION_JSON)
    open fun doMessage(@PathParam("message") message: String): List<String> {
        val m = producer.session.createTextMessage(message)
        producer.send(m, producer.queue)
        return listOf("Sent $message")
    }
}