package org.example.temporalsleepexploration.parent;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface MyActivity {

    @ActivityMethod
    void someSetup();

    @ActivityMethod
    void doSomething();
}
