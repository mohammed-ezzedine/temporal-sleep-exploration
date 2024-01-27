package org.example.temporalsleepexploration.parent;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import org.example.temporalsleepexploration.child.ChildWorkflow;
import org.slf4j.Logger;

import java.time.Duration;

@WorkflowImpl(taskQueues = "testing-environment-sleep")
public class MyWorkflowImpl implements MyWorkflow {

    private final Logger log = Workflow.getLogger(MyWorkflowImpl.class);

    private final MyActivity myActivity = Workflow.newActivityStub(MyActivity.class, ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(60)).build());

    private boolean setupIsDone = false;
    private boolean userActionProvided = false;
    private boolean exit = false;

    @Override
    public void execute() {
        try {
            log.info("workflow started");

            myActivity.someSetup();
            this.setupIsDone = true;

            Workflow.await(() -> userActionProvided);
            log.info("workflow continued after the user requested an action");

            myActivity.doSomething();

            Workflow.await(() -> exit);
            log.info("workflow finished");
        } finally {
            runChildWorkflow();
        }
    }

    private static void runChildWorkflow() {
        ChildWorkflow childWorkflow = Workflow.newChildWorkflowStub(ChildWorkflow.class, ChildWorkflowOptions.newBuilder()
                .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON).build());
        Async.procedure(childWorkflow::execute);
        Workflow.getWorkflowExecution(childWorkflow).get();
    }

    @Override
    public void receiveUserAction() {
        if (setupIsDone) {
            log.info("user requested an action");
            userActionProvided = true;
        }
    }

    @Override
    public void exit() {
        if (userActionProvided) {
            log.info("user requested to finish the workflow");
            exit = true;
        }
    }
}
