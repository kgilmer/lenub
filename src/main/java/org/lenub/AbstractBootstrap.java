package org.lenub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

/**
 * Parent of startup class of application.
 * 
 * Subclasser should have a static main() method that creates an instance of itself and calls AbstractBootstrap.run().
 * 
 * @param <T>
 */
public abstract class AbstractBootstrap<T> {
    private static final Logger logger = Logger.getLogger(AbstractBootstrap.class.getName());
    
    /**
     * Should be called from static main() in concrete class.
     * @param args
     * @param name
     * @param configType
     * @throws Exception
     */
    public void run(String[] args, String name, Class<T> configType) throws Exception {
        if (name == null) {
            name = this.getClass().getSimpleName();
        }
        
        if (configType == null) {
            throw new IllegalArgumentException("Must specify configuration type.");
        }
        
        InputStream configInputStream = getConfiguration(args);
        
        if (configInputStream == null) {
            System.err.println("Usage: " + name + " <configuration.yml>");
            System.exit(1);
        }
        
        logger.info("Loading configuration for " + name + " from " + configInputStream);
        
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        T config = mapper.readValue(configInputStream, configType);
        configInputStream.close();
        
        Iterable<? extends Service> services = getServices(args, config);
        
        if (services == null || Iterables.isEmpty(services)) {
            throw new IllegalStateException("No services returned from getServices().");
        }
        
        final ServiceManager sm = new ServiceManager(services);
        
        logger.info("Starting services...");
        
        sm.startAsync();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Shutting down...");
                shutdown();
                sm.stopAsync();
            }
        });
        
        logger.info(name + " is running.");
    }

    /**
     * Provides the configuration as inputstream.  Intended for customization.
     * 
     * @param args from static main
     * @return input stream of configuration.
     */
    protected InputStream getConfiguration(String[] args) {
        if (args == null || args.length != 1) {
            return null;
        }
        
        File configFile = new File(args[0]);

        if (!configFile.exists() || !configFile.isFile()) {
            throw new IllegalArgumentException(args[0] + " is not a valid file.");
        }
        
        try {
            return new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            // Filename already validated.  Ignoring.
            return null;
        }
    }

    /**
     * Handle any global resource deallocation.
     */
    protected void shutdown() {
    }
    
    /**
     * Get the application services.
     * 
     * @param args 
     * @param config 
     * @return
     * @throws JAXBException 
     * @throws IOException 
     */
    protected abstract Iterable<? extends Service> getServices(String[] args, T config) throws Exception;
}
