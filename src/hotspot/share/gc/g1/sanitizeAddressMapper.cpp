#include "sanitizeAddressMapper.hpp"

#include <memory/resourceArea.hpp>

#include "sanitizeHeapRegionList.hpp"

const void* SanitizeGCMapper::mapNewAddrToOriginalAddr(const void* newAddr) {
    const region_info_t ri = SanitizeGCHeapRegionList::get_region_from_list(newAddr, true);
    // if (SanitizeGCRegionMaps::are_initialized) {
    //     const void* original_addr = SanitizeGCRegionMaps::moved_map->get(newAddr);
    //
    //     if (original_addr != nullptr) {
    //         uintptr_t new_ptr = (uintptr_t)newAddr;
    //         const uintptr_t low_mask = ((uintptr_t)1 << (SanitizeGCConsts::mask_size - 5)) - 1;
    //         uintptr_t mask = new_ptr & low_mask;
    //         uintptr_t original_masked = (uintptr_t)original_addr & ~low_mask;
    //         original_masked |= mask;
    //
    //         printf("-%p\n", (const void*) original_masked);
    //     }
    // }

    if (ri.offset != 0) {
        printf("+%p\n", static_cast<byte_ptr>(newAddr) + ri.offset);
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
