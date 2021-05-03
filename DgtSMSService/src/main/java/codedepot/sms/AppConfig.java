package codedepot.sms;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties()       
@Component
public class AppConfig {

    private java.util.Map<String, String> app = new HashMap<>();  // it will store all properties start with app
    public Map<String, String> getApp() {
        return app;
    }
    public void setApp(Map<String, String> map) {
        this.app = map;
    }
	
}

