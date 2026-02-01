package runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "steps, pages, utils")
// Lo ponemos en false para que corra TODO el archivo feature
@ConfigurationParameter(key = "cucumber.execution.fail-fast.enabled", value = "false") 
@ConfigurationParameter(key = "cucumber.plugin", value = "io.cucumber.core.plugin.SerenityReporter,pretty")
public class CucumberTest {
}