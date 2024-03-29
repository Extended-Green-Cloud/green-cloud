import React from 'react'
import { IconProps } from 'types/assets/icon-props'

interface Styles {
   modalStyle: React.CSSProperties
   content: React.CSSProperties
   title: React.CSSProperties
   iconStyle: IconProps
}

export const styles: Styles = {
   modalStyle: {
      width: '40%',
      minWidth: 0,
      height: 'fit-content'
   },
   title: {
      marginLeft: '5px'
   },
   iconStyle: {
      size: '1.3rem',
      color: 'var(--gray-3)'
   },
   content: {
      marginTop: '10px',
      textAlign: 'center',
      fontWeight: 300,
      color: 'var(--gray-3)'
   }
}
