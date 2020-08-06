import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.components.cli.CLI
import com.cloudbees.flowpdf.components.cli.Command
import com.cloudbees.flowpdf.components.cli.ExecutionResult

/**
 * GCloud
 */
class GCloud extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                defaultConfigValues: [:]
        ]
    }

    private static void createConfig(Log log, Credential cred, Map<String, String> cMap) {

        log.info("Create configuration: ${cMap.configurationNameGCP}")

        CLI cli = CLI.newInstance()
        Command command = cli.newCommand(
                cMap.gcloudPath,
                [
                        '--quiet',
                        'config',
                        'configurations',
                        'create',
                        cMap.configurationNameGCP
                ] as ArrayList<String>
        )
        ExecutionResult result = cli.runCommand(command)
        if (!result.isSuccess() && !(result.stdErr =~ /it already exists/)) {
            log.info(result)
            throw new Exception("Can't create configuration ${result.code}: ${result.stdErr}")
        }

        log.info("Activate configuration: ${cMap.configurationNameGCP}")

        command = cli.newCommand(
                cMap.gcloudPath,
                [
                        '--quiet',
                        'config',
                        'configurations',
                        'activate',
                        cMap.configurationNameGCP
                ] as ArrayList<String>
        )
        result = cli.runCommand(command)
        if (!result.isSuccess()) {
            log.info(result)
            throw new Exception("Can't activate configuration ${result.code}: ${result.stdErr}")
        }

        String userName = cred.userName
        String password = cred.secretValue

        log.info("Activate service account: ${userName}")

        File file = File.createTempFile("key", ".json")
        file.deleteOnExit()
        file.write(password)

        ArrayList<String> params = [
                '--quiet',
                'auth',
                'activate-service-account',
                userName,
                '--key-file', file.absolutePath,
        ]

        if (cMap.projectName) {
            params.add('--project')
            params.add(cMap.projectName)
        }

        command = cli.newCommand(cMap.gcloudPath, params)
        result = cli.runCommand(command)
        if (!result.isSuccess()) {
            log.info(result)
            throw new Exception("Can't activate account ${result.code}: ${result.stdErr}")
        }

        if (cMap.proprtiesGCP) {
            cMap.proprtiesGCP.split(/\r?\n/).each {
                String[] pair = it.split()

                log.info("Set property '${pair[0]}' to '${pair[1]}'")

                command = cli.newCommand(cMap.gcloudPath, '--quiet', 'config', 'set', pair[0], pair[1])
                result = cli.runCommand(command)
                if (!result.isSuccess()) {
                    log.info(result)
                    throw new Exception("Can't set property '${pair[0]}' ${result.code}: ${result.stdErr}")
                }
            }
        }
    }

/** This is a special method for checking connection during configuration creation
 */
    def checkConnection(StepParameters p, StepResult sr) {
        // Use this pre-defined method to check connection parameters
        try {
            // Put some checks here
            def config = context.configValues
            log.info(config)
            // Getting parameters:
//             log.info config.asMap.get('config')
//             log.info config.asMap.get('desc')
//             log.info config.asMap.get('gcloudPath')
//             log.info config.asMap.get('configurationNameGCP')
//             log.info config.asMap.get('credential')
//             log.info config.asMap.get('projectName')
//             log.info config.asMap.get('proprtiesGCP')
//             log.info config.asMap.get('checkConnectionResource')

            // assert config.getRequiredCredential("credential").secretValue == "secret"

            Credential cred = p.getCredential('credential')
            Map<String, String> cMap = config.getAsMap() as Map<String, String>

            createConfig(log, cred, cMap)
        } catch (Throwable e) {
            def errMsg = "Connection check for gcloud failed: " + e.message
            def suggestions = '''Reasons could be due to one or more of the following. Please ensure they are correct and try again:
1. Is your 'gcloud Path' correct?
2. Are your credentials correct?
   Are you able to use these credentials to work with gcloud CLI?
'''

            log.logErrorDiag("Create Configuration failed." + System.lineSeparator() + System.lineSeparator() + errMsg);
            log.logInfoDiag(suggestions);

            sr.setJobStepOutcome('error')
            sr.setJobStepSummary('Error: ' + suggestions + System.lineSeparator() + System.lineSeparator() + errMsg)

            // Set this property to show the error in the UI
            sr.setOutcomeProperty("/myJob/configError", suggestions + System.lineSeparator() + System.lineSeparator() + errMsg)
            sr.apply()

            throw e
        }
    }
// === check connection ends ===

    private static void processResult(StepResult sr, Boolean success, String summary, String resultPropertySheet, String value = "") {
        sr.setJobSummary(summary)

        if (success) {
            sr.setOutcomeProperty(resultPropertySheet, value)
        }

        sr.setJobStepOutcome(success ? 'success' : 'error')
        sr.setJobStepSummary(summary)

        sr.apply()
    }

/**
 * runCustomCommand - Run Custom Command/Run Custom Command
 * Add your code into this method and it will be called when the step runs
 * @param config (required: true)
 * @param group (required: true)
 * @param command (required: true)
 * @param subCommands (required: false)
 * @param options (required: false)
 * @param actionOnError (required: true)
 * @param errorValue (required: false)
 * @param resultPropertySheet (required: true)

 */
    def runCustomCommand(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        RunCustomCommandParameters sp = RunCustomCommandParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('group')
        log.info p.asMap.get('command')
        log.info p.asMap.get('subCommands')
        log.info p.asMap.get('options')
        log.info p.asMap.get('actionOnError')
        log.info p.asMap.get('errorValue')
        log.info p.asMap.get('resultPropertySheet')


        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null' as String)

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Run Custom Command has been finished")
    }

/**
 * runAnything - Run Anything/Run Anything
 * Add your code into this method and it will be called when the step runs
 * @param config (required: true)
 * @param anything (required: true)
 * @param resultPropertySheet (required: true)

 */
    def runAnything(StepParameters p, StepResult sr) {
        // Use this parameters wrapper for convenient access to your parameters
        RunAnythingParameters sp = RunAnythingParameters.initParameters(p)

        // Calling logger:
        log.info p.asMap.get('config')
        log.info p.asMap.get('anything')
        log.info p.asMap.get('resultPropertySheet')


        // Setting job step summary to the config name
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null' as String)

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Run Anything has been finished")
    }

// === step ends ===

}