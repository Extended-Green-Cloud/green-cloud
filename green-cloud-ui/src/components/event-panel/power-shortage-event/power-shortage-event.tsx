import { useEffect, useState } from 'react'

import { AgentEvent, EventState, PowerShortageEventData } from '@types'
import { toast } from 'react-toastify'
import NumericInput from 'components/common/numeric-input/numeric-input'
import { Button } from 'components/common'

interface Props {
   event: AgentEvent
   label: string
   agentName: string
   maxCapacity: number
   triggerPowerShortage: (data: PowerShortageEventData) => void
}

const placeholder = 'Provide maximum capacity'
const buttonWaitLabel = 'Wait before next event triggering'
const topButtonLabel = 'Maximum Capacity'
/**
 * Component represents fields comnnected with the trigger power shortage event for given agent
 *
 * @param {AgentEvent}[event] - power shortage event
 * @param {string}[label] - label describing event card
 * @param {string}[agentName] - name of the agent affected by power shortage
 * @param {string}[maxCapacity] - initial maximum capacity of given agent
 * @param {func}[triggerPowerShortage] - action responsible for power shortage event
 *
 * @returns JSX Element
 */
const PowerShortageEvent = ({ event, label, agentName, maxCapacity, triggerPowerShortage }: Props) => {
   const [inputVal, setInputVal] = useState<number>(0)

   const disabled = event.state === EventState.INACTIVE || event.disabled
   const buttonLabel = event.disabled ? buttonWaitLabel : label

   useEffect(() => {
      setInputVal(0)
   }, [agentName])

   const getButtonStyle = () => {
      const eventStyle = event.state === EventState.ACTIVE ? 'event-active-button' : 'event-inactive-button'
      return ['event-button', eventStyle].join(' ')
   }

   function handlePowerShortageChange(e: React.ChangeEvent<HTMLInputElement>) {
      const value = parseFloat(e.target.value)
      const parsedVal = value < 0 ? 0 : value > maxCapacity ? maxCapacity : value
      setInputVal(parsedVal)
   }

   function handlePowerShortageTrigger() {
      if (typeof inputVal === 'undefined' && event?.state === EventState.ACTIVE) {
         toast.dismiss()
         toast.info('The new maximum capacity must be specified!')
      } else {
         const message = event?.state === EventState.ACTIVE ? 'triggered' : 'finished'
         toast.dismiss()
         toast.warn(`Power shortage ${message} in ${agentName}`)
         triggerPowerShortage({
            agentName,
            newMaximumCapacity: inputVal as number,
         })
         setInputVal(0)
      }
   }

   return (
      <>
         <div>
            <NumericInput
               {...{
                  value: inputVal,
                  handleChange: handlePowerShortageChange,
                  label: topButtonLabel,
                  max: maxCapacity,
                  disabled,
                  placeholder,
               }}
            />
         </div>
         <Button
            {...{
               buttonClassName: getButtonStyle(),
               onClick: handlePowerShortageTrigger,
               isDisabled: event.disabled,
               title: buttonLabel.toUpperCase(),
            }}
         />
      </>
   )
}

export default PowerShortageEvent