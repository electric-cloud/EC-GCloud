package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.GCloud
import com.cloudbees.pdk.hen.procedures.TestConfiguration
import spock.lang.Ignore

class CreateConfigurationTest extends PluginTestHelper {

    def 'create config'() {
        when:
        def name = new Random().nextInt(9999999) + " test config"
        println getKey()
        createPluginConfiguration(pluginName, name, [checkConnection: '1', projectName: getProjectName(), zone: getZone()], "admin", getKey())
        then:
        assert true
        cleanup:
        deleteConfiguration(pluginName, name)
    }

    def 'create config wrong token'() {
        when:
        createPluginConfiguration(pluginName, 'wrong config', [checkConnection: '1', projectName: getProjectName(), zone: getZone()], username: 'test', 'token')
        then:
        thrown RuntimeException
    }

    def 'test connection'() {
        when:
        def gcp = GCloud.createWithoutConfig()
        def r = gcp.testConfiguration
            .projectName(getProjectName())
            .authType(TestConfiguration.AuthTypeOptions.KEY)
            .zone(getZone())
            .credential('', getKeyClean())
            .runNaked()
        then:
        assert r.successful
    }

    @Ignore
    def 'test connection resource'() {
        when:
        def gcp = GCloud.createWithoutConfig()
        def r = gcp.testConfiguration.projectName(getProjectName()).authType(TestConfiguration.AuthTypeOptions.INSTANCE_METADATA).zone(getZone()).runNaked()
        then:
        assert r.successful
    }
}
