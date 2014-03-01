package org.lenub;

import java.util.Collections;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;

public class BootStrap extends AbstractBootstrap<Config>{

    public static void main(String[] args) throws Exception {
        BootStrap bs = new BootStrap();
        bs.run(args, "Example", Config.class);
    }
    
    @Override
    protected Iterable<? extends Service> getServices(String[] args, Config config) throws Exception {
        return Collections.singleton(new MyService());
    }

    private class MyService extends AbstractIdleService {

        @Override
        protected void startUp() throws Exception {
            System.out.println("Hello!");
        }

        @Override
        protected void shutDown() throws Exception {
            System.out.println("Goodbye!");
        }
        
    }
}
