import React from "react"
import './agent-statistics-config'
import DetailsField from "../details-field/details-field"
import { getStatisticsMapForAgent, mapCloudNetworkAgentFields, mapGreenEnergyAgentFields, mapMonitoringAgentFields, mapServerAgentFields } from "./agent-statistics-config"
import SubtitleContainer from "../subtitle-container/subtitle-container"
import { getSelectedAgent } from "store/cloud-network/api"
import { styles } from './agent-statistics-panel-styles'

import {
    Agent, AgentType, CloudNetworkAgent,
    GreenEnergyAgent, MonitoringAgent, ServerAgent
} from "@types"
import { useAppSelector } from "@store"
import { Card } from '@components'


const header = 'Agent Statistics'.toUpperCase()
const description = 'Click on an agent to display its statistics'

/**
 * Component represents panel gathering all information about given agent
 * 
 * @returns JSX Element 
 */
const AgentStatisticsPanel = () => {
    const cloudNetworkState = useAppSelector(state => state.cloudNetwork)
    const selectedAgent = getSelectedAgent(cloudNetworkState.agents)

    const getHeader = () => {
        return !selectedAgent ?
            header :
            <div style={styles.agentHeader}>
                <span>{header}:</span>
                <span style={styles.agentNameHeader}>
                    {selectedAgent.name.toUpperCase()}
                </span>
            </div>
    }

    const getActiveBadge = (state: string) => {
        const badgeStyle = state === 'ACTIVE' ?
            styles.activeBadge :
            styles.inActiveBadge
        const style = { ...styles.badge, ...badgeStyle }
        return (<span {...{ style }}>{state}</span>)
    }

    const getAgentFields = (agent: Agent) => {
        switch (agent.type) {
            case AgentType.SERVER:
                return mapServerAgentFields(agent as ServerAgent)
            case AgentType.CLOUD_NETWORK:
                return mapCloudNetworkAgentFields(agent as CloudNetworkAgent)
            case AgentType.GREEN_ENERGY:
                return mapGreenEnergyAgentFields(agent as GreenEnergyAgent)
            case AgentType.MONITORING:
                return mapMonitoringAgentFields(agent as MonitoringAgent)
        }
    }

    const mapToStatistics = (agent: Agent, statisticsMap: any[]) => {
        return statisticsMap.map(field => {
            const { label, key } = field
            const agentFields = getAgentFields(agent)
            const agentValue = { ...agentFields as any }[key] ?? ''

            const value = key === 'isActive' ?
                getActiveBadge(agentValue) :
                agentValue
            const property = key === 'isActive' ?
                'valueObject' :
                'value'

            return (<DetailsField {...{ label, [property]: value }} />)
        })
    }


    const generateDetailsFields = () => {
        if (selectedAgent) {
            const map = getStatisticsMapForAgent(selectedAgent)
            return (<div>{mapToStatistics(selectedAgent, map)}</div>)
        } else {
            return (<SubtitleContainer text={description} />)
        }
    }

    return (
        <Card {...{
            header: getHeader(),
            containerStyle: styles.agentContainer
        }}>
            {generateDetailsFields()}
        </Card>
    )
}

export default AgentStatisticsPanel