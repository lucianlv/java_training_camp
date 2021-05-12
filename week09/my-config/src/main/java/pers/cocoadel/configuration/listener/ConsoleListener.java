package pers.cocoadel.configuration.listener;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.eclipse.microprofile.config.ConfigProvider;

public class ConsoleListener implements ConfigurationListener {

    @Override
    public void configurationChanged(ConfigurationEvent event) {
        if (!event.isBeforeUpdate())
        {
            System.out.println("Received event!");
            System.out.println("Type = " + event.getType());
            if (event.getPropertyName() != null)
            {
                System.out.println("Property name = " + event.getPropertyName());
            }
            if (event.getPropertyValue() != null)
            {
                System.out.println("Property value = " + event.getPropertyValue());
            }
            System.out.println(" ********** " +
                    ConfigProvider.getConfig().getValue(event.getPropertyName(), String.class));
        }


    }
}
