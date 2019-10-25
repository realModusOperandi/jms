package jms.app

import jms.app.beans.MqQueueBean
import jms.app.beans.MqTopicBean
import javax.ejb.EJB
import javax.ejb.Stateless
import javax.jms.Message
import javax.ws.rs.*

@Path("/mq/topic")
@Stateless
open class MqTopicApi {
    @EJB
    open lateinit var remoteBean: MqTopicBean

    @GET
    @Path("/receive")
    @Produces("application/json")
    open fun receiveMessage(): List<String> {
        return try {
            listOf(remoteBean.receive(Message::class.java).getStringProperty("testKey") as String)
        } catch (e: Exception) {
            listOf("An error occurred: ${e.message}")
        }
    }

    @POST
    @Path("/send")
    @Produces("application/json")
    open fun sendMessage(message: String): List<String> {
        remoteBean.send(message)
        return listOf("Sent $message")
    }
}