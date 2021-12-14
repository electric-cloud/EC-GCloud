package com.cloudbees.plugin.spec

import com.cloudbees.pdk.hen.*
import com.cloudbees.pdk.hen.models.Resource
import com.cloudbees.pdk.hen.models.Workspace
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
@Slf4j
class PluginTestHelper extends Specification {

    static ServerHandler serverHandler = ServerHandler.getInstance()
    static RunOptions runOpts = new RunOptions()

    static final String gcloudAgentHost = Utils.env("GCLOUD_AGENT_HOST", "efagent-gcloud")
    static final String gcloudAgentPort = Utils.env("GCLOUD_AGENT_PORT", "7808")
    static final String gcloudAgentOs = Utils.env("GCLOUD_AGENT_OS", "linux")

    static final String gcloudPath = Utils.env("GCLOUD_PATH", "/usr/bin/gcloud")
    static final String gcloudKey = getKey("GCLOUD_KEY_BASE64")
    static final String gcloudZone = Utils.env("GCLOUD_ZONE", "us-east1-b")
    static final String gcloudProject = Utils.env("GCLOUD_PROJECT")
    static final String gcloudConfigurationName = Utils.env("GCLOUD_CONFIGURATION_NAME", "default")

    static String credsPrivateKeyFieldName = "credential"
    static Credential credsKey = new Credential(userName: "", password: gcloudKey)

    def static getKey(String envName) {
        String base64 = Utils.env(envName)
        assert base64
        String res = new String(base64.decodeBase64())?.trim()
        return res
    }

    static String getTmp() {
        if (gcloudAgentOs == "linux") {
            return "/tmp"
        } else if (gcloudAgentOs == "windows") {
            return "C:\\Users\\Administrator\\AppData\\Local\\Temp\\"
        } else {
            return System.getProperty('java.io.tmpdir')
        }
    }

    Resource createResource(String host, String port) {
        Resource resource = Resource.find(host, port)
        if (resource) {
            log.debug('#0901: ' + 'Resource already exists: ' + Utils.objectToJson(resource))
            return resource
        }

        String tmpDir = getTmp()
        String resourceName = host + ':' + port
        Workspace workspace = Workspace.create(
            resourceName + "-" + Utils.randomName(),
            [
                agentDrivePath: tmpDir,
                agentUncPath  : tmpDir,
                agentUnixPath : tmpDir,
                local         : "1"
            ]
        )

        resource = Resource.create(
            resourceName,
            [
                hostName     : host,
                port         : port,
                workspaceName: workspace.getName()
            ]
        )

        log.debug('#0902: ' + Utils.objectToJson(resource))

        return resource
    }

    static boolean checkFile(String fileName, boolean exists, Resource resource) {
        JobResponse output = ServerHandler.getInstance().runCommand("ls ${fileName}", "sh", resource.getName())

        return ((exists && (output.outcome == JobOutcome.SUCCESS) && (output.jobLog =~ /${fileName}/))
            || (!exists && (output.outcome == JobOutcome.ERROR) && (output.jobLog =~ /No such file or directory/)))
    }
}
