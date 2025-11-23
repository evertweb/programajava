/**
 * API Cache Hook
 * Custom hook for caching API responses and reducing redundant calls
 * Now with connection-aware fetching (hybrid approach)
 */

import { useState, useEffect, useCallback, useRef } from 'react';
import { getConnectionContext } from '../context/ConnectionContext';

interface CacheEntry<T> {
    data: T;
    timestamp: number;
}

const cache = new Map<string, CacheEntry<unknown>>();
const CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

interface UseApiCacheOptions {
    enabled?: boolean;
    cacheDuration?: number;
}

interface UseApiCacheReturn<T> {
    data: T | null;
    loading: boolean;
    error: Error | null;
    isDisconnected: boolean;
    refetch: () => Promise<void>;
    invalidate: () => void;
}

export function useApiCache<T>(
    key: string,
    fetcher: () => Promise<T>,
    options: UseApiCacheOptions = {}
): UseApiCacheReturn<T> {
    const { enabled = true, cacheDuration = CACHE_DURATION } = options;
    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);
    const [isDisconnected, setIsDisconnected] = useState(false);
    const abortControllerRef = useRef<AbortController | null>(null);
    const isMountedRef = useRef(true);
    const hasAttemptedFetch = useRef(false);

    const fetchData = useCallback(async (force = false) => {
        if (!enabled) return;

        // Check connection status before fetching (only if not forcing)
        const connectionContext = getConnectionContext();
        if (connectionContext?.status === 'disconnected' && !force) {
            setIsDisconnected(true);
            setLoading(false);
            return;
        }

        // Check cache first
        if (!force) {
            const cached = cache.get(key);
            if (cached && Date.now() - cached.timestamp < cacheDuration) {
                setData(cached.data as T);
                setLoading(false);
                setIsDisconnected(false);
                return;
            }
        }

        // Abort previous request
        if (abortControllerRef.current) {
            abortControllerRef.current.abort();
        }

        abortControllerRef.current = new AbortController();
        setLoading(true);
        setError(null);

        try {
            const result = await fetcher();

            if (isMountedRef.current) {
                cache.set(key, { data: result, timestamp: Date.now() });
                setData(result);
                setIsDisconnected(false);
            }
        } catch (err) {
            if (err instanceof Error && err.name !== 'AbortError' && isMountedRef.current) {
                setError(err);
                // Check if it was a network error
                const errorMsg = err.message || '';
                if (errorMsg.includes('Network Error') ||
                    errorMsg.includes('ECONNREFUSED') ||
                    errorMsg.includes('ERR_NETWORK')) {
                    setIsDisconnected(true);
                }
            }
        } finally {
            if (isMountedRef.current) {
                setLoading(false);
            }
        }
    }, [key, fetcher, enabled, cacheDuration]);

    // Initial fetch based on connection status
    useEffect(() => {
        isMountedRef.current = true;
        hasAttemptedFetch.current = false;

        const connectionContext = getConnectionContext();

        // Determine if we should fetch
        if (connectionContext?.status === 'disconnected') {
            // Disconnected - don't fetch, show disconnected state
            setIsDisconnected(true);
            setLoading(false);
        } else if (connectionContext?.status === 'checking') {
            // Still checking - wait a bit then try
            setLoading(true);
            const timer = setTimeout(() => {
                if (isMountedRef.current && !hasAttemptedFetch.current) {
                    hasAttemptedFetch.current = true;
                    fetchData();
                }
            }, 500);
            return () => clearTimeout(timer);
        } else {
            // Connected or no context - fetch immediately
            hasAttemptedFetch.current = true;
            fetchData();
        }

        return () => {
            isMountedRef.current = false;
            if (abortControllerRef.current) {
                abortControllerRef.current.abort();
            }
        };
    }, [fetchData]);

    const invalidate = useCallback(() => {
        cache.delete(key);
    }, [key]);

    return {
        data,
        loading,
        error,
        isDisconnected,
        refetch: () => {
            hasAttemptedFetch.current = true;
            return fetchData(true);
        },
        invalidate,
    };
}
