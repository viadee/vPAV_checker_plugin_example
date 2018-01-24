# vPAV checker plugin example

This is an example project for an external Checker for the [vPAV](https://github.com/viadee/vPAV).

## Requirements
- vPAV as dependency
- The checker-class has to extend `de.viadee.bpm.vPAV.processing.checker.AbstractElementChecker`.
- Only the parameters `de.viadee.bpm.vPAV.config.model.Rule` and `de.viadee.bpm.vPAV.BPMNScanner` are allowed in the constructor.
- Return a collection of `de.viadee.bpm.vPAV.processing.model.data.CheckerIssue`.
