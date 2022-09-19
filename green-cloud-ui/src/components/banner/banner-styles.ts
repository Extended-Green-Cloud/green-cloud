import React from "react"

interface Styles {
    parentContainer: React.CSSProperties,
    bannerContainer: React.CSSProperties,
    bannerText: React.CSSProperties,
    bannerIcon: React.CSSProperties
}

export const styles: Styles = {
    parentContainer: {
        backgroundColor: 'var(--green-1)',
        boxShadow: 'var(--banner-shadow)',
        height: 'min-content',
        paddingBottom: '5%'
    },
    bannerContainer: {
        display: 'flex',
        alignItems: 'center',
        color: 'var(--white)',
        fontFamily: 'var(--font-1)',
        fontSize: 'var(--font-size-1)',
        fontWeight: '300',
        paddingLeft: '10px',
        paddingTop: '10px',
        height: '50px',
    },
    bannerText: {
        paddingLeft: '15px',
        opacity: '0.85',
    },
    bannerIcon: {
        height: '90%',
        opacity: '0.85',
    }
}