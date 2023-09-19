"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.handleUpdateJobExecutionProportion = exports.handleSetClientJobTimeFrame = exports.handleSetClientJobStatus = exports.handleSetClientJobDurationMap = void 0;
var _agentUtils = require("../../utils/agent-utils");
var _clientsState = require("./clients-state");
var handleSetClientJobStatus = function handleSetClientJobStatus(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  var jobStatus = msg.status;
  if (agent) {
    agent.status = jobStatus;
  }
};
exports.handleSetClientJobStatus = handleSetClientJobStatus;
var handleSetClientJobTimeFrame = function handleSetClientJobTimeFrame(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  var _msg$data = msg.data,
    start = _msg$data.start,
    end = _msg$data.end;
  if (agent) {
    agent.job.start = start;
    agent.job.end = end;
  }
};
exports.handleSetClientJobTimeFrame = handleSetClientJobTimeFrame;
var handleSetClientJobDurationMap = function handleSetClientJobDurationMap(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  if (agent) {
    agent.durationMap = msg.data;
  }
};
exports.handleSetClientJobDurationMap = handleSetClientJobDurationMap;
var handleUpdateJobExecutionProportion = function handleUpdateJobExecutionProportion(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  var proportion = msg.data;
  if (agent) {
    agent.jobExecutionProportion = proportion;
  }
};
exports.handleUpdateJobExecutionProportion = handleUpdateJobExecutionProportion;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfYWdlbnRVdGlscyIsInJlcXVpcmUiLCJfY2xpZW50c1N0YXRlIiwiaGFuZGxlU2V0Q2xpZW50Sm9iU3RhdHVzIiwibXNnIiwiYWdlbnQiLCJnZXRBZ2VudEJ5TmFtZSIsIkNMSUVOVFNfU1RBVEUiLCJjbGllbnRzIiwiYWdlbnROYW1lIiwiam9iU3RhdHVzIiwic3RhdHVzIiwiZXhwb3J0cyIsImhhbmRsZVNldENsaWVudEpvYlRpbWVGcmFtZSIsIl9tc2ckZGF0YSIsImRhdGEiLCJzdGFydCIsImVuZCIsImpvYiIsImhhbmRsZVNldENsaWVudEpvYkR1cmF0aW9uTWFwIiwiZHVyYXRpb25NYXAiLCJoYW5kbGVVcGRhdGVKb2JFeGVjdXRpb25Qcm9wb3J0aW9uIiwicHJvcG9ydGlvbiIsImpvYkV4ZWN1dGlvblByb3BvcnRpb24iXSwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvbW9kdWxlL2NsaWVudHMvbWVzc2FnZS1oYW5kbGVyLnRzIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IGdldEFnZW50QnlOYW1lIH0gZnJvbSBcIi4uLy4uL3V0aWxzL2FnZW50LXV0aWxzXCI7XHJcbmltcG9ydCB7IENMSUVOVFNfU1RBVEUsIENsaWVudCB9IGZyb20gXCIuL2NsaWVudHMtc3RhdGVcIjtcclxuXHJcbmNvbnN0IGhhbmRsZVNldENsaWVudEpvYlN0YXR1cyA9IChtc2cpID0+IHtcclxuXHRjb25zdCBhZ2VudCA9IGdldEFnZW50QnlOYW1lKENMSUVOVFNfU1RBVEUuY2xpZW50cywgbXNnLmFnZW50TmFtZSk7XHJcblx0Y29uc3Qgam9iU3RhdHVzID0gbXNnLnN0YXR1cztcclxuXHJcblx0aWYgKGFnZW50KSB7XHJcblx0XHRhZ2VudC5zdGF0dXMgPSBqb2JTdGF0dXM7XHJcblx0fVxyXG59O1xyXG5cclxuY29uc3QgaGFuZGxlU2V0Q2xpZW50Sm9iVGltZUZyYW1lID0gKG1zZykgPT4ge1xyXG5cdGNvbnN0IGFnZW50ID0gZ2V0QWdlbnRCeU5hbWUoQ0xJRU5UU19TVEFURS5jbGllbnRzLCBtc2cuYWdlbnROYW1lKTtcclxuXHRjb25zdCB7IHN0YXJ0LCBlbmQgfSA9IG1zZy5kYXRhO1xyXG5cclxuXHRpZiAoYWdlbnQpIHtcclxuXHRcdGFnZW50LmpvYi5zdGFydCA9IHN0YXJ0O1xyXG5cdFx0YWdlbnQuam9iLmVuZCA9IGVuZDtcclxuXHR9XHJcbn07XHJcblxyXG5jb25zdCBoYW5kbGVTZXRDbGllbnRKb2JEdXJhdGlvbk1hcCA9IChtc2cpID0+IHtcclxuXHRjb25zdCBhZ2VudCA9IGdldEFnZW50QnlOYW1lKENMSUVOVFNfU1RBVEUuY2xpZW50cywgbXNnLmFnZW50TmFtZSk7XHJcblxyXG5cdGlmIChhZ2VudCkge1xyXG5cdFx0YWdlbnQuZHVyYXRpb25NYXAgPSBtc2cuZGF0YTtcclxuXHR9XHJcbn07XHJcblxyXG5jb25zdCBoYW5kbGVVcGRhdGVKb2JFeGVjdXRpb25Qcm9wb3J0aW9uID0gKG1zZykgPT4ge1xyXG5cdGNvbnN0IGFnZW50OiBDbGllbnQgPSBnZXRBZ2VudEJ5TmFtZShDTElFTlRTX1NUQVRFLmNsaWVudHMsIG1zZy5hZ2VudE5hbWUpO1xyXG5cdGNvbnN0IHByb3BvcnRpb24gPSBtc2cuZGF0YTtcclxuXHJcblx0aWYgKGFnZW50KSB7XHJcblx0XHRhZ2VudC5qb2JFeGVjdXRpb25Qcm9wb3J0aW9uID0gcHJvcG9ydGlvbjtcclxuXHR9XHJcbn07XHJcblxyXG5leHBvcnQge1xyXG5cdGhhbmRsZVNldENsaWVudEpvYlN0YXR1cyxcclxuXHRoYW5kbGVTZXRDbGllbnRKb2JUaW1lRnJhbWUsXHJcblx0aGFuZGxlU2V0Q2xpZW50Sm9iRHVyYXRpb25NYXAsXHJcblx0aGFuZGxlVXBkYXRlSm9iRXhlY3V0aW9uUHJvcG9ydGlvbixcclxufTtcclxuIl0sIm1hcHBpbmdzIjoiOzs7Ozs7QUFBQSxJQUFBQSxXQUFBLEdBQUFDLE9BQUE7QUFDQSxJQUFBQyxhQUFBLEdBQUFELE9BQUE7QUFFQSxJQUFNRSx3QkFBd0IsR0FBRyxTQUEzQkEsd0JBQXdCQSxDQUFJQyxHQUFHLEVBQUs7RUFDekMsSUFBTUMsS0FBSyxHQUFHLElBQUFDLDBCQUFjLEVBQUNDLDJCQUFhLENBQUNDLE9BQU8sRUFBRUosR0FBRyxDQUFDSyxTQUFTLENBQUM7RUFDbEUsSUFBTUMsU0FBUyxHQUFHTixHQUFHLENBQUNPLE1BQU07RUFFNUIsSUFBSU4sS0FBSyxFQUFFO0lBQ1ZBLEtBQUssQ0FBQ00sTUFBTSxHQUFHRCxTQUFTO0VBQ3pCO0FBQ0QsQ0FBQztBQUFDRSxPQUFBLENBQUFULHdCQUFBLEdBQUFBLHdCQUFBO0FBRUYsSUFBTVUsMkJBQTJCLEdBQUcsU0FBOUJBLDJCQUEyQkEsQ0FBSVQsR0FBRyxFQUFLO0VBQzVDLElBQU1DLEtBQUssR0FBRyxJQUFBQywwQkFBYyxFQUFDQywyQkFBYSxDQUFDQyxPQUFPLEVBQUVKLEdBQUcsQ0FBQ0ssU0FBUyxDQUFDO0VBQ2xFLElBQUFLLFNBQUEsR0FBdUJWLEdBQUcsQ0FBQ1csSUFBSTtJQUF2QkMsS0FBSyxHQUFBRixTQUFBLENBQUxFLEtBQUs7SUFBRUMsR0FBRyxHQUFBSCxTQUFBLENBQUhHLEdBQUc7RUFFbEIsSUFBSVosS0FBSyxFQUFFO0lBQ1ZBLEtBQUssQ0FBQ2EsR0FBRyxDQUFDRixLQUFLLEdBQUdBLEtBQUs7SUFDdkJYLEtBQUssQ0FBQ2EsR0FBRyxDQUFDRCxHQUFHLEdBQUdBLEdBQUc7RUFDcEI7QUFDRCxDQUFDO0FBQUNMLE9BQUEsQ0FBQUMsMkJBQUEsR0FBQUEsMkJBQUE7QUFFRixJQUFNTSw2QkFBNkIsR0FBRyxTQUFoQ0EsNkJBQTZCQSxDQUFJZixHQUFHLEVBQUs7RUFDOUMsSUFBTUMsS0FBSyxHQUFHLElBQUFDLDBCQUFjLEVBQUNDLDJCQUFhLENBQUNDLE9BQU8sRUFBRUosR0FBRyxDQUFDSyxTQUFTLENBQUM7RUFFbEUsSUFBSUosS0FBSyxFQUFFO0lBQ1ZBLEtBQUssQ0FBQ2UsV0FBVyxHQUFHaEIsR0FBRyxDQUFDVyxJQUFJO0VBQzdCO0FBQ0QsQ0FBQztBQUFDSCxPQUFBLENBQUFPLDZCQUFBLEdBQUFBLDZCQUFBO0FBRUYsSUFBTUUsa0NBQWtDLEdBQUcsU0FBckNBLGtDQUFrQ0EsQ0FBSWpCLEdBQUcsRUFBSztFQUNuRCxJQUFNQyxLQUFhLEdBQUcsSUFBQUMsMEJBQWMsRUFBQ0MsMkJBQWEsQ0FBQ0MsT0FBTyxFQUFFSixHQUFHLENBQUNLLFNBQVMsQ0FBQztFQUMxRSxJQUFNYSxVQUFVLEdBQUdsQixHQUFHLENBQUNXLElBQUk7RUFFM0IsSUFBSVYsS0FBSyxFQUFFO0lBQ1ZBLEtBQUssQ0FBQ2tCLHNCQUFzQixHQUFHRCxVQUFVO0VBQzFDO0FBQ0QsQ0FBQztBQUFDVixPQUFBLENBQUFTLGtDQUFBLEdBQUFBLGtDQUFBIn0=