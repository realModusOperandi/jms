package jms.app.beans

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource
import javax.ejb.*
import javax.jms.*

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
open class MqTopicBean {
    @Resource(lookup = "jms/mqRemoteTCF")
    open lateinit var cf: TopicConnectionFactory

    @Resource(lookup = "jms/mqRemoteTopic")
    open lateinit var topic: Topic

    open var conn: TopicConnection? = null
    open var session: TopicSession? = null
    open var subscriber: TopicSubscriber? = null

    open var messages: MutableList<Message> = mutableListOf()

    init {
    }

    @PostConstruct
    open fun initBean() {
        // Topics only keep messages for as long as it takes to send to all existing subscribers
        // (ignoring durable subscriptions) so the listener has to be set up immediately

    }

    @PreDestroy
    open fun tearDownBean() {
        println("Tearing down MQ topic bean")

    }

    open fun send(message: String) {
        conn = cf.createTopicConnection()
        conn!!.start()

        session = conn!!.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        subscriber = session!!.createDurableSubscriber(topic, "mqtest")
        val m = session!!.createMessage()
        m.setObjectProperty("testKey", message)

        val publisher = session!!.createPublisher(topic)
        publisher.send(m)

        publisher.close()
        subscriber!!.close()
        session!!.close()
        conn!!.close()
    }


    open fun <T : Message> receive(type: Class<T>): T {
        conn = cf.createTopicConnection()
        conn!!.start()

        session = conn!!.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        subscriber = session!!.createDurableSubscriber(topic, "mqtest")
        val result = type.cast(subscriber!!.receive(5000L))

        subscriber!!.close()
        session!!.close()
        conn!!.close()

        return type.cast(result)
    }

    open fun <T : Message> sendAndReceive(message: T, type: Class<T>): T {
        send(message.getObjectProperty("testKey") as String)
        return receive(type)
    }
}