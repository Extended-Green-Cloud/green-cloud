"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.updateAgentsReportsState = exports.changeCloudNetworkCapacityEvent = void 0;
var _index = require("../../constants/index.js");
var _index2 = require("../../utils/index.js");
var _agentsState = require("./agents-state.js");
var changeCloudNetworkCapacityEvent = function changeCloudNetworkCapacityEvent(cnaName, serverName, cpu, isAdded, isNew) {
  var _AGENTS_REPORTS_STATE;
  var events = (_AGENTS_REPORTS_STATE = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === cnaName;
  })[0]) === null || _AGENTS_REPORTS_STATE === void 0 ? void 0 : _AGENTS_REPORTS_STATE.events;
  if (events) {
    var eventName = isAdded ? isNew ? "New Server" : "Server enabled" : "Server disabled";
    var event = isAdded ? isNew ? "added to ".concat(cnaName) : "enabled for ".concat(cnaName) : "disabled from ".concat(cnaName);
    var eventDescription = "Server ".concat(serverName, " with CPU ").concat(cpu, " was ").concat(event);
    events.push({
      type: _index.EVENT_TYPE.AGENT_CONNECTION_CHANGE,
      time: (0, _index2.getCurrentTime)(),
      name: eventName,
      description: eventDescription
    });
  }
};
exports.changeCloudNetworkCapacityEvent = changeCloudNetworkCapacityEvent;
var reportSystemTraffic = function reportSystemTraffic(time) {
  var currentState = _agentsState.AGENTS_STATE.agents.filter(function (agent) {
    return agent.type === _index.AGENT_TYPES.CLOUD_NETWORK;
  }).reduce(function (sum, agent) {
    sum.capacity = sum.capacity + agent.maximumCapacity;
    sum.traffic = sum.traffic + agent.maximumCapacity * agent.traffic;
    return sum;
  }, {
    capacity: 0,
    traffic: 0
  });
  var currentTraffic = currentState.capacity === 0 ? 0 : currentState.traffic / currentState.capacity;
  return {
    time: time,
    value: currentTraffic
  };
};
var reportSchedulerData = function reportSchedulerData(agent, time) {
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  var queueCapacity = agent.maxQueueSize === 0 ? 0 : agent.scheduledJobs.length;
  reports["deadlinePriorityReport"].push({
    time: time,
    value: agent.deadlinePriority
  });
  reports["cpuPriorityReport"].push({
    time: time,
    value: agent.cpuPriority
  });
  reports["queueCapacityReport"].push({
    time: time,
    value: queueCapacity
  });
  reports["trafficReport"].push(reportSystemTraffic(time));
};
var reportCloudNetworkData = function reportCloudNetworkData(agent, time) {
  var _agent$successRatio;
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  reports["clientsReport"].push({
    time: time,
    value: agent.totalNumberOfClients
  });
  reports["capacityReport"].push({
    time: time,
    value: agent.maximumCapacity
  });
  reports["trafficReport"].push({
    time: time,
    value: agent.traffic
  });
  reports["successRatioReport"].push({
    time: time,
    value: (_agent$successRatio = agent.successRatio) !== null && _agent$successRatio !== void 0 ? _agent$successRatio : 0
  });
};
var reportServerData = function reportServerData(agent, time) {
  var _agent$successRatio2;
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  reports["trafficReport"].push({
    time: time,
    value: agent.traffic
  });
  reports["capacityReport"].push({
    time: time,
    value: agent.currentMaximumCapacity
  });
  reports["greenPowerUsageReport"].push({
    time: time,
    value: agent.traffic
  });
  reports["backUpPowerUsageReport"].push({
    time: time,
    value: agent.currentMaximumCapacity * agent.backUpTraffic
  });
  reports["successRatioReport"].push({
    time: time,
    value: (_agent$successRatio2 = agent.successRatio) !== null && _agent$successRatio2 !== void 0 ? _agent$successRatio2 : 0
  });
};
var reportGreenSourceData = function reportGreenSourceData(agent, time) {
  var _agent$successRatio3;
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  reports["trafficReport"].push({
    time: time,
    value: agent.traffic
  });
  reports["availableGreenPowerReport"].push({
    time: time,
    value: agent.availableGreenEnergy
  });
  reports["capacityReport"].push({
    time: time,
    value: agent.currentMaximumCapacity
  });
  reports["jobsOnGreenPowerReport"].push({
    time: time,
    value: agent.numberOfExecutedJobs
  });
  reports["jobsOnHoldReport"].push({
    time: time,
    value: agent.numberOfJobsOnHold
  });
  reports["successRatioReport"].push({
    time: time,
    value: (_agent$successRatio3 = agent.successRatio) !== null && _agent$successRatio3 !== void 0 ? _agent$successRatio3 : 0
  });
};
var updateAgentsReportsState = function updateAgentsReportsState(time) {
  _agentsState.AGENTS_STATE.agents.forEach(function (agent) {
    if (agent.type === _index.AGENT_TYPES.CLOUD_NETWORK) {
      reportCloudNetworkData(agent, time);
    } else if (agent.type === _index.AGENT_TYPES.SERVER) {
      reportServerData(agent, time);
    } else if (agent.type === _index.AGENT_TYPES.GREEN_ENERGY) {
      reportGreenSourceData(agent, time);
    } else if (agent.type === _index.AGENT_TYPES.SCHEDULER) {
      reportSchedulerData(agent, time);
    }
  });
};
exports.updateAgentsReportsState = updateAgentsReportsState;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfaW5kZXgiLCJyZXF1aXJlIiwiX2luZGV4MiIsIl9hZ2VudHNTdGF0ZSIsImNoYW5nZUNsb3VkTmV0d29ya0NhcGFjaXR5RXZlbnQiLCJjbmFOYW1lIiwic2VydmVyTmFtZSIsImNwdSIsImlzQWRkZWQiLCJpc05ldyIsIl9BR0VOVFNfUkVQT1JUU19TVEFURSIsImV2ZW50cyIsIkFHRU5UU19SRVBPUlRTX1NUQVRFIiwiYWdlbnRzUmVwb3J0cyIsImZpbHRlciIsImFnZW50UmVwb3J0IiwibmFtZSIsImV2ZW50TmFtZSIsImV2ZW50IiwiY29uY2F0IiwiZXZlbnREZXNjcmlwdGlvbiIsInB1c2giLCJ0eXBlIiwiRVZFTlRfVFlQRSIsIkFHRU5UX0NPTk5FQ1RJT05fQ0hBTkdFIiwidGltZSIsImdldEN1cnJlbnRUaW1lIiwiZGVzY3JpcHRpb24iLCJleHBvcnRzIiwicmVwb3J0U3lzdGVtVHJhZmZpYyIsImN1cnJlbnRTdGF0ZSIsIkFHRU5UU19TVEFURSIsImFnZW50cyIsImFnZW50IiwiQUdFTlRfVFlQRVMiLCJDTE9VRF9ORVRXT1JLIiwicmVkdWNlIiwic3VtIiwiY2FwYWNpdHkiLCJtYXhpbXVtQ2FwYWNpdHkiLCJ0cmFmZmljIiwiY3VycmVudFRyYWZmaWMiLCJ2YWx1ZSIsInJlcG9ydFNjaGVkdWxlckRhdGEiLCJyZXBvcnRzIiwicXVldWVDYXBhY2l0eSIsIm1heFF1ZXVlU2l6ZSIsInNjaGVkdWxlZEpvYnMiLCJsZW5ndGgiLCJkZWFkbGluZVByaW9yaXR5IiwiY3B1UHJpb3JpdHkiLCJyZXBvcnRDbG91ZE5ldHdvcmtEYXRhIiwiX2FnZW50JHN1Y2Nlc3NSYXRpbyIsInRvdGFsTnVtYmVyT2ZDbGllbnRzIiwic3VjY2Vzc1JhdGlvIiwicmVwb3J0U2VydmVyRGF0YSIsIl9hZ2VudCRzdWNjZXNzUmF0aW8yIiwiY3VycmVudE1heGltdW1DYXBhY2l0eSIsImJhY2tVcFRyYWZmaWMiLCJyZXBvcnRHcmVlblNvdXJjZURhdGEiLCJfYWdlbnQkc3VjY2Vzc1JhdGlvMyIsImF2YWlsYWJsZUdyZWVuRW5lcmd5IiwibnVtYmVyT2ZFeGVjdXRlZEpvYnMiLCJudW1iZXJPZkpvYnNPbkhvbGQiLCJ1cGRhdGVBZ2VudHNSZXBvcnRzU3RhdGUiLCJmb3JFYWNoIiwiU0VSVkVSIiwiR1JFRU5fRU5FUkdZIiwiU0NIRURVTEVSIl0sInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL21vZHVsZS9hZ2VudHMvcmVwb3J0LWhhbmRsZXIudHMiXSwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgQUdFTlRfVFlQRVMsIEVWRU5UX1RZUEUgfSBmcm9tIFwiLi4vLi4vY29uc3RhbnRzL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IGdldEN1cnJlbnRUaW1lIH0gZnJvbSBcIi4uLy4uL3V0aWxzL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IEFHRU5UU19SRVBPUlRTX1NUQVRFLCBBR0VOVFNfU1RBVEUgfSBmcm9tIFwiLi9hZ2VudHMtc3RhdGUuanNcIjtcclxuXHJcbmNvbnN0IGNoYW5nZUNsb3VkTmV0d29ya0NhcGFjaXR5RXZlbnQgPSAoY25hTmFtZSwgc2VydmVyTmFtZSwgY3B1LCBpc0FkZGVkLCBpc05ldykgPT4ge1xyXG5cdGNvbnN0IGV2ZW50cyA9IEFHRU5UU19SRVBPUlRTX1NUQVRFLmFnZW50c1JlcG9ydHMuZmlsdGVyKChhZ2VudFJlcG9ydCkgPT4gYWdlbnRSZXBvcnQubmFtZSA9PT0gY25hTmFtZSlbMF0/LmV2ZW50cztcclxuXHJcblx0aWYgKGV2ZW50cykge1xyXG5cdFx0Y29uc3QgZXZlbnROYW1lID0gaXNBZGRlZCA/IChpc05ldyA/IFwiTmV3IFNlcnZlclwiIDogXCJTZXJ2ZXIgZW5hYmxlZFwiKSA6IFwiU2VydmVyIGRpc2FibGVkXCI7XHJcblx0XHRjb25zdCBldmVudCA9IGlzQWRkZWQgPyAoaXNOZXcgPyBgYWRkZWQgdG8gJHtjbmFOYW1lfWAgOiBgZW5hYmxlZCBmb3IgJHtjbmFOYW1lfWApIDogYGRpc2FibGVkIGZyb20gJHtjbmFOYW1lfWA7XHJcblx0XHRjb25zdCBldmVudERlc2NyaXB0aW9uID0gYFNlcnZlciAke3NlcnZlck5hbWV9IHdpdGggQ1BVICR7Y3B1fSB3YXMgJHtldmVudH1gO1xyXG5cclxuXHRcdGV2ZW50cy5wdXNoKHtcclxuXHRcdFx0dHlwZTogRVZFTlRfVFlQRS5BR0VOVF9DT05ORUNUSU9OX0NIQU5HRSxcclxuXHRcdFx0dGltZTogZ2V0Q3VycmVudFRpbWUoKSxcclxuXHRcdFx0bmFtZTogZXZlbnROYW1lLFxyXG5cdFx0XHRkZXNjcmlwdGlvbjogZXZlbnREZXNjcmlwdGlvbixcclxuXHRcdH0pO1xyXG5cdH1cclxufTtcclxuXHJcbmNvbnN0IHJlcG9ydFN5c3RlbVRyYWZmaWMgPSAodGltZSkgPT4ge1xyXG5cdGNvbnN0IGN1cnJlbnRTdGF0ZSA9IEFHRU5UU19TVEFURS5hZ2VudHNcclxuXHRcdC5maWx0ZXIoKGFnZW50KSA9PiBhZ2VudC50eXBlID09PSBBR0VOVF9UWVBFUy5DTE9VRF9ORVRXT1JLKVxyXG5cdFx0LnJlZHVjZShcclxuXHRcdFx0KHN1bSwgYWdlbnQpID0+IHtcclxuXHRcdFx0XHRzdW0uY2FwYWNpdHkgPSBzdW0uY2FwYWNpdHkgKyBhZ2VudC5tYXhpbXVtQ2FwYWNpdHk7XHJcblx0XHRcdFx0c3VtLnRyYWZmaWMgPSBzdW0udHJhZmZpYyArIGFnZW50Lm1heGltdW1DYXBhY2l0eSAqIGFnZW50LnRyYWZmaWM7XHJcblx0XHRcdFx0cmV0dXJuIHN1bTtcclxuXHRcdFx0fSxcclxuXHRcdFx0eyBjYXBhY2l0eTogMCwgdHJhZmZpYzogMCB9XHJcblx0XHQpO1xyXG5cdGNvbnN0IGN1cnJlbnRUcmFmZmljID0gY3VycmVudFN0YXRlLmNhcGFjaXR5ID09PSAwID8gMCA6IGN1cnJlbnRTdGF0ZS50cmFmZmljIC8gY3VycmVudFN0YXRlLmNhcGFjaXR5O1xyXG5cclxuXHRyZXR1cm4geyB0aW1lLCB2YWx1ZTogY3VycmVudFRyYWZmaWMgfTtcclxufTtcclxuXHJcbmNvbnN0IHJlcG9ydFNjaGVkdWxlckRhdGEgPSAoYWdlbnQsIHRpbWUpID0+IHtcclxuXHRjb25zdCByZXBvcnRzID0gQUdFTlRTX1JFUE9SVFNfU1RBVEUuYWdlbnRzUmVwb3J0cy5maWx0ZXIoKGFnZW50UmVwb3J0KSA9PiBhZ2VudFJlcG9ydC5uYW1lID09PSBhZ2VudC5uYW1lKVswXVxyXG5cdFx0LnJlcG9ydHM7XHJcblxyXG5cdGNvbnN0IHF1ZXVlQ2FwYWNpdHkgPSBhZ2VudC5tYXhRdWV1ZVNpemUgPT09IDAgPyAwIDogYWdlbnQuc2NoZWR1bGVkSm9icy5sZW5ndGg7XHJcblxyXG5cdHJlcG9ydHNbXCJkZWFkbGluZVByaW9yaXR5UmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuZGVhZGxpbmVQcmlvcml0eSB9KTtcclxuXHRyZXBvcnRzW1wiY3B1UHJpb3JpdHlSZXBvcnRcIl0ucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5jcHVQcmlvcml0eSB9KTtcclxuXHRyZXBvcnRzW1wicXVldWVDYXBhY2l0eVJlcG9ydFwiXS5wdXNoKHsgdGltZSwgdmFsdWU6IHF1ZXVlQ2FwYWNpdHkgfSk7XHJcblx0cmVwb3J0c1tcInRyYWZmaWNSZXBvcnRcIl0ucHVzaChyZXBvcnRTeXN0ZW1UcmFmZmljKHRpbWUpKTtcclxufTtcclxuXHJcbmNvbnN0IHJlcG9ydENsb3VkTmV0d29ya0RhdGEgPSAoYWdlbnQsIHRpbWUpID0+IHtcclxuXHRjb25zdCByZXBvcnRzID0gQUdFTlRTX1JFUE9SVFNfU1RBVEUuYWdlbnRzUmVwb3J0cy5maWx0ZXIoKGFnZW50UmVwb3J0KSA9PiBhZ2VudFJlcG9ydC5uYW1lID09PSBhZ2VudC5uYW1lKVswXVxyXG5cdFx0LnJlcG9ydHM7XHJcblxyXG5cdHJlcG9ydHNbXCJjbGllbnRzUmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQudG90YWxOdW1iZXJPZkNsaWVudHMgfSk7XHJcblx0cmVwb3J0c1tcImNhcGFjaXR5UmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQubWF4aW11bUNhcGFjaXR5IH0pO1xyXG5cdHJlcG9ydHNbXCJ0cmFmZmljUmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQudHJhZmZpYyB9KTtcclxuXHRyZXBvcnRzW1wic3VjY2Vzc1JhdGlvUmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuc3VjY2Vzc1JhdGlvID8/IDAgfSk7XHJcbn07XHJcblxyXG5jb25zdCByZXBvcnRTZXJ2ZXJEYXRhID0gKGFnZW50LCB0aW1lKSA9PiB7XHJcblx0Y29uc3QgcmVwb3J0cyA9IEFHRU5UU19SRVBPUlRTX1NUQVRFLmFnZW50c1JlcG9ydHMuZmlsdGVyKChhZ2VudFJlcG9ydCkgPT4gYWdlbnRSZXBvcnQubmFtZSA9PT0gYWdlbnQubmFtZSlbMF1cclxuXHRcdC5yZXBvcnRzO1xyXG5cclxuXHRyZXBvcnRzW1widHJhZmZpY1JlcG9ydFwiXS5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LnRyYWZmaWMgfSk7XHJcblx0cmVwb3J0c1tcImNhcGFjaXR5UmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuY3VycmVudE1heGltdW1DYXBhY2l0eSB9KTtcclxuXHRyZXBvcnRzW1wiZ3JlZW5Qb3dlclVzYWdlUmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQudHJhZmZpYyB9KTtcclxuXHRyZXBvcnRzW1wiYmFja1VwUG93ZXJVc2FnZVJlcG9ydFwiXS5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LmN1cnJlbnRNYXhpbXVtQ2FwYWNpdHkgKiBhZ2VudC5iYWNrVXBUcmFmZmljIH0pO1xyXG5cdHJlcG9ydHNbXCJzdWNjZXNzUmF0aW9SZXBvcnRcIl0ucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5zdWNjZXNzUmF0aW8gPz8gMCB9KTtcclxufTtcclxuXHJcbmNvbnN0IHJlcG9ydEdyZWVuU291cmNlRGF0YSA9IChhZ2VudCwgdGltZSkgPT4ge1xyXG5cdGNvbnN0IHJlcG9ydHMgPSBBR0VOVFNfUkVQT1JUU19TVEFURS5hZ2VudHNSZXBvcnRzLmZpbHRlcigoYWdlbnRSZXBvcnQpID0+IGFnZW50UmVwb3J0Lm5hbWUgPT09IGFnZW50Lm5hbWUpWzBdXHJcblx0XHQucmVwb3J0cztcclxuXHJcblx0cmVwb3J0c1tcInRyYWZmaWNSZXBvcnRcIl0ucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC50cmFmZmljIH0pO1xyXG5cdHJlcG9ydHNbXCJhdmFpbGFibGVHcmVlblBvd2VyUmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuYXZhaWxhYmxlR3JlZW5FbmVyZ3kgfSk7XHJcblx0cmVwb3J0c1tcImNhcGFjaXR5UmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuY3VycmVudE1heGltdW1DYXBhY2l0eSB9KTtcclxuXHRyZXBvcnRzW1wiam9ic09uR3JlZW5Qb3dlclJlcG9ydFwiXS5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50Lm51bWJlck9mRXhlY3V0ZWRKb2JzIH0pO1xyXG5cdHJlcG9ydHNbXCJqb2JzT25Ib2xkUmVwb3J0XCJdLnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQubnVtYmVyT2ZKb2JzT25Ib2xkIH0pO1xyXG5cdHJlcG9ydHNbXCJzdWNjZXNzUmF0aW9SZXBvcnRcIl0ucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5zdWNjZXNzUmF0aW8gPz8gMCB9KTtcclxufTtcclxuXHJcbmNvbnN0IHVwZGF0ZUFnZW50c1JlcG9ydHNTdGF0ZSA9ICh0aW1lKSA9PiB7XHJcblx0QUdFTlRTX1NUQVRFLmFnZW50cy5mb3JFYWNoKChhZ2VudCkgPT4ge1xyXG5cdFx0aWYgKGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLkNMT1VEX05FVFdPUkspIHtcclxuXHRcdFx0cmVwb3J0Q2xvdWROZXR3b3JrRGF0YShhZ2VudCwgdGltZSk7XHJcblx0XHR9IGVsc2UgaWYgKGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLlNFUlZFUikge1xyXG5cdFx0XHRyZXBvcnRTZXJ2ZXJEYXRhKGFnZW50LCB0aW1lKTtcclxuXHRcdH0gZWxzZSBpZiAoYWdlbnQudHlwZSA9PT0gQUdFTlRfVFlQRVMuR1JFRU5fRU5FUkdZKSB7XHJcblx0XHRcdHJlcG9ydEdyZWVuU291cmNlRGF0YShhZ2VudCwgdGltZSk7XHJcblx0XHR9IGVsc2UgaWYgKGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLlNDSEVEVUxFUikge1xyXG5cdFx0XHRyZXBvcnRTY2hlZHVsZXJEYXRhKGFnZW50LCB0aW1lKTtcclxuXHRcdH1cclxuXHR9KTtcclxufTtcclxuXHJcbmV4cG9ydCB7IGNoYW5nZUNsb3VkTmV0d29ya0NhcGFjaXR5RXZlbnQsIHVwZGF0ZUFnZW50c1JlcG9ydHNTdGF0ZSB9O1xyXG4iXSwibWFwcGluZ3MiOiI7Ozs7OztBQUFBLElBQUFBLE1BQUEsR0FBQUMsT0FBQTtBQUNBLElBQUFDLE9BQUEsR0FBQUQsT0FBQTtBQUNBLElBQUFFLFlBQUEsR0FBQUYsT0FBQTtBQUVBLElBQU1HLCtCQUErQixHQUFHLFNBQWxDQSwrQkFBK0JBLENBQUlDLE9BQU8sRUFBRUMsVUFBVSxFQUFFQyxHQUFHLEVBQUVDLE9BQU8sRUFBRUMsS0FBSyxFQUFLO0VBQUEsSUFBQUMscUJBQUE7RUFDckYsSUFBTUMsTUFBTSxJQUFBRCxxQkFBQSxHQUFHRSxpQ0FBb0IsQ0FBQ0MsYUFBYSxDQUFDQyxNQUFNLENBQUMsVUFBQ0MsV0FBVztJQUFBLE9BQUtBLFdBQVcsQ0FBQ0MsSUFBSSxLQUFLWCxPQUFPO0VBQUEsRUFBQyxDQUFDLENBQUMsQ0FBQyxjQUFBSyxxQkFBQSx1QkFBM0ZBLHFCQUFBLENBQTZGQyxNQUFNO0VBRWxILElBQUlBLE1BQU0sRUFBRTtJQUNYLElBQU1NLFNBQVMsR0FBR1QsT0FBTyxHQUFJQyxLQUFLLEdBQUcsWUFBWSxHQUFHLGdCQUFnQixHQUFJLGlCQUFpQjtJQUN6RixJQUFNUyxLQUFLLEdBQUdWLE9BQU8sR0FBSUMsS0FBSyxlQUFBVSxNQUFBLENBQWVkLE9BQU8sbUJBQUFjLE1BQUEsQ0FB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