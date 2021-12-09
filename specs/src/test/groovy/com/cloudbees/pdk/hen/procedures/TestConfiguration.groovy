package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class TestConfiguration extends Procedure {

    static TestConfiguration create(Plugin plugin) {
        return new TestConfiguration(procedureName: 'TestConfiguration', plugin: plugin, credentials: [
            
            'credential': null,
            
        ])
    }


    TestConfiguration flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }

    TestConfiguration withUser(User user) {
        this.contextUser = user
        return this
    }

    //Generated
    
    TestConfiguration checkConnection(boolean checkConnection) {
        this.addParam('checkConnection', checkConnection)
        return this
    }
    
    
    TestConfiguration checkConnectionResource(String checkConnectionResource) {
        this.addParam('checkConnectionResource', checkConnectionResource)
        return this
    }
    
    
    TestConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    TestConfiguration configurationNameGCP(String configurationNameGCP) {
        this.addParam('configurationNameGCP', configurationNameGCP)
        return this
    }
    
    
    TestConfiguration debugLevel(String debugLevel) {
        this.addParam('debugLevel', debugLevel)
        return this
    }
    
    TestConfiguration debugLevel(DebugLevelOptions debugLevel) {
        this.addParam('debugLevel', debugLevel.toString())
        return this
    }
    
    
    TestConfiguration desc(String desc) {
        this.addParam('desc', desc)
        return this
    }
    
    
    TestConfiguration gcloudPath(String gcloudPath) {
        this.addParam('gcloudPath', gcloudPath)
        return this
    }
    
    
    TestConfiguration projectName(String projectName) {
        this.addParam('projectName', projectName)
        return this
    }
    
    
    TestConfiguration proprtiesGCP(String proprtiesGCP) {
        this.addParam('proprtiesGCP', proprtiesGCP)
        return this
    }
    
    
    
    TestConfiguration credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }

    TestConfiguration credentialReference(String path) {
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