package jms.app.beans

import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.ejb.*
import javax.jms.*

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
open class MqMessageProducer {

    @Resource(lookup = "jms/mqMdbQcf")
    open lateinit var cf: QueueConnectionFactory

    @Resource(lookup = "jms/mqMdbQueue")
    open lateinit var queue: Queue

    open var conn: QueueConnection? = null
    open var session: QueueSession? = null

    init {

    }

    open fun <T : Message> send(message: T, destination: Destination) {
        conn = cf.createQueueConnection()
        conn!!.start()

        session = conn!!.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)

        val producer = session!!.createProducer(destination)
        producer.send(message)
        producer.close()
    }
}