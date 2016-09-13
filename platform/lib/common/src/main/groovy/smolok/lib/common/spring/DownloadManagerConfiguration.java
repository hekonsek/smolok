package smolok.lib.common.spring;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smolok.lib.common.DownloadManager;

import java.io.File;

@Configuration
public class DownloadManagerConfiguration {

    @Bean
    DownloadManager downloadManager() {
        return new DownloadManager(new File(new File(SystemUtils.getUserHome(), ".smolok"), "downloads"));
    }

}
