/**
 * API Cache Hook
 * Custom hook for caching API responses and reducing redundant calls
 */

import { useState, useEffect, useCallback, useRef } from 'react';

interface CacheEntry<T> {
    data: T;
    timestamp: number;
}

const cache = new Map<string, CacheEntry<any>>();
const CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

interface UseApiCacheOptions {
    enabled?: boolean;
    cacheDuration?: number;
}

interface UseApiCacheReturn<T> {
    data: T | null;
    loading: boolean;
    error: Error | null;
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
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);
    const abortControllerRef = useRef<AbortController | null>(null);
    const isMountedRef = useRef(true);

    const fetchData = useCallback(async (force = false) => {
        if (!enabled) return;

        // Check cache
        if (!force) {
            const cached = cache.get(key);
            if (cached && Date.now() - cached.timestamp < cacheDuration) {
                setData(cached.data);
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
            }
        } catch (err) {
            if (err instanceof Error && err.name !== 'AbortError' && isMountedRef.current) {
                setError(err);
            }
        } finally {
            if (isMountedRef.current) {
                setLoading(false);
            }
        }
    }, [key, fetcher, enabled, cacheDuration]);

    useEffect(() => {
        isMountedRef.current = true;
        fetchData();

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
        refetch: () => fetchData(true),
        invalidate,
    };
}
