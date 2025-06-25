#include "sanitizeAddressMapper.hpp"

ptrdiff_t SanitizeGCMapper::movedRegionOffset = 0;
const void* SanitizeGCMapper::movedRegionStart = nullptr;
const void* SanitizeGCMapper::movedRegionEnd = nullptr;
const void* SanitizeGCMapper::originalRegionStart = nullptr;
const void* SanitizeGCMapper::originalRegionEnd = nullptr;

// Since we can't use void* for pointer arithmetic, we need another pointer
// type, whose base element size is the unit for movedRegionOffset. We must use
// the same pointer type when computing movedRegionOffset as when applying it,
// so we define it here to ensure it is the same at both places. We choose char*
// which makes movedRegionOffset unit to be bytes.
using byte_ptr = const char*;

void SanitizeGCMapper::initializeMapping(const void* originalRegionStart,
        const void* originalRegionEnd, const void* movedRegionStart, const void* movedRegionEnd) {
    SanitizeGCMapper::movedRegionOffset =
            static_cast<byte_ptr>(originalRegionStart) -
            static_cast<byte_ptr>(movedRegionStart);
    SanitizeGCMapper::movedRegionStart = movedRegionStart;
    SanitizeGCMapper::movedRegionEnd = movedRegionEnd;
    SanitizeGCMapper::originalRegionStart = originalRegionStart;
    SanitizeGCMapper::originalRegionEnd = originalRegionEnd;
}

const void* SanitizeGCMapper::mapNewAddrToOriginalAddr(const void* newAddr) {
    if (movedRegionOffset != 0 &&
            newAddr >= movedRegionStart && newAddr <= movedRegionEnd) {
        return static_cast<byte_ptr>(newAddr) + movedRegionOffset;
    }

    return newAddr;
}

const void* SanitizeGCMapper::mapOriginalAddrToNewAddr(const void* originalAddr) {
    if (movedRegionOffset != 0 &&
            originalAddr >= originalRegionStart && originalAddr <= originalRegionEnd) {
        return static_cast<byte_ptr>(originalAddr) - movedRegionOffset;
            }

    return originalAddr;
}
