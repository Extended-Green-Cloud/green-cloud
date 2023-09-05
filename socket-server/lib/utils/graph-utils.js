"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getServerState = exports.getNodeState = exports.getGreenEnergyState = exports.getCloudNetworkState = exports.createServerEdges = exports.createNodeForAgent = exports.createGreenEnergyEdges = exports.createEdge = exports.createCloudNetworkEdges = exports.createAgentConnections = void 0;
var _constants = require("../constants/constants");
var _module = require("../module");
function _typeof(obj) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (obj) { return typeof obj; } : function (obj) { return obj && "function" == typeof Symbol && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }, _typeof(obj); }
function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); enumerableOnly && (symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; })), keys.push.apply(keys, symbols); } return keys; }
function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = null != arguments[i] ? arguments[i] : {}; i % 2 ? ownKeys(Object(source), !0).forEach(function (key) { _defineProperty(target, key, source[key]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)) : ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } return target; }
function _defineProperty(obj, key, value) { key = _toPropertyKey(key); if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return _typeof(key) === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (_typeof(input) !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (_typeof(res) !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
var getCloudNetworkState = function getCloudNetworkState(cloudNetwork) {
  if (cloudNetwork.traffic > 85) return "high";
  if (cloudNetwork.traffic > 50) return "medium";
  return cloudNetwork.traffic > 0 ? "low" : "inactive";
};
exports.getCloudNetworkState = getCloudNetworkState;
var getServerState = function getServerState(server) {
  if (server.numberOfJobsOnHold > 0) return "on_hold";
  if (server.backUpTraffic > 0) return "back_up";
  return server.isActive ? "active" : "inactive";
};
exports.getServerState = getServerState;
var getGreenEnergyState = function getGreenEnergyState(greenEnergy) {
  if (greenEnergy.numberOfJobsOnHold > 0 && greenEnergy.numberOfExecutedJobs > 0) return "on_hold";
  return greenEnergy.isActive ? "active" : "inactive";
};
exports.getGreenEnergyState = getGreenEnergyState;
var getNodeState = function getNodeState(agent) {
  switch (agent.type) {
    case _constants.AGENT_TYPES.CLOUD_NETWORK:
      return getCloudNetworkState(agent);
    case _constants.AGENT_TYPES.GREEN_ENERGY:
      return getGreenEnergyState(agent);
    case _constants.AGENT_TYPES.SERVER:
      return getServerState(agent);
    default:
      return null;
  }
};
exports.getNodeState = getNodeState;
var createCloudNetworkEdges = function createCloudNetworkEdges(agent) {
  var scheduler = _module.AGENTS_STATE.agents.find(function (agent) {
    return agent.type === _constants.AGENT_TYPES.SCHEDULER;
  });
  var schedulerEdge = createEdge(agent.name, scheduler.name);
  return [schedulerEdge];
};
exports.createCloudNetworkEdges = createCloudNetworkEdges;
var createServerEdges = function createServerEdges(agent) {
  var cloudNetworkEdge = createEdge(agent.name, agent.cloudNetworkAgent);
  return [cloudNetworkEdge];
};
exports.createServerEdges = createServerEdges;
var createGreenEnergyEdges = function createGreenEnergyEdges(agent) {
  var edgeMonitoring = createEdge(agent.name, agent.monitoringAgent);
  var edgesServers = agent.connectedServers.map(function (server) {
    return createEdge(agent.name, server);
  });
  return edgesServers.concat(edgeMonitoring);
};
exports.createGreenEnergyEdges = createGreenEnergyEdges;
var createEdge = function createEdge(source, target) {
  var id = [source, target, "BI"].join("-");
  return {
    data: {
      id: id,
      source: source,
      target: target
    },
    state: "inactive"
  };
};
exports.createEdge = createEdge;
var createNodeForAgent = function createNodeForAgent(agent) {
  var node = {
    id: agent.name,
    label: agent.name,
    type: agent.type,
    adaptation: agent.adaptation
  };
  switch (agent.type) {
    case _constants.AGENT_TYPES.CLOUD_NETWORK:
    case _constants.AGENT_TYPES.GREEN_ENERGY:
    case _constants.AGENT_TYPES.SERVER:
      return _objectSpread({
        state: "inactive"
      }, node);
    default:
      return node;
  }
};
exports.createNodeForAgent = createNodeForAgent;
var createAgentConnections = function createAgentConnections(agent) {
  switch (agent.type) {
    case _constants.AGENT_TYPES.SERVER:
      return createServerEdges(agent);
    case _constants.AGENT_TYPES.GREEN_ENERGY:
      return createGreenEnergyEdges(agent);
    case _constants.AGENT_TYPES.CLOUD_NETWORK:
      return createCloudNetworkEdges(agent);
    default:
      return [];
  }
};
exports.createAgentConnections = createAgentConnections;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfY29uc3RhbnRzIiwicmVxdWlyZSIsIl9tb2R1bGUiLCJfdHlwZW9mIiwib2JqIiwiU3ltYm9sIiwiaXRlcmF0b3IiLCJjb25zdHJ1Y3RvciIsInByb3RvdHlwZSIsIm93bktleXMiLCJvYmplY3QiLCJlbnVtZXJhYmxlT25seSIsImtleXMiLCJPYmplY3QiLCJnZXRPd25Qcm9wZXJ0eVN5bWJvbHMiLCJzeW1ib2xzIiwiZmlsdGVyIiwic3ltIiwiZ2V0T3duUHJvcGVydHlEZXNjcmlwdG9yIiwiZW51bWVyYWJsZSIsInB1c2giLCJhcHBseSIsIl9vYmplY3RTcHJlYWQiLCJ0YXJnZXQiLCJpIiwiYXJndW1lbnRzIiwibGVuZ3RoIiwic291cmNlIiwiZm9yRWFjaCIsImtleSIsIl9kZWZpbmVQcm9wZXJ0eSIsImdldE93blByb3BlcnR5RGVzY3JpcHRvcnMiLCJkZWZpbmVQcm9wZXJ0aWVzIiwiZGVmaW5lUHJvcGVydHkiLCJ2YWx1ZSIsIl90b1Byb3BlcnR5S2V5IiwiY29uZmlndXJhYmxlIiwid3JpdGFibGUiLCJhcmciLCJfdG9QcmltaXRpdmUiLCJTdHJpbmciLCJpbnB1dCIsImhpbnQiLCJwcmltIiwidG9QcmltaXRpdmUiLCJ1bmRlZmluZWQiLCJyZXMiLCJjYWxsIiwiVHlwZUVycm9yIiwiTnVtYmVyIiwiZ2V0Q2xvdWROZXR3b3JrU3RhdGUiLCJjbG91ZE5ldHdvcmsiLCJ0cmFmZmljIiwiZXhwb3J0cyIsImdldFNlcnZlclN0YXRlIiwic2VydmVyIiwibnVtYmVyT2ZKb2JzT25Ib2xkIiwiYmFja1VwVHJhZmZpYyIsImlzQWN0aXZlIiwiZ2V0R3JlZW5FbmVyZ3lTdGF0ZSIsImdyZWVuRW5lcmd5IiwibnVtYmVyT2ZFeGVjdXRlZEpvYnMiLCJnZXROb2RlU3RhdGUiLCJhZ2VudCIsInR5cGUiLCJBR0VOVF9UWVBFUyIsIkNMT1VEX05FVFdPUksiLCJHUkVFTl9FTkVSR1kiLCJTRVJWRVIiLCJjcmVhdGVDbG91ZE5ldHdvcmtFZGdlcyIsInNjaGVkdWxlciIsIkFHRU5UU19TVEFURSIsImFnZW50cyIsImZpbmQiLCJTQ0hFRFVMRVIiLCJzY2hlZHVsZXJFZGdlIiwiY3JlYXRlRWRnZSIsIm5hbWUiLCJjcmVhdGVTZXJ2ZXJFZGdlcyIsImNsb3VkTmV0d29ya0VkZ2UiLCJjbG91ZE5ldHdvcmtBZ2VudCIsImNyZWF0ZUdyZWVuRW5lcmd5RWRnZXMiLCJlZGdlTW9uaXRvcmluZyIsIm1vbml0b3JpbmdBZ2VudCIsImVkZ2VzU2VydmVycyIsImNvbm5lY3RlZFNlcnZlcnMiLCJtYXAiLCJjb25jYXQiLCJpZCIsImpvaW4iLCJkYXRhIiwic3RhdGUiLCJjcmVhdGVOb2RlRm9yQWdlbnQiLCJub2RlIiwibGFiZWwiLCJhZGFwdGF0aW9uIiwiY3JlYXRlQWdlbnRDb25uZWN0aW9ucyJdLCJzb3VyY2VzIjpbIi4uLy4uL3NyYy91dGlscy9ncmFwaC11dGlscy50cyJdLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBBR0VOVF9UWVBFUyB9IGZyb20gXCIuLi9jb25zdGFudHMvY29uc3RhbnRzXCI7XHJcbmltcG9ydCB7IEFHRU5UU19TVEFURSB9IGZyb20gXCIuLi9tb2R1bGVcIjtcclxuaW1wb3J0IHsgQ2xvdWROZXR3b3JrQWdlbnQgfSBmcm9tIFwiLi4vbW9kdWxlL2FnZW50cy90eXBlc1wiO1xyXG5pbXBvcnQgeyBTZXJ2ZXJBZ2VudCB9IGZyb20gXCIuLi9tb2R1bGUvYWdlbnRzL3R5cGVzL3NlcnZlci1hZ2VudFwiO1xyXG5cclxuY29uc3QgZ2V0Q2xvdWROZXR3b3JrU3RhdGUgPSAoY2xvdWROZXR3b3JrOiBDbG91ZE5ldHdvcmtBZ2VudCkgPT4ge1xyXG5cdGlmIChjbG91ZE5ldHdvcmsudHJhZmZpYyA+IDg1KSByZXR1cm4gXCJoaWdoXCI7XHJcblx0aWYgKGNsb3VkTmV0d29yay50cmFmZmljID4gNTApIHJldHVybiBcIm1lZGl1bVwiO1xyXG5cclxuXHRyZXR1cm4gY2xvdWROZXR3b3JrLnRyYWZmaWMgPiAwID8gXCJsb3dcIiA6IFwiaW5hY3RpdmVcIjtcclxufTtcclxuXHJcbmNvbnN0IGdldFNlcnZlclN0YXRlID0gKHNlcnZlcjogU2VydmVyQWdlbnQpID0+IHtcclxuXHRpZiAoc2VydmVyLm51bWJlck9mSm9ic09uSG9sZCA+IDApIHJldHVybiBcIm9uX2hvbGRcIjtcclxuXHRpZiAoc2VydmVyLmJhY2tVcFRyYWZmaWMgPiAwKSByZXR1cm4gXCJiYWNrX3VwXCI7XHJcblxyXG5cdHJldHVybiBzZXJ2ZXIuaXNBY3RpdmUgPyBcImFjdGl2ZVwiIDogXCJpbmFjdGl2ZVwiO1xyXG59O1xyXG5cclxuY29uc3QgZ2V0R3JlZW5FbmVyZ3lTdGF0ZSA9IChncmVlbkVuZXJneSkgPT4ge1xyXG5cdGlmIChncmVlbkVuZXJneS5udW1iZXJPZkpvYnNPbkhvbGQgPiAwICYmIGdyZWVuRW5lcmd5Lm51bWJlck9mRXhlY3V0ZWRKb2JzID4gMCkgcmV0dXJuIFwib25faG9sZFwiO1xyXG5cclxuXHRyZXR1cm4gZ3JlZW5FbmVyZ3kuaXNBY3RpdmUgPyBcImFjdGl2ZVwiIDogXCJpbmFjdGl2ZVwiO1xyXG59O1xyXG5cclxuY29uc3QgZ2V0Tm9kZVN0YXRlID0gKGFnZW50KSA9PiB7XHJcblx0c3dpdGNoIChhZ2VudC50eXBlKSB7XHJcblx0XHRjYXNlIEFHRU5UX1RZUEVTLkNMT1VEX05FVFdPUks6XHJcblx0XHRcdHJldHVybiBnZXRDbG91ZE5ldHdvcmtTdGF0ZShhZ2VudCk7XHJcblx0XHRjYXNlIEFHRU5UX1RZUEVTLkdSRUVOX0VORVJHWTpcclxuXHRcdFx0cmV0dXJuIGdldEdyZWVuRW5lcmd5U3RhdGUoYWdlbnQpO1xyXG5cdFx0Y2FzZSBBR0VOVF9UWVBFUy5TRVJWRVI6XHJcblx0XHRcdHJldHVybiBnZXRTZXJ2ZXJTdGF0ZShhZ2VudCk7XHJcblx0XHRkZWZhdWx0OlxyXG5cdFx0XHRyZXR1cm4gbnVsbDtcclxuXHR9XHJcbn07XHJcblxyXG5jb25zdCBjcmVhdGVDbG91ZE5ldHdvcmtFZGdlcyA9IChhZ2VudCkgPT4ge1xyXG5cdGNvbnN0IHNjaGVkdWxlciA9IEFHRU5UU19TVEFURS5hZ2VudHMuZmluZCgoYWdlbnQpID0+IGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLlNDSEVEVUxFUik7XHJcblx0Y29uc3Qgc2NoZWR1bGVyRWRnZSA9IGNyZWF0ZUVkZ2UoYWdlbnQubmFtZSwgc2NoZWR1bGVyLm5hbWUpO1xyXG5cclxuXHRyZXR1cm4gW3NjaGVkdWxlckVkZ2VdO1xyXG59O1xyXG5cclxuY29uc3QgY3JlYXRlU2VydmVyRWRnZXMgPSAoYWdlbnQpID0+IHtcclxuXHRjb25zdCBjbG91ZE5ldHdvcmtFZGdlID0gY3JlYXRlRWRnZShhZ2VudC5uYW1lLCBhZ2VudC5jbG91ZE5ldHdvcmtBZ2VudCk7XHJcblxyXG5cdHJldHVybiBbY2xvdWROZXR3b3JrRWRnZV07XHJcbn07XHJcblxyXG5jb25zdCBjcmVhdGVHcmVlbkVuZXJneUVkZ2VzID0gKGFnZW50KSA9PiB7XHJcblx0Y29uc3QgZWRnZU1vbml0b3JpbmcgPSBjcmVhdGVFZGdlKGFnZW50Lm5hbWUsIGFnZW50Lm1vbml0b3JpbmdBZ2VudCk7XHJcblx0Y29uc3QgZWRnZXNTZXJ2ZXJzID0gYWdlbnQuY29ubmVjdGVkU2VydmVycy5tYXAoKHNlcnZlcikgPT4gY3JlYXRlRWRnZShhZ2VudC5uYW1lLCBzZXJ2ZXIpKTtcclxuXHJcblx0cmV0dXJuIGVkZ2VzU2VydmVycy5jb25jYXQoZWRnZU1vbml0b3JpbmcpO1xyXG59O1xyXG5cclxuY29uc3QgY3JlYXRlRWRnZSA9IChzb3VyY2UsIHRhcmdldCkgPT4ge1xyXG5cdGNvbnN0IGlkID0gW3NvdXJjZSwgdGFyZ2V0LCBcIkJJXCJdLmpvaW4oXCItXCIpO1xyXG5cdHJldHVybiB7IGRhdGE6IHsgaWQsIHNvdXJjZSwgdGFyZ2V0IH0sIHN0YXRlOiBcImluYWN0aXZlXCIgfTtcclxufTtcclxuXHJcbmNvbnN0IGNyZWF0ZU5vZGVGb3JBZ2VudCA9IChhZ2VudCkgPT4ge1xyXG5cdGNvbnN0IG5vZGUgPSB7XHJcblx0XHRpZDogYWdlbnQubmFtZSxcclxuXHRcdGxhYmVsOiBhZ2VudC5uYW1lLFxyXG5cdFx0dHlwZTogYWdlbnQudHlwZSxcclxuXHRcdGFkYXB0YXRpb246IGFnZW50LmFkYXB0YXRpb24sXHJcblx0fTtcclxuXHRzd2l0Y2ggKGFnZW50LnR5cGUpIHtcclxuXHRcdGNhc2UgQUdFTlRfVFlQRVMuQ0xPVURfTkVUV09SSzpcclxuXHRcdGNhc2UgQUdFTlRfVFlQRVMuR1JFRU5fRU5FUkdZOlxyXG5cdFx0Y2FzZSBBR0VOVF9UWVBFUy5TRVJWRVI6XHJcblx0XHRcdHJldHVybiB7IHN0YXRlOiBcImluYWN0aXZlXCIsIC4uLm5vZGUgfTtcclxuXHRcdGRlZmF1bHQ6XHJcblx0XHRcdHJldHVybiBub2RlO1xyXG5cdH1cclxufTtcclxuXHJcbmNvbnN0IGNyZWF0ZUFnZW50Q29ubmVjdGlvbnMgPSAoYWdlbnQpID0+IHtcclxuXHRzd2l0Y2ggKGFnZW50LnR5cGUpIHtcclxuXHRcdGNhc2UgQUdFTlRfVFlQRVMuU0VSVkVSOlxyXG5cdFx0XHRyZXR1cm4gY3JlYXRlU2VydmVyRWRnZXMoYWdlbnQpO1xyXG5cdFx0Y2FzZSBBR0VOVF9UWVBFUy5HUkVFTl9FTkVSR1k6XHJcblx0XHRcdHJldHVybiBjcmVhdGVHcmVlbkVuZXJneUVkZ2VzKGFnZW50KTtcclxuXHRcdGNhc2UgQUdFTlRfVFlQRVMuQ0xPVURfTkVUV09SSzpcclxuXHRcdFx0cmV0dXJuIGNyZWF0ZUNsb3VkTmV0d29ya0VkZ2VzKGFnZW50KTtcclxuXHRcdGRlZmF1bHQ6XHJcblx0XHRcdHJldHVybiBbXTtcclxuXHR9XHJcbn07XHJcblxyXG5leHBvcnQge1xyXG5cdGdldENsb3VkTmV0d29ya1N0YXRlLFxyXG5cdGdldEdyZWVuRW5lcmd5U3RhdGUsXHJcblx0Z2V0U2VydmVyU3RhdGUsXHJcblx0Z2V0Tm9kZVN0YXRlLFxyXG5cdGNyZWF0ZUNsb3VkTmV0d29ya0VkZ2VzLFxyXG5cdGNyZWF0ZVNlcnZlckVkZ2VzLFxyXG5cdGNyZWF0ZUdyZWVuRW5lcmd5RWRnZXMsXHJcblx0Y3JlYXRlRWRnZSxcclxuXHRjcmVhdGVOb2RlRm9yQWdlbnQsXHJcblx0Y3JlYXRlQWdlbnRDb25uZWN0aW9ucyxcclxufTtcclxuIl0sIm1hcHBpbmdzIjoiOzs7Ozs7QUFBQSxJQUFBQSxVQUFBLEdBQUFDLE9BQUE7QUFDQSxJQUFBQyxPQUFBLEdBQUFELE9BQUE7QUFBeUMsU0FBQUUsUUFBQUMsR0FBQSxzQ0FBQUQsT0FBQSx3QkFBQUUsTUFBQSx1QkFBQUEsTUFBQSxDQUFBQyxRQUFBLGFBQUFGLEdBQUEsa0JBQUFBLEdBQUEsZ0JBQUFBLEdBQUEsV0FBQUEsR0FBQSx5QkFBQUMsTUFBQSxJQUFBRCxHQUFBLENBQUFHLFdBQUEsS0FBQUYsTUFBQSxJQUFBRCxHQUFBLEtBQUFDLE1BQUEsQ0FBQUcsU0FBQSxxQkFBQUosR0FBQSxLQUFBRCxPQUFBLENBQUFDLEdBQUE7QUFBQSxTQUFBSyxRQUFBQyxNQUFBLEVBQUFDLGNBQUEsUUFBQUMsSUFBQSxHQUFBQyxNQUFBLENBQUFELElBQUEsQ0FBQUYsTUFBQSxPQUFBRyxNQUFBLENBQUFDLHFCQUFBLFFBQUFDLE9BQUEsR0FBQUYsTUFBQSxDQUFBQyxxQkFBQSxDQUFBSixNQUFBLEdBQUFDLGNBQUEsS0FBQUksT0FBQSxHQUFBQSxPQUFBLENBQUFDLE1BQUEsV0FBQUMsR0FBQSxXQUFBSixNQUFBLENBQUFLLHdCQUFBLENBQUFSLE1BQUEsRUFBQU8sR0FBQSxFQUFBRSxVQUFBLE9BQUFQLElBQUEsQ0FBQVEsSUFBQSxDQUFBQyxLQUFBLENBQUFULElBQUEsRUFBQUcsT0FBQSxZQUFBSCxJQUFBO0FBQUEsU0FBQVUsY0FBQUMsTUFBQSxhQUFBQyxDQUFBLE1BQUFBLENBQUEsR0FBQUMsU0FBQSxDQUFBQyxNQUFBLEVBQUFGLENBQUEsVUFBQUcsTUFBQSxXQUFBRixTQUFBLENBQUFELENBQUEsSUFBQUMsU0FBQSxDQUFBRCxDQUFBLFFBQUFBLENBQUEsT0FBQWYsT0FBQSxDQUFBSSxNQUFBLENBQUFjLE1BQUEsT0FBQUMsT0FB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