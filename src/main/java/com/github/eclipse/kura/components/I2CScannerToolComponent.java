package com.github.eclipse.kura.components;

import static org.eclipse.kura.camel.runner.CamelRunner.createOsgiRegistry;
import static org.osgi.framework.FrameworkUtil.getBundle;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.kura.camel.bean.PayloadFactory;
import org.eclipse.kura.camel.runner.CamelRunner;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.metatype.annotations.Designate;

import com.github.eclipse.kura.i2cscannertool.I2CScannerTool;

@Designate(ocd = com.github.eclipse.kura.configs.Config.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class I2CScannerToolComponent implements ConfigurableComponent {

    private CamelRunner runner;
    private ServiceReference httpServiceRef;
    private boolean registerService;
    private BundleContext bundleContext = null;
    private I2CScannerTool i2cScannerTool = new I2CScannerTool();

    @Activate
    public void activate(Map<String, Object> properties) throws Exception {
        this.runner = createRunner(properties);
        if (this.runner != null) {
            this.runner.setRoutes(createRoutes());
            this.runner.start();
        }
    }

    @Modified
    public void modified(Map<String, Object> properties) throws Exception {
        deactivate();
        activate(properties);
    }

    @Deactivate
    public void deactivate() {
        if (this.runner != null) {
            this.runner.stop();
            this.runner = null;
        }
        unRegisterServlet();
    }

    private CamelRunner createRunner(final Map<String, ?> properties) throws Exception {
        final Map<String, Object> services = new HashMap<>();
        services.put("payloadFactory", new PayloadFactory());

        bundleContext = getBundle(I2CScannerToolComponent.class).getBundleContext();
        registerServlet(bundleContext);
        final CamelRunner.Builder builder = new CamelRunner.Builder(bundleContext).disableJmx(false)
                .requireComponent("stream").requireComponent("timer")
                .registryFactory(createOsgiRegistry(bundleContext, services));
        return builder.build();
    }

    private RouteBuilder createRoutes() {

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);
                // .apiContextPath("/api-doc").apiProperty("api.title", "i2c tool API").apiProperty("api.version",
                // "0.1").apiProperty("cors", "true");
                rest("bus/{i2cBus}").produces("application/json").get("/list").route().id("list")
                        .process(new Processor() {

                            public void process(Exchange outExchange) throws Exception {
                                int i2cBus;
                                try {
                                    i2cBus = Integer.valueOf(outExchange.getIn().getHeader("i2cBus").toString());
                                } catch (NumberFormatException e) {
                                    i2cBus = 1;
                                }
                                outExchange.getOut().setBody(i2cScannerTool.scan(i2cBus));
                            }
                        }).endRest().get("/device/{id}/{address}").route().id("device").process(new Processor() {

                            public void process(Exchange outExchange) throws Exception {
                                int i2cBus = 1;
                                int id = 0;
                                int address = 0;
                                try {
                                    i2cBus = Integer.valueOf(outExchange.getIn().getHeader("i2cBus").toString());
                                    id = Integer.parseInt(outExchange.getIn().getHeader("id").toString(), 16);
                                    address = Integer.parseInt(outExchange.getIn().getHeader("address").toString(), 16);
                                } catch (NumberFormatException e) {
                                }
                                outExchange.getOut().setBody(i2cScannerTool.readAsHex(i2cBus, id, address));
                            }
                        });
            }
        };
    }

    private void registerServlet(BundleContext bundleContext) throws Exception {
        httpServiceRef = bundleContext.getServiceReference(HttpService.class.getName());
        if (httpServiceRef != null && !registerService) {
            final HttpService httpService = (HttpService) bundleContext.getService(httpServiceRef);
            if (httpService != null) {
                final HttpContext httpContext = httpService.createDefaultHttpContext();
                final Dictionary<String, String> initParams = new Hashtable<String, String>();
                initParams.put("matchOnUriPrefix", "false");
                initParams.put("servlet-name", "CamelServlet");
                httpService.registerServlet("/i2c", new CamelHttpTransportServlet(), initParams, httpContext);
                registerService = true;
            }
        }
    }

    private void unRegisterServlet() {
        if (httpServiceRef != null) {
            final HttpService httpService = (HttpService) bundleContext.getService(httpServiceRef);
            if (httpService != null) {
                httpService.unregister("/rest");
            }
            bundleContext.ungetService(httpServiceRef);
            httpServiceRef = null;
        }
    }
}