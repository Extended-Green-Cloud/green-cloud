import React from "react"
import { styles } from './card-styles'

interface Props {
    children?: React.ReactNode,
    header?: string | React.ReactNode,
    removeScroll?: boolean,
    containerStyle?: React.CSSProperties
}

/**
 * Component representing common container with shadow effect
 * 
 * @param {object}[children] - content to be displayed inside container
 * @param {string}[header] - header displayed at the top of the container
 * @param {boolean}[removeScroll] -flag indicating whether the scroll bar   should be removed 
 * @param {object}[containerStyle] - optional styling applied to the container
 * @returns 
 */
const Card = ({
    header,
    children,
    containerStyle,
    removeScroll
}: Props) => {

    const contentStyle = removeScroll ?
        { ...styles.cardContent } :
        { ...styles.cardContent, ...styles.cardContentScroll }
    const parentContainerStyle = { ...styles.cardContainer, ...containerStyle }

    const mapHeader = () =>
        typeof header === 'string' ?
            <div style={styles.cardHeader}>{header?.toUpperCase()}</div> :
            header

    return (
        <div style={parentContainerStyle}>
            {header && mapHeader()}
            <div style={contentStyle}>
                {children}
            </div>
        </div>
    )
}

export default Card