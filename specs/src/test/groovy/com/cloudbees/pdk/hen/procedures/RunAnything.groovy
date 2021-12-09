package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class RunAnything extends Procedure {

    static RunAnything create(Plugin plugin) {
        return new RunAnything(procedureName: 'Run Anything', plugin: plugin, )
    }


    RunAnything flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }

    RunAnything withUser(User user) {
        this.contextUser = user
        return this
    }

    //Generated
    
    RunAnything anything(String anything) {
        this.addParam('anything', anything)
        return this
    }
    
    
    RunAnything config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    RunAnything resultPropertySheet(String resultPropertySheet) {
        this.addParam('resultPropertySheet', resultPropertySheet)
        return this
    }
    
    
    
    
}