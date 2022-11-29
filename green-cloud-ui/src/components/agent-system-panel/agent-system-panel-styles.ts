import React from 'react'

interface Styles {
   cloudContainer: React.CSSProperties
}

export const styles: Styles = {
   cloudContainer: {
      flexShrink: 0,
      backgroundColor: 'var(--white)',
      height: '95%',
      marginLeft: '20px',
      minWidth: 'fit-content',
   },
}