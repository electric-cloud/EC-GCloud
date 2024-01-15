package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.GCloud
import com.cloudbees.pdk.hen.models.Project
import com.cloudbees.pdk.hen.models.Resource
import com.cloudbees.pdk.hen.procedures.TestConfiguration
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

@Ignore
@Slf4j
@Stepwise
class CreateConfigurationTest extends PluginTestHelper {
    @Shared
    TestConfiguration procedure

    def setupSpec() {
        project = new Project(projectName)
        project.create()
        resource = createResource(gcloudAgentHost, gcloudAgentPort)
        GCloud plugin = GCloud.createWithoutConfig(resource.getName())
        procedure = plugin.testConfiguration
    }

    def cleanupSpec() {
        project.delete()
    }

    @Unroll
    def 'Check config: key'() {
        when:
        def response = procedure.flush()
            .gcloudPath(gcloudPath)
            .authType('key')
            .credential('', gcloudKey)
            .gcloudConfigurationName(gcloudConfigurationName)
            .projectName(gcloudProject)
            .gcloudProprties("""compute/zone ${gcloudZone}""")
            .checkConnectionResource(resource.getName())
            .runNaked()
        then:
        assert response.successful
    }

    @Unroll
    def 'Check config: env'() {
        when:
        def response = procedure.flush()
            .gcloudPath(gcloudPath)
            .authType('env')
//            .credential('', gcloudKey)
            .gcloudConfigurationName(gcloudConfigurationName)
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
            .gcloudConfigurationName(gcloudConfigurationName)
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
            .gcloudConfigurationName(gcloudConfigurationName)
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
