#ifndef SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
#define SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP

#include "runtime/globals.hpp"

class SanitizeGCMapper {
private:
    using byte = unsigned char;
    using byte_ptr = const byte*;

public:

    static const void* mapNewAddrToOriginalAddr(const void* newAddr);
    static const void* mapNewEdgeAddrToOriginalAddr(const void* newAddr);
    static const void* mapOriginalAddrToNewAddr(const void *newAddr);
    static const void* mapOriginalEdgeAddrToNewAddr(const void* newAddr);

    template <typename T> static inline void remapAddress(T &addr) {
      if (SanitizeGC) {
        addr = (T) mapNewAddrToOriginalAddr(addr);
      }
    }
    template <typename T> static inline void remapEdgeAddress(T &addr) {
        if (SanitizeGC) {
            addr = (T) mapNewEdgeAddrToOriginalAddr(addr);
        }
    }
    template <typename T> static inline void reverseRemapAddress(T &addr) {
        if (SanitizeGC) {
            addr = (T) mapOriginalAddrToNewAddr(addr);
        }
    }
    template <typename T> static inline void reverseRemapEdgeAddress(T &addr) {
        if (SanitizeGC) {
            addr = (T) mapOriginalEdgeAddrToNewAddr(addr);
        }
    }
};

#endif // SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
