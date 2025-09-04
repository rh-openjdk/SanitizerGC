#include "sanitizeAddressMapper.hpp"
#include "sanitizeGCHeapRegionMap.hpp"

const void* SanitizeGCMapper::mapNewAddrToOriginalAddrImpl(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::moved_to_original->remap_address(newAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return newAddr;
}

const void* SanitizeGCMapper::mapNewEdgeAddrToOriginalAddrImpl(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::moved_to_original->remap_end_address(newAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return newAddr;
}

const void* SanitizeGCMapper::mapOriginalAddrToNewAddrImpl(const void* originalAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::original_to_moved->remap_address(originalAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return originalAddr;
}

const void* SanitizeGCMapper::mapOriginalEdgeAddrToNewAddrImpl(const void* newAddr) {
    if (SanitizeGCRegionMaps::are_initialized) {
        const void* remapped = SanitizeGCRegionMaps::original_to_moved->remap_end_address(newAddr);
        if (remapped != nullptr) {
            return remapped;
        }
    }

    return newAddr;
}
