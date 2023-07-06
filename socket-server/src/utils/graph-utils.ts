import { AGENT_TYPES } from "../constants/constants"
import { AGENTS_STATE } from "../module"

const getCloudNetworkState = (cloudNetwork) => {
    if (cloudNetwork.traffic > 85) return 'high'
    if (cloudNetwork.traffic > 50) return 'medium'

    return cloudNetwork.traffic > 0 ? 'low' : 'inactive'
}

const getServerState = (server) => {
    if (server.numberOfJobsOnHold > 0) return 'on_hold'
    if (server.backUpTraffic > 0) return 'back_up'

    return server.isActive ? 'active' : 'inactive'
}

const getGreenEnergyState = (greenEnergy) => {
    if (greenEnergy.numberOfJobsOnHold > 0 && greenEnergy.numberOfExecutedJobs > 0)
        return 'on_hold'

    return greenEnergy.isActive ? 'active' : 'inactive'
}

const getNodeState = (agent) => {
    switch (agent.type) {
        case AGENT_TYPES.CLOUD_NETWORK:
            return getCloudNetworkState(agent)
        case AGENT_TYPES.GREEN_ENERGY:
            return getGreenEnergyState(agent)
        case AGENT_TYPES.SERVER:
            return getServerState(agent)
        default:
            return null
    }
}

const createCloudNetworkEdges = (agent) => {
    const scheduler = AGENTS_STATE.agents.find(agent => agent.type === AGENT_TYPES.SCHEDULER)
    const schedulerEdge = createEdge(agent.name, scheduler.name)

    return [schedulerEdge]
}

const createServerEdges = (agent) => {
    const cloudNetworkEdge = createEdge(agent.name, agent.cloudNetworkAgent)

    return [cloudNetworkEdge]
}

const createGreenEnergyEdges = (agent) => {
    const edgeMonitoring = createEdge(agent.name, agent.monitoringAgent)
    const edgesServers = agent.connectedServers.map(server => createEdge(agent.name, server))

    return edgesServers.concat(edgeMonitoring)
}

const createEdge = (source, target) => {
    const id = [source, target, 'BI'].join('-')
    return ({ data: { id, source, target }, state: 'inactive' })
}

const createNodeForAgent = (agent) => {
    const node = {
        id: agent.name,
        label: agent.name,
        type: agent.type,
        adaptation: agent.adaptation,
    }
    switch (agent.type) {
        case AGENT_TYPES.CLOUD_NETWORK:
        case AGENT_TYPES.GREEN_ENERGY:
        case AGENT_TYPES.SERVER:
            return { state: 'inactive', ...node }
        default:
            return node
    }
}

const createAgentConnections = (agent) => {
    switch (agent.type) {
        case AGENT_TYPES.SERVER: return createServerEdges(agent)
        case AGENT_TYPES.GREEN_ENERGY: return createGreenEnergyEdges(agent)
        case AGENT_TYPES.CLOUD_NETWORK: return createCloudNetworkEdges(agent)
        default: return []
    }
}

export {
    getCloudNetworkState,
    getGreenEnergyState,
    getServerState,
    getNodeState,
    createCloudNetworkEdges,
    createServerEdges,
    createGreenEnergyEdges,
    createEdge,
    createNodeForAgent,
    createAgentConnections
}