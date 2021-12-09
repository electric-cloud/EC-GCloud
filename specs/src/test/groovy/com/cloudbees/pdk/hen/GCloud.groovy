package com.cloudbees.pdk.hen

import com.cloudbees.pdk.hen.procedures.*
import com.cloudbees.pdk.hen.Plugin

import static com.cloudbees.pdk.hen.Utils.env

class GCloud extends Plugin {

    static GCloud create() {
        GCloud plugin = new GCloud(name: 'EC-GCloud')
        plugin.configure(plugin.config)
        return plugin
    }
    static GCloud createWithoutConfig() {
        GCloud plugin = new GCloud(name: 'EC-GCloud')
        return plugin
    }

    //user-defined after boilerplate was generated, default parameters setup
    GCloudConfig config = GCloudConfig
        .create(this)
        //.parameter(value) add parameters here


    EditConfiguration editConfiguration = EditConfiguration.create(this)

    RunAnything runAnything = RunAnything.create(this)

    RunCustomCommand runCustomCommand = RunCustomCommand.create(this)

    TestConfiguration testConfiguration = TestConfiguration.create(this)

}