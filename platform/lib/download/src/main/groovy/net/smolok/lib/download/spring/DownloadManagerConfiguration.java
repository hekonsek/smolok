package net.smolok.lib.download.spring;

import net.smolok.lib.download.DownloadManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.apache.commons.lang3.SystemUtils.getUserHome;

@Configuration
public class DownloadManagerConfiguration {

    @Bean
    DownloadManager downloadManager() {
        return new DownloadManager(new File(new File(getUserHome(), ".smolok"), "downloads"));
    }

}
