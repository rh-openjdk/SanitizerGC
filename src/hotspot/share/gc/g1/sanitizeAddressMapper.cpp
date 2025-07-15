#include "sanitizeAddressMapper.hpp"
#include "sanitizeHeapRegionList.hpp"

const void* SanitizeGCMapper::mapNewAddrToOriginalAddr(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* original_addr = SanitizeGCRegionMaps::moved_map->get(newAddr);

        if (original_addr != nullptr) {
            uintptr_t new_ptr = (uintptr_t)newAddr;
            const uintptr_t mask_bits = ((uintptr_t)1 << (SanitizeGCConsts::mask_size)) - 1;
            uintptr_t low_bits = new_ptr & mask_bits;

            uintptr_t original_masked = (uintptr_t)original_addr | low_bits;

            return (const void*) original_masked;
        }
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
