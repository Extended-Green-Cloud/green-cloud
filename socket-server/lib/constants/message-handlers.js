"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.MESSAGE_HANDLERS = void 0;
var _module = require("../module");
var _messageHandlers = require("../module/agents/message-handlers");
var MESSAGE_HANDLERS = {
  INCREMENT_FINISHED_JOBS: _module.handleIncrementFinishJobs,
  INCREMENT_FAILED_JOBS: _module.handleIncrementFailedJobs,
  INCREMENT_WEAK_ADAPTATIONS: _module.handleIncrementWeakAdaptations,
  INCREMENT_STRONG_ADAPTATIONS: _module.handleIncrementStrongAdaptations,
  UPDATE_JOB_QUEUE: _messageHandlers.handleUpdateJobQueue,
  UPDATE_SERVER_CONNECTION: _messageHandlers.handleUpdateServerConnection,
  UPDATE_INDICATORS: _module.handleUpdateIndicators,
  UPDATE_SCHEDULER_CPU_PRIORITY: _messageHandlers.handleUpdateCpuPriority,
  UPDATE_SCHEDULER_DEADLINE_PRIORITY: _messageHandlers.handleUpdateDeadlinePriority,
  UPDATE_CURRENT_CLIENTS: _module.handleCurrentClientsNumber,
  UPDATE_CURRENT_PLANNED_JOBS: _module.handlePlannedJobs,
  UPDATE_CURRENT_ACTIVE_JOBS: _module.handleExecutedJobs,
  SET_TRAFFIC: _messageHandlers.handleSetTraffic,
  SET_IS_ACTIVE: _messageHandlers.handleSetActive,
  SET_JOBS_COUNT: _messageHandlers.handleSetJobsCount,
  SET_ON_HOLD_JOBS_COUNT: _messageHandlers.handleSetJobsOnHold,
  SET_CLIENT_NUMBER: _messageHandlers.handleSetClientNumber,
  SET_CLIENT_JOB_STATUS: _module.handleSetClientJobStatus,
  SET_CLIENT_JOB_TIME_FRAME: _module.handleSetClientJobTimeFrame,
  SET_CLIENT_JOB_DURATION_MAP: _module.handleSetClientJobDurationMap,
  SET_SERVER_BACK_UP_TRAFFIC: _messageHandlers.handleSetBackUpTraffic,
  SET_JOB_SUCCESS_RATIO: _messageHandlers.handleSetSuccessRatio,
  SET_WEATHER_PREDICTION_ERROR: _messageHandlers.handleWeatherPredictionError,
  SET_AVAILABLE_GREEN_ENERGY: _messageHandlers.handleUpdateGreenEnergy,
  REGISTER_AGENT: _messageHandlers.handleRegisterAgent,
  REMOVE_AGENT: _messageHandlers.handleRemoveAgent,
  REGISTER_MANAGING: _module.handleRegisterGoals,
  ADD_ADAPTATION_LOG: _module.handleAddAdaptationLog,
  REPORT_SYSTEM_START_TIME: _module.handleSystemTimeMessage,
  DISABLE_SERVER: _messageHandlers.handleServerDisabling,
  ENABLE_SERVER: _messageHandlers.handleServerEnabling,
  UPDATE_ADAPTATION_ACTION: _module.handleUpdateAdaptationAction,
  UPDATE_SERVER_RESOURCES: _messageHandlers.handleUpdateResources,
  UPDATE_ENERGY_IN_USE: _messageHandlers.handleUpdateEnergyInUse,
  UPDATE_JOB_EXECUTION_PROPORTION: _module.handleUpdateJobExecutionProportion
};
exports.MESSAGE_HANDLERS = MESSAGE_HANDLERS;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfbW9kdWxlIiwicmVxdWlyZSIsIl9tZXNzYWdlSGFuZGxlcnMiLCJNRVNTQUdFX0hBTkRMRVJTIiwiSU5DUkVNRU5UX0ZJTklTSEVEX0pPQlMiLCJoYW5kbGVJbmNyZW1lbnRGaW5pc2hKb2JzIiwiSU5DUkVNRU5UX0ZBSUxFRF9KT0JTIiwiaGFuZGxlSW5jcmVtZW50RmFpbGVkSm9icyIsIklOQ1JFTUVOVF9XRUFLX0FEQVBUQVRJT05TIiwiaGFuZGxlSW5jcmVtZW50V2Vha0FkYXB0YXRpb25zIiwiSU5DUkVNRU5UX1NUUk9OR19BREFQVEFUSU9OUyIsImhhbmRsZUluY3JlbWVudFN0cm9uZ0FkYXB0YXRpb25zIiwiVVBEQVRFX0pPQl9RVUVVRSIsImhhbmRsZVVwZGF0ZUpvYlF1ZXVlIiwiVVBEQVRFX1NFUlZFUl9DT05ORUNUSU9OIiwiaGFuZGxlVXBkYXRlU2VydmVyQ29ubmVjdGlvbiIsIlVQREFURV9JTkRJQ0FUT1JTIiwiaGFuZGxlVXBkYXRlSW5kaWNhdG9ycyIsIlVQREFURV9TQ0hFRFVMRVJfQ1BVX1BSSU9SSVRZIiwiaGFuZGxlVXBkYXRlQ3B1UHJpb3JpdHkiLCJVUERBVEVfU0NIRURVTEVSX0RFQURMSU5FX1BSSU9SSVRZIiwiaGFuZGxlVXBkYXRlRGVhZGxpbmVQcmlvcml0eSIsIlVQREFURV9DVVJSRU5UX0NMSUVOVFMiLCJoYW5kbGVDdXJyZW50Q2xpZW50c051bWJlciIsIlVQREFURV9DVVJSRU5UX1BMQU5ORURfSk9CUyIsImhhbmRsZVBsYW5uZWRKb2JzIiwiVVBEQVRFX0NVUlJFTlRfQUNUSVZFX0pPQlMiLCJoYW5kbGVFeGVjdXRlZEpvYnMiLCJTRVRfVFJBRkZJQyIsImhhbmRsZVNldFRyYWZmaWMiLCJTRVRfSVNfQUNUSVZFIiwiaGFuZGxlU2V0QWN0aXZlIiwiU0VUX0pPQlNfQ09VTlQiLCJoYW5kbGVTZXRKb2JzQ291bnQiLCJTRVRfT05fSE9MRF9KT0JTX0NPVU5UIiwiaGFuZGxlU2V0Sm9ic09uSG9sZCIsIlNFVF9DTElFTlRfTlVNQkVSIiwiaGFuZGxlU2V0Q2xpZW50TnVtYmVyIiwiU0VUX0NMSUVOVF9KT0JfU1RBVFVTIiwiaGFuZGxlU2V0Q2xpZW50Sm9iU3RhdHVzIiwiU0VUX0NMSUVOVF9KT0JfVElNRV9GUkFNRSIsImhhbmRsZVNldENsaWVudEpvYlRpbWVGcmFtZSIsIlNFVF9DTElFTlRfSk9CX0RVUkFUSU9OX01BUCIsImhhbmRsZVNldENsaWVudEpvYkR1cmF0aW9uTWFwIiwiU0VUX1NFUlZFUl9CQUNLX1VQX1RSQUZGSUMiLCJoYW5kbGVTZXRCYWNrVXBUcmFmZmljIiwiU0VUX0pPQl9TVUNDRVNTX1JBVElPIiwiaGFuZGxlU2V0U3VjY2Vzc1JhdGlvIiwiU0VUX1dFQVRIRVJfUFJFRElDVElPTl9FUlJPUiIsImhhbmRsZVdlYXRoZXJQcmVkaWN0aW9uRXJyb3IiLCJTRVRfQVZBSUxBQkxFX0dSRUVOX0VORVJHWSIsImhhbmRsZVVwZGF0ZUdyZWVuRW5lcmd5IiwiUkVHSVNURVJfQUdFTlQiLCJoYW5kbGVSZWdpc3RlckFnZW50IiwiUkVNT1ZFX0FHRU5UIiwiaGFuZGxlUmVtb3ZlQWdlbnQiLCJSRUdJU1RFUl9NQU5BR0lORyIsImhhbmRsZVJlZ2lzdGVyR29hbHMiLCJBRERfQURBUFRBVElPTl9MT0ciLCJoYW5kbGVBZGRBZGFwdGF0aW9uTG9nIiwiUkVQT1JUX1NZU1RFTV9TVEFSVF9USU1FIiwiaGFuZGxlU3lzdGVtVGltZU1lc3NhZ2UiLCJESVNBQkxFX1NFUlZFUiIsImhhbmRsZVNlcnZlckRpc2FibGluZyIsIkVOQUJMRV9TRVJWRVIiLCJoYW5kbGVTZXJ2ZXJFbmFibGluZyIsIlVQREFURV9BREFQVEFUSU9OX0FDVElPTiIsImhhbmRsZVVwZGF0ZUFkYXB0YXRpb25BY3Rpb24iLCJVUERBVEVfU0VSVkVSX1JFU09VUkNFUyIsImhhbmRsZVVwZGF0ZVJlc291cmNlcyIsIlVQREFURV9FTkVSR1lfSU5fVVNFIiwiaGFuZGxlVXBkYXRlRW5lcmd5SW5Vc2UiLCJVUERBVEVfSk9CX0VYRUNVVElPTl9QUk9QT1JUSU9OIiwiaGFuZGxlVXBkYXRlSm9iRXhlY3V0aW9uUHJvcG9ydGlvbiIsImV4cG9ydHMiXSwic291cmNlcyI6WyIuLi8uLi9zcmMvY29uc3RhbnRzL21lc3NhZ2UtaGFuZGxlcnMudHMiXSwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHtcclxuXHRoYW5kbGVJbmNyZW1lbnRGYWlsZWRKb2JzLFxyXG5cdGhhbmRsZUluY3JlbWVudEZpbmlzaEpvYnMsXHJcblx0aGFuZGxlSW5jcmVtZW50U3Ryb25nQWRhcHRhdGlvbnMsXHJcblx0aGFuZGxlSW5jcmVtZW50V2Vha0FkYXB0YXRpb25zLFxyXG5cdGhhbmRsZVJlZ2lzdGVyR29hbHMsXHJcblx0aGFuZGxlQWRkQWRhcHRhdGlvbkxvZyxcclxuXHRoYW5kbGVTZXRDbGllbnRKb2JEdXJhdGlvbk1hcCxcclxuXHRoYW5kbGVTZXRDbGllbnRKb2JTdGF0dXMsXHJcblx0aGFuZGxlU2V0Q2xpZW50Sm9iVGltZUZyYW1lLFxyXG5cdGhhbmRsZVN5c3RlbVRpbWVNZXNzYWdlLFxyXG5cdGhhbmRsZVVwZGF0ZUluZGljYXRvcnMsXHJcblx0aGFuZGxlQ3VycmVudENsaWVudHNOdW1iZXIsXHJcblx0aGFuZGxlRXhlY3V0ZWRKb2JzLFxyXG5cdGhhbmRsZVBsYW5uZWRKb2JzLFxyXG5cdGhhbmRsZVVwZGF0ZUFkYXB0YXRpb25BY3Rpb24sXHJcblx0aGFuZGxlVXBkYXRlSm9iRXhlY3V0aW9uUHJvcG9ydGlvbixcclxufSBmcm9tIFwiLi4vbW9kdWxlXCI7XHJcbmltcG9ydCB7XHJcblx0aGFuZGxlVXBkYXRlRGVhZGxpbmVQcmlvcml0eSxcclxuXHRoYW5kbGVVcGRhdGVDcHVQcmlvcml0eSxcclxuXHRoYW5kbGVVcGRhdGVKb2JRdWV1ZSxcclxuXHRoYW5kbGVTZXRUcmFmZmljLFxyXG5cdGhhbmRsZVNldEJhY2tVcFRyYWZmaWMsXHJcblx0aGFuZGxlVXBkYXRlUmVzb3VyY2VzLFxyXG5cdGhhbmRsZVNldEpvYnNDb3VudCxcclxuXHRoYW5kbGVTZXRDbGllbnROdW1iZXIsXHJcblx0aGFuZGxlU2VydmVyRGlzYWJsaW5nLFxyXG5cdGhhbmRsZVNlcnZlckVuYWJsaW5nLFxyXG5cdGhhbmRsZVNldEFjdGl2ZSxcclxuXHRoYW5kbGVTZXRKb2JzT25Ib2xkLFxyXG5cdGhhbmRsZVNldFN1Y2Nlc3NSYXRpbyxcclxuXHRoYW5kbGVVcGRhdGVTZXJ2ZXJDb25uZWN0aW9uLFxyXG5cdGhhbmRsZVdlYXRoZXJQcmVkaWN0aW9uRXJyb3IsXHJcblx0aGFuZGxlVXBkYXRlR3JlZW5FbmVyZ3ksXHJcblx0aGFuZGxlVXBkYXRlRW5lcmd5SW5Vc2UsXHJcblx0aGFuZGxlUmVnaXN0ZXJBZ2VudCxcclxuXHRoYW5kbGVSZW1vdmVBZ2VudCxcclxufSBmcm9tIFwiLi4vbW9kdWxlL2FnZW50cy9tZXNzYWdlLWhhbmRsZXJzXCI7XHJcblxyXG5leHBvcnQgY29uc3QgTUVTU0FHRV9IQU5ETEVSUyA9IHtcclxuXHRJTkNSRU1FTlRfRklOSVNIRURfSk9CUzogaGFuZGxlSW5jcmVtZW50RmluaXNoSm9icyxcclxuXHRJTkNSRU1FTlRfRkFJTEVEX0pPQlM6IGhhbmRsZUluY3JlbWVudEZhaWxlZEpvYnMsXHJcblx0SU5DUkVNRU5UX1dFQUtfQURBUFRBVElPTlM6IGhhbmRsZUluY3JlbWVudFdlYWtBZGFwdGF0aW9ucyxcclxuXHRJTkNSRU1FTlRfU1RST05HX0FEQVBUQVRJT05TOiBoYW5kbGVJbmNyZW1lbnRTdHJvbmdBZGFwdGF0aW9ucyxcclxuXHRVUERBVEVfSk9CX1FVRVVFOiBoYW5kbGVVcGRhdGVKb2JRdWV1ZSxcclxuXHRVUERBVEVfU0VSVkVSX0NPTk5FQ1RJT046IGhhbmRsZVVwZGF0ZVNlcnZlckNvbm5lY3Rpb24sXHJcblx0VVBEQVRFX0lORElDQVRPUlM6IGhhbmRsZVVwZGF0ZUluZGljYXRvcnMsXHJcblx0VVBEQVRFX1NDSEVEVUxFUl9DUFVfUFJJT1JJVFk6IGhhbmRsZVVwZGF0ZUNwdVByaW9yaXR5LFxyXG5cdFVQREFURV9TQ0hFRFVMRVJfREVBRExJTkVfUFJJT1JJVFk6IGhhbmRsZVVwZGF0ZURlYWRsaW5lUHJpb3JpdHksXHJcblx0VVBEQVRFX0NVUlJFTlRfQ0xJRU5UUzogaGFuZGxlQ3VycmVudENsaWVudHNOdW1iZXIsXHJcblx0VVBEQVRFX0NVUlJFTlRfUExBTk5FRF9KT0JTOiBoYW5kbGVQbGFubmVkSm9icyxcclxuXHRVUERBVEVfQ1VSUkVOVF9BQ1RJVkVfSk9CUzogaGFuZGxlRXhlY3V0ZWRKb2JzLFxyXG5cdFNFVF9UUkFGRklDOiBoYW5kbGVTZXRUcmFmZmljLFxyXG5cdFNFVF9JU19BQ1RJVkU6IGhhbmRsZVNldEFjdGl2ZSxcclxuXHRTRVRfSk9CU19DT1VOVDogaGFuZGxlU2V0Sm9ic0NvdW50LFxyXG5cdFNFVF9PTl9IT0xEX0pPQlNfQ09VTlQ6IGhhbmRsZVNldEpvYnNPbkhvbGQsXHJcblx0U0VUX0NMSUVOVF9OVU1CRVI6IGhhbmRsZVNldENsaWVudE51bWJlcixcclxuXHRTRVRfQ0xJRU5UX0pPQl9TVEFUVVM6IGhhbmRsZVNldENsaWVudEpvYlN0YXR1cyxcclxuXHRTRVRfQ0xJRU5UX0pPQl9USU1FX0ZSQU1FOiBoYW5kbGVTZXRDbGllbnRKb2JUaW1lRnJhbWUsXHJcblx0U0VUX0NMSUVOVF9KT0JfRFVSQVRJT05fTUFQOiBoYW5kbGVTZXRDbGllbnRKb2JEdXJhdGlvbk1hcCxcclxuXHRTRVRfU0VSVkVSX0JBQ0tfVVBfVFJBRkZJQzogaGFuZGxlU2V0QmFja1VwVHJhZmZpYyxcclxuXHRTRVRfSk9CX1NVQ0NFU1NfUkFUSU86IGhhbmRsZVNldFN1Y2Nlc3NSYXRpbyxcclxuXHRTRVRfV0VBVEhFUl9QUkVESUNUSU9OX0VSUk9SOiBoYW5kbGVXZWF0aGVyUHJlZGljdGlvbkVycm9yLFxyXG5cdFNFVF9BVkFJTEFCTEVfR1JFRU5fRU5FUkdZOiBoYW5kbGVVcGRhdGVHcmVlbkVuZXJneSxcclxuXHRSRUdJU1RFUl9BR0VOVDogaGFuZGxlUmVnaXN0ZXJBZ2VudCxcclxuXHRSRU1PVkVfQUdFTlQ6IGhhbmRsZVJlbW92ZUFnZW50LFxyXG5cdFJFR0lTVEVSX01BTkFHSU5HOiBoYW5kbGVSZWdpc3RlckdvYWxzLFxyXG5cdEFERF9BREFQVEFUSU9OX0xPRzogaGFuZGxlQWRkQWRhcHRhdGlvbkxvZyxcclxuXHRSRVBPUlRfU1lTVEVNX1NUQVJUX1RJTUU6IGhhbmRsZVN5c3RlbVRpbWVNZXNzYWdlLFxyXG5cdERJU0FCTEVfU0VSVkVSOiBoYW5kbGVTZXJ2ZXJEaXNhYmxpbmcsXHJcblx0RU5BQkxFX1NFUlZFUjogaGFuZGxlU2VydmVyRW5hYmxpbmcsXHJcblx0VVBEQVRFX0FEQVBUQVRJT05fQUNUSU9OOiBoYW5kbGVVcGRhdGVBZGFwdGF0aW9uQWN0aW9uLFxyXG5cdFVQREFURV9TRVJWRVJfUkVTT1VSQ0VTOiBoYW5kbGVVcGRhdGVSZXNvdXJjZXMsXHJcblx0VVBEQVRFX0VORVJHWV9JTl9VU0U6IGhhbmRsZVVwZGF0ZUVuZXJneUluVXNlLFxyXG5cdFVQREFURV9KT0JfRVhFQ1VUSU9OX1BST1BPUlRJT046IGhhbmRsZVVwZGF0ZUpvYkV4ZWN1dGlvblByb3BvcnRpb24sXHJcbn07XHJcbiJdLCJtYXBwaW5ncyI6Ijs7Ozs7O0FBQUEsSUFBQUEsT0FBQSxHQUFBQyxPQUFBO0FBa0JBLElBQUFDLGdCQUFBLEdBQUFELE9BQUE7QUFzQk8sSUFBTUUsZ0JBQWdCLEdBQUc7RUFDL0JDLHVCQUF1QixFQUFFQyxpQ0FBeUI7RUFDbERDLHFCQUFxQixFQUFFQyxpQ0FBeUI7RUFDaERDLDBCQUEwQixFQUFFQyxzQ0FBOEI7RUFDMURDLDRCQUE0QixFQUFFQyx3Q0FBZ0M7RUFDOURDLGdCQUFnQixFQUFFQyxxQ0FBb0I7RUFDdENDLHdCQUF3QixFQUFFQyw2Q0FBNEI7RUFDdERDLGlCQUFpQixFQUFFQyw4QkFBc0I7RUFDekNDLDZCQUE2QixFQUFFQyx3Q0FBdUI7RUFDdERDLGtDQUFrQyxFQUFFQyw2Q0FBNEI7RUFDaEVDLHNCQUFzQixFQUFFQyxrQ0FBMEI7RUFDbERDLDJCQUEyQixFQUFFQyx5QkFBaUI7RUFDOUNDLDBCQUEwQixFQUFFQywwQkFBa0I7RUFDOUNDLFdBQVcsRUFBRUMsaUNBQWdCO0VBQzdCQyxhQUFhLEVBQUVDLGdDQUFlO0VBQzlCQyxjQUFjLEVBQUVDLG1DQUFrQjtFQUNsQ0Msc0JBQXNCLEVBQUVDLG9DQUFtQjtFQUMzQ0MsaUJBQWlCLEVBQUVDLHNDQUFxQjtFQUN4Q0MscUJBQXFCLEVBQUVDLGdDQUF3QjtFQUMvQ0MseUJBQXlCLEVBQUVDLG1DQUEyQjtFQUN0REMsMkJBQTJCLEVBQUVDLHFDQUE2QjtFQUMxREMsMEJBQTBCLEVBQUVDLHVDQUFzQjtFQUNsREMscUJBQXFCLEVBQUVDLHNDQUFxQjtFQUM1Q0MsNEJBQTRCLEVBQUVDLDZDQUE0QjtFQUMxREMsMEJBQTBCLEVBQUVDLHdDQUF1QjtFQUNuREMsY0FBYyxFQUFFQyxvQ0FBbUI7RUFDbkNDLFlBQVksRUFBRUMsa0NBQWlCO0VBQy9CQyxpQkFBaUIsRUFBRUMsMkJBQW1CO0VBQ3RDQyxrQkFBa0IsRUFBRUMsOEJBQXNCO0VBQzFDQyx3QkFBd0IsRUFBRUMsK0JBQXVCO0VBQ2pEQyxjQUFjLEVBQUVDLHNDQUFxQjtFQUNyQ0MsYUFBYSxFQUFFQyxxQ0FBb0I7RUFDbkNDLHdCQUF3QixFQUFFQyxvQ0FBNEI7RUFDdERDLHVCQUF1QixFQUFFQyxzQ0FBcUI7RUFDOUNDLG9CQUFvQixFQUFFQyx3Q0FBdUI7RUFDN0NDLCtCQUErQixFQUFFQztBQUNsQyxDQUFDO0FBQUNDLE9BQUEsQ0FBQXZFLGdCQUFBLEdBQUFBLGdCQUFBIn0=