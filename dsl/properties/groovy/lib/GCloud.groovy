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

    private static void createConfig(Log log, Config config) {

        Map<String, String> cMap = config.getAsMap() as Map<String, String>

        if (cMap.authType) {
            cMap.authType = cMap.authType.toLowerCase()
        } else {
            cMap.authType = "env"
        }

        CLI cli = CLI.newInstance()
        Command command
        ExecutionResult result

        if (cMap.authType == "key") {
            log.info("Create configuration: ${cMap.gcloudConfigurationName}")
            command = cli.newCommand(
                cMap.gcloudPath,
                [
                    '--quiet',
                    'config',
                    'configurations',
                    'create',
                    cMap.gcloudConfigurationName
                ] as ArrayList<String>
            )
            result = cli.runCommand(command)
            if (!result.isSuccess() && !(result.stdErr =~ /it already exists/)) {
                log.error(result)
                throw new RuntimeException("Can't create configuration ${result.code}: ${result.stdErr}")
            }
        } else {
            log.info("Using instance metadata")
        }

        log.info("Activate configuration: ${cMap.gcloudConfigurationName}")

        command = cli.newCommand(
            cMap.gcloudPath,
            [
                '--quiet',
                'config',
                'configurations',
                'activate',
                cMap.gcloudConfigurationName
            ] as ArrayList<String>
        )
        result = cli.runCommand(command)
        if (!result.isSuccess()) {
            log.error(result)
            throw new RuntimeException("Can't activate configuration ${result.code}: ${result.stdErr}")
        }

        if (cMap.authType == "key") {
            Credential cred = config.getCredential('credential')
//        String userName = cred.userName
            String password = cred.secretValue

            log.info("Activate service account")

            File file = File.createTempFile("key", ".json")
            file.deleteOnExit()
            file.write(password)

            ArrayList<String> params = [
                '--quiet',
                'auth',
                'activate-service-account',
//            userName,
                '--key-file', file.absolutePath,
            ]

            if (cMap.projectName) {
                params.add('--project')
                params.add(cMap.projectName)
            }

            command = cli.newCommand(cMap.gcloudPath, params)
            result = cli.runCommand(command)
            if (!result.isSuccess()) {
                log.error(result)
                throw new RuntimeException("Can't activate account ${result.code}: ${result.stdErr}")
            }
        }

        if (cMap.gcloudProprties) {
            cMap.gcloudProprties.split(/\r?\n/).each {
                String[] pair = it.split(" ", 2)

                if (pair.length > 0) {
                    if (pair.length < 2) {
                        throw new RuntimeException("Wrong property ${it}")
                    }
                    log.info("Set property '${pair[0]}' to '${pair[1]}'")

                    command = cli.newCommand(cMap.gcloudPath, '--quiet', 'config', 'set', pair[0], pair[1])
                    result = cli.runCommand(command)
                    if (!result.isSuccess()) {
                        log.info(result)
                        throw new RuntimeException("Can't set property '${pair[0]}' ${result.code}: ${result.stdErr}")
                    }
                }
            }
        }
    }

/** This is a special method for checking connection during configuration creation
 * @param config (required: true)
 * @param desc (required: false)
 * @param gcloudPath (required: true)
 * @param gcloudConfigurationName (required: true)
 * @param credential (required: true)
 * @param projectName (required: false)
 * @param gcloudProprties (required: false)
 * @param checkConnectionResource (required: false)

 */
    def checkConnection(StepParameters p, StepResult sr) {
        try {
            def config = context.configValues
            log.info(config)

            createConfig(log, config)
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

/**
 * runCustomCommand - Run Custom Command/Run Custom Command
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
        RunCustomCommandParameters sp = RunCustomCommandParameters.initParameters(p)

        log.info("CONTEXT: " + context.getRunContext())

        Config config = context.configValues

        CLI cli = CLI.newInstance()

        ArrayList<String> params = [
            '--quiet',
            sp.group,
            sp.command,
        ]

        sp.subCommands.split(/\r?\n/).each {
            if (it) {
                params.add(it.trim())
            }
        }

        sp.options.split(/\r?\n/).each {
            String[] opts = it.split()
            if (opts.length > 0) {
                params.add(opts[0])
                if (opts.length > 1) {
                    params.add(opts[1])
                }
            }
        }

        Command command = cli.newCommand(config.getParameter("gcloudPath").value as String, params)

        CallResult callResult = new CallResult()
            .sr(sr)
            .logger(log)

        try {
            createConfig(log, config)

            ExecutionResult result = cli.runCommand(command)
            if (!result.isSuccess()) {
                log.error(result)
                throw new RuntimeException("${result.code}: ${result.stdErr}")
            }

            def data = result.stdOut

            callResult
                .summary("The command succeeded")
                .flowProperty(sp.resultPropertySheet, data)
                .outputParameter("runCustomCommand", data)
        } catch (Throwable e) {
            callResult.summary(e)
        }

        callResult.processResult()

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
        RunAnythingParameters sp = RunAnythingParameters.initParameters(p)

        log.info("CONTEXT: " + context.getRunContext())

        Config config = context.configValues

        CLI cli = CLI.newInstance()

        String anything = sp.anything
        if (!(anything =~ /^#!/)) {
            anything = "#!/bin/bash" + System.lineSeparator() + System.lineSeparator() + anything
        }

        CallResult callResult = new CallResult()
            .sr(sr)
            .logger(log)

        try {
            createConfig(log, config)

            File file = File.createTempFile("anything", "")
            file.deleteOnExit()
            file.write(anything)
            file.setExecutable(true)

            Command command = cli.newCommand(file.absolutePath)

            ExecutionResult result = cli.runCommand(command)
            if (!result.isSuccess()) {
                log.error(result)
                throw new RuntimeException("${result.code}: ${result.stdErr}")
            }

            def data = result.stdOut
            callResult
                .summary("The command succeeded")
                .flowProperty(sp.resultPropertySheet, data)
                .outputParameter("runAnything", data)
        } catch (Throwable e) {
            callResult.summary(e)
        }

        callResult.processResult()

        log.info("step Run Anything has been finished")
    }

// === step ends ===

}