package org.example.temporalsleepexploration.parent;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyWorkflow {

    @WorkflowMethod
    void execute();

    @SignalMethod
    void receiveUserAction();

    @SignalMethod
    void exit();
}
