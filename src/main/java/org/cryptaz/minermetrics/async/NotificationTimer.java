package org.cryptaz.minermetrics.async;

import org.apache.log4j.Logger;
import org.cryptaz.minermetrics.models.dto.NotificationDTO;

import java.util.TimerTask;

public class NotificationTimer extends TimerTask{

    private final static Logger log = Logger.getLogger(NotificationTimer.class);


    public NotificationTimer(NotificationDTO notificationDTO) {
    }

    @Override
    public void run() {

    }
}
