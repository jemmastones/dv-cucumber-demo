package com.jemmastones.bdd.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        format = {"pretty", "junit:target/results/results.xml"},
        features = {"src/test/resources/cucumber/"},
        glue="com.jemmastones.bdd.steps"
)
public class RunTest {
}