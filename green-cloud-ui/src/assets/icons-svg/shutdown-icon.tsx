import React from 'react'
import { IconProps } from '@types'

/**
 * Svg shutdown icon
 *
 * @param {string}[size] - icon size passed as string (eg. '2px', '10%')
 * @param {string}[color] - optional icon fill
 * @returns JSX object representing svg icon
 */
const ShutdownIcon = ({ size, color }: IconProps) => {
   const fill = color ?? '#505050'
   return (
      <svg fill={fill} height={size} width={size} viewBox="0 0 512 512">
         <g>
            <g>
               <path
                  d="M256,0C114.84,0,0,114.84,0,256s114.84,256,256,256s256-114.84,256-256S397.16,0,256,0z M237.268,95.557
           c0-10.345,8.387-18.732,18.732-18.732s18.732,8.387,18.732,18.732v99.234c0,10.345-8.387,18.732-18.732,18.732
           s-18.732-8.387-18.732-18.732V95.557z M256,399.61c-79.186,0-143.61-64.423-143.61-143.61c0-45.857,22.245-89.352,59.506-116.351
           c8.376-6.072,20.088-4.2,26.159,4.177c6.07,8.377,4.2,20.089-4.177,26.159c-27.567,19.976-44.025,52.13-44.025,86.015
           c0,58.529,47.617,106.146,106.146,106.146S362.146,314.529,362.146,256c0-34.897-17.238-67.604-46.112-87.49
           c-8.52-5.868-10.671-17.532-4.803-26.052c5.867-8.522,17.532-10.671,26.051-4.803C376.31,164.533,399.61,208.775,399.61,256
           C399.61,335.186,335.186,399.61,256,399.61z"
               />
            </g>
         </g>
      </svg>
   )
}

export default ShutdownIcon