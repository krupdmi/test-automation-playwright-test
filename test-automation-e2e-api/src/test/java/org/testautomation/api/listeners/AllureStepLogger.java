package org.testautomation.api.listeners;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import io.qameta.allure.Allure;

/**
 * Cucumber event listener that logs each step result to Allure as a named step,
 * making step-level pass/fail visible in the Allure report without relying solely
 * on the default Cucumber Allure adapter output.
 */
public class AllureStepLogger implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
    }

    private void onStepFinished(TestStepFinished event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep step)) return;

        String stepText = step.getStep().getText();
        Status status = event.getResult().getStatus();

        switch (status) {
            case PASSED  -> Allure.step("✓ " + stepText);
            case FAILED  -> Allure.step("✗ " + stepText);
            case SKIPPED -> Allure.step("⊘ " + stepText + " (skipped)");
            default      -> Allure.step(stepText + " [" + status + "]");
        }
    }
}
