package org.example.temporalsleepexploration.child;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ChildWorkflow {

    @WorkflowMethod
    void execute();
}
