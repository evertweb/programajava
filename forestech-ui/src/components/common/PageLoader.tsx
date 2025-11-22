/**
 * PageLoader Component
 * Loading fallback for lazy-loaded components
 */

import { Box, CircularProgress, Typography } from '@mui/material';

export default function PageLoader() {
    return (
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '60vh',
                flexDirection: 'column',
                gap: 2,
            }}
        >
            <CircularProgress size={48} thickness={4} />
            <Typography variant="body2" color="text.secondary">
                Cargando módulo...
            </Typography>
        </Box>
    );
}
