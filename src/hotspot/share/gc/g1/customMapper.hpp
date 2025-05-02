#ifndef CUSTOMMAPPER_H
#define CUSTOMMAPPER_H

#include <cstddef>
#include "runtime/globals.hpp"

class SanitizerGCMapper {
private:
    // TODO, put the public variables back here

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

    static void testPrint(void* newAddr);
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

#endif //CUSTOMMAPPER_H
