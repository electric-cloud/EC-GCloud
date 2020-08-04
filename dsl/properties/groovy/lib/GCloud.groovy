import com.cloudbees.flowpdf.*

/**
* GCloud
*/
class GCloud extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName     : '@PLUGIN_KEY@',
                pluginVersion  : '@PLUGIN_VERSION@',
                configFields   : ['config'],
                configLocations: ['ec_plugin_cfgs'],
                defaultConfigValues: [:]
        ]
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
            // log.info config.asMap.get('config')
            // log.info config.asMap.get('desc')
            // log.info config.asMap.get('gcloudPath')
            // log.info config.asMap.get('configurationNameGCP')
            // log.info config.asMap.get('credential')
            // log.info config.asMap.get('projectName')
            // log.info config.asMap.get('proprtiesGCP')
            // log.info config.asMap.get('checkConnectionResource')
            
            // assert config.getRequiredCredential("credential").secretValue == "secret"
        }  catch (Throwable e) {
            // Set this property to show the error in the UI
            sr.setOutcomeProperty("/myJob/configError", e.message + System.lineSeparator() + "Please change the code of checkConnection method to incorporate your own connection checking logic")
            sr.apply()
            throw e
        }
    }
// === check connection ends ===
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
        sr.setJobStepSummary(p.getParameter('config').getValue() ?: 'null')

        sr.setReportUrl("Sample Report", 'https://cloudbees.com')
        sr.apply()
        log.info("step Run Custom Command has been finished")
    }

// === step ends ===

}