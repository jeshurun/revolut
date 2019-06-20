package com.revolut;

import com.revolut.health.ApplicationHealthCheck;
import com.revolut.resources.AccountResource;
import com.revolut.resources.TransferResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class RevolutApplication extends Application<RevolutConfiguration> {

    public static void main(final String[] args) throws Exception {
        new RevolutApplication().run(args);
    }

    @Override
    public String getName() {
        return "Revolut Banking API";
    }

    @Override
    public void initialize(final Bootstrap<RevolutConfiguration> bootstrap) {}

    @Override
    public void run(final RevolutConfiguration configuration,
                    final Environment environment) {
        final AccountResource accountResource = new AccountResource(configuration.getAccounts());
        environment.jersey().register(accountResource);
        
        final TransferResource transferResource = new TransferResource(configuration.getAccounts());
        environment.jersey().register(transferResource);
        
        final ApplicationHealthCheck appHealthCheck = new ApplicationHealthCheck(configuration.getAccounts());
        environment.healthChecks().register("appHealthCheck", appHealthCheck);
    }

}
