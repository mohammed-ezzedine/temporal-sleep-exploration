# Experiment
We have our workflow [MyWorkflowImpl.java](src/main/java/org/example/temporalsleepexploration/parent/MyWorkflowImpl.java) with the following workflow method:

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

And we have the following two [tests](src/test/java/org/example/temporalsleepexploration/MyWorkflowImplIntegrationTest.java):

    @Test
    void test_a() {
        MyWorkflow myWorkflow = startWorkflow();
        testWorkflowEnvironment.sleep(Duration.ofSeconds(5));

        myWorkflow.receiveUserAction();

        myWorkflow.exit();

        verify(childWorkflow, timeout(1000)).execute();
    }

    @Test
    void test_b() {
        MyWorkflow myWorkflow = startWorkflow();
        testWorkflowEnvironment.sleep(Duration.ofSeconds(5));

        myWorkflow.receiveUserAction();

        verify(myActivity, timeout(1000)).doSomething();
    }

If we run each test case separately on their own, they will take around 1 sec each. However, when we run both tests together, the order of their execution affects the duration:

- if we run `test_a` first: `test_a` takes ~ 1 sec while `test_b` takes no less than 5 seconds to finish, which is the exact duration spent in the `testWorkflowEnvironment.sleep()` method
- if we run `test_b` first: `test_b` takes ~ 1 sec while `test_a` finishes in matter of milliseconds.

# Observations:
Therefore, we can observe that:

- The order of the tests affects the behavior of `testWorkflowEnvironment.sleep()` by making it block the test thread
- Tests can sometimes take less time to finish when executed within a test class than alone, again, based on the order of the tests execution.
