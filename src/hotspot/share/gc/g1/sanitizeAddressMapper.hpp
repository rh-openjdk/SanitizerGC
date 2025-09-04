#ifndef SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
#define SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP

class SanitizeGCMapper {
public:

    static const void* mapNewAddrToOriginalAddrImpl(const void* newAddr);
    static const void* mapNewEdgeAddrToOriginalAddrImpl(const void* newAddr);
    static const void* mapOriginalAddrToNewAddrImpl(const void *newAddr);
    static const void* mapOriginalEdgeAddrToNewAddrImpl(const void* newAddr);

    template <typename T> static inline void mapNewAddrToOriginalAddr(T &addr) {
      addr = (T) mapNewAddrToOriginalAddrImpl(addr);
    }
    template <typename T> static inline void mapNewEdgeAddrToOriginalAddr(T &addr) {
      addr = (T) mapNewEdgeAddrToOriginalAddrImpl(addr);
    }
    template <typename T> static inline void mapOriginalAddrToNewAddr(T &addr) {
      addr = (T) mapOriginalAddrToNewAddrImpl(addr);
    }
    template <typename T> static inline void mapOriginalEdgeAddrToNewAddr(T &addr) {
      addr = (T) mapOriginalEdgeAddrToNewAddrImpl(addr);
    }
};

#endif // SHARE_GC_G1_SANITIZEADDRESSMAPPER_HPP
