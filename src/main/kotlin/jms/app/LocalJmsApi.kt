package jms.app

import jms.app.beans.LocalQueueBean
import javax.ejb.EJB
import javax.ejb.Stateless
import javax.jms.Destination
import javax.jms.Message
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/local")
@Stateless
open class LocalJmsApi {
    @EJB
    open lateinit var localBean: LocalQueueBean

    @GET
    @Path("echo/{message}")
    @Produces("application/json")
    open fun echoTest(@PathParam("message") message: String): List<String> {
        val m = localBean.session.createMessage()
        m.setObjectProperty("testKey", message)
        return listOf(localBean.sendAndReceive(m, Message::class.java, localBean.queue).getObjectProperty("testKey") as String)
    }
}