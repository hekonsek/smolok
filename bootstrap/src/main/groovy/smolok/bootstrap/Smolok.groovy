package smolok.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication(scanBasePackages = 'smolok')
class Smolok {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Smolok.class).run(args)
    }

}