package org.testautomation.ui.utils;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles browser-native dialogs (alert, confirm, prompt) in Playwright.
 * Register the handler before the action that triggers the dialog.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlertHandler {

    /** Accepts the next dialog unconditionally. */
    public static void acceptNext(Page page) {
        page.onceDialog(Dialog::accept);
    }

    /** Dismisses the next dialog. */
    public static void dismissNext(Page page) {
        page.onceDialog(Dialog::dismiss);
    }

    /** Accepts the next prompt dialog and fills {@code text}. */
    public static void acceptNextWithText(Page page, String text) {
        page.onceDialog(dialog -> dialog.accept(text));
    }

    /** Returns the message of the next dialog (accepts it after reading). */
    public static String captureNextMessage(Page page) {
        final String[] message = {null};
        page.onceDialog(dialog -> {
            message[0] = dialog.message();
            dialog.accept();
        });
        return message[0];
    }
}
