package kr.kdev.demo;

import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Component
public class CustomBeforeSendCallback implements SentryOptions.BeforeSendCallback {

    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Override
    public SentryEvent execute(SentryEvent event, Hint hint) {
        if (buildProperties != null) {
            event.setRelease(buildProperties.getVersion());
        }
        return event;
    }
}