package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.GCloud
import com.cloudbees.pdk.hen.models.Project
import com.cloudbees.pdk.hen.models.Resource
import com.cloudbees.pdk.hen.procedures.TestConfiguration
import spock.lang.Shared
import spock.lang.Unroll

class CreateConfigurationTest extends PluginTestHelper {
    static String projectName = "GCloudTestProject"
    static Project project
    static Resource resource

    @Shared
    TestConfiguration procedure

    def setupSpec() {
        project = new Project(projectName)
        project.create()
        resource = createResource(gcloudAgentHost, gcloudAgentPort)
        def plugin = GCloud.createWithoutConfig()
        procedure = plugin.testConfiguration
    }

    def cleanupSpec() {
        project.delete()
    }

    @Unroll
    def 'Check config'() {
        when:
        def response = procedure.flush()
            .gcloudPath(gcloudPath)
            .authType('key')
            .credential('', gcloudKey)
            .gcloudCconfigurationName(gcloudConfigurationName)
            .projectName(gcloudProject)
            .gcloudProprties("""compute/zone ${gcloudZone}""")
            .checkConnectionResource(resource.getName())
            .runNaked()
        then:
        assert response.successful
    }

    @Unroll
    def 'Check config: invalid key'() {
        when:
        def response = procedure.flush()
            .gcloudPath(gcloudPath)
            .authType('key')
            .credential('', "{}")
            .gcloudCconfigurationName(gcloudConfigurationName)
            .projectName(gcloudProject)
            .gcloudProprties("""compute/zone ${gcloudZone}""")
            .checkConnectionResource(resource.getName())
            .runNaked()
        then:
        assert !response.successful
        and:
        assert response.jobProperties.configError =~ /The \.json key file is not in a valid format/
    }

    @Unroll
    def 'Check config: invalid resource'() {
        when:
        def response = procedure.flush()
            .gcloudPath(gcloudPath)
            .authType('key')
            .credential('', gcloudKey)
            .gcloudCconfigurationName(gcloudConfigurationName)
            .projectName(gcloudProject)
            .gcloudProprties("""compute/zone ${gcloudZone}""")
//            .checkConnectionResource("")
            .runNaked()
        then:
        assert !response.successful
        and:
        assert response.jobProperties.configError =~ /No such file or directory/
    }
}
