package pers.cocoadel.user.platform.management;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import pers.cocoadel.configuration.sources.adapter.ConfigSourceAdapter;

public class ApplicationManager implements ApplicationManagerMBean {

    @Override
    public String getApplicationName() {
        return ConfigProvider.getConfig().getValue("application.name",String.class);
    }

    @Override
    public void setApplicationName(String applicationName) {
        Iterable<ConfigSource> configSources = ConfigProvider.getConfig().getConfigSources();
        for (ConfigSource configSource : configSources) {
            if (configSource instanceof ConfigSourceAdapter) {
                ConfigSourceAdapter adapter = (ConfigSourceAdapter) configSource;
                if (StringUtils.isNotBlank(adapter.getValue("application.name"))) {
                    adapter.setProperty("application.name",applicationName);
                    break;
                }
            }
        }
    }
}
