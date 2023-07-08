import { AGENTS_REPORTS_STATE } from "../agents-state";
import { ServerAgent } from "../types/server-agent";

const reportServerData = (agent: ServerAgent, time) => {
	const reports = AGENTS_REPORTS_STATE.agentsReports.filter((agentReport) => agentReport.name === agent.name)[0]
		.reports;

	reports["trafficReport"].push({ time, value: agent.traffic + agent.backUpTraffic });
	reports["cpuInUseReport"].push({ time, value: agent.inUseCpu });
	reports["memoryInUseReport"].push({ time, value: agent.inUseMemory });
	reports["storageInUseReport"].push({ time, value: agent.inUseStorage });
	reports["powerConsumptionReport"].push({ time, value: agent.powerConsumption });
	reports["backUpPowerConsumptionReport"].push({ time, value: agent.powerConsumptionBackUp });
	reports["successRatioReport"].push({ time, value: agent.successRatio ?? 0 });
};

export { reportServerData };