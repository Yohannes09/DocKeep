package com.dockeep.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    // Necessary Config, allowing GraphQL scalar usage.
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(){
        return wiringBuilder ->
                        wiringBuilder
                                .scalar(ExtendedScalars.GraphQLLong)
                                .scalar(ExtendedScalars.DateTime);
    }

}
