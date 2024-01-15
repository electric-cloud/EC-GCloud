package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.GCloud
import com.cloudbees.pdk.hen.JobOutcome
import com.cloudbees.pdk.hen.JobResponse
import com.cloudbees.pdk.hen.Utils
import com.cloudbees.pdk.hen.models.Project
import com.cloudbees.pdk.hen.procedures.GCloudConfig
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

import static com.cloudbees.pdk.hen.Utils.setConfigurationValues

//@Ignore
@Slf4j
@Stepwise
class RunCustomCommandTest extends PluginTestHelper {
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
    def 'RunCustomCommand basic: #caseId: #command'() {
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
        JobResponse response = plugin.runCustomCommand
            .group(group)
            .command(command)
            .subCommands(subCommands)
            .options(options)
            .resultPropertySheet("/myJob/runCustomCommand")
            .run(runOpts)

        then:
        println(response.jobLink)
        assert response.isSuccessful()
        assert response.outcome == outcome
        List<Map<String, String>> list = (new JsonSlurper()).parseText(response.jobProperties["runCustomCommand"].toString()) as List<Map>
        Map<String, String> found = findRecord(list, name, what)
        assert found

        where:
        caseId | group     | command         | subCommands | options         | outcome            | name   | what
        "#010" | "compute" | "zones"         | "list"      | "--format=json" | JobOutcome.SUCCESS | "name" | "us-east1-b"
        "#011" | "compute" | "disk-types"    | "list"      | "--format=json" | JobOutcome.SUCCESS | "name" | "pd-standard"
// hangs on JsonSlurper
//        "#012" | "compute" | "machine-types" | "list"      | "--format=json" | JobOutcome.SUCCESS | "name" | "n1-standard-1"
    }

    @Unroll
    def 'RunCustomCommand complex'() {
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
        JobResponse response = plugin.runCustomCommand
                .group('compute')
                .command('instances')
                .subCommands('list')
                .options("--format='json(name,zone)'")
                .resultPropertySheet("/myJob/runCustomCommand")
                .run(runOpts)

//        Getting name and zone of first instance from output
        List<Map<String, String>> instances = (new JsonSlurper()).parseText(response.jobProperties["runCustomCommand"].toString()) as List<Map>

//        Running "compute instances describe <instance> --zone=<zone> --format=json"
        response = plugin.runCustomCommand
                .group('compute')
                .command('instances')
                .subCommands("""describe
${instances[0].name}""")
                .options("""--zone=${instances[0].zone}
--format=json""")
                .resultPropertySheet("/myJob/runCustomCommand")
                .run(runOpts)

        then:

        println(response.jobLink)
        assert response.isSuccessful()
        assert response.outcome == JobOutcome.SUCCESS
    }

    @Unroll
    def 'RunCustomCommand negative: #caseId'() {
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
        JobResponse response = plugin.runCustomCommand
                .group(group)
                .command(command)
                .subCommands(subCommands)
                .options(options)
                .resultPropertySheet("/myJob/runCustomCommand")
                .run(runOpts)

        then:
        println(response.jobLink)
        assert !response.isSuccessful()
        assert response.outcome == JobOutcome.ERROR

        assert response.jobSteps.last().summary =~ /${message}/

        where:
        caseId                  | group     | command         | subCommands | options         | message
        "invalid group"         | "invalid" | ""            | ""            | ""        | "Invalid choice"
        "invalid command"       | "compute" | "invalid"     | ""            | ""        | "Invalid choice"
        "invalid sub-command"   | "compute" | "zones"       | "invalid"     | ""        | "Invalid choice"
        "invalid zone"          | "compute" | "zones"       | "describe"    | "invalid" | "The resource 'projects/${gcloudProject}/zones/invalid' was not found"
        "invalid instance"      | "compute" | "instances"   | "describe"    | "invalid" | "The resource 'projects/${gcloudProject}/zones/${gcloudZone}/instances/invalid' was not found"
    }

}
