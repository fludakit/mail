package io.github.fludakit.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailTrackingRegistry {

    private final List<MailMessage> deliveredMessages = Collections.synchronizedList(new ArrayList<>());

    public void track(MailMessage message) {
        deliveredMessages.add(message);
    }

    public List<MailMessage> getMessages() {
        return new ArrayList<>(deliveredMessages);
    }

    public void clear() {
        deliveredMessages.clear();
    }

    public MailMessage getLastMessage() {
        if (deliveredMessages.isEmpty()) {
            return null;
        }
        return deliveredMessages.getLast();
    }
}
