package de.viadee.vPAV_checker_plugin_example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.Task;

import de.viadee.bpm.vPAV.config.model.ElementConvention;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.ProcessingException;
import de.viadee.bpm.vPAV.processing.checker.AbstractElementChecker;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

public class TaskNamingConventionCheckerExtern extends AbstractElementChecker {

    public TaskNamingConventionCheckerExtern(final Rule rule) {
        super(rule);
    }

    /**
     * Check if elements adhere to a configurable naming convention
     *
     * @return issues
     */
    public Collection<CheckerIssue> check(final BpmnElement element) {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

        final BaseElement baseElement = element.getBaseElement();
        if (baseElement instanceof Task) {
            final Collection<ElementConvention> elementConventions = rule.getElementConventions();
            if (elementConventions == null || elementConventions.size() < 1
                    || elementConventions.size() > 1) {
                throw new ProcessingException(
                        "task naming convention checker must have one element convention!");
            }
            final String patternString = elementConventions.iterator().next().getPattern();
            final String taskName = baseElement.getAttributeValue("name");
            if (taskName != null && taskName.trim().length() > 0) {
                final Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(taskName);
                if (!matcher.matches()) {
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
                            element.getProcessdefinition(), null, baseElement.getId(),
                            baseElement.getAttributeValue("name"), null, null, null,
                            "task name '" + taskName + "' is against the naming convention"));
                }
            } else {
                issues.add(
                        new CheckerIssue(rule.getName(), CriticalityEnum.ERROR, element.getProcessdefinition(),
                                null, baseElement.getId(), baseElement.getAttributeValue("name"), null, null, null,
                                "task name must be specified"));
            }
        }
        return issues;
    }
}
