package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class GCloudConfig extends Procedure {

    static GCloudConfig create(Plugin plugin) {
        return new GCloudConfig(procedureName: 'CreateConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
        ])
    }


    GCloudConfig flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }

    GCloudConfig withUser(User user) {
        this.contextUser = user
        return this
    }

    //Generated
    
    GCloudConfig checkConnection(boolean checkConnection) {
        this.addParam('checkConnection', checkConnection)
        return this
    }
    
    
    GCloudConfig checkConnectionResource(String checkConnectionResource) {
        this.addParam('checkConnectionResource', checkConnectionResource)
        return this
    }
    
    
    GCloudConfig config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    GCloudConfig configurationNameGCP(String configurationNameGCP) {
        this.addParam('configurationNameGCP', configurationNameGCP)
        return this
    }
    
    
    GCloudConfig debugLevel(String debugLevel) {
        this.addParam('debugLevel', debugLevel)
        return this
    }
    
    GCloudConfig debugLevel(DebugLevelOptions debugLevel) {
        this.addParam('debugLevel', debugLevel.toString())
        return this
    }
    
    
    GCloudConfig desc(String desc) {
        this.addParam('desc', desc)
        return this
    }
    
    
    GCloudConfig gcloudPath(String gcloudPath) {
        this.addParam('gcloudPath', gcloudPath)
        return this
    }
    
    
    GCloudConfig projectName(String projectName) {
        this.addParam('projectName', projectName)
        return this
    }
    
    
    GCloudConfig proprtiesGCP(String proprtiesGCP) {
        this.addParam('proprtiesGCP', proprtiesGCP)
        return this
    }
    
    
    
    GCloudConfig credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }

    GCloudConfig credentialReference(String path) {
        this.addCredentialReference('credential', path)
        return this
    }
    
    
    enum DebugLevelOptions {
    
    INFO("0"),
    
    DEBUG("1"),
    
    TRACE("2")
    
    private String value
    DebugLevelOptions(String value) {
        this.value = value
    }

    String toString() {
        return this.value
    }
}
    
}