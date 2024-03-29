import { AdaptationPanel } from '@components'
import ClientStatistics from 'components/main-panel/client-panel/client-panel-connected'
import CloudPanel from 'components/main-panel/cloud-panel/cloud-panel-connected'
import AgentPanel from './agent-panel/agent-panel-connected'
import { MenuTab } from '@types'
import CreatorPanel from './creator-panel/creator-panel-connected'

export const PANEL_TABS = [
   {
      header: 'Cloud network statistics',
      id: MenuTab.CLOUD_SUMMARY,
      panel: <CloudPanel />,
      removeScroll: false
   },
   {
      header: 'Agents statistics',
      id: MenuTab.AGENTS,
      panel: <AgentPanel />,
      removeScroll: false
   },
   {
      header: 'Clients statistics',
      id: MenuTab.CLIENTS,
      panel: <ClientStatistics />,
      removeScroll: true
   },
   {
      header: 'System adaptation statistics',
      id: MenuTab.ADAPTATION,
      panel: <AdaptationPanel />,
      removeScroll: false
   },
   {
      header: 'Agent creator panel',
      id: MenuTab.CREATOR,
      panel: <CreatorPanel />,
      removeScroll: true
   }
]
