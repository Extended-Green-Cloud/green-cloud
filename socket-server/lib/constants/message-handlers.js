"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.MESSAGE_HANDLERS = void 0;
var _module = require("../module");
var MESSAGE_HANDLERS = {
  INCREMENT_FINISHED_JOBS: _module.handleIncrementFinishJobs,
  INCREMENT_FAILED_JOBS: _module.handleIncrementFailedJobs,
  INCREMENT_WEAK_ADAPTATIONS: _module.handleIncrementWeakAdaptations,
  INCREMENT_STRONG_ADAPTATIONS: _module.handleIncrementStrongAdaptations,
  UPDATE_JOB_QUEUE: _module.handleUpdateJobQueue,
  UPDATE_SERVER_CONNECTION: _module.handleUpdateServerConnection,
  UPDATE_INDICATORS: _module.handleUpdateIndicators,
  UPDATE_SCHEDULER_POWER_PRIORITY: _module.handleUpdatePowerPriority,
  UPDATE_SCHEDULER_DEADLINE_PRIORITY: _module.handleUpdateDeadlinePriority,
  UPDATE_CURRENT_CLIENTS: _module.handleCurrentClientsNumber,
  UPDATE_CURRENT_PLANNED_JOBS: _module.handlePlannedJobs,
  UPDATE_CURRENT_ACTIVE_JOBS: _module.handleExecutedJobs,
  SET_MAXIMUM_CAPACITY: _module.handleSetMaximumCapacity,
  SET_TRAFFIC: _module.handleSetTraffic,
  SET_IS_ACTIVE: _module.handleSetActive,
  SET_JOBS_COUNT: _module.handleSetJobsCount,
  SET_ON_HOLD_JOBS_COUNT: _module.handleSetJobsOnHold,
  SET_CLIENT_NUMBER: _module.handleSetClientNumber,
  SET_CLIENT_JOB_STATUS: _module.handleSetClientJobStatus,
  SET_CLIENT_JOB_TIME_FRAME: _module.handleSetClientJobTimeFrame,
  SET_CLIENT_JOB_DURATION_MAP: _module.handleSetClientJobDurationMap,
  SET_SERVER_BACK_UP_TRAFFIC: _module.handleSetBackUpTraffic,
  SET_JOB_SUCCESS_RATIO: _module.handleSetSuccessRatio,
  SET_WEATHER_PREDICTION_ERROR: _module.handleWeatherPredictionError,
  SET_AVAILABLE_GREEN_ENERGY: _module.handleUpdateGreenEnergy,
  SPLIT_JOB: _module.handleJobSplit,
  REGISTER_AGENT: _module.handleRegisterAgent,
  REMOVE_AGENT: _module.handleRemoveAgent,
  REGISTER_MANAGING: _module.handleRegisterGoals,
  ADD_ADAPTATION_LOG: _module.handleAddAdaptationLog,
  REPORT_SYSTEM_START_TIME: _module.handleSystemTimeMesage,
  DISABLE_SERVER: _module.handleServerDisabling
};
exports.MESSAGE_HANDLERS = MESSAGE_HANDLERS;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfbW9kdWxlIiwicmVxdWlyZSIsIk1FU1NBR0VfSEFORExFUlMiLCJJTkNSRU1FTlRfRklOSVNIRURfSk9CUyIsImhhbmRsZUluY3JlbWVudEZpbmlzaEpvYnMiLCJJTkNSRU1FTlRfRkFJTEVEX0pPQlMiLCJoYW5kbGVJbmNyZW1lbnRGYWlsZWRKb2JzIiwiSU5DUkVNRU5UX1dFQUtfQURBUFRBVElPTlMiLCJoYW5kbGVJbmNyZW1lbnRXZWFrQWRhcHRhdGlvbnMiLCJJTkNSRU1FTlRfU1RST05HX0FEQVBUQVRJT05TIiwiaGFuZGxlSW5jcmVtZW50U3Ryb25nQWRhcHRhdGlvbnMiLCJVUERBVEVfSk9CX1FVRVVFIiwiaGFuZGxlVXBkYXRlSm9iUXVldWUiLCJVUERBVEVfU0VSVkVSX0NPTk5FQ1RJT04iLCJoYW5kbGVVcGRhdGVTZXJ2ZXJDb25uZWN0aW9uIiwiVVBEQVRFX0lORElDQVRPUlMiLCJoYW5kbGVVcGRhdGVJbmRpY2F0b3JzIiwiVVBEQVRFX1NDSEVEVUxFUl9QT1dFUl9QUklPUklUWSIsImhhbmRsZVVwZGF0ZVBvd2VyUHJpb3JpdHkiLCJVUERBVEVfU0NIRURVTEVSX0RFQURMSU5FX1BSSU9SSVRZIiwiaGFuZGxlVXBkYXRlRGVhZGxpbmVQcmlvcml0eSIsIlVQREFURV9DVVJSRU5UX0NMSUVOVFMiLCJoYW5kbGVDdXJyZW50Q2xpZW50c051bWJlciIsIlVQREFURV9DVVJSRU5UX1BMQU5ORURfSk9CUyIsImhhbmRsZVBsYW5uZWRKb2JzIiwiVVBEQVRFX0NVUlJFTlRfQUNUSVZFX0pPQlMiLCJoYW5kbGVFeGVjdXRlZEpvYnMiLCJTRVRfTUFYSU1VTV9DQVBBQ0lUWSIsImhhbmRsZVNldE1heGltdW1DYXBhY2l0eSIsIlNFVF9UUkFGRklDIiwiaGFuZGxlU2V0VHJhZmZpYyIsIlNFVF9JU19BQ1RJVkUiLCJoYW5kbGVTZXRBY3RpdmUiLCJTRVRfSk9CU19DT1VOVCIsImhhbmRsZVNldEpvYnNDb3VudCIsIlNFVF9PTl9IT0xEX0pPQlNfQ09VTlQiLCJoYW5kbGVTZXRKb2JzT25Ib2xkIiwiU0VUX0NMSUVOVF9OVU1CRVIiLCJoYW5kbGVTZXRDbGllbnROdW1iZXIiLCJTRVRfQ0xJRU5UX0pPQl9TVEFUVVMiLCJoYW5kbGVTZXRDbGllbnRKb2JTdGF0dXMiLCJTRVRfQ0xJRU5UX0pPQl9USU1FX0ZSQU1FIiwiaGFuZGxlU2V0Q2xpZW50Sm9iVGltZUZyYW1lIiwiU0VUX0NMSUVOVF9KT0JfRFVSQVRJT05fTUFQIiwiaGFuZGxlU2V0Q2xpZW50Sm9iRHVyYXRpb25NYXAiLCJTRVRfU0VSVkVSX0JBQ0tfVVBfVFJBRkZJQyIsImhhbmRsZVNldEJhY2tVcFRyYWZmaWMiLCJTRVRfSk9CX1NVQ0NFU1NfUkFUSU8iLCJoYW5kbGVTZXRTdWNjZXNzUmF0aW8iLCJTRVRfV0VBVEhFUl9QUkVESUNUSU9OX0VSUk9SIiwiaGFuZGxlV2VhdGhlclByZWRpY3Rpb25FcnJvciIsIlNFVF9BVkFJTEFCTEVfR1JFRU5fRU5FUkdZIiwiaGFuZGxlVXBkYXRlR3JlZW5FbmVyZ3kiLCJTUExJVF9KT0IiLCJoYW5kbGVKb2JTcGxpdCIsIlJFR0lTVEVSX0FHRU5UIiwiaGFuZGxlUmVnaXN0ZXJBZ2VudCIsIlJFTU9WRV9BR0VOVCIsImhhbmRsZVJlbW92ZUFnZW50IiwiUkVHSVNURVJfTUFOQUdJTkciLCJoYW5kbGVSZWdpc3RlckdvYWxzIiwiQUREX0FEQVBUQVRJT05fTE9HIiwiaGFuZGxlQWRkQWRhcHRhdGlvbkxvZyIsIlJFUE9SVF9TWVNURU1fU1RBUlRfVElNRSIsImhhbmRsZVN5c3RlbVRpbWVNZXNhZ2UiLCJESVNBQkxFX1NFUlZFUiIsImhhbmRsZVNlcnZlckRpc2FibGluZyIsImV4cG9ydHMiXSwic291cmNlcyI6WyIuLi8uLi9zcmMvY29uc3RhbnRzL21lc3NhZ2UtaGFuZGxlcnMudHMiXSwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHtcclxuICAgIGhhbmRsZUluY3JlbWVudEZhaWxlZEpvYnMsXHJcbiAgICBoYW5kbGVJbmNyZW1lbnRGaW5pc2hKb2JzLFxyXG4gICAgaGFuZGxlSW5jcmVtZW50U3Ryb25nQWRhcHRhdGlvbnMsXHJcbiAgICBoYW5kbGVJbmNyZW1lbnRXZWFrQWRhcHRhdGlvbnMsXHJcbiAgICBoYW5kbGVKb2JTcGxpdCxcclxuICAgIGhhbmRsZVJlZ2lzdGVyQWdlbnQsXHJcbiAgICBoYW5kbGVSZWdpc3RlckdvYWxzLFxyXG4gICAgaGFuZGxlUmVtb3ZlQWdlbnQsXHJcbiAgICBoYW5kbGVTZXJ2ZXJEaXNhYmxpbmcsXHJcbiAgICBoYW5kbGVTZXRBY3RpdmUsXHJcbiAgICBoYW5kbGVBZGRBZGFwdGF0aW9uTG9nLFxyXG4gICAgaGFuZGxlU2V0QmFja1VwVHJhZmZpYyxcclxuICAgIGhhbmRsZVNldENsaWVudEpvYkR1cmF0aW9uTWFwLFxyXG4gICAgaGFuZGxlU2V0Q2xpZW50Sm9iU3RhdHVzLFxyXG4gICAgaGFuZGxlU2V0Q2xpZW50Sm9iVGltZUZyYW1lLFxyXG4gICAgaGFuZGxlU2V0Q2xpZW50TnVtYmVyLFxyXG4gICAgaGFuZGxlU2V0Sm9ic0NvdW50LFxyXG4gICAgaGFuZGxlU2V0Sm9ic09uSG9sZCxcclxuICAgIGhhbmRsZVNldE1heGltdW1DYXBhY2l0eSxcclxuICAgIGhhbmRsZVNldFN1Y2Nlc3NSYXRpbyxcclxuICAgIGhhbmRsZVNldFRyYWZmaWMsXHJcbiAgICBoYW5kbGVTeXN0ZW1UaW1lTWVzYWdlLFxyXG4gICAgaGFuZGxlVXBkYXRlRGVhZGxpbmVQcmlvcml0eSxcclxuICAgIGhhbmRsZVVwZGF0ZUdyZWVuRW5lcmd5LFxyXG4gICAgaGFuZGxlVXBkYXRlSW5kaWNhdG9ycyxcclxuICAgIGhhbmRsZVVwZGF0ZUpvYlF1ZXVlLFxyXG4gICAgaGFuZGxlVXBkYXRlUG93ZXJQcmlvcml0eSxcclxuICAgIGhhbmRsZVVwZGF0ZVNlcnZlckNvbm5lY3Rpb24sXHJcbiAgICBoYW5kbGVXZWF0aGVyUHJlZGljdGlvbkVycm9yLFxyXG4gICAgaGFuZGxlQ3VycmVudENsaWVudHNOdW1iZXIsXHJcbiAgICBoYW5kbGVFeGVjdXRlZEpvYnMsXHJcbiAgICBoYW5kbGVQbGFubmVkSm9ic1xyXG59IGZyb20gXCIuLi9tb2R1bGVcIlxyXG5cclxuZXhwb3J0IGNvbnN0IE1FU1NBR0VfSEFORExFUlMgPSB7XHJcbiAgICBJTkNSRU1FTlRfRklOSVNIRURfSk9CUzogaGFuZGxlSW5jcmVtZW50RmluaXNoSm9icyxcclxuICAgIElOQ1JFTUVOVF9GQUlMRURfSk9CUzogaGFuZGxlSW5jcmVtZW50RmFpbGVkSm9icyxcclxuICAgIElOQ1JFTUVOVF9XRUFLX0FEQVBUQVRJT05TOiBoYW5kbGVJbmNyZW1lbnRXZWFrQWRhcHRhdGlvbnMsXHJcbiAgICBJTkNSRU1FTlRfU1RST05HX0FEQVBUQVRJT05TOiBoYW5kbGVJbmNyZW1lbnRTdHJvbmdBZGFwdGF0aW9ucyxcclxuICAgIFVQREFURV9KT0JfUVVFVUU6IGhhbmRsZVVwZGF0ZUpvYlF1ZXVlLFxyXG4gICAgVVBEQVRFX1NFUlZFUl9DT05ORUNUSU9OOiBoYW5kbGVVcGRhdGVTZXJ2ZXJDb25uZWN0aW9uLFxyXG4gICAgVVBEQVRFX0lORElDQVRPUlM6IGhhbmRsZVVwZGF0ZUluZGljYXRvcnMsXHJcbiAgICBVUERBVEVfU0NIRURVTEVSX1BPV0VSX1BSSU9SSVRZOiBoYW5kbGVVcGRhdGVQb3dlclByaW9yaXR5LFxyXG4gICAgVVBEQVRFX1NDSEVEVUxFUl9ERUFETElORV9QUklPUklUWTogaGFuZGxlVXBkYXRlRGVhZGxpbmVQcmlvcml0eSxcclxuICAgIFVQREFURV9DVVJSRU5UX0NMSUVOVFM6IGhhbmRsZUN1cnJlbnRDbGllbnRzTnVtYmVyLFxyXG4gICAgVVBEQVRFX0NVUlJFTlRfUExBTk5FRF9KT0JTOiBoYW5kbGVQbGFubmVkSm9icyxcclxuICAgIFVQREFURV9DVVJSRU5UX0FDVElWRV9KT0JTOiBoYW5kbGVFeGVjdXRlZEpvYnMsXHJcbiAgICBTRVRfTUFYSU1VTV9DQVBBQ0lUWTogaGFuZGxlU2V0TWF4aW11bUNhcGFjaXR5LFxyXG4gICAgU0VUX1RSQUZGSUM6IGhhbmRsZVNldFRyYWZmaWMsXHJcbiAgICBTRVRfSVNfQUNUSVZFOiBoYW5kbGVTZXRBY3RpdmUsXHJcbiAgICBTRVRfSk9CU19DT1VOVDogaGFuZGxlU2V0Sm9ic0NvdW50LFxyXG4gICAgU0VUX09OX0hPTERfSk9CU19DT1VOVDogaGFuZGxlU2V0Sm9ic09uSG9sZCxcclxuICAgIFNFVF9DTElFTlRfTlVNQkVSOiBoYW5kbGVTZXRDbGllbnROdW1iZXIsXHJcbiAgICBTRVRfQ0xJRU5UX0pPQl9TVEFUVVM6IGhhbmRsZVNldENsaWVudEpvYlN0YXR1cyxcclxuICAgIFNFVF9DTElFTlRfSk9CX1RJTUVfRlJBTUU6IGhhbmRsZVNldENsaWVudEpvYlRpbWVGcmFtZSxcclxuICAgIFNFVF9DTElFTlRfSk9CX0RVUkFUSU9OX01BUDogaGFuZGxlU2V0Q2xpZW50Sm9iRHVyYXRpb25NYXAsXHJcbiAgICBTRVRfU0VSVkVSX0JBQ0tfVVBfVFJBRkZJQzogaGFuZGxlU2V0QmFja1VwVHJhZmZpYyxcclxuICAgIFNFVF9KT0JfU1VDQ0VTU19SQVRJTzogaGFuZGxlU2V0U3VjY2Vzc1JhdGlvLFxyXG4gICAgU0VUX1dFQVRIRVJfUFJFRElDVElPTl9FUlJPUjogaGFuZGxlV2VhdGhlclByZWRpY3Rpb25FcnJvcixcclxuICAgIFNFVF9BVkFJTEFCTEVfR1JFRU5fRU5FUkdZOiBoYW5kbGVVcGRhdGVHcmVlbkVuZXJneSxcclxuICAgIFNQTElUX0pPQjogaGFuZGxlSm9iU3BsaXQsXHJcbiAgICBSRUdJU1RFUl9BR0VOVDogaGFuZGxlUmVnaXN0ZXJBZ2VudCxcclxuICAgIFJFTU9WRV9BR0VOVDogaGFuZGxlUmVtb3ZlQWdlbnQsXHJcbiAgICBSRUdJU1RFUl9NQU5BR0lORzogaGFuZGxlUmVnaXN0ZXJHb2FscyxcclxuICAgIEFERF9BREFQVEFUSU9OX0xPRzogaGFuZGxlQWRkQWRhcHRhdGlvbkxvZyxcclxuICAgIFJFUE9SVF9TWVNURU1fU1RBUlRfVElNRTogaGFuZGxlU3lzdGVtVGltZU1lc2FnZSxcclxuICAgIERJU0FCTEVfU0VSVkVSOiBoYW5kbGVTZXJ2ZXJEaXNhYmxpbmcsXHJcbn0iXSwibWFwcGluZ3MiOiI7Ozs7OztBQUFBLElBQUFBLE9BQUEsR0FBQUMsT0FBQTtBQW1DTyxJQUFNQyxnQkFBZ0IsR0FBRztFQUM1QkMsdUJBQXVCLEVBQUVDLGlDQUF5QjtFQUNsREMscUJBQXFCLEVBQUVDLGlDQUF5QjtFQUNoREMsMEJBQTBCLEVBQUVDLHNDQUE4QjtFQUMxREMsNEJBQTRCLEVBQUVDLHdDQUFnQztFQUM5REMsZ0JBQWdCLEVBQUVDLDRCQUFvQjtFQUN0Q0Msd0JBQXdCLEVBQUVDLG9DQUE0QjtFQUN0REMsaUJBQWlCLEVBQUVDLDhCQUFzQjtFQUN6Q0MsK0JBQStCLEVBQUVDLGlDQUF5QjtFQUMxREMsa0NBQWtDLEVBQUVDLG9DQUE0QjtFQUNoRUMsc0JBQXNCLEVBQUVDLGtDQUEwQjtFQUNsREMsMkJBQTJCLEVBQUVDLHlCQUFpQjtFQUM5Q0MsMEJBQTBCLEVBQUVDLDBCQUFrQjtFQUM5Q0Msb0JBQW9CLEVBQUVDLGdDQUF3QjtFQUM5Q0MsV0FBVyxFQUFFQyx3QkFBZ0I7RUFDN0JDLGFBQWEsRUFBRUMsdUJBQWU7RUFDOUJDLGNBQWMsRUFBRUMsMEJBQWtCO0VBQ2xDQyxzQkFBc0IsRUFBRUMsMkJBQW1CO0VBQzNDQyxpQkFBaUIsRUFBRUMsNkJBQXFCO0VBQ3hDQyxxQkFBcUIsRUFBRUMsZ0NBQXdCO0VBQy9DQyx5QkFBeUIsRUFBRUMsbUNBQTJCO0VBQ3REQywyQkFBMkIsRUFBRUMscUNBQTZCO0VBQzFEQywwQkFBMEIsRUFBRUMsOEJBQXNCO0VBQ2xEQyxxQkFBcUIsRUFBRUMsNkJBQXFCO0VBQzVDQyw0QkFBNEIsRUFBRUMsb0NBQTRCO0VBQzFEQywwQkFBMEIsRUFBRUMsK0JBQXVCO0VBQ25EQyxTQUFTLEVBQUVDLHNCQUFjO0VBQ3pCQyxjQUFjLEVBQUVDLDJCQUFtQjtFQUNuQ0MsWUFBWSxFQUFFQyx5QkFBaUI7RUFDL0JDLGlCQUFpQixFQUFFQywyQkFBbUI7RUFDdENDLGtCQUFrQixFQUFFQyw4QkFBc0I7RUFDMUNDLHdCQUF3QixFQUFFQyw4QkFBc0I7RUFDaERDLGNBQWMsRUFBRUM7QUFDcEIsQ0FBQztBQUFBQyxPQUFBLENBQUFqRSxnQkFBQSxHQUFBQSxnQkFBQSJ9