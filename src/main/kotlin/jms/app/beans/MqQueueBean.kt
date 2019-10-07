package jms.app.beans

import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.ejb.*
import javax.jms.*

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
open class MqQueueBean {
    @Resource(lookup = "jms/mqRemoteQCF")
    open lateinit var cf: QueueConnectionFactory

    @Resource(lookup = "jms/mqRemoteQueue")
    open lateinit var queue: Queue

    open var conn: QueueConnection? = null
    open var session: QueueSession? = null

    init {

    }

    @PostConstruct
    open fun initBean() {
//        conn = cf.createQueueConnection()
//        conn.start()
//
//        session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
        //clearQueue(queue)
    }


    open fun clearQueue(queue: Queue): Int {
        var elements = 0

        val consumer = session!!.createConsumer(queue)
        while (null != consumer.receiveNoWait()) {
            elements++
        }
        consumer.close()

        return elements
    }


    open fun send(message: String, destination: Destination) {
        conn = cf.createQueueConnection()
        conn!!.start()

        session = conn!!.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)

        val m = session!!.createMessage()
        m.setObjectProperty("testKey", message)

        val producer = session!!.createProducer(destination)
        producer.send(m)

        producer.close()
        session!!.close()
        conn!!.close()
    }


    open fun <T : Message> receive(type: Class<T>, destination: Destination): T {
        conn = cf.createQueueConnection()
        conn!!.start()

        session = conn!!.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)

        val consumer = session!!.createConsumer(destination)
        val result = type.cast(consumer.receive(5000L))

        consumer.close()
        session!!.close()
        conn!!.close()

        return result
    }

    open fun <T : Message> sendAndReceive(message: T, type: Class<T>, destination: Destination): T {
        send(message.getObjectProperty("testKey") as String, destination)
        return receive(type, destination)
    }
}