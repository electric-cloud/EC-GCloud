package com.cloudbees.pdk.hen.procedures

import com.cloudbees.pdk.hen.Plugin
import com.cloudbees.pdk.hen.Procedure
import com.cloudbees.pdk.hen.User
import groovy.transform.AutoClone

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

    EditConfiguration withUser(User user) {
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

    TestConfiguration authType(String authType) {
        this.addParam('authType', authType ?: "env")
        return this
    }

    TestConfiguration authType(AuthTypeOptions authType) {
        this.addParam('authType', authType.toString())
        return this
    }

    TestConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }


    TestConfiguration gcloudConfigurationName(String gcloudConfigurationName) {
        this.addParam('gcloudConfigurationName', gcloudConfigurationName)
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


    TestConfiguration gcloudProprties(String gcloudProprties) {
        this.addParam('gcloudProprties', gcloudProprties)
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

    enum AuthTypeOptions {
        KEY("key"),
        INSTANCE_METADATA("env")

        private String value
        AuthTypeOptions(String value) {
            this.value = value
        }

        String toString() {
            return this.value
        }
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