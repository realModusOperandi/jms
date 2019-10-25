package jms.app.beans

import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.ejb.*
import javax.jms.*

@Stateless
open class MqMessageProducer {

    @Resource(lookup = "jms/mqMdbQcf")
    open lateinit var cf: ConnectionFactory

    @Resource(lookup = "jms/mqMdbQueue")
    open lateinit var queue: Queue

    open lateinit var conn: Connection
    open lateinit var session: Session

    init {

    }

    @PostConstruct
    open fun initBean() {
        conn = cf.createConnection()
        conn.start()

        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)
        clearQueue(queue)
    }

    open fun clearQueue(queue: Queue): Int {
        var elements = 0

        val consumer = session.createConsumer(queue)
        while (null != consumer.receiveNoWait()) {
            elements++
        }
        consumer.close()

        return elements
    }

    open fun <T : Message> send(message: T, destination: Destination) {
        val producer = session.createProducer(destination)
        producer.send(message)
        producer.close()
    }
}