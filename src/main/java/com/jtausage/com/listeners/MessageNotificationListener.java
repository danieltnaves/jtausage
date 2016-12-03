package com.jtausage.com.listeners;

import com.jtausage.com.configs.JMSDestinations;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * Created by danielnaves on 02/12/16.
 */
@Service
public class MessageNotificationListener {

    @JmsListener(destination = JMSDestinations.queueDestinationPoc)
    public void onNewMessage(String id) {
        System.out.println("Message ID: " + id);
    }

}
