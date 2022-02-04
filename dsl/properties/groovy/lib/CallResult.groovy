import com.cloudbees.flowpdf.FlowAPI
import com.cloudbees.flowpdf.Log
import com.cloudbees.flowpdf.StepResult
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.Canonical
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "")
@Canonical
class CallResult {
    Map<String, Object> outputParameters = [:]
    String outcome = "success"
    String summary = ""
    String reportName = ""
    String reportUrl = ""
    String linkName = ""
    Map<String, Object> flowProperties = [:]
    Map<String, Object> files = [:]

    Log logger
    StepResult sr

    @Lazy
    private ObjectMapper mapper = {
        return new ObjectMapper()
    }()

    CallResult summary(String fmt, Object... data) {
        this.summary = String.format(fmt, data)
        return this
    }

    CallResult summary(Throwable ex) {
        String message = ex.message

        if (logger) {
            logger.error(message)
            if (logger.getLogLevel() > 0) {
                ex.printStackTrace()
            }
        }

        this.summary = message
        this.outcome = "error"

        return this
    }

    CallResult outputParameter(String key, Object data) {
        if (key) {
            this.outputParameters.put(key, data)
        }
        return this
    }

    CallResult flowProperty(String key, Object data) {
        if (key) {
            this.flowProperties.put(key, data)
        }
        return this
    }

    CallResult file(String key, Object data) {
        if (key) {
            this.files.put(key, data)
        }
        return this
    }

    def void processResult() {
        if (!outcome) {
            outcome = "success"
        }

//        sr.setJobSummary(summary)
        sr.setJobStepSummary(summary)
        sr.setJobStepOutcome(outcome)

        if (outcome != "error") {
            outputParameters.each {
                if (it.value instanceof String) {
                    sr.setOutputParameter(it.key, it.value.toString())
                } else {
                    sr.setOutputParameter(it.key, mapper.writeValueAsString(it.value))
                }
            }
        }

        if (reportName) {
            if (linkName) {
                sr.setReportUrl(reportName, reportUrl, linkName)
            } else {
                sr.setReportUrl(reportName, reportUrl)
            }
        }

        flowProperties.each {
            if (it.value instanceof String) {
                FlowAPI.setFlowProperty(it.key, it.value.toString())
            } else {
                FlowAPI.setFlowProperty(it.key, mapper.writeValueAsString(it.value))
            }
        }

        files.each {
            if (it.value.is(String)) {
                File f = new File(it.key)
                f.write(it.value.toString())
            } else {
                mapper.writeValue(new File(it.key), it.value)
            }
        }

        sr.apply()
    }
}
