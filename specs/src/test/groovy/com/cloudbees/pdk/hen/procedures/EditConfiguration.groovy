package com.cloudbees.pdk.hen.procedures

import com.cloudbees.pdk.hen.Plugin
import com.cloudbees.pdk.hen.Procedure
import com.cloudbees.pdk.hen.User
import groovy.transform.AutoClone

@AutoClone
//generated
class EditConfiguration extends Procedure {

    static EditConfiguration create(Plugin plugin) {
        return new EditConfiguration(procedureName: 'EditConfiguration', plugin: plugin, credentials: [
            'credential': null,
        ])
    }


    EditConfiguration flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }

    EditConfiguration withUser(User user) {
        this.contextUser = user
        return this
    }

    //Generated

    EditConfiguration checkConnection(boolean checkConnection) {
        this.addParam('checkConnection', checkConnection)
        return this
    }


    EditConfiguration checkConnectionResource(String checkConnectionResource) {
        this.addParam('checkConnectionResource', checkConnectionResource)
        return this
    }

    EditConfiguration authType(String authType) {
        this.addParam('authType', authType ?: "env")
        return this
    }

    EditConfiguration authType(AuthTypeOptions authType) {
        this.addParam('authType', authType.toString())
        return this
    }

    EditConfiguration config(String config) {
        this.addParam('config', config)
        return this
    }


    EditConfiguration gcloudCconfigurationName(String gcloudCconfigurationName) {
        this.addParam('gcloudCconfigurationName', gcloudCconfigurationName)
        return this
    }


    EditConfiguration debugLevel(String debugLevel) {
        this.addParam('debugLevel', debugLevel)
        return this
    }

    EditConfiguration debugLevel(DebugLevelOptions debugLevel) {
        this.addParam('debugLevel', debugLevel.toString())
        return this
    }


    EditConfiguration desc(String desc) {
        this.addParam('desc', desc)
        return this
    }


    EditConfiguration gcloudPath(String gcloudPath) {
        this.addParam('gcloudPath', gcloudPath)
        return this
    }


    EditConfiguration projectName(String projectName) {
        this.addParam('projectName', projectName)
        return this
    }


    EditConfiguration gcloudProprties(String gcloudProprties) {
        this.addParam('gcloudProprties', gcloudProprties)
        return this
    }


    EditConfiguration credential(String user, String password) {
        this.addCredential('credential', user, password)
        return this
    }

    EditConfiguration credentialReference(String path) {
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