package org.testautomation.ui.listeners;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import io.qameta.allure.Allure;

/**
 * Logs each step result to Allure — mirrors the API module equivalent.
 */
public class AllureStepLogger implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
    }

    private void onStepFinished(TestStepFinished event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep step)) return;
        String text = step.getStep().getText();
        switch (event.getResult().getStatus()) {
            case PASSED  -> Allure.step("✓ " + text);
            case FAILED  -> Allure.step("✗ " + text);
            case SKIPPED -> Allure.step("⊘ " + text + " (skipped)");
            default      -> Allure.step(text + " [" + event.getResult().getStatus() + "]");
        }
    }
}
