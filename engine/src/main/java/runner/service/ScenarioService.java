package runner.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import runner.domain.ScenarioArgs;
import runner.factory.AgentControllerFactory;
import runner.factory.AgentControllerFactoryImpl;

public class ScenarioService {

    private static final String RESOURCE_SCENARIO_PATH = "./scenarios/";
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    private final AgentControllerFactory factory;

    public ScenarioService(ContainerController containerController) {
        this.factory = new AgentControllerFactoryImpl(containerController);
    }

    public void createAgentsFromScenarioFile(final String fileName) {
        final File scenarioFile = getFileFromResourceFileName(fileName);
        try {
            final ScenarioArgs scenario = XML_MAPPER.readValue(scenarioFile, ScenarioArgs.class);

            if (Objects.nonNull(scenario.getServerAgentsArgs())) {
                scenario.getAgentsArgs().forEach(agentArgs -> {
                    try {
                        factory.createAgentController(agentArgs).start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFileFromResourceFileName(final String fileName) {
        try {
            final InputStream inputStream = ScenarioService.class.getClassLoader()
                .getResourceAsStream(RESOURCE_SCENARIO_PATH + fileName + ".xml");
            final File tempFile;
            tempFile = File.createTempFile(fileName, ".xml");
            if (Objects.nonNull(inputStream)) {
                FileUtils.copyToFile(inputStream, tempFile);
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}