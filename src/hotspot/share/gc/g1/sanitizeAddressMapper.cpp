#include "sanitizeAddressMapper.hpp"
#include "sanitizeHeapRegionList.hpp"

const void* SanitizeGCMapper::mapNewAddrToOriginalAddr(const void* newAddr) {
    const region_info_t ri = SanitizeGCHeapRegionList::get_region_from_list(newAddr, true);

    if (ri.offset != 0) {
        return static_cast<byte_ptr>(newAddr) + ri.offset;
    }

    return newAddr;
}

const void* SanitizeGCMapper::mapOriginalAddrToNewAddr(const void* originalAddr) {
    const region_info_t ri = SanitizeGCHeapRegionList::get_region_from_list(originalAddr, false);

    if (ri.offset != 0) {
        return static_cast<byte_ptr>(originalAddr) - ri.offset;
    }

    return originalAddr;
}
