package org.cryptaz.minermetrics.async;

import org.cryptaz.minermetrics.models.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class NotificationTimer extends TimerTask{

    private static Logger log = LoggerFactory.getLogger(NotificationTimer.class);


    public NotificationTimer(NotificationDTO notificationDTO) {
    }

    @Override
    public void run() {

    }
}
