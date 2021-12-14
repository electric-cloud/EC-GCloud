
// DO NOT EDIT THIS BLOCK BELOW=== Parameters starts ===
// PLEASE DO NOT EDIT THIS FILE

import com.cloudbees.flowpdf.StepParameters

class RunAnythingParameters {
    /**
    * Label: Anything, type: textarea
    */
    String anything
    /**
    * Label: Result property sheet, type: entry
    */
    String resultPropertySheet

    static RunAnythingParameters initParameters(StepParameters sp) {
        RunAnythingParameters parameters = new RunAnythingParameters()

        def anything = sp.getRequiredParameter('anything').value
        parameters.anything = anything
        def resultPropertySheet = sp.getRequiredParameter('resultPropertySheet').value
        parameters.resultPropertySheet = resultPropertySheet

        return parameters
    }
}
// DO NOT EDIT THIS BLOCK ABOVE ^^^=== Parameters ends, checksum: 648dd7ebbcb61ffcc33c4a334d9fa0ed ===
