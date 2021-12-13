package com.cloudbees.plugin.spec

import com.electriccloud.spec.PluginSpockTestSupport
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.util.concurrent.PollingConditions
import com.cloudbees.pdk.hen.*

class PluginTestHelper extends PluginSpockTestSupport {
    static def configId = 1
    static def pluginName = GCloud.pluginName
    static final String projectName = 'GCloud Spec Tests'

    static def procedureName = ""
    static String CONFIG_NAME = 'specConfig'


    String getZone() {
        return Utils.env("GCP_ZONE", 'us-east1-b')
    }

    String getProjectName() {
        return Utils.env("GCP_PROJECT")
    }

    String getKeyClean() {
        def key = Utils.env("GCP_KEY", "")
        if (!key) {
            key = Utils.env('GCP_KEY_BASE64')
            key = new String(key.decodeBase64()).trim()
        }

        return key
    }

    String getKey() {
        def key = getKeyClean()
        def parsed = new JsonSlurper().parseText(key)
        JsonOutput.toJson(parsed).replaceAll(/\\n/, /\\\\n/)
    }

    def getStepSummary(def jobId, def stepName) {
        assert jobId
        def summary
        def property = "/myJob/jobSteps/RunProcedure/steps/$stepName/summary"
        try {
            summary = getJobProperty(property, jobId)
        } catch (Throwable e) {
            logger.debug("Can't retrieve Upper Step Summary from the property: '$property'; check job: " + jobId)
        }
        return summary
    }
}

