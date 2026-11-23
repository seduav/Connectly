export function getErrorMessage(error, fallback) {
    return (
        error.response?.data?.message ||
        error.response?.data?.error ||
        fallback
    );
}