package org.example.temporalsleepexploration.child;

import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

@WorkflowImpl(taskQueues = "testing-environment-sleep")
public class ChildWorkflowImpl implements ChildWorkflow {

    private final Logger log = Workflow.getLogger(ChildWorkflowImpl.class);

    @Override
    public void execute() {
        log.info("child workflow executed");
    }
}
