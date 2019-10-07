package jms.app

import jms.app.beans.MqQueueBean
import javax.ejb.EJB
import javax.ejb.Stateless
import javax.jms.Message
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/mq")
@Stateless
open class MqApi {
    @EJB
    open lateinit var remoteBean: MqQueueBean

    @GET
    @Path("receive")
    @Produces("application/json")
    open fun receiveMessage(): List<String> {
        return try {
            listOf(remoteBean.receive(Message::class.java, remoteBean.queue).getStringProperty("testKey") as String)
        } catch (e: Exception) {
            listOf("An error occurred: ${e.message}")
        }
    }

    @GET
    @Path("send/{message}")
    @Produces("application/json")
    open fun sendMessage(@PathParam("message") message: String): List<String> {
        remoteBean.send(message, remoteBean.queue)
        return listOf("Sent $message")
    }
}