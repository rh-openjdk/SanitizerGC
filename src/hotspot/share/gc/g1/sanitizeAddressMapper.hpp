#ifndef SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
#define SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP

#include <cstddef>
#include "runtime/globals.hpp"

class SanitizeGCMapper {
private:
    // TODO, put the public variables back here
    using byte = unsigned char;
    using byte_ptr = const byte*;

public:
    static ptrdiff_t movedRegionOffset;
    static const void* movedRegionStart;
    static const void* movedRegionEnd;
    static const void* originalRegionStart;
    static const void* originalRegionEnd;

    static void initializeMapping(const void* originalRegionStart,
            const void* originalRegionEnd, const void* movedRegionStart, const void* movedRegionEnd);
    static const void* mapNewAddrToOriginalAddr(const void* newAddr);
    static const void* mapOriginalAddrToNewAddr(const void *newAddr);

    template <typename T> static inline void remapAddress(T &addr) {
      if (SanitizeGC) {
        addr = (T) mapNewAddrToOriginalAddr(addr);
      }
    }
    template <typename T> static inline void reverseRemapAddress(T &addr) {
        if (SanitizeGC) {
            addr = (T) mapOriginalAddrToNewAddr(addr);
        }
    }
};

#endif // SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
