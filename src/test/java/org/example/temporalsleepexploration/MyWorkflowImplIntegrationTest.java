package org.example.temporalsleepexploration;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import org.example.temporalsleepexploration.child.ChildWorkflow;
import org.example.temporalsleepexploration.parent.MyActivityImpl;
import org.example.temporalsleepexploration.parent.MyWorkflow;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.time.Duration;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = MyWorkflowImplIntegrationTest.Configuration.class)
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MyWorkflowImplIntegrationTest {

    @Autowired
    private TestWorkflowEnvironment testWorkflowEnvironment;

    @Autowired
    private WorkflowClient workflowClient;

    @MockBean
    private MyActivityImpl myActivity;

    private ChildWorkflow childWorkflow;

    @BeforeAll
    void beforeAll() {
        testWorkflowEnvironment.getWorkerFactory().getWorker("testing-environment-sleep")
                        .registerWorkflowImplementationFactory(ChildWorkflow.class, () -> childWorkflow);

        testWorkflowEnvironment.start();
    }

    @AfterAll
    void afterAll() {
        testWorkflowEnvironment.shutdown();
    }

    @BeforeEach
    void setUp() {
        childWorkflow = mock(ChildWorkflow.class);
    }

    @Test
    @Order(2)
    void test_a() {
        MyWorkflow myWorkflow = startWorkflow();
        testWorkflowEnvironment.sleep(Duration.ofSeconds(5));

        myWorkflow.receiveUserAction();

        myWorkflow.exit();

        verify(childWorkflow, timeout(1000)).execute();
    }

    @Test
    @Order(1)
    void test_b() {
        MyWorkflow myWorkflow = startWorkflow();
        testWorkflowEnvironment.sleep(Duration.ofSeconds(5));

        myWorkflow.receiveUserAction();

        verify(myActivity, timeout(1000)).doSomething();
    }

    private MyWorkflow startWorkflow() {
        MyWorkflow myWorkflow = workflowClient.newWorkflowStub(MyWorkflow.class, WorkflowOptions.newBuilder()
                .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                .setTaskQueue("testing-environment-sleep")
                .build());

        WorkflowClient.start(myWorkflow::execute);

        return myWorkflow;
    }

    @ComponentScan
    static class Configuration {

    }
}