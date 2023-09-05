"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.handleSystemTimeMessage = void 0;
var _simulationState = require("./simulation-state");
var handleSystemTimeMessage = function handleSystemTimeMessage(msg) {
  if (_simulationState.SIMULATION_STATE.systemStartTime === null) {
    _simulationState.SIMULATION_STATE.systemStartTime = msg.time;
    _simulationState.SIMULATION_STATE.secondsPerHour = msg.secondsPerHour;
  }
};
exports.handleSystemTimeMessage = handleSystemTimeMessage;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfc2ltdWxhdGlvblN0YXRlIiwicmVxdWlyZSIsImhhbmRsZVN5c3RlbVRpbWVNZXNzYWdlIiwibXNnIiwiU0lNVUxBVElPTl9TVEFURSIsInN5c3RlbVN0YXJ0VGltZSIsInRpbWUiLCJzZWNvbmRzUGVySG91ciIsImV4cG9ydHMiXSwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvbW9kdWxlL3NpbXVsYXRpb24vbWVzc2FnZS1oYW5kbGVyLnRzIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IFNJTVVMQVRJT05fU1RBVEUgfSBmcm9tIFwiLi9zaW11bGF0aW9uLXN0YXRlXCI7XHJcblxyXG5jb25zdCBoYW5kbGVTeXN0ZW1UaW1lTWVzc2FnZSA9IChtc2cpID0+IHtcclxuXHRpZiAoU0lNVUxBVElPTl9TVEFURS5zeXN0ZW1TdGFydFRpbWUgPT09IG51bGwpIHtcclxuXHRcdFNJTVVMQVRJT05fU1RBVEUuc3lzdGVtU3RhcnRUaW1lID0gbXNnLnRpbWU7XHJcblx0XHRTSU1VTEFUSU9OX1NUQVRFLnNlY29uZHNQZXJIb3VyID0gbXNnLnNlY29uZHNQZXJIb3VyO1xyXG5cdH1cclxufTtcclxuXHJcbmV4cG9ydCB7IGhhbmRsZVN5c3RlbVRpbWVNZXNzYWdlIH07XHJcbiJdLCJtYXBwaW5ncyI6Ijs7Ozs7O0FBQUEsSUFBQUEsZ0JBQUEsR0FBQUMsT0FBQTtBQUVBLElBQU1DLHVCQUF1QixHQUFHLFNBQTFCQSx1QkFBdUJBLENBQUlDLEdBQUcsRUFBSztFQUN4QyxJQUFJQyxpQ0FBZ0IsQ0FBQ0MsZUFBZSxLQUFLLElBQUksRUFBRTtJQUM5Q0QsaUNBQWdCLENBQUNDLGVBQWUsR0FBR0YsR0FBRyxDQUFDRyxJQUFJO0lBQzNDRixpQ0FBZ0IsQ0FBQ0csY0FBYyxHQUFHSixHQUFHLENBQUNJLGNBQWM7RUFDckQ7QUFDRCxDQUFDO0FBQUNDLE9BQUEsQ0FBQU4sdUJBQUEsR0FBQUEsdUJBQUEifQ==