AdminTask.createSIBus('[-bus jmsbus -busSecurity false -scriptCompatibility 6.1 ]')
AdminTask.addSIBusMember('[-bus jmsbus -node DefaultNode01 -server server1 -fileStore  -logSize 100 -minPermanentStoreSize 200 -maxPermanentStoreSize 500 -unlimitedPermanentStoreSize false -minTemporaryStoreSize 200 -maxTemporaryStoreSize 500 -unlimitedTemporaryStoreSize false ]')
AdminTask.createSIBDestination('[-bus jmsbus -name JMSTestJSD -type Queue -reliability ASSURED_PERSISTENT -description  -node DefaultNode01 -server server1 ]')
AdminTask.createSIBJMSConnectionFactory('DefaultNode01(cells/DefaultCell01/nodes/DefaultNode01|node.xml)', '[-type queue -name JMSTestQCF -jndiName jms/localCF -description  -category  -busName jmsbus -nonPersistentMapping ExpressNonPersistent -readAhead Default -tempQueueNamePrefix  -target  -targetType BusMember -targetSignificance Preferred -targetTransportChain  -providerEndPoints  -connectionProximity Bus -authDataAlias  -containerAuthAlias  -mappingAlias  -shareDataSourceWithCMP false -logMissingTransactionContext false -manageCachedHandles false -xaRecoveryAuthAlias  -persistentMapping ReliablePersistent -consumerDoesNotModifyPayloadAfterGet false -producerDoesNotModifyPayloadAfterSet false]')
AdminTask.createSIBJMSQueue('DefaultNode01(cells/DefaultCell01/nodes/DefaultNode01|node.xml)', '[-name JMSTestQueue -jndiName jms/jmsQueue1 -description  -deliveryMode NonPersistent -readAhead AsConnection -busName  -queueName JMSTestJSD -scopeToLocalQP false -producerBind false -producerPreferLocal true -gatherMessages false]')

AdminApp.install('/work/app/jms.war', '[  -nopreCompileJSPs -distributeApp -nouseMetaDataFromBinary -nodeployejb -appname jms -createMBeansForResources -noreloadEnabled -nodeployws -validateinstall warn -noprocessEmbeddedConfig -filepermission .*\.dll=755#.*\.so=755#.*\.a=755#.*\.sl=755 -noallowDispatchRemoteInclude -noallowServiceRemoteInclude -asyncRequestDispatchType DISABLED -nouseAutoLink -noenableClientModule -clientMode isolated -novalidateSchema -contextroot jms  -MapModulesToServers [[ jms.war jms.war,WEB-INF/web.xml WebSphere:cell=DefaultCell01,node=DefaultNode01,server=server1 ]] -MapWebModToVH [[ jms.war jms.war,WEB-INF/web.xml default_host ]] -CtxRootForWebMod [[ jms.war jms.war,WEB-INF/web.xml jms ]]]' )

AdminConfig.save()