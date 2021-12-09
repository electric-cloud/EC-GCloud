package com.cloudbees.pdk.hen.procedures

import groovy.transform.AutoClone
import com.cloudbees.pdk.hen.*
import com.cloudbees.pdk.hen.*

@AutoClone
//generated
class RunCustomCommand extends Procedure {

    static RunCustomCommand create(Plugin plugin) {
        return new RunCustomCommand(procedureName: 'Run Custom Command', plugin: plugin, )
    }


    RunCustomCommand flush() {
        this.flushParams()
        this.contextUser = null
        return this
    }

    RunCustomCommand withUser(User user) {
        this.contextUser = user
        return this
    }

    //Generated
    
    RunCustomCommand command(String command) {
        this.addParam('command', command)
        return this
    }
    
    
    RunCustomCommand config(String config) {
        this.addParam('config', config)
        return this
    }
    
    
    RunCustomCommand group(String group) {
        this.addParam('group', group)
        return this
    }
    
    
    RunCustomCommand options(String options) {
        this.addParam('options', options)
        return this
    }
    
    
    RunCustomCommand resultPropertySheet(String resultPropertySheet) {
        this.addParam('resultPropertySheet', resultPropertySheet)
        return this
    }
    
    
    RunCustomCommand subCommands(String subCommands) {
        this.addParam('subCommands', subCommands)
        return this
    }
    
    
    
    
}