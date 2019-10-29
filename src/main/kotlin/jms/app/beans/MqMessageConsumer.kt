package jms.app.beans

import javax.ejb.ActivationConfigProperty
import javax.ejb.MessageDriven
import javax.ejb.TransactionManagement
import javax.ejb.TransactionManagementType
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage

@MessageDriven(activationConfig = [
    ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Queue")
])
@TransactionManagement(TransactionManagementType.BEAN)
open class MqMessageConsumer : MessageListener {
    override fun onMessage(message: Message) {
        val textMessage = message as TextMessage;
        try {
            println("MQ Message received: ${textMessage.text}")
        } catch (e: JMSException) {
            println("Error while trying to consume MQ messages: ${e.message}")
        }
    }
}