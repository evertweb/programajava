/**
 * PageTransition Component
 * Smooth transitions between routes using Framer Motion
 */

import { motion, AnimatePresence } from 'framer-motion';
import type { ReactNode } from 'react';

interface PageTransitionProps {
    children: ReactNode;
    routeKey: string;
}

const pageVariants = {
    initial: {
        opacity: 0,
        y: 8,
        scale: 0.98,
    },
    animate: {
        opacity: 1,
        y: 0,
        scale: 1,
        transition: {
            duration: 0.2,
            ease: [0.4, 0, 0.2, 1] as const, // cubic-bezier matching theme
        },
    },
    exit: {
        opacity: 0,
        y: -8,
        scale: 0.98,
        transition: {
            duration: 0.15,
            ease: [0.4, 0, 1, 1] as const,
        },
    },
};

export default function PageTransition({ children, routeKey }: PageTransitionProps) {
    return (
        <AnimatePresence mode="wait">
            <motion.div
                key={routeKey}
                initial="initial"
                animate="animate"
                exit="exit"
                variants={pageVariants}
                style={{ height: '100%' }}
            >
                {children}
            </motion.div>
        </AnimatePresence>
    );
}
