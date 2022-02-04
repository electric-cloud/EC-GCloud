package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.GCloud
import com.cloudbees.pdk.hen.JobOutcome
import com.cloudbees.pdk.hen.JobResponse
import com.cloudbees.pdk.hen.Utils
import com.cloudbees.pdk.hen.models.Project
import com.cloudbees.pdk.hen.procedures.GCloudConfig
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

import static com.cloudbees.pdk.hen.Utils.setConfigurationValues

//@Ignore
@Slf4j
@Stepwise
class RunAnythingTest extends PluginTestHelper {
    @Shared
    GCloud plugin

    def setupSpec() {
        project = new Project(projectName)
        project.create()
        resource = createResource(gcloudAgentHost, gcloudAgentPort)
        plugin = GCloud.createWithoutConfig(resource.getName())
    }

    def cleanupSpec() {
        project.delete()
    }

    @Unroll
    def 'RunAnything: #caseId: #command'() {
        when:
        GCloudConfig gcloudConfig = GCloudConfig.create(plugin)
        gcloudConfig.gcloudPath(gcloudPath)
            .authType('key')
            .credential('', gcloudKey)
            .gcloudConfigurationName(gcloudConfigurationName)
            .projectName(gcloudProject)
            .gcloudProprties("""compute/zone ${gcloudZone}""")
            .checkConnectionResource(resource.getName())
            .debugLevel("2")

        setConfigurationValues(Utils.CredsStates.RUNTIME, credentialFieldName, credential, gcloudConfig)
        plugin.configure(gcloudConfig)

        and: "Run Plugin Procedure - Run Custom Command"
        JobResponse response = plugin.runAnything
            .anything(anything)
            .resultPropertySheet("/myJob/runAnything")
            .run(runOpts)

        then:
        assert response.isSuccessful()
        assert response.outcome == outcome
        String result = response.jobProperties["runAnything"].toString()
//        println("#001: " + result)
        assert result =~ expected

        where:
        caseId | anything                                       | outcome            | expected
        "#020" | "#!/usr/bin/bash\n\necho \"Hello Bash!\""      | JobOutcome.SUCCESS | "Hello Bash!"
        "#021" | "#!/usr/bin/perl\n\nprint \"Hello Perl!\\n\";" | JobOutcome.SUCCESS | "Hello Perl!"
    }

}
