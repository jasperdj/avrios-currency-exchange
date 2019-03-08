import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Cucumber.class)
@PowerMockIgnore({"javax.net.ssl.*"})
@CucumberOptions(features = {
        "src/test/features/"
}, glue = {
        ""
})
@PrepareForTest()
public class CucumberTest {
}
