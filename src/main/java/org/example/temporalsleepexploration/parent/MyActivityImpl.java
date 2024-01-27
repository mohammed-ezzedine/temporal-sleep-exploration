package org.example.temporalsleepexploration.parent;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActivityImpl(taskQueues = "testing-environment-sleep")
public class MyActivityImpl implements MyActivity {

    @Override
    public void someSetup() {
        log.info("some setup");
    }

    @Override
    public void doSomething() {
      log.info("doing something");
    }

}
