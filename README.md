# vPAV checker plugin example

This is an example project for an external Checker for the [vPAV](https://github.com/viadee/vPAV).

## Requirements

- The checker-class have to extends the `de.viadee.bpm.vPAV.processing.checker.AbstractElementChecker`.
- Only the parameter `de.viadee.bpm.vPAV.config.model.Rule` is allowed in the constructor.
