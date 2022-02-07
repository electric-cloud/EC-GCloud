package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.Plugin
import com.cloudbees.pdk.hen.procedures.*

class GCloud extends Plugin {
    static String pluginName = 'EC-GCloud'

    static GCloud create() {
        GCloud plugin = new GCloud(name: pluginName)
        plugin.configure(plugin.config)
        return plugin
    }

    static GCloud createWithoutConfig() {
        GCloud plugin = new GCloud(name: pluginName)
        return plugin
    }

    static GCloud createWithoutConfig(String resourceName) {
        GCloud plugin = new GCloud(name: pluginName, defaultResource: resourceName)
        return plugin
    }

    //user-defined after boilerplate was generated, default parameters setup
    GCloudConfig config = GCloudConfig
        .create(this)
    //.parameter(value) add parameters here

    EditConfiguration editConfig() {
        editConfiguration.addParam(this.configFieldNameCreateConfiguration, this.configName)
        return editConfiguration
    }

//    EditConfiguration editConfiguration = EditConfiguration.create(this)

    RunCustomCommand runCustomCommand = RunCustomCommand.create(this)

    TestConfiguration testConfiguration = TestConfiguration.create(this)

}