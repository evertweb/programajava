/**
 * Formatting Utilities
 * Shared formatters for currency, dates, and other common data types
 */

/**
 * Format a number as currency
 * @param amount - The amount to format
 * @param currency - Currency code (default: CRC for Costa Rica)
 * @param locale - Locale for formatting (default: es-CR)
 */
export const formatCurrency = (
    amount: number,
    currency: string = 'CRC',
    locale: string = 'es-CR'
): string => {
    return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency,
    }).format(amount);
};

/**
 * Format a date string or Date object
 * @param date - The date to format
 * @param options - Intl.DateTimeFormatOptions
 */
export const formatDate = (
    date: string | Date,
    options?: Intl.DateTimeFormatOptions
): string => {
    const dateObj = typeof date === 'string' ? new Date(date) : date;

    const defaultOptions: Intl.DateTimeFormatOptions = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        ...options,
    };

    return new Intl.DateTimeFormat('es-CR', defaultOptions).format(dateObj);
};

/**
 * Format a date string to include time
 * @param date - The date to format
 */
export const formatDateTime = (date: string | Date): string => {
    return formatDate(date, {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    });
};

/**
 * Format a boolean value with custom labels
 * @param value - The boolean value
 * @param labels - Tuple of [trueLabel, falseLabel]
 */
export const formatBoolean = (
    value: boolean,
    labels: [string, string] = ['Sí', 'No']
): string => {
    return value ? labels[0] : labels[1];
};

/**
 * Format a number with thousand separators
 * @param value - The number to format
 * @param locale - Locale for formatting (default: es-CR)
 */
export const formatNumber = (
    value: number,
    locale: string = 'es-CR'
): string => {
    return new Intl.NumberFormat(locale).format(value);
};

/**
 * Truncate text to a maximum length with ellipsis
 * @param text - The text to truncate
 * @param maxLength - Maximum length before truncation
 */
export const truncateText = (text: string, maxLength: number = 50): string => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
};
