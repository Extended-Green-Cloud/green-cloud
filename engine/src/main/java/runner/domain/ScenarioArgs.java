package runner.domain;

import static java.util.stream.Stream.concat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.io.Serializable;
import java.util.List;

public class ScenarioArgs implements Serializable {

    @JacksonXmlElementWrapper(localName = "clientAgentsArgs")
    private List<ImmutableClientAgentArgs> clientAgentsArgs;
    @JacksonXmlElementWrapper(localName = "cloudNetworkAgentsArgs")
    private List<ImmutableCloudNetworkArgs> cloudNetworkAgentsArgs;
    @JacksonXmlElementWrapper(localName = "serverAgentsArgs")
    private List<ImmutableServerAgentArgs> serverAgentsArgs;

    public ScenarioArgs() {
    }

    public ScenarioArgs(List<ImmutableClientAgentArgs> clientAgentsArgs,
        List<ImmutableCloudNetworkArgs> cloudNetworkAgentsArgs,
        List<ImmutableServerAgentArgs> serverAgentsArgs) {
        this.clientAgentsArgs = clientAgentsArgs;
        this.cloudNetworkAgentsArgs = cloudNetworkAgentsArgs;
        this.serverAgentsArgs = serverAgentsArgs;
    }

    public List<ImmutableClientAgentArgs> getClientAgentsArgs() {
        return clientAgentsArgs;
    }

    public void setClientAgentsArgs(List<ImmutableClientAgentArgs> clientAgentsArgs) {
        this.clientAgentsArgs = clientAgentsArgs;
    }

    public List<ImmutableCloudNetworkArgs> getCloudNetworkAgentsArgs() {
        return cloudNetworkAgentsArgs;
    }

    public void setCloudNetworkAgentsArgs(List<ImmutableCloudNetworkArgs> cloudNetworkAgentsArgs) {
        this.cloudNetworkAgentsArgs = cloudNetworkAgentsArgs;
    }

    public List<ImmutableServerAgentArgs> getServerAgentsArgs() {
        return serverAgentsArgs;
    }

    public void setServerAgentsArgs(List<ImmutableServerAgentArgs> serverAgentsArgs) {
        this.serverAgentsArgs = serverAgentsArgs;
    }

    public List<AgentArgs> getAgentsArgs() {
        var clientArgs = clientAgentsArgs.stream().map(AgentArgs.class::cast);
        var serverArgs = serverAgentsArgs.stream().map(AgentArgs.class::cast);
        var cloudNetworkArgs = cloudNetworkAgentsArgs.stream().map(AgentArgs.class::cast);

        return concat(clientArgs, concat(serverArgs, cloudNetworkArgs)).toList();
    }
}